package com.tuto.realestatemanager.ui.createproperty

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.ActivityCreatePropertyBinding
import com.tuto.realestatemanager.ui.utils.RealPathUtil
import dagger.hilt.android.AndroidEntryPoint

private const val INTENT_REQUEST_CODE = 100
private const val PERMISSION_REQUEST_CODE = 200
private const val RESULT_DATA_OK = 300

@AndroidEntryPoint
class CreatePropertyActivity : AppCompatActivity() {


    private lateinit var binding: ActivityCreatePropertyBinding
    private val viewModel by viewModels<CreatePropertyViewModel>()
    private var list = mutableListOf<String>()



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

        //binding.typeDropdown.getSelectedItem().toString()

//        binding.typeDropdown.onItemClickListener =
//            AdapterView.OnItemClickListener { parent, _, position, _ ->
//                viewModel.onTypeSelected(parent.getItemAtPosition(position).toString())
//            }

        binding.typeDropdown.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                type = parent.getItemAtPosition(position).toString()
            }

        binding.typeDropdown.inputType = InputType.TYPE_NULL

//        binding.addPictureButton.setOnClickListener() {
//            Intent(Intent.ACTION_PICK).data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        }

        //viewModel.onTypeSelected(binding.typeDropdown.text.toString())
//        try {
//        viewModel.onPriceChanged(Integer.parseInt(binding.price.text.toString()))
//        }catch (e:NumberFormatException) {
//            println("not a number");
//        }
        //viewModel.onCountyChanged(binding.country.text.toString())
//        try {
//        viewModel.onSurfaceChanged(Integer.parseInt(binding.surface.text.toString()))
//        }catch (e:NumberFormatException) {
//            println("not a number");
//        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            // Demande de permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission accordée, on peut lancer l'intent

        }

        viewModel.photo.observe(this){
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
            val uri: Uri = data.data!!
            val realPath: String? = RealPathUtil.getRealPathFromURI_API19(this, uri)
            list.add(realPath!!)
            viewModel.createTemporaryPhoto(realPath)

        }else{
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Sélectionnez une photo"), INTENT_REQUEST_CODE)
    }

//    val getContent: ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//        list.add(uri.toString())
//    }



}