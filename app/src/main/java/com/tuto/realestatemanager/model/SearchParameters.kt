package com.tuto.realestatemanager.model

data class SearchParameters(
    val type: String?,
    val priceMinimum: Int?,
    val priceMaximum: Int?,
    val surfaceMinimum: Int?,
    val surfaceMaximum: Int?,
    val city: String?,
    val poiTrain: Boolean,
    val poiAirport: Boolean,
    val poiResto: Boolean,
    val poiSchool: Boolean,
    val poiBus: Boolean,
    val poiPark: Boolean
)
