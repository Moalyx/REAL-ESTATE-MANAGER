package com.tuto.realestatemanager.data.repository.placedetail.model

import com.google.gson.annotations.SerializedName


data class Geometry(

    @SerializedName("location") val location: Location?,
    @SerializedName("viewport") val viewport: Viewport?,

)