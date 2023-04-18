package com.tuto.realestatemanager.data.repository.autocomplete

import com.tuto.realestatemanager.data.repository.autocomplete.model.PredictionResponse

interface AutocompleteRepository {

    suspend fun getAutocompleteResult(address : String) : PredictionResponse
}