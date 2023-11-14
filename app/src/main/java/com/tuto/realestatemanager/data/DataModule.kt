package com.tuto.realestatemanager.data

import android.content.Context
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tuto.realestatemanager.MainApplication
import com.tuto.realestatemanager.data.api.GoogleApi
import com.tuto.realestatemanager.data.database.PropertyDao
import com.tuto.realestatemanager.data.database.PropertyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Clock
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    companion object {
        private const val BASE_URL = "https://maps.googleapis.com/"
    }


    @Provides
    @Singleton
    fun providePropertyDatabase(
        @ApplicationContext context: Context,
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
        propertyDatabase: PropertyDatabase,
    ): PropertyDao {
        return propertyDatabase.getPropertyDao()
    }

    @Provides
    @Singleton
    fun provideGoogleApi(): GoogleApi {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)
        val gson: Gson = GsonBuilder().setLenient().create()
        val httpClient = OkHttpClient().newBuilder()
            .addInterceptor(interceptor)
            .connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).build()

        val retrofitService: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofitService.create(GoogleApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.systemDefaultZone()

}