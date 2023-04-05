package com.tuto.realestatemanager.repository.geocode

import com.tuto.realestatemanager.BuildConfig
import com.tuto.realestatemanager.api.GoogleApi
import com.tuto.realestatemanager.repository.autocomplete.model.Predictions
import com.tuto.realestatemanager.repository.geocode.model.GeocodeResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Response
import javax.inject.Inject

class GeocodeRepositoryImpl @Inject constructor(
    private val googleApi: GoogleApi
) : GeocodeRepository {


    private val geocodeResultMutableStateFlow = MutableStateFlow<String>("")


    override suspend fun getLocationByAddress(address: String): Flow<String> {

        val response = googleApi.autocompleteResult(BuildConfig.GOOGLE_PLACES_KEY, address)
        if (response.isSuccessful){
            geocodeResultMutableStateFlow.tryEmit(response.body().toString())
        }
        else{
            println("geocode API call error")
        }


        return geocodeResultMutableStateFlow
    }




}