package com.tuto.realestatemanager.data.repository.temporaryphoto

import com.tuto.realestatemanager.model.TemporaryPhoto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class TemporaryPhotoRepositoryImpl @Inject constructor(

) : TemporaryPhotoRepository{

    private val temporaryPhotoListFlow = MutableStateFlow<List<TemporaryPhoto>>(emptyList())

//    fun onAddTemporaryPhoto(uriPhoto : String, titlePhoto : String){
//        val temporaryPhoto = TemporaryPhoto(null, null)
//        temporaryPhotoListFlow.update {
//            it + temporaryPhoto(uriPhoto, titlePhoto)
//        }
//    }

    override fun onAddTemporaryPhoto(temporaryPhoto: TemporaryPhoto){
        temporaryPhotoListFlow.update {
            it + temporaryPhoto
        }
    }

    override fun getTemporaryPhotoList(): Flow<List<TemporaryPhoto>> {
        return temporaryPhotoListFlow
    }

}