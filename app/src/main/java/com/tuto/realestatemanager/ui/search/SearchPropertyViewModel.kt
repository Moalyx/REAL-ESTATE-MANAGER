package com.tuto.realestatemanager.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.tuto.realestatemanager.domain.usecase.Search.GetParametersFlowUseCase
import com.tuto.realestatemanager.domain.usecase.Search.SetParametersUseCase
import com.tuto.realestatemanager.model.SearchParameters
import com.tuto.realestatemanager.ui.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class SearchPropertyViewModel @Inject constructor(
    private val getParametersFlowUseCase: GetParametersFlowUseCase,
    private val setParametersUseCase: SetParametersUseCase
) : ViewModel() {

    val navigateSingleLiveEvent: SingleLiveEvent<SearchViewAction> = SingleLiveEvent()

    fun getParametersLiveData(): LiveData<SearchParameters?> {
        return getParametersFlowUseCase.invoke().asLiveData(Dispatchers.IO)
    }

    fun setParameters(
        type: String?,
        priceMinimum: String?,
        priceMaximum: String?,
        surfaceMinimum: String?,
        surfaceMaximum: String?,
        city: String?,
        poiTrain: Boolean,
        poiAirport: Boolean,
        poiResto: Boolean,
        poiSchool: Boolean,
        poiBus: Boolean,
        poiPark: Boolean,
        soldStatus: Boolean?,
        minimumPhotos: Int?
    ) {
        setParametersUseCase.invoke(
            SearchParameters(
                type = stringParameter(type),
                priceMinimum = stringParameter(priceMinimum)?.toIntOrNull(),
                priceMaximum = stringParameter(priceMaximum)?.toIntOrNull(),
                surfaceMinimum = stringParameter(surfaceMinimum)?.toIntOrNull(),
                surfaceMaximum = stringParameter(surfaceMaximum)?.toIntOrNull(),
                city = stringParameter(city),
                poiTrain = poiTrain,
                poiAirport = poiAirport,
                poiResto = poiResto,
                poiSchool = poiSchool,
                poiBus = poiBus,
                poiPark = poiPark,
                soldStatus = soldStatus,
                minimumPhotos = minimumPhotos
            )
        )
    }

    fun clearParameters() {
        setParametersUseCase.invoke(SearchParameters())
    }

    fun onNavigateToMainActivity() {
        navigateSingleLiveEvent.setValue(SearchViewAction.NavigateToMainActivity)
    }

    private fun stringParameter(value: String?): String? {
        return value?.trim()?.takeIf { it.isNotEmpty() }
    }
}