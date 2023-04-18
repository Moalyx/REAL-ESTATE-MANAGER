package com.tuto.realestatemanager.domain.place

import com.tuto.realestatemanager.domain.place.model.AddressComponentsEntity
import javax.inject.Inject

class GetPlaceAddressComponentsUseCase @Inject constructor(
    private val placeDetailRepository: PlaceDetailRepository,
) {
    suspend fun invoke(id: String): AddressComponentsEntity? = placeDetailRepository.getAddressById(id)
}