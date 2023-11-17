package com.tuto.realestatemanager.domain.usecase.temporaryphoto

import com.tuto.realestatemanager.data.repository.temporaryphoto.TemporaryPhotoRepository
import com.tuto.realestatemanager.model.TemporaryPhoto
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetTemporaryPhotoListUseCase @Inject constructor(
    private val temporaryPhotoRepository: TemporaryPhotoRepository
) {
    fun invoke() : StateFlow<List<TemporaryPhoto>> = temporaryPhotoRepository.getTemporaryPhotoList()
}