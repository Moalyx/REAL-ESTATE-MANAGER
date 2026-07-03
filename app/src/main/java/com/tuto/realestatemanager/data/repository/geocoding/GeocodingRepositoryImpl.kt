package com.tuto.realestatemanager.data.repository.geocoding

import com.tuto.realestatemanager.BuildConfig
import com.tuto.realestatemanager.data.api.GoogleApi
import com.tuto.realestatemanager.data.repository.geocoding.model.GeocodingResponse
import com.tuto.realestatemanager.domain.usecase.geocode.model.LocationEntity
import javax.inject.Inject

class GeocodingRepositoryImpl @Inject constructor(
    private val googleApi: GoogleApi
) : GeocodingRepository {

    override suspend fun getLatLngLocation(address: String): LocationEntity {

        return try {
            val response: GeocodingResponse =
                googleApi.getLatLngLocation(BuildConfig.GOOGLE_PLACES_KEY, address)

            if (response.status != "OK") {
                return LocationEntity(
                    lat = null,
                    lng = null
                )
            }

            val location = response.results
                .firstOrNull()
                ?.geometry
                ?.location

            LocationEntity(
                lat = location?.lat,
                lng = location?.lng
            )

        } catch (e: Exception) {
            LocationEntity(
                lat = null,
                lng = null
            )
        }
    }
}