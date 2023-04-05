package com.tuto.realestatemanager.repository.autocomplete

import com.tuto.realestatemanager.api.GoogleApi
import com.tuto.realestatemanager.repository.autocomplete.model.Predictions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Response

interface AutocompleteRepository {

    suspend fun getAutocompleteResult(address : String) : Flow<String>
}