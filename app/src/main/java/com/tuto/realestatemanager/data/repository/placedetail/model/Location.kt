package com.tuto.realestatemanager.data.repository.placedetail.model

import com.google.gson.annotations.SerializedName


data class Location(

    @SerializedName("lat") val lat: Double?,
    @SerializedName("lng") val lng: Double?

)