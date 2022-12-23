package com.tuto.realestatemanager.data

import com.tuto.realestatemanager.repository.photo.PhotoRepository
import com.tuto.realestatemanager.repository.photo.PhotoRepositoryImpl
import com.tuto.realestatemanager.repository.property.PropertyRepository
import com.tuto.realestatemanager.repository.property.PropertyRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindingModule {

    @Binds
    @Singleton
    abstract fun bindsPropertyRepository(impl: PropertyRepositoryImpl) : PropertyRepository

    @Binds
    @Singleton
    abstract fun bindsPhotoRepository(impl: PhotoRepositoryImpl) : PhotoRepository
}