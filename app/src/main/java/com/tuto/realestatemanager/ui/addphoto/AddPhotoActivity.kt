package com.tuto.realestatemanager.ui.addphoto

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.tuto.realestatemanager.databinding.ActivityAddPhotoBinding
import com.tuto.realestatemanager.ui.utils.RealPathUtil
import dagger.hilt.android.AndroidEntryPoint

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


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCodes: Int, resultCodes: Int, data: Intent?) {
        super.onActivityResult(requestCodes, resultCodes, data)
        if (requestCodes == INTENT_REQUEST_CODE && resultCodes == RESULT_OK && data != null) {
            val uri: Uri = data.data!!

            val realpathutil = RealPathUtil.getRealPathFromURI_API19(this, uri)

            Glide.with(binding.image)
                .load(uri.toString())
                .into(binding.image)

            binding.addPictureButton.setOnClickListener {
                if (fromEditPropertyActivity == "XXX"){
                    viewModel.insertPhoto(
                        0,
                        getEditPropertyId,
                        binding.title.text.toString(),
                        realpathutil!!

                    )
                }else{
                    viewModel.onAddTemporaryPhoto(
                        title = binding.title.text.toString(),
                        uri = uri.toString()
                    )
                }

                finish()
            }
        } else {
            Toast.makeText(this, "no permissions", Toast.LENGTH_SHORT).show()
        }

        binding.cancelAction.setOnClickListener{
            finish()
        }
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