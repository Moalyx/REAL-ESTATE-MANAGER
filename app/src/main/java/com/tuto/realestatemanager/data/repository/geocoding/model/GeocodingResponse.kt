package com.tuto.realestatemanager.data.repository.geocoding.model

import com.google.gson.annotations.SerializedName


data class GeocodingResponse (

  @SerializedName("results" ) var results : ArrayList<Results> = arrayListOf(),
  @SerializedName("status"  ) var status  : String?            = null

)