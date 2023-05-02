package com.tuto.realestatemanager.ui.addpicturecamera

import androidx.lifecycle.ViewModel
import com.tuto.realestatemanager.data.repository.temporaryphoto.TemporaryPhotoRepository
import com.tuto.realestatemanager.model.TemporaryPhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddPictureCameraViewModel @Inject constructor(
    private val temporaryPhotoRepository: TemporaryPhotoRepository
) : ViewModel() {

    fun onAddTemporaryPhoto(title: String, uri :String){
        temporaryPhotoRepository.onAddTemporaryPhoto(TemporaryPhoto(title, uri ))
    }


}