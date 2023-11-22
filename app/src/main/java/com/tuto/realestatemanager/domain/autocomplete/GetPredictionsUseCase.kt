package com.tuto.realestatemanager.domain.autocomplete

import com.tuto.realestatemanager.domain.autocomplete.model.PredictionAddressEntity
import javax.inject.Inject

class GetPredictionsUseCase @Inject constructor(
    private val autocompleteRepository: AutocompleteRepository
)
{
    suspend fun invoke(address : String, localisation : String): List<PredictionAddressEntity> =
        autocompleteRepository.getAutocompleteResult(address, localisation)
}