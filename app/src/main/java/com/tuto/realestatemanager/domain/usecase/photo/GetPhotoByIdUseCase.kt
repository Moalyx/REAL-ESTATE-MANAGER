package com.tuto.realestatemanager.domain.usecase.photo

import com.tuto.realestatemanager.data.repository.photo.PhotoRepository
import com.tuto.realestatemanager.model.PhotoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPhotoByIdUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    fun invoke(id : Long) : Flow<PhotoEntity> = photoRepository.getPhotoById(id)
}