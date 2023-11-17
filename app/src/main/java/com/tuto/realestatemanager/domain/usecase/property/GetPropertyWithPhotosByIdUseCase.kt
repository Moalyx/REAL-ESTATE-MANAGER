package com.tuto.realestatemanager.domain.usecase.property

import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.model.PropertyWithPhotosEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPropertyWithPhotosByIdUseCase @Inject constructor(
    private val propertyRepository: PropertyRepository,
) {
    fun invoke(id: Long): Flow<PropertyWithPhotosEntity> = propertyRepository.getPropertyWithPhotoById(id)
}