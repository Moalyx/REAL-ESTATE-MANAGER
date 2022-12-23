package com.tuto.realestatemanager.data

import android.content.Context
import androidx.room.Room
import com.tuto.realestatemanager.database.PropertyDao
import com.tuto.realestatemanager.database.PropertyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun providePropertyDatabase(
        @ApplicationContext context: Context
    ): PropertyDatabase {
        return Room.databaseBuilder(
            context,
            PropertyDatabase::class.java,
            "property_database"
        )
            .build()
    }

    @Provides
    @Singleton
    fun providePropertyDao(
        propertyDatabase : PropertyDatabase
    ): PropertyDao {
        return propertyDatabase.getPropertyDao()
    }
}