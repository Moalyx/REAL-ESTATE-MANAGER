package com.tuto.realestatemanager.ui.list

data class PropertyViewState(
    val id: Int,
    val type: String,
    val price: Int,
    val photoList: List<String>,
    val county: String,
    val onItemClicked: () -> Unit,
//    val onClickForIntent: () -> Unit,
)