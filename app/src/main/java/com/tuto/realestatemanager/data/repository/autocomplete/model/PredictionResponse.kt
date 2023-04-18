package com.tuto.realestatemanager.data.repository.autocomplete.model

import com.google.gson.annotations.SerializedName


data class PredictionResponse (

  @SerializedName("predictions" ) var predictions : ArrayList<Predictions> = arrayListOf(),
  @SerializedName("status"      ) var status      : String?                = null

)