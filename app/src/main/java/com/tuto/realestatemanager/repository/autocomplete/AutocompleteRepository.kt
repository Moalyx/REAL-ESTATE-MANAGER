package com.tuto.realestatemanager.repository.autocomplete

import com.tuto.realestatemanager.repository.autocomplete.model.PredictionResponse

interface AutocompleteRepository {

    suspend fun getAutocompleteResult(address : String) : PredictionResponse
}