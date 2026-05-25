package com.tuto.realestatemanager.domain.usecase.photo

import com.tuto.realestatemanager.data.repository.photo.PhotoRepository
import javax.inject.Inject

class DeletePhotoByIdUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    suspend fun invoke(id : Long) = photoRepository.deletePhotoById(id)
}