package com.tuto.realestatemanager.domain.usecase.property

import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.model.PropertyEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdatePropertyUseCase @Inject constructor(
    private val propertyRepository: PropertyRepository
){
    suspend fun invoke(propertyEntity: PropertyEntity) = propertyRepository.updateProperty(propertyEntity)
}