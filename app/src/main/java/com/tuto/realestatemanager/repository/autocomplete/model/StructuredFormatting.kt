package com.tuto.realestatemanager.repository.autocomplete.model

import com.google.gson.annotations.SerializedName


data class StructuredFormatting (

  @SerializedName("main_text"                         ) var mainText                       : String?                                   = null,
  @SerializedName("main_text_matched_substrings"      ) var mainTextMatchedSubstrings      : ArrayList<MainTextMatchedSubstrings>      = arrayListOf(),
  @SerializedName("secondary_text"                    ) var secondaryText                  : String?                                   = null,
  @SerializedName("secondary_text_matched_substrings" ) var secondaryTextMatchedSubstrings : ArrayList<SecondaryTextMatchedSubstrings> = arrayListOf()

)