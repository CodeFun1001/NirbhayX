package com.img.nirbhayx.data

data class CommunityAlert(
    val userId: String = "",
    val username: String = "",
    val contact: String = "",
    val message: String = "🚨 Someone nearby needs help!",
    val timestamp: Long = System.currentTimeMillis(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationText: String = "",
    val isResolved: Boolean = false
)
