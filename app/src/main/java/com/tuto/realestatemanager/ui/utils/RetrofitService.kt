package com.tuto.realestatemanager.ui.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class RetrofitService {

    companion object{
        private val gson: Gson = GsonBuilder().setLenient().create()
        private val httpClient = OkHttpClient().newBuilder().build()
        private const val BASE_URL = "https://maps.googleapis.com/maps/api/geocode/"
        private val retrofitService: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        fun getGeocodingService(){
            return retrofitService.create()
        }

    }

}