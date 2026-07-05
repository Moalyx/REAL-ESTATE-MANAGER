package com.tuto.realestatemanager.model

data class SearchParameters(
    val type: String? = null,
    val priceMinimum: Int? = null,
    val priceMaximum: Int? = null,
    val surfaceMinimum: Int? = null,
    val surfaceMaximum: Int? = null,
    val city: String? = null,
    val poiTrain: Boolean = false,
    val poiAirport: Boolean = false,
    val poiResto: Boolean = false,
    val poiSchool: Boolean = false,
    val poiBus: Boolean = false,
    val poiPark: Boolean = false,
    val soldStatus: Boolean? = null,
    val minimumPhotos: Int? = null
)