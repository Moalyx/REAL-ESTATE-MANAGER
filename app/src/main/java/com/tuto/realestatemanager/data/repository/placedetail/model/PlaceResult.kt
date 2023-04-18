package com.tuto.realestatemanager.data.repository.placedetail.model

import com.google.gson.annotations.SerializedName


data class PlaceResult(

    @SerializedName("address_components")
    val addressComponents: List<AddressComponentsResponse>,
    @SerializedName("adr_address")
    val adrAddress: String?,
    @SerializedName("formatted_address")
    val formattedAddress: String?,
    @SerializedName("geometry")
    val geometry: Geometry?,
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("icon_background_color")
    val iconBackgroundColor: String?,
    @SerializedName("icon_mask_base_uri")
    val iconMaskBaseUri: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("place_id")
    val placeId: String?,
    @SerializedName("reference")
    val reference: String?,
    @SerializedName("types")
    val types: List<String>,
    @SerializedName("url")
    val url: String?,
    @SerializedName("utc_offset")
    val utcOffset: Int?,
    @SerializedName("vicinity")
    val vicinity: String?

)