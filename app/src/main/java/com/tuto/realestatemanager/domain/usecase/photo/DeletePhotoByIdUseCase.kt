package com.tuto.realestatemanager.domain.usecase.photo

import com.tuto.realestatemanager.data.repository.photo.PhotoRepository
import com.tuto.realestatemanager.model.PhotoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeletePhotoByIdUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    suspend fun invoke(id : Long) = photoRepository.deletePhotoById(id)
}