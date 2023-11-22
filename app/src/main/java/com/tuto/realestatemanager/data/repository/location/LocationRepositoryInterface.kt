package com.tuto.realestatemanager.data.repository.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepositoryInterface {

    fun getUserLocation(): Flow<Location>

}