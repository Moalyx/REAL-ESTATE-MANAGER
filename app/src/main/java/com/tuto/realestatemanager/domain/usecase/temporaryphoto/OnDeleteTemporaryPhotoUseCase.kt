package com.tuto.realestatemanager.domain.usecase.temporaryphoto

import com.tuto.realestatemanager.data.repository.temporaryphoto.TemporaryPhotoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnDeleteTemporaryPhotoUseCase @Inject constructor(
    private val temporaryPhotoRepository: TemporaryPhotoRepository
) {
    fun invoke() = temporaryPhotoRepository.onDeleteTemporaryPhotoRepo()
}