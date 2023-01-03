package com.tuto.realestatemanager.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodeService {

    @GET("maps/api/geocode/json")
    suspend fun getGeocodingLocation(
        @Query("address") address: String,
        @Query("number") number: Int,
        @Query("street") street: String,
        @Query("city") city: String,
        @Query("zipcode") zipcode: Int,
        @Query("country") country: String
    ): String


}