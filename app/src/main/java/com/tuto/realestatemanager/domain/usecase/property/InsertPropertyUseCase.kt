package com.tuto.realestatemanager.domain.usecase.property

import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.model.PropertyEntity
import javax.inject.Inject

class InsertPropertyUseCase @Inject constructor(
    private val propertyRepository: PropertyRepository
){
    suspend fun invoke(propertyEntity: PropertyEntity) : Long = propertyRepository.insertProperty(propertyEntity)
}