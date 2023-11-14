package com.tuto.realestatemanager.domain.autocomplete

import com.tuto.realestatemanager.data.repository.autocomplete.model.PredictionResponse
import com.tuto.realestatemanager.domain.autocomplete.model.PredictionAddressEntity

interface AutocompleteRepository {

    suspend fun getAutocompleteResult(address : String, localisation : String) : List<PredictionAddressEntity>
}