package com.tuto.realestatemanager.domain.usecase.temporaryphoto

import com.tuto.realestatemanager.data.repository.temporaryphoto.TemporaryPhotoRepository
import com.tuto.realestatemanager.model.TemporaryPhoto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnAddTemporaryPhotoUseCase @Inject constructor(
    private val temporaryPhotoRepository: TemporaryPhotoRepository
) {
    fun invoke(temporaryPhoto: TemporaryPhoto) = temporaryPhotoRepository.onAddTemporaryPhoto(temporaryPhoto)
}