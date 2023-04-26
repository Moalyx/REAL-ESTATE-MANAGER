package com.tuto.realestatemanager.ui.map

import com.google.android.gms.maps.model.Marker

data class MapViewState(

    private val marker: List<Marker>,
    val onItemClicked: () -> Unit
)
