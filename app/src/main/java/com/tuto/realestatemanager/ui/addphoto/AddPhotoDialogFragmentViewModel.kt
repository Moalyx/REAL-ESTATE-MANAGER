package com.tuto.realestatemanager.ui.addphoto

import androidx.lifecycle.ViewModel
import com.tuto.realestatemanager.data.repository.temporaryphoto.TemporaryPhotoRepository
import com.tuto.realestatemanager.model.TemporaryPhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddPhotoDialogFragmentViewModel @Inject constructor(
    private val temporaryPhotoRepository: TemporaryPhotoRepository
) : ViewModel() {


    fun onAddTemporaryPhoto(uri : String, title :String){
        temporaryPhotoRepository.onAddTemporaryPhoto(TemporaryPhoto(title, uri ))
    }





}