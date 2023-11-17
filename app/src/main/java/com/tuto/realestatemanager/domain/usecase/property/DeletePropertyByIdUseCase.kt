package com.tuto.realestatemanager.domain.usecase.property

import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeletePropertyByIdUseCase @Inject constructor(
    private val propertyRepository: PropertyRepository
) {
    suspend fun invoke(id : Long) = propertyRepository.deletePropertyById(id)
}