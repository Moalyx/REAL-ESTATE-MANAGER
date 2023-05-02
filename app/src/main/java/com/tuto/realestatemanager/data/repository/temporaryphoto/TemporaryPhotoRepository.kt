package com.tuto.realestatemanager.data.repository.temporaryphoto

import com.tuto.realestatemanager.model.TemporaryPhoto
import kotlinx.coroutines.flow.StateFlow

interface TemporaryPhotoRepository {

    fun getTemporaryPhotoList(): StateFlow<List<TemporaryPhoto>>

    fun onAddTemporaryPhoto(temporaryPhoto: TemporaryPhoto)

}