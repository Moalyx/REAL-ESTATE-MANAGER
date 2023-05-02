package com.tuto.realestatemanager.data.repository.temporaryphoto

import com.tuto.realestatemanager.model.TemporaryPhoto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class TemporaryPhotoRepositoryImpl @Inject constructor() : TemporaryPhotoRepository {

    private val temporaryPhotoListFlow = MutableStateFlow<List<TemporaryPhoto>>(emptyList())

    override fun onAddTemporaryPhoto(temporaryPhoto: TemporaryPhoto) {
        temporaryPhotoListFlow.update {
            it + temporaryPhoto
        }
    }

    override fun getTemporaryPhotoList(): StateFlow<List<TemporaryPhoto>> = temporaryPhotoListFlow
}