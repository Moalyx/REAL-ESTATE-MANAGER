package com.tuto.realestatemanager.repository.geocode

interface GeocodeRepository {

    suspend fun getLocationByAddress(address : String) : String

}