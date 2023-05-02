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
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.ActivityCreatePropertyBinding
import com.tuto.realestatemanager.ui.addphoto.AddPhotoActivity
import com.tuto.realestatemanager.ui.addpicturecamera.AddPictureCameraActivity
import com.tuto.realestatemanager.ui.list.PropertyListFragment
import com.tuto.realestatemanager.ui.main.MainActivity
import com.tuto.realestatemanager.ui.utils.RealPathUtil
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.util.*


@AndroidEntryPoint
class CreatePropertyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePropertyBinding
    private val viewModel by viewModels<CreatePropertyViewModel>()
    private var lat: Double = 0.0
    private var lng: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_property)

        binding = ActivityCreatePropertyBinding.inflate(layoutInflater)
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

//        binding.address.doAfterTextChanged {
//            viewModel.onAddressSearchChanged(it?.toString())
//            //recyclerview.isVisible
//        }

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
            binding.address.setText("${it.number} ${it.address}")
            //binding.complement.setText(it.number + " " +  it.address)
            binding.zipcode.setText(it.zipCode)
            binding.state.setText(it.state)
            binding.country.setText(it.country)
            binding.city.setText(it.city)
            binding.complement.setText(it.lat.toString() + it.lng.toString())
            lat = it.lat
            lng = it.lng
            //todo ici recup  le latlng avec la variable latlng ligne 61 et la mettre ligne 173 pour creer la propertyentity
        }

        viewModel.predictionListViewState.observe(this) {
//            Log.d("TAAAG", "onCreate() called $it")
            val recyclerview: RecyclerView = binding.predictionRecyclerview
            val adapter = SearchAdapter(object : SearchAdapter.OnSearchClickListener{
                override fun onPredictionClicked(id: String) {
                    viewModel.onGetAutocompleteAddressId(id)
                    binding.searchview.clearFocus()
                    searchView.setQuery("", false)
                    binding.predictionRecyclerview.isVisible
                }
            })
            recyclerview.layoutManager = LinearLayoutManager(this)
            recyclerview.adapter = adapter
            adapter.submitList(it)
        }

//        recyclerViewBinding.photoTitle.doAfterTextChanged {
//            viewModel.createTemporaryPhoto(null, it.toString())
//        }



//        viewModel.photo.observe(this) {
//            val recyclerView = binding.createUpdatePhotoRecyclerview
//            val adapter = CreatePropertyPhotoAdapter()
//            recyclerView.layoutManager = LinearLayoutManager(this)
//            recyclerView.adapter = adapter
//            adapter.submitList(it)
//        }

        val adapter = CreatePropertyPhotoAdapterTwo()
        binding.createUpdatePhotoRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.createUpdatePhotoRecyclerview.adapter = adapter

        viewModel.temporaryPhotoLiveData.observe(this) {
            adapter.submitList(it)
            //viewModel.createPhoto(it)
        }

        binding.addPictureButton.setOnClickListener {
//            supportFragmentManager.beginTransaction()
//                .replace(binding.container.id, AddPhotoDialogFragment())
//                .addToBackStack("AddPhotoDialogFragment")
//                .commit()
            startActivity(Intent(this, AddPhotoActivity::class.java))

            //launchIntent()
//            getContent.launch("image/*")
        }

        binding.takePictureButton.setOnClickListener {
            startActivity(Intent(this, AddPictureCameraActivity::class.java))
        }

        binding.saveButton.setOnClickListener {
            viewModel.createProperty(
                type,
                Integer.parseInt(binding.price.text.toString()),
                binding.address.text.toString(),
                binding.city.text.toString(),
                binding.state.text.toString(),
                Integer.parseInt(binding.zipcode.text.toString()),
                binding.country.text.toString(),
                Integer.parseInt(binding.surface.text.toString()),
                lat,
                lng,
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

            )
            startActivity(Intent(this, MainActivity::class.java))

        }
    }
}