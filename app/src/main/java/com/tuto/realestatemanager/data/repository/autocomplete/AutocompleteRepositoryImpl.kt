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

    private companion object{
        private const val RADIUS = "5000"
    }
    override suspend fun getAutocompleteResult(
        address: String,
        localisation: String,
    ): List<PredictionAddressEntity> {


        try {

            val response: PredictionResponse = googleApi.autocompleteResult(
                BuildConfig.GOOGLE_AUTOCOMPLETE_KEY,
                localisation,
                RADIUS,
                address,
                true
            )

            val addresses: List<Pair<String, String>> = response.predictions.map {
                val predictionAddress = it.description ?: ""
                val id = it.placeId ?: ""
                predictionAddress to id
            }

            return addresses.map {
                PredictionAddressEntity(
                    prediction = it.first,
                    placeId = it.second
                )
            }
        } catch (_: Exception) {
            return emptyList()
        }

    }

}



