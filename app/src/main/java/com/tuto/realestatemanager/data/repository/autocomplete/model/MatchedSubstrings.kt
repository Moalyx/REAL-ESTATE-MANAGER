package com.tuto.realestatemanager.data.repository.autocomplete.model

import com.google.gson.annotations.SerializedName


data class MatchedSubstrings (

  @SerializedName("length" ) var length : Int? = null,
  @SerializedName("offset" ) var offset : Int? = null

)