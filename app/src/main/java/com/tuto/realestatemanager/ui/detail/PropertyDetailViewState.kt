package com.tuto.realestatemanager.ui.detail

data class PropertyDetailViewState(
    val id: Int,
    val type: String,
    val price: Int,
    val photoList: List<String>,
    val county: String,
    val surface: Int
) {
}