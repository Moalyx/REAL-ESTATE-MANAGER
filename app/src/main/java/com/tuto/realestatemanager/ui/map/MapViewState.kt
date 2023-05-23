package com.tuto.realestatemanager.ui.map

data class MapViewState(
    val lat: Double,
    val lng: Double,
    val marker: List<MarkerPlace>
)
