package com.tuto.realestatemanager.ui.detail

import com.tuto.realestatemanager.model.PhotoEntity

data class PropertyDetailViewState(
    val id: Long,
    val type: String,
    val price: Int,
    val photoList: List<PhotoEntity>,
    val address: String,
    val city: String,
    val zipcode: Int,
    val state: String,
    val country: String,
    val surface: Int,
    val description: String,
    val room: Int,
    val bathroom: Int,
    val bedroom: Int,
    val saleSince: String,
    val poiTrain: Boolean,
    val poiAirport: Boolean,
    val poiResto: Boolean,
    val poiSchool: Boolean,
    val poiBus: Boolean,
    val poiPark: Boolean
) {
}