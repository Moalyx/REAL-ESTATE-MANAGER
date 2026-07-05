package com.tuto.realestatemanager.ui.addphoto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.tuto.realestatemanager.BuildConfig
import com.tuto.realestatemanager.databinding.ActivityAddPhotoBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@Suppress("DEPRECATION")
@AndroidEntryPoint
class AddPhotoActivity : AppCompatActivity() {

    companion object {
        private const val INTENT_REQUEST_CODE = 100
        private const val KEY_PHOTO_URI = "KEY_PHOTO_URI"
    }

    private val viewModel by viewModels<AddPhotoDialogFragmentViewModel>()

    private lateinit var binding: ActivityAddPhotoBinding

    private var fromEditPropertyActivity: String? = null
    private var getEditPropertyId = 0L
    private var permanentPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fromEditPropertyActivity = intent.getStringExtra("XXX")
        getEditPropertyId = intent.getLongExtra("edit_property", -1)

        setupListeners()

        permanentPhotoUri = savedInstanceState
            ?.getString(KEY_PHOTO_URI)
            ?.let { Uri.parse(it) }

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
                        "please enter a description",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                uri.isNullOrBlank() -> {
                    Toast.makeText(
                        this,
                        "please choose a photo",
                        Toast.LENGTH_SHORT
                    ).show()
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

        binding.cancelAction.setOnClickListener {
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != INTENT_REQUEST_CODE) return

        if (resultCode != RESULT_OK) {
            finish()
            return
        }

        val originalUri = data?.data

        if (originalUri == null) {
            Toast.makeText(
                this,
                "photo missing",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        val copiedUri = copyUriToInternalStorage(originalUri)

        if (copiedUri == null) {
            Toast.makeText(
                this,
                "unable to load photo",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        permanentPhotoUri = copiedUri

        Glide.with(this)
            .load(permanentPhotoUri)
            .into(binding.image)
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

        permanentPhotoUri?.let {
            outState.putString(KEY_PHOTO_URI, it.toString())
        }
    }

    private fun launchIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }

        startActivityForResult(
            Intent.createChooser(intent, "choose a photo"),
            INTENT_REQUEST_CODE
        )
    }
}