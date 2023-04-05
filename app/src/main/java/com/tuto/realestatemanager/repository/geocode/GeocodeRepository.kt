package com.tuto.realestatemanager.repository.geocode

import com.tuto.realestatemanager.repository.geocode.model.GeocodeResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface GeocodeRepository {

    suspend fun getLocationByAddress(address : String) : Flow<String>

}