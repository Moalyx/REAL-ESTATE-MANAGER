package com.tuto.realestatemanager.data.repository.placedetail.model

import com.google.gson.annotations.SerializedName


data class AddressComponentsResponse(

    @SerializedName("long_name") val longName: String?,
    @SerializedName("short_name") val shortName: String?,
    @SerializedName("types") val types: List<String>,

)