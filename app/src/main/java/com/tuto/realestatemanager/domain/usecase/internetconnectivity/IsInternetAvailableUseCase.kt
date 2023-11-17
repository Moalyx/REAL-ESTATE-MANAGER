package com.tuto.realestatemanager.domain.usecase.internetconnectivity

import com.tuto.realestatemanager.data.repository.connectivity.ConnectivityRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IsInternetAvailableUseCase @Inject constructor(
    private val connectivityRepository: ConnectivityRepository
) {
    fun invoke() = connectivityRepository.isInternetAvailable()
}