package com.tuto.realestatemanager.ui.editproperty

class UpdatePropertyViewState(
    val id: Long,
    val type: String,
    val price: Int,
    //val photoList: List<PhotoEntity>, //todo changer en photoentity puis change adapter
    val address: String,
    val city: String,
    val zipcode: Int,
    val state: String,
    val country: String,
    val lat: Double?,
    val lng: Double?,
    val surface: Int,
    val description: String,
    val agent: String,
    val room: Int,
    val bathroom: Int,
    val bedroom: Int,
    val saleSince: String,
    val poiTrain: Boolean,
    val poiAirport: Boolean,
    val poiResto: Boolean,
    val poiSchool: Boolean,
    val poiBus: Boolean,
    val poiPark: Boolean,
    val isSold: Boolean
)