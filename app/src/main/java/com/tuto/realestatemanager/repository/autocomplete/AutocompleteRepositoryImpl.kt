package com.tuto.realestatemanager.repository.autocomplete

import com.tuto.realestatemanager.BuildConfig
import com.tuto.realestatemanager.api.GoogleApi
import com.tuto.realestatemanager.repository.autocomplete.AutocompleteRepository
import com.tuto.realestatemanager.repository.autocomplete.model.Predictions
import com.tuto.realestatemanager.ui.utils.RetrofitService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

class AutocompleteRepositoryImpl @Inject constructor(
    private val googleApi: GoogleApi
) : AutocompleteRepository {

    private val autocompleteResultMutableStateFlow = MutableStateFlow<String>("")


    override suspend fun getAutocompleteResult(address: String): Flow<String> {

        val response = googleApi.autocompleteResult(BuildConfig.GOOGLE_PLACES_KEY, address)
        if (response.isSuccessful){
            autocompleteResultMutableStateFlow.tryEmit(response.body().toString())
        }
        else{
            println("autocomplete API call error")
        }


            return autocompleteResultMutableStateFlow
    }
}



