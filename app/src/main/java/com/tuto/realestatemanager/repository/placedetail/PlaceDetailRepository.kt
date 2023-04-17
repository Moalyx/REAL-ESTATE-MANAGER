package com.tuto.realestatemanager.repository.placedetail

import com.tuto.realestemanager.repository.placedetail.model.PlaceDetailResponse

interface PlaceDetailRepository {

    suspend fun getAdressById(id : String) : PlaceDetailResponse

}