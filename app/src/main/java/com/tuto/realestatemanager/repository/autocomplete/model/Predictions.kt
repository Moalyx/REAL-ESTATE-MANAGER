package com.tuto.realestatemanager.repository.autocomplete.model

import com.google.gson.annotations.SerializedName


data class Predictions (

  @SerializedName("description"           ) var description          : String?                      = null,
  @SerializedName("matched_substrings"    ) var matchedSubstrings    : ArrayList<MatchedSubstrings> = arrayListOf(),
  @SerializedName("place_id"              ) var placeId              : String?                      = null,
  @SerializedName("reference"             ) var reference            : String?                      = null,
  @SerializedName("structured_formatting" ) var structuredFormatting : StructuredFormatting?        = StructuredFormatting(),
  @SerializedName("terms"                 ) var terms                : ArrayList<Terms>             = arrayListOf(),
  @SerializedName("types"                 ) var types                : ArrayList<String>            = arrayListOf()

)