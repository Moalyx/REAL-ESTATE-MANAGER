package com.tuto.realestatemanager.data.repository.placedetail.model

import com.google.gson.annotations.SerializedName


data class Viewport(

    @SerializedName("northeast") val northeast: Northeast?,
    @SerializedName("southwest") val southwest: Southwest?

)