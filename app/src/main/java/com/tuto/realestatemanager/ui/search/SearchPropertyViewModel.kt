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

    fun setParameters(searchParameters: SearchParameters?) {
        SearchRepository.setParameters(searchParameters)
    }

}