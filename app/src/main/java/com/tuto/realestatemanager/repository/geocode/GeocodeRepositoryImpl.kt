package com.tuto.realestatemanager.repository.geocode

import com.tuto.realestatemanager.BuildConfig
import com.tuto.realestatemanager.api.GoogleApi
import javax.inject.Inject

class GeocodeRepositoryImpl @Inject constructor(
    private val googleApi: GoogleApi
) : GeocodeRepository {


    override suspend fun getLocationByAddress(address: String): String {

        return googleApi.getGeocodingLocation(address, BuildConfig.GOOGLE_PLACES_KEY).toString()
    }




}