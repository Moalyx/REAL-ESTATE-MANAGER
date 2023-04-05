package com.tuto.realestatemanager.repository.autocomplete.model

import com.google.gson.annotations.SerializedName


data class Terms (

  @SerializedName("offset" ) var offset : Int?    = null,
  @SerializedName("value"  ) var value  : String? = null

)