package com.tuto.realestatemanager.domain.usecase.Search

import com.tuto.realestatemanager.data.repository.search.SearchRepositoryInterface
import com.tuto.realestatemanager.model.SearchParameters
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetParametersUseCase @Inject constructor(
    private val searchRepositoryInterface: SearchRepositoryInterface
) {
    fun invoke(searchParameters: SearchParameters) = searchRepositoryInterface.setParameters(searchParameters)
}