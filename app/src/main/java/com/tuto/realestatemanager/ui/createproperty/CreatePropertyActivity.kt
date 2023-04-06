package com.tuto.realestatemanager.ui.createproperty

import android.Manifest
import android.R.attr
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.ActivityCreatePropertyBinding
import com.tuto.realestatemanager.ui.utils.RealPathUtil
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@AndroidEntryPoint
class CreatePropertyActivity : AppCompatActivity() {

    companion object{
        private const val INTENT_REQUEST_CODE = 100
        private const val PERMISSION_REQUEST_CODE = 200
        private const val RESULT_DATA_OK = 300
    }

    private lateinit var binding: ActivityCreatePropertyBinding
    private val viewModel by viewModels<CreatePropertyViewModel>()
    private var list = mutableListOf<String>()
    private var isFromCamera = false
    lateinit var currentPhotoPath: String
    private var uriImageSelected: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_property)

        val binding = ActivityCreatePropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var type = ""


        val types = arrayOf("House", "Penthouse", "Duplex", "Loft", "Flat")
        val dropdownAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, types
        )
        binding.typeDropdown.setAdapter(dropdownAdapter)
        binding.typeDropdown.threshold

        binding.typeDropdown.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                type = parent.getItemAtPosition(position).toString()
            }

        binding.typeDropdown.inputType = InputType.TYPE_NULL

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Demande de permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission accordée, on peut lancer l'intent

        }



        binding.address.doAfterTextChanged {viewModel.onAddressSearchChanged(it?.toString()) }

        viewModel.predictions.observe(this){
            Log.d("TAG", "onCreate() called $it" )
            binding.city.setText(it)
        }



        viewModel.photo.observe(this) {
            val recyclerView = binding.createUpdatePhotoRecyclerview
            val adapter = CreatePropertyPhotoAdapter()
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
            adapter.submitList(list)

        }



        binding.addPictureButton.setOnClickListener {
            launchIntent()
//            getContent.launch("image/*")
        }

        binding.takePictureButton.setOnClickListener {
            pictureIntent()
        }

        binding.saveButton.setOnClickListener() {
            viewModel.createProperty(
                type,
                Integer.parseInt(binding.price.text.toString()),
                binding.country.text.toString(),
                Integer.parseInt(binding.surface.text.toString()),
                binding.description.text.toString(),
                Integer.parseInt(binding.rooms.text.toString()),
                Integer.parseInt(binding.bedrooms.text.toString()),
                Integer.parseInt(binding.bathrooms.text.toString()),
                binding.checkboxtrTrain.isChecked,
                binding.checkboxAirport.isChecked,
                binding.checkboxRestaurant.isChecked,
                binding.checkboxSchool.isChecked,
                binding.checkboxBus.isChecked,
                binding.checkboxPark.isChecked
//                photoUrl = "content://com.android.providers.media.documents/document/image%3A160281"
            )
            finish()

        }

    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCodes: Int, resultCodes: Int, data: Intent?) {
        super.onActivityResult(requestCodes, resultCodes, data)
        if (requestCodes == INTENT_REQUEST_CODE && resultCodes == RESULT_OK && data != null) {
            if(isFromCamera){}

            val photo = intent.extras
            val uri: Uri = data.data!!
            val realPath: String? = RealPathUtil.getRealPathFromURI_API19(this, uri)
            list.add(realPath!!)
            list.add(photo.toString())
            viewModel.createTemporaryPhoto(realPath)

        } else {
            Toast.makeText(this, "vous n'avez pas les autorisations", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchIntent() {
        isFromCamera = false
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Sélectionnez une photo"), INTENT_REQUEST_CODE
        )
    }

    private fun pictureIntent() {
            isFromCamera = true
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val photoFile = createImageFile()
            val uri = Uri.fromFile(photoFile)
            //uriImageSelected = FileProvider.getUriForFile(this, "", photoFile)

            //intent.action = Intent.ACTION_CAMERA_BUTTON
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            startActivityForResult(intent, INTENT_REQUEST_CODE)


    }

    // When photo is created, we need to create an image file
    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = LocalDate.now().toString()
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
//            .apply {
//            // Save a file: path for use with CAMERA
//            currentPhotoPath = absolutePath
//        }
    }

//    val getContent: ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//        list.add(uri.toString())
//    }


}