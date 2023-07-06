package com.tuto.realestatemanager.ui.addpicturecamera

import android.app.Activity
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
    }

    private val viewModel by viewModels<AddPictureCameraViewModel>()

    private var fromEditPropertyActivity: String? = "edit_property"
    private var getEditPropertyId = 0L

    private lateinit var binding: ActivityAddPictureCameraBinding

    private var currentPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddPictureCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            currentPhotoUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                savedInstanceState.getParcelable(KEY_CURRENT_PHOTO_URI, Uri::class.java)
            } else {
                @Suppress("DEPRECATION") // I KNOW
                savedInstanceState.getParcelable(KEY_CURRENT_PHOTO_URI)
            }
        }

        fromEditPropertyActivity = intent.getStringExtra("XXX")
        getEditPropertyId = intent.getLongExtra("edit_property", -1)

        launchIntentCamera()


    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION") // ActivityResultContracts are equally bad since they can't be registered after onCreate...
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            Glide.with(binding.mainImageViewPhoto)
                .load(currentPhotoUri)
                .into(binding.mainImageViewPhoto)
        }

        binding.addPictureButton.setOnClickListener {

            if (binding.title.text.toString() == "") {
                Toast.makeText(this, "please enter a description", Toast.LENGTH_SHORT).show()
            } else {

                if (fromEditPropertyActivity == "XXX") {
                    viewModel.insertPhoto(
                        0,
                        getEditPropertyId,
                        binding.title.text.toString(),
                        currentPhotoUri.toString()

                    )
                } else {
                    viewModel.onAddTemporaryPhoto(
                        title = binding.title.text?.toString(),
                        uri = currentPhotoUri?.toString()
                    )
                }
                finish()
            }
        }

        binding.cancelButton.setOnClickListener {
            finish()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(KEY_CURRENT_PHOTO_URI, currentPhotoUri)
    }

    private fun launchIntentCamera() {
        currentPhotoUri = FileProvider.getUriForFile(
            this,
            BuildConfig.APPLICATION_ID + ".provider",
            File.createTempFile(
                "JPEG_",
                ".jpg",
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            .putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)

        startActivityForResult(intent, 0)
    }
}
