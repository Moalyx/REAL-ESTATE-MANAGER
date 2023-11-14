package com.tuto.realestatemanager.data

import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdIdRepositoryImpl
import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import com.tuto.realestatemanager.domain.autocomplete.AutocompleteRepository
import com.tuto.realestatemanager.data.repository.autocomplete.AutocompleteRepositoryImpl
import com.tuto.realestatemanager.data.repository.connectivity.ConnectivityRepository
import com.tuto.realestatemanager.data.repository.connectivity.ConnectivityRepositoryImpl
import com.tuto.realestatemanager.data.repository.geocoding.GeocodingRepository
import com.tuto.realestatemanager.data.repository.geocoding.GeocodingRepositoryImpl
import com.tuto.realestatemanager.domain.place.PlaceDetailRepository
import com.tuto.realestatemanager.data.repository.placedetail.PlaceDetailRepositoryImpl
import com.tuto.realestatemanager.data.repository.photo.PhotoRepository
import com.tuto.realestatemanager.data.repository.photo.PhotoRepositoryImpl
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.data.repository.property.PropertyRepositoryImpl
import com.tuto.realestatemanager.data.repository.temporaryphoto.TemporaryPhotoRepository
import com.tuto.realestatemanager.data.repository.temporaryphoto.TemporaryPhotoRepositoryImpl
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
    abstract fun bindsPlaceDetailRepository(impl: PlaceDetailRepositoryImpl) : PlaceDetailRepository

    @Binds
    @Singleton
    abstract fun bindsAutocompleteRepository(impl : AutocompleteRepositoryImpl) : AutocompleteRepository

    @Binds
    @Singleton
    abstract fun bindsTemporaryPhotoRepository(impl : TemporaryPhotoRepositoryImpl) : TemporaryPhotoRepository

    @Binds
    @Singleton
    abstract fun bindsGeocodingRepository(impl : GeocodingRepositoryImpl) : GeocodingRepository

    @Binds
    @Singleton
    abstract fun bindsConnectivityRepository(impl : ConnectivityRepositoryImpl) : ConnectivityRepository

}