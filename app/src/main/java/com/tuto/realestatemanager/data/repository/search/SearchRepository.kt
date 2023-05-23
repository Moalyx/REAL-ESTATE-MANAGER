package com.tuto.realestatemanager.data.repository.search

import com.tuto.realestatemanager.model.SearchParameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor() {

    private val searchParametersMutableStateFlow: MutableStateFlow<SearchParameters?> =
        MutableStateFlow(
//            value = SearchParameters(
//                type = null,
//                priceMinimum = null,
//                priceMaximum = null,
//                surfaceMinimum = null,
//                surfaceMaximum = null,
//                city = null,
//                poiTrain = false,
//                poiAirport = false,
//                poiResto = false,
//                poiSchool = true,
//                poiBus = false,
//                poiPark = true
//            )
    null
        )


    fun setParameters(searchParameters: SearchParameters?) {
        searchParametersMutableStateFlow.value = searchParameters
    }

    fun getParametersFlow(): Flow<SearchParameters?> = searchParametersMutableStateFlow

}