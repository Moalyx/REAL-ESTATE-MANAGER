package com.tuto.realestatemanager.di

import android.content.Context
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.GsonBuilder

import com.tuto.realestatemanager.data.api.GoogleApi
import com.tuto.realestatemanager.data.database.PropertyDao
import com.tuto.realestatemanager.data.database.PropertyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Clock
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    companion object {
        private const val BASE_URL = "https://maps.googleapis.com/"
    }

    @Provides
    @Singleton
    fun provideCoroutineDispatchers(): CoroutineContext {
        return Dispatchers.IO
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
    ): PropertyDao = propertyDatabase.getPropertyDao()

    @Provides
    @Singleton
    fun provideGoogleApi() : GoogleApi {
                val interceptor = HttpLoggingInterceptor()
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                val gson: Gson = GsonBuilder().setLenient().create()
                val httpClient = OkHttpClient().newBuilder()
                    .addInterceptor(interceptor)
                    .connectTimeout(10, TimeUnit.SECONDS).
                    readTimeout(10, TimeUnit.SECONDS).build()

        val retrofitService: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofitService.create(GoogleApi::class.java)
    }


//    @Provides
//    @Singleton
//    fun provideGoogleApi(retrofit: Retrofit): GoogleApi =
//        retrofit.create(GoogleApi::class.java)


    @Provides
    @Singleton
    fun provideGoogleApiRetrofit(
        @GoogleApiHttpClient httpClient: OkHttpClient,
        gson: Gson,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()


    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    @Provides
    @Singleton
    @GoogleApiHttpClient
    fun provideHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient().newBuilder()
            .addInterceptor(interceptor)
            .connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS)
            //.addInterceptor(provideInterceptorForKey())
            .build()

//    @Provides
//    @Singleton
//    fun provideInterceptorForKey(): Interceptor = Interceptor { chain ->
//        chain.proceed(
//            chain.request().let {request ->
//                request.newBuilder()
//                    .url(
//                        request.url.newBuilder()
//                            .addQueryParameter("key", BuildConfig.GOOGLE_AUTOCOMPLETE_KEY)
//                            .build()
//                    )
//                    .build()
//            }
//        )
//    }

    @Provides
    @Singleton
    fun provideInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }


    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class GoogleApiHttpClient


    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.systemDefaultZone()

}