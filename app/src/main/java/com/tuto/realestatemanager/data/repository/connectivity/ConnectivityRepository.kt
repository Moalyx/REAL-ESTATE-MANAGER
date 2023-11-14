package com.tuto.realestatemanager.data.repository.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityRepository {

    fun isInternetAvailable() : Flow<Boolean>

}