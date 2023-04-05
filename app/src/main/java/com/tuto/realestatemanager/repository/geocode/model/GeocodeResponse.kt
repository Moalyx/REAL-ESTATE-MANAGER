package com.tuto.realestatemanager.repository.geocode.model

import com.google.gson.annotations.SerializedName


data class GeocodeResponse(

    @SerializedName("results") var results: ArrayList<Results> = arrayListOf(),
    @SerializedName("status") var status: String? = null

)