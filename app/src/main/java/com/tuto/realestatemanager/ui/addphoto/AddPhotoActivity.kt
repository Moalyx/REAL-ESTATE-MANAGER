package com.tuto.realestatemanager.ui.addphoto

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.ActivityAddPhotoBinding
import com.tuto.realestatemanager.ui.createproperty.CreatePropertyActivity
import com.tuto.realestatemanager.ui.utils.RealPathUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPhotoActivity : AppCompatActivity() {

    companion object {
        private const val INTENT_REQUEST_CODE = 100
    }

    private val viewModel by viewModels<AddPhotoDialogFragmentViewModel>()

    private lateinit var binding: ActivityAddPhotoBinding
    private var title: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        launchIntent()

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCodes: Int, resultCodes: Int, data: Intent?) {
        super.onActivityResult(requestCodes, resultCodes, data)
        if (requestCodes == INTENT_REQUEST_CODE && resultCodes == RESULT_OK && data != null) {

            val uri: Uri = data.data!!

            Glide.with(binding.image)
                .load(uri)
                .into(binding.image)

            binding.addPictureButton.setOnClickListener {
                viewModel.onAddTemporaryPhoto(uri.toString(), binding.title.text.toString())
                finish()
            }

        } else {
            Toast.makeText(this, "vous n'avez pas les autorisations", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Sélectionnez une photo"),
            INTENT_REQUEST_CODE
        )
    }

}