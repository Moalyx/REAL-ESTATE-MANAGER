package com.tuto.realestatemanager.ui.createproperty

data class PlaceDetailViewState(
    val number: String,
    val address: String,
    val city: String,
    val zipCode: String,
    val state: String,
    val country: String,
    val lat: Double,
    val lng: Double
)
