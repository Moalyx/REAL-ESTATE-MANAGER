package com.tuto.realestatemanager.repository.autocomplete.model

import com.google.gson.annotations.SerializedName


data class MatchedSubstrings (

  @SerializedName("length" ) var length : Int? = null,
  @SerializedName("offset" ) var offset : Int? = null

)