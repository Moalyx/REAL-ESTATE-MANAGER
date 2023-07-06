package com.tuto.realestatemanager.data.api

import com.tuto.realestatemanager.data.repository.autocomplete.model.PredictionResponse
import com.tuto.realestatemanager.data.repository.geocoding.model.GeocodingResponse
import com.tuto.realestatemanager.data.repository.placedetail.model.PlaceDetailResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleApi {

    @GET("maps/api/place/details/json")
    suspend fun getPlaceDetailResponse(
        @Query("key") key : String,
        @Query("place_id") id : String
    ): PlaceDetailResponse

    @GET("maps/api/place/autocomplete/json")
    suspend fun autocompleteResult(
        @Query("key") key : String,
        @Query("location") location : String,
        @Query("radius") radius : String,
        @Query("input") input :String
    ) : PredictionResponse

    @GET("maps/api/geocode/json")
    suspend fun getLatLngLocation(
        @Query("key") key : String,
        @Query("address") address : String

    ) : GeocodingResponse


}