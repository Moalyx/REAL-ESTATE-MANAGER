package com.tuto.realestatemanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "temporary_photo_table")
data class TemporaryPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val photoUri: String,
)