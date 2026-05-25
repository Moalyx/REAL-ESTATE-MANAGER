package com.tuto.realestatemanager.data.repository.connectivity

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectivityRepositoryImpl @Inject constructor(
    private val context: Application
) : ConnectivityRepository {

    private val isConnected: MutableStateFlow<Boolean> = MutableStateFlow(false)


    @RequiresApi(Build.VERSION_CODES.M)
    override fun isInternetAvailable(): Flow<Boolean> {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        isConnected.value =
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        return isConnected
    }

}