package com.tuto.realestatemanager.data.repository.placedetail.model

import com.google.gson.annotations.SerializedName

data class PlaceDetailResponse(

    @SerializedName("html_attributions") val htmlAttributions: List<String>,
    @SerializedName("result") val placeResult: PlaceResult?,
    @SerializedName("status") val status: String?

)