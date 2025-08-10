package com.img.nirbhayx.viewmodels

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.img.nirbhayx.data.Graph


class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: MutableLiveData<AuthState> = _authState

    private val _profile = mutableStateMapOf(
        "name" to "",
        "email" to "",
        "address" to "",
        "phone" to ""
    )

    fun changeProfile(name: String, email: String, address: String, phone: String) {
        _profile["name"] = name
        _profile["email"] = email
        _profile["address"] = address
        _profile["phone"] = phone
    }

    fun getProfile(key: String): String {
        when (key) {
            "name" -> return _profile["name"] ?: ""
            "email" -> return _profile["email"] ?: ""
            "address" -> return _profile["address"] ?: ""
            "phone" -> return _profile["phone"] ?: ""
            else -> return ""
        }
    }

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Graph.createRepositories(currentUser.uid)
            _authState.value = AuthState.Authenticated
        } else {
            Graph.clearRepositories()
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        Graph.createRepositories(uid)
                        _authState.value = AuthState.Authenticated
                    } else {
                        _authState.value = AuthState.Error("Failed to get user ID")
                    }
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signup(
        email: String,
        password: String,
        name: String,
        address: String,
        phone: String,
        confirmPassword: String
    ) {
        if (email.isBlank() || password.isBlank() || name.isBlank() || address.isBlank() || phone.isBlank()) {
            _authState.value = AuthState.Error("None of the fields can be empty")
            return
        } else if (phone.length != 10 || !phone.all { it.isDigit() }) {
            _authState.value = AuthState.Error("Please enter a valid phone number")
            return
        } else if (password.length < 8 || (!password.any { it.isDigit() } || !password.any { it.isLetter() })) {
            _authState.value =
                AuthState.Error("Password should be at least 8 characters and include letters and numbers")
            return
        } else if (password != confirmPassword) {
            _authState.value = AuthState.Error("Passwords do not match")
            return
        }

        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val userProfile = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "address" to address,
                        "phone" to phone
                    )
                    changeProfile(name, email, address, phone)

                    if (userId != null) {
                        Graph.createRepositories(userId)

                        firestore.collection("users").document(userId)
                            .set(userProfile)
                            .addOnSuccessListener {
                                _authState.value = AuthState.Authenticated
                            }
                            .addOnFailureListener { e ->
                                _authState.value =
                                    AuthState.Error("User created, but failed to save profile: ${e.message}")
                            }
                    } else {
                        _authState.value = AuthState.Error("Failed to get user ID")
                    }
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun logout() {
        auth.signOut()
        Graph.clearRepositories()
        _authState.value = AuthState.Unauthenticated
    }

}

sealed class AuthState() {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}