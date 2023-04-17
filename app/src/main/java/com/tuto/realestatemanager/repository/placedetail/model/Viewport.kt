package com.tuto.realestemanager.repository.placedetail.model

import com.google.gson.annotations.SerializedName


data class Viewport (

  @SerializedName("northeast" ) var northeast : Northeast? = Northeast(),
  @SerializedName("southwest" ) var southwest : Southwest? = Southwest()

)