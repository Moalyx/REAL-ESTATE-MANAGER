package com.tuto.realestatemanager.data.repository.autocomplete

import com.tuto.realestatemanager.BuildConfig
import com.tuto.realestatemanager.data.api.GoogleApi
import com.tuto.realestatemanager.data.repository.autocomplete.model.PredictionResponse
import com.tuto.realestatemanager.domain.autocomplete.AutocompleteRepository
import com.tuto.realestatemanager.domain.autocomplete.model.PredictionAddressEntity
import javax.inject.Inject

class AutocompleteRepositoryImpl @Inject constructor(
    private val googleApi: GoogleApi,
) : AutocompleteRepository {
    override suspend fun getAutocompleteResult(
        address: String,
        localisation: String,
    ): List<PredictionAddressEntity> {
        //return googleApi.autocompleteResult(BuildConfig.GOOGLE_AUTOCOMPLETE_KEY, localisation, "2", address) //todo changer loc a "48.849920, 2.637041" si probleme

        val response: PredictionResponse = googleApi.autocompleteResult(
            BuildConfig.GOOGLE_AUTOCOMPLETE_KEY,
            localisation,
            "2",
            address
        )

        val addresses: List<Pair<String, String>?> = response.predictions.map {
            val predictionAdress = it.description ?: ""
            val id = it.placeId ?: ""
            predictionAdress to id
        }

        return addresses.filterNotNull().map {
            PredictionAddressEntity(
                prediction = it.first,
                placeId = it.second
            )
        }

    }

}



