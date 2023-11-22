package com.tuto.realestatemanager.domain.usecase.currentproperty

import com.tuto.realestatemanager.data.current_property.CurrentPropertyIdRepository
import javax.inject.Inject

class CurrentIdFlowUseCase @Inject constructor(
    private val currentPropertyIdRepository: CurrentPropertyIdRepository
){
    fun invoke() = currentPropertyIdRepository.currentIdFlow
}