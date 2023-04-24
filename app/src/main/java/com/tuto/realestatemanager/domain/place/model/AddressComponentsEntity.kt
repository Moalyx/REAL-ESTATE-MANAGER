package com.tuto.realestatemanager.domain.place.model

import com.google.android.gms.maps.model.LatLng

data class AddressComponentsEntity(
    val streetNumber: String,
    val fullAddress: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String,
    val lat: Double,
    val lng: Double,
)