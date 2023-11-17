package com.tuto.realestatemanager.domain.usecase.photo

import com.tuto.realestatemanager.data.repository.photo.PhotoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteAllPhotosByPropertyIdUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    suspend fun invoke(id : Long) = photoRepository.deleteAllPropertyPhotos(id)
}