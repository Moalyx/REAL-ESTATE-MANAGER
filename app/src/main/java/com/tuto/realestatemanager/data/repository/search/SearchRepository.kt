package com.tuto.realestatemanager.data.repository.search

import com.tuto.realestatemanager.model.SearchParameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class SearchRepository @Inject constructor() {

    private val searchParametersMutableStateFlow = MutableStateFlow<SearchParameters?>(null)


    fun setParameters(searchParameters: SearchParameters?) {
        searchParametersMutableStateFlow.value = searchParameters
    }

    fun getParametersFlow(): Flow<SearchParameters?> = searchParametersMutableStateFlow

}