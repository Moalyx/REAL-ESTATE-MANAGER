//package com.tuto.realestatemanager.database
//
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import com.tuto.realestatemanager.model.PropertyEntity
//import kotlinx.coroutines.flow.Flow
//
//@Dao
//interface PropertyDao {
//
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    fun insertProperty(propertyEntity: PropertyEntity)
//
//    @Query("SELECT * FROM property_table")
//    suspend fun getAllProperties(): Flow<List<PropertyEntity>>
//
//    @Query("SELECT * FROM property_table WHERE id=:id")
//    suspend fun getPropertyById(id : Int): Flow<PropertyEntity>
//
//    @Query("DELETE FROM property_table WHERE id=:id")
//    fun deleteProperty(id: Int)
//
//}