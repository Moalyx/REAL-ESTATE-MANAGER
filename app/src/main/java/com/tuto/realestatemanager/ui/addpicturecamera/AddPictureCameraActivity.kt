package com.tuto.realestatemanager.ui.addpicturecamera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.core.widget.doAfterTextChanged
import com.bumptech.glide.Glide
import com.tuto.realestatemanager.BuildConfig
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.ActivityAddPhotoBinding
import com.tuto.realestatemanager.databinding.ActivityAddPictureCameraBinding
import com.tuto.realestatemanager.ui.addphoto.AddPhotoDialogFragmentViewModel
import com.tuto.realestatemanager.ui.utils.RealPathUtil
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AddPictureCameraActivity : AppCompatActivity() {

    companion object {
        private const val KEY_CURRENT_PHOTO_URI = "KEY_CURRENT_PHOTO_URI"
    }

    private val viewModel by viewModels<AddPictureCameraViewModel>()

    private var title: String = ""

    lateinit var binding: ActivityAddPictureCameraBinding

    private var currentPhotoUri: Uri? = null

    private val takePictureCallback =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { successful ->
            if (successful) {
                Glide.with(binding.mainImageViewPhoto)
                    .load(currentPhotoUri)
                    .into(binding.mainImageViewPhoto)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPictureCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        launchIntentCamera()

        binding.title.doAfterTextChanged {
            title = it.toString()
        }

        binding.mainButtonPictureWithContracts.setOnClickListener {
            currentPhotoUri = FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID + ".provider",
                File.createTempFile(
                    "JPEG_",
                    ".jpg",
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                )
            )

            takePictureCallback.launch(currentPhotoUri)
        }

        binding.addPictureButton.setOnClickListener{
            currentPhotoUri = FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID + ".provider",
                File.createTempFile(
                    "JPEG_",
                    ".jpg",
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES)))
            //val realPath: String? = RealPathUtil.getRealPathFromURI_API19(this, currentPhotoUri!!)

            //val theRealPath = getRealPathFromURI(this, currentPhotoUri!!)

            viewModel.onAddTemporaryPhoto(title, currentPhotoUri?.path!!)

            finish()
        }

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

    fun getRealPathFromURI(context: Context, uri: Uri): String? {
        var realPath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.let {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            realPath = cursor.getString(columnIndex)
            cursor.close()
        }
        return realPath
    }
}
