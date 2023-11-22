package com.tuto.realestatemanager.domain.usecase.currentproperty

import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetCurrentPropertyIdFlowUseCase @Inject constructor(
    private val currentPropertyIdRepository: CurrentPropertyIdRepository
) {
    fun invoke(currentId: Long) = currentPropertyIdRepository.setCurrentId(currentId)
}