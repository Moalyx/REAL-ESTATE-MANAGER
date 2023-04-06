package com.tuto.realestatemanager.api

import com.tuto.realestatemanager.repository.autocomplete.model.Predictions
import com.tuto.realestatemanager.repository.geocode.model.GeocodeResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleApi {

    @GET("maps/api/geocode/json")
    suspend fun getGeocodingLocation(
        @Query("key") key : String,
        @Query("address") address: String
//        @Query("number") number: Int,
//        @Query("street") street: String,
//        @Query("city") city: String,
//        @Query("zipcode") zipcode: Int,
//        @Query("country") country: String
    ): GeocodeResponse

    @GET("maps/api/place/autocomplete/json")
    suspend fun autocompleteResult(
        @Query("key") key : String,
        @Query("input") input :String
    ) : Response<Predictions>


}