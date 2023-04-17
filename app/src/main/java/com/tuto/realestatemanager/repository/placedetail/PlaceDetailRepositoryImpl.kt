package com.tuto.realestatemanager.repository.placedetail

import com.tuto.realestatemanager.BuildConfig
import com.tuto.realestatemanager.api.GoogleApi
import com.tuto.realestemanager.repository.placedetail.model.PlaceDetailResponse
import javax.inject.Inject

class PlaceDetailRepositoryImpl @Inject constructor(
    private val googleApi: GoogleApi
) : PlaceDetailRepository {


    override suspend fun getAdressById(id: String): PlaceDetailResponse {

        return googleApi.getPlaceDetailResponse(BuildConfig.GOOGLE_AUTOCOMPLETE_KEY, id )
    }




}