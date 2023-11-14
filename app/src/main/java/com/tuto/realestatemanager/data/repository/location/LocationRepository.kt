package com.tuto.realestatemanager.data.repository.location

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
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
    private val client : FusedLocationProviderClient
){
    companion object {
        private const val UPDATE_INTERVAL_SECS = 2000L
        private const val FASTEST_UPDATE_INTERVAL_SECS = 2000L
    }

    @SuppressLint("MissingPermission")
    fun getUserLocation(): Flow<Location> = callbackFlow {
        val locationRequest = LocationRequest.create()
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_SECS)
            .setInterval(UPDATE_INTERVAL_SECS)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        val callBack = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location: Location? = locationResult.lastLocation
                trySend(location!!)
            }
        }

        client.requestLocationUpdates(locationRequest, callBack, Looper.getMainLooper())
        awaitClose { client.removeLocationUpdates(callBack) }
    }


}