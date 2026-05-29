package com.tuto.realestatemanager.domain.usecase.photo

import com.tuto.realestatemanager.data.repository.temporaryphoto.TemporaryPhotoRepository
import com.tuto.realestatemanager.model.TemporaryPhoto
import javax.inject.Inject

class DeleteTemporaryPhotoUseCase @Inject constructor(
    private val temporaryPhotoRepository: TemporaryPhotoRepository
) {
    operator fun invoke(temporaryPhoto: TemporaryPhoto) {
        temporaryPhotoRepository.deleteTemporaryPhoto(temporaryPhoto)
    }
}