package com.tuto.realestatemanager.data.repository.autocomplete

import com.tuto.realestatemanager.BuildConfig
import com.tuto.realestatemanager.data.api.GoogleApi
import com.tuto.realestatemanager.data.repository.autocomplete.model.PredictionResponse
import javax.inject.Inject

class AutocompleteRepositoryImpl @Inject constructor(
    private val googleApi: GoogleApi
) : AutocompleteRepository {

    override suspend fun getAutocompleteResult(address: String): PredictionResponse {
            return googleApi.autocompleteResult(BuildConfig.GOOGLE_AUTOCOMPLETE_KEY, "48.849920, 2.637041", "50000", address)
    }

}



