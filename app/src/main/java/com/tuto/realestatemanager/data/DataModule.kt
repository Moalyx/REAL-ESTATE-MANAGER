package com.tuto.realestatemanager.data

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tuto.realestatemanager.api.GoogleApi
import com.tuto.realestatemanager.database.PropertyDao
import com.tuto.realestatemanager.database.PropertyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    companion object{
        private const val BASE_URL = "https://maps.googleapis.com/maps/api/geocode/"
    }


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

    @Provides
    @Singleton
    fun provideGoogleApi() : GoogleApi {
        val gson: Gson = GsonBuilder().setLenient().create()
        val httpClient = OkHttpClient().newBuilder().build()
        val retrofitService: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofitService.create(GoogleApi::class.java)
    }

}