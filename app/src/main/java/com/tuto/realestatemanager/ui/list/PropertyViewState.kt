package com.tuto.realestatemanager.ui.list

import com.tuto.realestatemanager.model.PhotoEntity

data class PropertyViewState(
    val id: Long,
    val type: String,
    val price: Int,
    val photoList: List<PhotoEntity>,
    val country: String,
    val onItemClicked: () -> Unit,
//    val onClickForIntent: () -> Unit,
)