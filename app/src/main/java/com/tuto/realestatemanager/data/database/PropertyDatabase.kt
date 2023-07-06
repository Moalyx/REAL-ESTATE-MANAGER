package com.tuto.realestatemanager.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity

@Database(
    entities = [
        PropertyEntity::class,
        PhotoEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class PropertyDatabase : RoomDatabase() {

    abstract fun getPropertyDao(): PropertyDao
}