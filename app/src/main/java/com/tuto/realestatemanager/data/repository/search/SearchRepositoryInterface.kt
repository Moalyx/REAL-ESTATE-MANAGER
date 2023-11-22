package com.tuto.realestatemanager.data.repository.search

import com.tuto.realestatemanager.model.SearchParameters
import kotlinx.coroutines.flow.Flow

interface SearchRepositoryInterface {

    fun setParameters(searchParameters: SearchParameters?)

    fun getParametersFlow(): Flow<SearchParameters?>

}