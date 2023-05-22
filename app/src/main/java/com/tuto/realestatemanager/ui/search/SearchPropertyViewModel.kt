package com.tuto.realestatemanager.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.tuto.realestatemanager.data.repository.search.SearchRepository
import com.tuto.realestatemanager.model.SearchParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class SearchPropertyViewModel @Inject constructor(
    private val SearchRepository: SearchRepository
) : ViewModel() {

    fun getParametersLiveData(): LiveData<SearchParameters?> {
        return SearchRepository.getParametersFlow().asLiveData(Dispatchers.IO)
    }

    fun setParameters(
        type: String?,
        priceMini: String?,
        priceMaxi: String?,
        surfaceMini: String?,
        surfaceMaxi: String?,
        city: String?,
        poiTrain: Boolean,
        poiAirport: Boolean,
        poiResto: Boolean,
        poiSchool: Boolean,
        poiBus: Boolean,
        poiPark: Boolean,


        ) { //todo mettre en parametre les attributs de l'objet


//        if (typeParameter(type) != null
//            && priceMiniParameter(priceMini) != null
//            && priceMaxiParameter(priceMaxi) != null
//            && surfaceMiniParameter(surfaceMini) != null
//            && surfaceMaxiParameter(surfaceMaxi) != null
//            && cityParameter(city) != null
//
//        ) {
            SearchRepository.setParameters(
                SearchParameters(
                    typeParameter(type),
                    priceMiniParameter(priceMini)?.toInt(),
                    priceMaxiParameter(priceMaxi)?.toInt(),
                    surfaceMiniParameter(surfaceMini)?.toInt(),
                    surfaceMaxiParameter(surfaceMaxi)?.toInt(),
                    cityParameter(city),
                    poiTrain,
                    poiAirport,
                    poiResto,
                    poiSchool,
                    poiBus,
                    poiPark
                )
            )
//        }
    }

    private fun typeParameter(type: String?): String? {
        if (type == "") {
            return null
        }
        return type
    }

    private fun priceMiniParameter(priceMini: String?): String? {
        if (priceMini.toString() == "") {
            return null
        }
        return priceMini
    }

    private fun priceMaxiParameter(priceMaxi: String?): String? {
        if (priceMaxi.toString() == "") {
            return null
        }
        return priceMaxi
    }

    private fun surfaceMiniParameter(surfaceMini: String?): String? {
        if (surfaceMini.toString() == "") {
            return null
        }
        return surfaceMini
    }

    private fun surfaceMaxiParameter(surfaceMaxi: String?): String? {
        if (surfaceMaxi.toString() == "") {
            return null
        }
        return surfaceMaxi
    }

    private fun cityParameter(city: String?): String? {
        if (city == "") {
            return null
        }
        return city
    }


}