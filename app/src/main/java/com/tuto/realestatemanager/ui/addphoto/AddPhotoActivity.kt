package com.tuto.realestatemanager.ui.addphoto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.tuto.realestatemanager.BuildConfig
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.ActivityAddPhotoBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import androidx.core.net.toUri

@AndroidEntryPoint
class AddPhotoActivity : AppCompatActivity() {

    companion object {
        private const val KEY_PHOTO_URI = "KEY_PHOTO_URI"
    }

    private val viewModel by viewModels<AddPhotoDialogFragmentViewModel>()

    private lateinit var binding: ActivityAddPhotoBinding

    private var permanentPhotoUri: Uri? = null

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val originalUri = result.data?.data

                if (originalUri != null) {
                    val copiedUri = copyUriToInternalStorage(originalUri)

                    if (copiedUri != null) {
                        permanentPhotoUri = copiedUri

                        Glide.with(this)
                            .load(permanentPhotoUri)
                            .into(binding.image)
                    } else {
                        Toast.makeText(
                            this,
                            getString(R.string.unable_to_load_photo),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                } else {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.photo_missing),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            } else {
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()

        val savedPhotoUri = savedInstanceState?.getString(KEY_PHOTO_URI)

        permanentPhotoUri = if (savedPhotoUri.isNullOrBlank()) {
            null
        } else {
            savedPhotoUri.toUri()
        }

        if (permanentPhotoUri != null) {
            Glide.with(this)
                .load(permanentPhotoUri)
                .into(binding.image)
        } else {
            launchIntent()
        }
    }

    private fun setupListeners() {
        binding.addPictureButton.setOnClickListener {
            val title = binding.title.text.toString().trim()
            val uri = permanentPhotoUri?.toString()

            when {
                title.isBlank() -> {
                    Toast.makeText(
                        this,
                        getString(R.string.please_enter_a_description),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                uri.isNullOrBlank() -> {
                    Toast.makeText(
                        this,
                        getString(R.string.please_choose_a_photo),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    viewModel.onAddTemporaryPhoto(
                        title = title,
                        uri = uri
                    )
                    finish()
                }
            }
        }

        binding.cancelAction.setOnClickListener {
            finish()
        }
    }

    private fun copyUriToInternalStorage(uri: Uri): Uri? {
        val inputStream = contentResolver.openInputStream(uri) ?: return null

        val fileName = "IMG_${System.currentTimeMillis()}.jpg"

        val file = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            fileName
        )

        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return FileProvider.getUriForFile(
            this,
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            file
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        permanentPhotoUri?.let { uri ->
            outState.putString(KEY_PHOTO_URI, uri.toString())
        }
    }

    private fun launchIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }

        galleryLauncher.launch(Intent.createChooser(intent, getString(R.string.choose_a_photo)))
    }
}