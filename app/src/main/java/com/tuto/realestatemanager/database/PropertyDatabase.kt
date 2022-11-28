//package com.tuto.realestatemanager.database
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import com.tuto.realestatemanager.model.PropertyEntity
//import kotlinx.coroutines.InternalCoroutinesApi
//import kotlinx.coroutines.internal.synchronized
//
//
//@Database(entities = [PropertyEntity::class], version = 1, exportSchema = false)
//abstract class PropertyDatabase: RoomDatabase() {
//
//    abstract fun propertyDao(): PropertyDao
//
//    companion object{
//
//        @Volatile
//        private var INSTANCE: PropertyDatabase? = null
//
//
//        fun getInstance(context: Context) : PropertyDatabase{
//
//            return INSTANCE ?: kotlin.synchronized(this){
//
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    PropertyDatabase::class.java,
//                    "property_database")
//                    .build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}