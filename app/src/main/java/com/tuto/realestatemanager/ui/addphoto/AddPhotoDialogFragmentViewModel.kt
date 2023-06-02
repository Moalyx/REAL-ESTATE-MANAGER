package com.tuto.realestatemanager.ui.addphoto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuto.realestatemanager.data.repository.photo.PhotoRepository
import com.tuto.realestatemanager.data.repository.temporaryphoto.TemporaryPhotoRepository
import com.tuto.realestatemanager.model.PhotoEntity
import com.tuto.realestatemanager.model.TemporaryPhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPhotoDialogFragmentViewModel @Inject constructor(
    private val temporaryPhotoRepository: TemporaryPhotoRepository,
    private val photoRepository: PhotoRepository
) : ViewModel() {


    fun onAddTemporaryPhoto(title: String?, uri: String?) {
        if (title != null && uri != null) {
            temporaryPhotoRepository.onAddTemporaryPhoto(
                TemporaryPhoto(
                    title = title,
                    uri = uri,
                )
            )
        }
    }

    fun insertPhoto(id: Long, propertyId: Long, title: String, uri: String){
        viewModelScope.launch {
            photoRepository.insertPhoto(
                PhotoEntity(
                    id = id,
                    propertyId = propertyId,
                    photoUri = uri,
                    photoTitle = title
                )
            )

        }

    }





}