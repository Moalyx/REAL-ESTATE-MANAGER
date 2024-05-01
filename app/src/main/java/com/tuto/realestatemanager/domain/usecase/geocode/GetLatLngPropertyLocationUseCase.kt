package com.tuto.realestatemanager.domain.usecase.geocode

import com.tuto.realestatemanager.data.repository.geocoding.GeocodingRepository
import com.tuto.realestatemanager.data.repository.geocoding.model.GeocodingResponse
import com.tuto.realestatemanager.domain.usecase.geocode.model.LocationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLatLngPropertyLocationUseCase @Inject constructor(
    private val geocodingRepository: GeocodingRepository
){
    suspend fun invoke(address : String) : LocationEntity = geocodingRepository.getLatLngLocation(address)
}