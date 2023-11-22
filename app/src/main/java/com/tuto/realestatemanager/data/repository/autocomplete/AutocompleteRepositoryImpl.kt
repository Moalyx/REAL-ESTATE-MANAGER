package com.tuto.realestatemanager.data.repository.autocomplete

import android.app.Application
import com.tuto.realestatemanager.BuildConfig
import com.tuto.realestatemanager.data.api.GoogleApi
import com.tuto.realestatemanager.data.repository.autocomplete.model.PredictionResponse
import com.tuto.realestatemanager.domain.autocomplete.AutocompleteRepository
import com.tuto.realestatemanager.domain.autocomplete.model.PredictionAddressEntity
import javax.inject.Inject

class AutocompleteRepositoryImpl @Inject constructor(
    private val mainApplication: Application,
    private val googleApi: GoogleApi,
) : AutocompleteRepository {
    override suspend fun getAutocompleteResult(
        address: String,
        localisation: String,
    ): List<PredictionAddressEntity> {
        //return googleApi.autocompleteResult(BuildConfig.GOOGLE_AUTOCOMPLETE_KEY, localisation, "2", address) //todo changer loc a "48.849920, 2.637041" si probleme


        try{

        val response: PredictionResponse = googleApi.autocompleteResult(
            BuildConfig.GOOGLE_AUTOCOMPLETE_KEY,
            localisation,
            "2",
            address
        )

        val addresses: List<Pair<String, String>?> = response.predictions.map {
            val predictionAddress = it.description ?: ""
            val id = it.placeId ?: ""
            predictionAddress to id
        }

        return addresses.filterNotNull().map {
            PredictionAddressEntity(
                prediction = it.first,
                placeId = it.second
            )
        }
        }
//        catch (e: IOException) {
//            // Gérer l'erreur d'absence de connexion ici
//            // Afficher un toast, enregistrez les erreurs, etc.
//            //Toast.makeText(mainApplication, "mon message", Toast.LENGTH_SHORT).show();
//            return emptyList()
//        }
        catch (e: Exception) {
            // Gérer d'autres exceptions possibles ici
            // Cela peut être des erreurs spécifiques à l'API, etc.
            return emptyList()
        }

    }

}



