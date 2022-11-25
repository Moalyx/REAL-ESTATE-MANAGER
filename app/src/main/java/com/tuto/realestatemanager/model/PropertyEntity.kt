package com.tuto.realestatemanager.model

import androidx.room.Entity

@Entity
class PropertyEntity(
    val id: Int,
    val type: String,
    val price: Int,
    val photo: String,
    val county: String,
    val surface: Int
) {
}