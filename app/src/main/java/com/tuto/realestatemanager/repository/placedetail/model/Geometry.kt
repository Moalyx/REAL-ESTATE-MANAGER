package com.tuto.realestemanager.repository.placedetail.model

import com.google.gson.annotations.SerializedName


data class Geometry (

  @SerializedName("location" ) var location : Location? = Location(),
  @SerializedName("viewport" ) var viewport : Viewport? = Viewport()

)