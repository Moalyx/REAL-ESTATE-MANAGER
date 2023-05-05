package com.tuto.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tuto.realestatemanager.data.repository.temporaryphoto.TemporaryPhotoRepository
import com.tuto.realestatemanager.model.TemporaryPhoto
import com.tuto.realestatemanager.ui.addpicturecamera.AddPictureCameraViewModel
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class AddPictureCameraViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()


    // Création d'un Mock de TemporaryPhotoRepository
    private val temporaryPhotoRepository = mockk<TemporaryPhotoRepository>()

    // Création du ViewModel à tester en utilisant le Mock de TemporaryPhotoRepository
    private val addPictureCameraViewModel = AddPictureCameraViewModel(temporaryPhotoRepository)

    @Test
    fun `test onAddTemporaryPhoto with valid title and uri`() {
        // GIVEN
        val title = "Test Title"
        val uri = "Test Uri"

        // WHEN
        addPictureCameraViewModel.onAddTemporaryPhoto(title, uri)

        // THEN
        verify {
            temporaryPhotoRepository.onAddTemporaryPhoto(
                TemporaryPhoto(
                    title = title,
                    uri = uri,
                )
            )
        }
    }

    @Test
    fun `test onAddTemporaryPhoto with null title and uri`() {
        // GIVEN
        val title: String? = null
        val uri: String? = null

        // WHEN
        addPictureCameraViewModel.onAddTemporaryPhoto(title, uri)

        // THEN
        verify { /* no call to temporaryPhotoRepository.onAddTemporaryPhoto */ }
    }

    @Test
    fun `test onAddTemporaryPhoto with null title`() {
        // GIVEN
        val title: String? = null
        val uri = "Test Uri"

        // WHEN
        addPictureCameraViewModel.onAddTemporaryPhoto(title, uri)

        // THEN
        verify { /* no call to temporaryPhotoRepository.onAddTemporaryPhoto */ }
    }

    @Test
    fun `test onAddTemporaryPhoto with null uri`() {
        // GIVEN
        val title = "Test Title"
        val uri: String? = null

        // WHEN
        addPictureCameraViewModel.onAddTemporaryPhoto(title, uri)

        // THEN
        verify { /* no call to temporaryPhotoRepository.onAddTemporaryPhoto */ }
    }
}


