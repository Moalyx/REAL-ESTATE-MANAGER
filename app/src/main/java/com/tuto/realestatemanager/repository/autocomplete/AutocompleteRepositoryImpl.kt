package com.tuto.realestatemanager.repository.autocomplete

import com.tuto.realestatemanager.BuildConfig
import com.tuto.realestatemanager.api.GoogleApi
import com.tuto.realestatemanager.repository.autocomplete.model.PredictionResponse
import javax.inject.Inject

class AutocompleteRepositoryImpl @Inject constructor(
    private val googleApi: GoogleApi
) : AutocompleteRepository {


    override suspend fun getAutocompleteResult(address: String): PredictionResponse {

            return googleApi.autocompleteResult(BuildConfig.GOOGLE_AUTOCOMPLETE_KEY, address)
    }
}



