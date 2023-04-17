package com.tuto.realestemanager.repository.placedetail.model

import com.google.gson.annotations.SerializedName


data class PlaceResult (

  @SerializedName("address_components"    ) var addressComponents   : ArrayList<AddressComponents> = arrayListOf(),
  @SerializedName("adr_address"           ) var adrAddress          : String?                      = null,
  @SerializedName("formatted_address"     ) var formattedAddress    : String?                      = null,
  @SerializedName("geometry"              ) var geometry            : Geometry?                    = Geometry(),
  @SerializedName("icon"                  ) var icon                : String?                      = null,
  @SerializedName("icon_background_color" ) var iconBackgroundColor : String?                      = null,
  @SerializedName("icon_mask_base_uri"    ) var iconMaskBaseUri     : String?                      = null,
  @SerializedName("name"                  ) var name                : String?                      = null,
  @SerializedName("place_id"              ) var placeId             : String?                      = null,
  @SerializedName("reference"             ) var reference           : String?                      = null,
  @SerializedName("types"                 ) var types               : ArrayList<String>            = arrayListOf(),
  @SerializedName("url"                   ) var url                 : String?                      = null,
  @SerializedName("utc_offset"            ) var utcOffset           : Int?                         = null,
  @SerializedName("vicinity"              ) var vicinity            : String?                      = null

)