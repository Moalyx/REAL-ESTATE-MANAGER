package com.tuto.realestatemanager.data.repository.geocoding

import com.tuto.realestatemanager.BuildConfig
import com.tuto.realestatemanager.data.api.GoogleApi
import com.tuto.realestatemanager.data.repository.geocoding.model.GeocodingResponse
import javax.inject.Inject

class GeocodingRepositoryImpl @Inject constructor(
    private val googleApi: GoogleApi
) : GeocodingRepository {

    override suspend fun getLatLngLocation(address : String) : GeocodingResponse{
        return googleApi.getLatLngLocation(BuildConfig.GOOGLE_PLACES_KEY, address)
    }


}