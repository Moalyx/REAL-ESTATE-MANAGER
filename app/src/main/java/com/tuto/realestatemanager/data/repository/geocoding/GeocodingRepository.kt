package com.tuto.realestatemanager.data.repository.geocoding

import com.tuto.realestatemanager.data.repository.geocoding.model.GeocodingResponse
import com.tuto.realestatemanager.domain.usecase.geocode.model.LocationEntity


interface GeocodingRepository {

    suspend fun getLatLngLocation(address :String) : LocationEntity
}