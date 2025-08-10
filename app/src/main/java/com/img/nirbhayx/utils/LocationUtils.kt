package com.img.nirbhayx.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.img.nirbhayx.data.LocationData
import java.util.Locale

class LocationUtils(val context: Context) {

    private val _fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(onLocationReceived: (LocationData) -> Unit) {
        if (!hasLocationPermission(context)) {
            Log.e("LocationUtils", "Location permission not granted")
            return
        }

        try {
            stopLocationUpdates()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    locationResult.lastLocation?.let { location ->
                        val locationData = LocationData(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                        onLocationReceived(locationData)

                        stopLocationUpdates()
                    }
                }
            }

            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 5000L
            ).apply {
                setMinUpdateIntervalMillis(2000L)
                setMaxUpdateDelayMillis(10000L)
                setMaxUpdates(1)
            }.build()

            _fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )

            Log.d("LocationUtils", "Location updates requested")

        } catch (e: SecurityException) {
            Log.e("LocationUtils", "Security exception in location updates", e)
        } catch (e: Exception) {
            Log.e("LocationUtils", "Exception in location updates", e)
        }
    }

    fun stopLocationUpdates() {
        locationCallback?.let { callback ->
            try {
                _fusedLocationClient.removeLocationUpdates(callback)
                Log.d("LocationUtils", "Location updates stopped")
            } catch (e: Exception) {
                Log.e("LocationUtils", "Error stopping location updates", e)
            } finally {
                locationCallback = null
            }
        }
    }


    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun reverseGeocodeLocation(location: LocationData): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses: MutableList<Address>? =
                geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                addresses[0].getAddressLine(0)
            } else {
                "ADDRESS NOT FOUND"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "GEOCODING ERROR"
        }
    }
}