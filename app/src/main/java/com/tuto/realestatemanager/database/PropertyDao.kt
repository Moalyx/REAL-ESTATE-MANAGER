package com.tuto.realestatemanager.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.PropertyEntity
import com.tuto.realestatemanager.model.PropertyWithPhotosEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {

    //////////   PROPERTY  //////////

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProperty(propertyEntity: PropertyEntity): Long

    @Update
    suspend fun updateProperty(propertyEntity: PropertyEntity)

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
    suspend fun deletePropertyById(id: Long)

    //////////  PHOTO  //////////

    @Query("SELECT * FROM photo_table WHERE id=:id")
    fun getPhotoByID(id: Long): Flow<PhotoEntity>

    @Update
    suspend fun updatePhoto(photoEntity: PhotoEntity)

    @Query("SELECT * FROM photo_table")
    fun getAllPhotos(): Flow<List<PhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhoto(photoEntity: PhotoEntity)

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertTemporaryPhoto(temporaryPhotoEntity: TemporaryPhotoEntity)

//    @Query("DELETE FROM temporary_photo_table")
//    suspend fun flushTemporaryPhotos()

    @Query("DELETE FROM photo_table WHERE id=:id")
    suspend fun deletePhotoById(id: Long)

    @Query("DELETE FROM photo_table WHERE propertyId =:propertyId")
    suspend fun deleteAllPropertyPhotos(propertyId: Long)

}