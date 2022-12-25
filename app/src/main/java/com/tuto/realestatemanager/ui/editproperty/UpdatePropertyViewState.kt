package com.tuto.realestatemanager.ui.editproperty

class UpdatePropertyViewState (
    val id: Long,
    val type: String,
    val price: Int,
    val photoList: List<String>,
    val county: String,
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
) {}