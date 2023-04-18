package com.tuto.realestatemanager.domain.place

import com.tuto.realestatemanager.domain.place.model.AddressComponentsEntity

interface PlaceDetailRepository {

    suspend fun getAddressById(id: String): AddressComponentsEntity?

}