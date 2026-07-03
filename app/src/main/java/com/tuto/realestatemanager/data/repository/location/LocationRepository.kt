package com.tuto.realestatemanager.data.repository.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("DEPRECATION")
class LocationRepository @Inject constructor(
    private val client: FusedLocationProviderClient,
    private val application: Application
) : LocationRepositoryInterface {

    companion object {
        private const val UPDATE_INTERVAL_SECS = 2000L
        private const val FASTEST_UPDATE_INTERVAL_SECS = 2000L
    }

    @SuppressLint("MissingPermission")
    override fun getUserLocation(): Flow<Location?> = callbackFlow {

        if (!hasLocationPermission()) {
            trySend(null)
            close()
            return@callbackFlow
        }

        client.lastLocation
            .addOnSuccessListener { location ->
                trySend(location)
            }
            .addOnFailureListener {
                trySend(null)
            }

        val locationRequest = LocationRequest.create()
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_SECS)
            .setInterval(UPDATE_INTERVAL_SECS)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        val callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                trySend(locationResult.lastLocation)
            }
        }

        client.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
        )

        awaitClose {
            client.removeLocationUpdates(callback)
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    application,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }
}