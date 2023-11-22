package com.tuto.realestatemanager.domain.usecase.Search

import com.tuto.realestatemanager.data.repository.search.SearchRepositoryInterface
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetParametersFlowUseCase @Inject constructor(
    private val searchRepositoryInterface: SearchRepositoryInterface
) {
    fun invoke() = searchRepositoryInterface.getParametersFlow()
}