package com.tuto.realestatemanager.ui.addpicturecamera

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.tuto.realestatemanager.BuildConfig
import com.tuto.realestatemanager.databinding.ActivityAddPictureCameraBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@Suppress("DEPRECATION")
@AndroidEntryPoint
class AddPictureCameraActivity : AppCompatActivity() {

    companion object {
        private const val KEY_CURRENT_PHOTO_URI = "KEY_CURRENT_PHOTO_URI"
        private const val REQUEST_IMAGE_CAPTURE = 100
    }

    private val viewModel by viewModels<AddPictureCameraViewModel>()

    private lateinit var binding: ActivityAddPictureCameraBinding

    private var currentPhotoUri: Uri? = null

    private var fromEditPropertyActivity: String? = null
    private var getEditPropertyId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddPictureCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        restoreCurrentPhotoUri(savedInstanceState)

        fromEditPropertyActivity = intent.getStringExtra("XXX")
        getEditPropertyId = intent.getLongExtra("edit_property", -1)

        setupListeners()

        if (savedInstanceState == null) {
            launchIntentCamera()
        } else {
            displayPhoto()
        }
    }

    private fun setupListeners() {
        binding.addPictureButton.setOnClickListener {
            val title = binding.title.text.toString().trim()
            val uri = currentPhotoUri?.toString()

            when {
                title.isBlank() -> {
                    Toast.makeText(this, "please enter a description", Toast.LENGTH_SHORT).show()
                }

                uri.isNullOrBlank() -> {
                    Toast.makeText(this, "photo missing", Toast.LENGTH_SHORT).show()
                }

                fromEditPropertyActivity == "XXX" -> {
                    viewModel.onAddTemporaryPhoto(
                        title = title,
                        uri = uri
                    )
                    finish()
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

        binding.cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun launchIntentCamera() {
        currentPhotoUri = createImageUri()

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    private fun createImageUri(): Uri {
        val imageFile = File.createTempFile(
            "JPEG_",
            ".jpg",
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )

        return FileProvider.getUriForFile(
            this,
            "${BuildConfig.APPLICATION_ID}.provider",
            imageFile
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != REQUEST_IMAGE_CAPTURE) return

        if (resultCode == RESULT_OK) {
            displayPhoto()
        } else {
            finish()
        }
    }

    private fun displayPhoto() {
        currentPhotoUri?.let { uri ->
            Glide.with(this)
                .load(uri)
                .into(binding.mainImageViewPhoto)
        }
    }

    private fun restoreCurrentPhotoUri(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) return

        currentPhotoUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            savedInstanceState.getParcelable(KEY_CURRENT_PHOTO_URI, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            savedInstanceState.getParcelable(KEY_CURRENT_PHOTO_URI)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_CURRENT_PHOTO_URI, currentPhotoUri)
    }
}