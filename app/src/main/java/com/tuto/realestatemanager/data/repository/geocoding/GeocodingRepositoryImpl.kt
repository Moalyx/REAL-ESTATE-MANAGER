package com.tuto.realestatemanager.data.repository.geocoding

import com.tuto.realestatemanager.BuildConfig
import com.tuto.realestatemanager.data.api.GoogleApi
import com.tuto.realestatemanager.data.repository.geocoding.model.GeocodingResponse
import com.tuto.realestatemanager.domain.usecase.geocode.model.LocationEntity
import javax.inject.Inject

class GeocodingRepositoryImpl @Inject constructor(
    private val googleApi: GoogleApi
) : GeocodingRepository {

    override suspend fun getLatLngLocation(address : String) : LocationEntity{

        val response : GeocodingResponse = googleApi.getLatLngLocation(BuildConfig.GOOGLE_PLACES_KEY, address)

        val lat = response.results[0].geometry?.location?.lat
        val lng = response.results[0].geometry?.location?.lng

        return LocationEntity(
            lat,
            lng
        )
    }


}