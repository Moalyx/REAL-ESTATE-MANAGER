package com.tuto.realestatemanager.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.model.PropertyWithPhotosEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProperty(propertyEntity: PropertyEntity)

    @Query("SELECT * FROM property_table")
    fun getAllProperties(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM property_table WHERE id=:id")
    fun getPropertyById(id: Long): Flow<PropertyEntity>

    @Transaction
    @Query("SELECT * FROM property_table")
    fun getAllPropertyWithPhotos(): Flow<List<PropertyWithPhotosEntity>>

    @Transaction
    @Query("SELECT * FROM property_table WHERE id=:id")
    fun getPropertyWithPhotosById(id: Long): Flow<PropertyWithPhotosEntity>

    @Query("DELETE FROM property_table WHERE id=:id")
    suspend fun deleteProperty(id: Long)

}