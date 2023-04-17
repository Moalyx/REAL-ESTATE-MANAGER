package com.tuto.realestatemanager.ui.createproperty

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.ActivityCreatePropertyBinding
import com.tuto.realestatemanager.databinding.ItemAddPictureRecyclerviewBinding
import com.tuto.realestatemanager.ui.utils.RealPathUtil
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.util.*


@AndroidEntryPoint
class CreatePropertyActivity : AppCompatActivity() {

    companion object {
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

//        val recyclerViewBinding = ItemAddPictureRecyclerviewBinding.inflate(layoutInflater)
//        setContentView(recyclerViewBinding.root)

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

        binding.address.doAfterTextChanged {
            viewModel.onAddressSearchChanged(it?.toString())
            //recyclerview.isVisible
        }

        val searchView = binding.searchview
        searchView.setOnQueryTextListener(object : OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                viewModel.onAddressSearchChanged(p0)
                return false
            }

        })

        viewModel.placeDetailViewState.observe(this){
            //binding.address.setText(it.number + it.address)
            binding.complement.setText(it.address)
            binding.zipcode.setText(it.zipCode)
            binding.state.setText(it.state)
            binding.country.setText(it.country)
            binding.city.setText(it.city)
        }

        viewModel.predictionListViewState.observe(this) {
//            Log.d("TAAAG", "onCreate() called $it")
            val recyclerview: RecyclerView = binding.predictionRecyclerview
            val adapter = SearchAdapter(object : SearchAdapter.OnSearchClickListener{
                override fun onPhotoClicked(id: String) {
                    viewModel.onGetAutocompleteAddressId(id)
                }
            })
            recyclerview.layoutManager = LinearLayoutManager(this)
            recyclerview.adapter = adapter
            adapter.submitList(it)
        }

//        recyclerViewBinding.photoTitle.doAfterTextChanged {
//            viewModel.createTemporaryPhoto(null, it.toString())
//        }



        viewModel.photo.observe(this) {
            val recyclerView = binding.createUpdatePhotoRecyclerview
            val adapter = CreatePropertyPhotoAdapter()
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
            adapter.submitList(it)

        }

        binding.addPictureButton.setOnClickListener {
            launchIntent()
//            getContent.launch("image/*")
        }

        binding.takePictureButton.setOnClickListener {
            pictureIntent()
            //capturePhoto()
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
//            if (isFromCamera) {

//                val photo = intent.extras
//                viewModel.createTemporaryPhoto(photo.toString())

//            }else{
                val realPath: String? = RealPathUtil.getRealPathFromURI_API19(this, uri)
                viewModel.createTemporaryPhoto(realPath!!)
//            }
            //val description = binding.address.text.toString()

            //val photo = intent.extras


            //list.add(realPath!!)
            //list.add(photo.toString())
//            if(realPath != null){
//                viewModel.createTemporaryPhoto(realPath)
//            }else{
//                Toast.makeText(this, "no photo", Toast.LENGTH_SHORT).show()
//            }
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
        if (intent.resolveActivity(this.packageManager) != null) {
            try {

                val tempFile = File.createTempFile("my_app", ".jpg")
                val fileName = tempFile.absolutePath.toUri()
                val uri = Uri.fromFile(tempFile)
                // Créer un fichier pour stocker la photo capturée
//                fichierPhoto.createNewFile()
//                val urix = fichierPhoto.toURI()

                // Passer l'URI du fichier comme extra pour l'intention de la caméra
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileName)

                // Lancer l'activité de la caméra avec une demande de résultat
                this.startActivityForResult(intent, 0)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        val photoFile = createImageFile()
//        val uri = Uri.fromFile(photoFile)
        //uriImageSelected = FileProvider.getUriForFile(this, "", photoFile)

        //intent.action = Intent.ACTION_CAMERA_BUTTON
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        //startActivityForResult(intent, INTENT_REQUEST_CODE)


    }

    fun capturePhoto() {

        val tempFile = File.createTempFile("my_app", ".jpg")
        val fileName = tempFile.absolutePath
        val uri = Uri.fromFile(tempFile)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, INTENT_REQUEST_CODE)

//        val photoFile = createImageFile()
//        val uri = Uri.fromFile(photoFile)
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
//            putExtra(MediaStore.EXTRA_OUTPUT, uri)
//        }
//        if (intent.resolveActivity(packageManager) != null) {
//            startActivityForResult(intent, 0)
//        }
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
            .apply {
            // Save a file: path for use with CAMERA
            currentPhotoPath = absolutePath
        }
    }

//    val getContent: ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//        list.add(uri.toString())
//    }


}