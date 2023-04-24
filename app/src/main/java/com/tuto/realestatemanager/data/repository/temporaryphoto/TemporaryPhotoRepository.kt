package com.tuto.realestatemanager.data.repository.temporaryphoto

import com.tuto.realestatemanager.model.TemporaryPhoto
import kotlinx.coroutines.flow.Flow

interface TemporaryPhotoRepository {

    fun getTemporaryPhotoList() : Flow<List<TemporaryPhoto>>

    fun onAddTemporaryPhoto(temporaryPhoto: TemporaryPhoto)

}