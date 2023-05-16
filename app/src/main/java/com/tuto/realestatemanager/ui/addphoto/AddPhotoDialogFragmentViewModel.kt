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





}