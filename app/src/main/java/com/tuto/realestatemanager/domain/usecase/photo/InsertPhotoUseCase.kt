package com.tuto.realestatemanager.domain.usecase.photo

import com.tuto.realestatemanager.data.repository.photo.PhotoRepository
import com.tuto.realestatemanager.data.repository.temporaryphoto.TemporaryPhotoRepository
import com.tuto.realestatemanager.model.PhotoEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsertPhotoUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    suspend fun invoke(photoEntity: PhotoEntity) = photoRepository.insertPhoto(photoEntity)
}