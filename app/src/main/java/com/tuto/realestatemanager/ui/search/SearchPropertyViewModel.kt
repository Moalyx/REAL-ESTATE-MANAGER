package com.tuto.realestatemanager.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuto.realestatemanager.domain.usecase.Search.GetParametersFlowUseCase
import com.tuto.realestatemanager.domain.usecase.Search.SetParametersUseCase
import com.tuto.realestatemanager.model.SearchParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchPropertyViewModel @Inject constructor(
    getParametersFlowUseCase: GetParametersFlowUseCase,
    private val setParametersUseCase: SetParametersUseCase
) : ViewModel() {

    private companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }

    private val _viewAction = MutableSharedFlow<SearchViewAction>(
        replay = 0,
        extraBufferCapacity = 1
    )

    val viewAction = _viewAction.asSharedFlow()

    val parametersStateFlow: StateFlow<SearchParameters?> =
        getParametersFlowUseCase.invoke()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    STOP_TIMEOUT_MILLIS
                ),
                initialValue = null
            )

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
                priceMinimum = stringParameter(priceMinimum)
                    ?.toIntOrNull(),
                priceMaximum = stringParameter(priceMaximum)
                    ?.toIntOrNull(),
                surfaceMinimum = stringParameter(surfaceMinimum)
                    ?.toIntOrNull(),
                surfaceMaximum = stringParameter(surfaceMaximum)
                    ?.toIntOrNull(),
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
        _viewAction.tryEmit(
            SearchViewAction.NavigateToMainActivity
        )
    }

    private fun stringParameter(value: String?): String? {
        return value
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
    }
}