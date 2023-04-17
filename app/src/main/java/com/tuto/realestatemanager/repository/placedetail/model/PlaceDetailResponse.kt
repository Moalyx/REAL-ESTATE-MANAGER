package com.tuto.realestemanager.repository.placedetail.model

import com.google.gson.annotations.SerializedName


data class PlaceDetailResponse (

  @SerializedName("html_attributions" ) var htmlAttributions : ArrayList<String> = arrayListOf(),
  @SerializedName("result"            ) var placeResult           : PlaceResult?           = PlaceResult(),
  @SerializedName("status"            ) var status           : String?           = null

)