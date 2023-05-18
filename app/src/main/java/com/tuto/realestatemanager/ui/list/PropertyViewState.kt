package com.tuto.realestatemanager.ui.list

import com.tuto.realestatemanager.model.PhotoEntity

data class PropertyViewState(
    val id: Long,
    val type: String,
    val price: String,
    val photoList: List<PhotoEntity>,
    val city: String,
    val onItemClicked: () -> Unit,
//    val onClickForIntent: () -> Unit,
)