package com.tuto.realestatemanager.data.repository.autocomplete.model

import com.google.gson.annotations.SerializedName


data class SecondaryTextMatchedSubstrings (

  @SerializedName("length" ) var length : Int? = null,
  @SerializedName("offset" ) var offset : Int? = null

)