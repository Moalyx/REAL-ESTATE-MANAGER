package com.tuto.realestatemanager.ui.addphoto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
    }

    private val viewModel by viewModels<AddPhotoDialogFragmentViewModel>()

    private lateinit var binding: ActivityAddPhotoBinding

    private var fromEditPropertyActivity: String? = "edit_property"
    private var getEditPropertyId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fromEditPropertyActivity = intent.getStringExtra("XXX")
        getEditPropertyId = intent.getLongExtra("edit_property", -1)

        launchIntent()

    }

    // Dans AddPhotoActivity.kt
    override fun onActivityResult(requestCodes: Int, resultCodes: Int, data: Intent?) {
        super.onActivityResult(requestCodes, resultCodes, data)
        if (requestCodes == INTENT_REQUEST_CODE && resultCodes == RESULT_OK && data != null) {
            val originalUri: Uri = data.data!!

            // On copie le fichier pour avoir un accès permanent
            val permanentUri = copyUriToInternalStorage(originalUri)

            if (permanentUri != null) {
                Glide.with(binding.image)
                    .load(permanentUri)
                    .into(binding.image)

                binding.addPictureButton.setOnClickListener {
                    if (binding.title.text.toString() == "") {
                        Toast.makeText(this, "please enter a description", Toast.LENGTH_SHORT).show()
                    } else {
                        if (fromEditPropertyActivity == "XXX") {
                            viewModel.insertPhoto(0, getEditPropertyId, binding.title.text.toString(), permanentUri.toString())
                        } else {
                            viewModel.onAddTemporaryPhoto(title = binding.title.text.toString(), uri = permanentUri.toString())
                        }
                        finish()
                    }
                }
            }
        }
    }

//    @Deprecated("Deprecated in Java")
//    override fun onActivityResult(requestCodes: Int, resultCodes: Int, data: Intent?) {
//        super.onActivityResult(requestCodes, resultCodes, data)
//        if (requestCodes == INTENT_REQUEST_CODE && resultCodes == RESULT_OK && data != null) {
//            val uri: Uri = data.data!!
//
//            val permanentUri = copyUriToInternalStorage(uri)
//
//            Glide.with(binding.image)
//                .load(uri.toString())
//                .into(binding.image)
//
//            binding.addPictureButton.setOnClickListener {
//                if (binding.title.text.toString() == "") {
//                    Toast.makeText(this, "please enter a description", Toast.LENGTH_SHORT).show()
//                } else {
//
//                    if (fromEditPropertyActivity == "XXX") {
//                        viewModel.insertPhoto(
//                            0,
//                            getEditPropertyId,
//                            binding.title.text.toString(),
//                            uri.toString()
//
//                        )
//                    } else {
//                        viewModel.onAddTemporaryPhoto(
//                            title = binding.title.text.toString(),
//                            uri = uri.toString()
//                        )
//                    }
//
//                    finish()
//                }
//            }
//        } else {
//            Toast.makeText(this, "no permissions", Toast.LENGTH_SHORT).show()
//        }
//
//        binding.cancelAction.setOnClickListener {
//            finish()
//        }
//    }

    private fun copyUriToInternalStorage(uri: Uri): Uri? {val inputStream = contentResolver.openInputStream(uri) ?: return null
        val fileName = "IMG_${System.currentTimeMillis()}.jpg"
        val file = File(
            getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES),
            fileName
        )

        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", file)
    }

    private fun launchIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "choose a photo"),
            INTENT_REQUEST_CODE
        )
    }
}