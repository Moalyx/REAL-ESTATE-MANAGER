package com.tuto.realestatemanager.data

import com.tuto.realestatemanager.current_property.CurrentPropertyIdIdRepositoryImpl
import com.tuto.realestatemanager.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.repository.autocomplete.AutocompleteRepository
import com.tuto.realestatemanager.repository.autocomplete.AutocompleteRepositoryImpl
import com.tuto.realestatemanager.repository.geocode.GeocodeRepository
import com.tuto.realestatemanager.repository.geocode.GeocodeRepositoryImpl
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

    @Binds
    @Singleton
    abstract fun bindsCurrentIdRepository(impl: CurrentPropertyIdIdRepositoryImpl) : CurrentPropertyIdRepository

    @Binds
    @Singleton
    abstract fun bindsGeocodeRepository(impl: GeocodeRepositoryImpl) : GeocodeRepository

    @Binds
    @Singleton
    abstract fun bindsAutocompleteRepository(impl : AutocompleteRepositoryImpl) : AutocompleteRepository

}