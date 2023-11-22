package com.tuto.realestatemanager.domain.usecase.location

import com.tuto.realestatemanager.data.repository.location.LocationRepositoryInterface
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUserLocationFlowUseCase @Inject constructor(
    private val locationRepositoryInterface: LocationRepositoryInterface
) {
    fun invoke() = locationRepositoryInterface.getUserLocation()
}