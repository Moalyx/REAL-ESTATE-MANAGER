package com.tuto.realestatemanager.data.repository.search

import com.tuto.realestatemanager.model.SearchParameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class SearchRepository @Inject constructor() {

    private val searchParametersMutableStateFlow: MutableStateFlow<SearchParameters?> = MutableStateFlow(
//        value = SearchParameters(
//            type = "Penthouse",
//            priceMinimum = 10,
//            priceMaximum = 10000000,
//            surfaceMinimum = null,
//            surfaceMaximum = null,
//            city = null,
//            poiTrain = true,
//            poiAirport = true,
//            poiResto = true,
//            poiSchool = true,
//            poiBus = true,
//            poiPark = true
//        )
    null
    )


    fun setParameters(searchParameters: SearchParameters?) {
       searchParametersMutableStateFlow.value = searchParameters
    }

    fun getParametersFlow(): Flow<SearchParameters?> = searchParametersMutableStateFlow

}