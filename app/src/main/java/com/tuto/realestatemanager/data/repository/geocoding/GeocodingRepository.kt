package com.tuto.realestatemanager.data.repository.geocoding

import com.tuto.realestatemanager.data.repository.geocoding.model.GeocodingResponse


interface GeocodingRepository {

    suspend fun getLatLngLocation(address :String) : GeocodingResponse
}