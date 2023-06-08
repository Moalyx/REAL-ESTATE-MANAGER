package com.tuto.realestatemanager.ui.createproperty

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuto.realestatemanager.databinding.ActivityCreatePropertyBinding
import com.tuto.realestatemanager.ui.addphoto.AddPhotoActivity
import com.tuto.realestatemanager.ui.addpicturecamera.AddPictureCameraActivity
import com.tuto.realestatemanager.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreatePropertyActivity : AppCompatActivity() {

    private val viewModel by viewModels<CreatePropertyViewModel>()
    private var lat: Double = 0.0
    private var lng: Double = 0.0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_create_property)

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

        val searchView = binding.searchview
        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                viewModel.onAddressSearchChanged(p0)
                return false
            }
        })

        viewModel.placeDetailViewState.observe(this) {
            binding.address.setText("${it.number} ${it.address}")
            //binding.complement.setText(it.number + " " +  it.address)
            binding.zipcode.setText(it.zipCode)
            binding.state.setText(it.state)
            binding.country.setText(it.country)
            binding.city.setText(it.city)
            binding.complement.setText(it.lat.toString() + it.lng.toString())
            lat = it.lat
            lng = it.lng
        }

        val searchAdapter = SearchAdapter(object : SearchAdapter.OnSearchClickListener {
            override fun onPredictionClicked(id: String) {
                viewModel.onGetAutocompleteAddressId(id)
                binding.searchview.clearFocus()
                searchView.setQuery("", false)
                binding.predictionRecyclerview.isVisible
            }
        })
        binding.predictionRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.predictionRecyclerview.adapter = searchAdapter
        viewModel.predictionListViewState.observe(this) {
            if(it.isEmpty() || it == null){
                Toast.makeText(this, "please add at least one photo", Toast.LENGTH_SHORT).show()
            }else{
                searchAdapter.submitList(it)
            }

        }

        val adapter = CreatePropertyPhotoAdapter()
        binding.createUpdatePhotoRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.createUpdatePhotoRecyclerview.adapter = adapter

        viewModel.temporaryPhotoLiveData.observe(this) {
            adapter.submitList(it)
        }

        binding.addPictureButton.setOnClickListener {
            startActivity(Intent(this, AddPhotoActivity::class.java))

        }

        binding.takePictureButton.setOnClickListener {
            startActivity(Intent(this, AddPictureCameraActivity::class.java))
        }

        binding.saveButton.setOnClickListener {

            val type = binding.typeDropdown.text.toString()
            val price = binding.price.text.toString()
            val address = binding.address.text.toString()
            val city = binding.city.text.toString()
            val state = binding.state.text.toString()
            val zipcode = binding.zipcode.text.toString()
            val country = binding.country.text.toString()
            val surface = binding.surface.text.toString()
            val description = binding.description.text.toString()
            val rooms = binding.rooms.text.toString()
            val bedrooms = binding.bedrooms.text.toString()
            val bathrooms = binding.bathrooms.text.toString()
            val agent = binding.agent.text.toString()


            if (type.isEmpty() || price.isEmpty() || address.isEmpty() || city.isEmpty() ||
                state.isEmpty() || zipcode.isEmpty() || country.isEmpty() || surface.isEmpty() ||
                description.isEmpty() || rooms.isEmpty() || bedrooms.isEmpty() ||
                bathrooms.isEmpty() || agent.isEmpty()
            ) {

                Toast.makeText(this, "Please fill all the required fields", Toast.LENGTH_SHORT).show()

            } else {

                viewModel.createProperty(
                    type,
                    binding.price.text.toString().toInt(),
                    binding.address.text.toString(),
                    binding.city.text.toString(),
                    binding.state.text.toString(),
                    binding.zipcode.text.toString().toInt(),
                    binding.country.text.toString(),
                    binding.surface.text.toString().toInt(),
                    lat,
                    lng,
                    binding.description.text.toString(),
                    binding.rooms.text.toString().toInt(),
                    binding.bedrooms.text.toString().toInt(),
                    binding.bathrooms.text.toString().toInt(),
                    binding.agent.text.toString(),
                    binding.checkboxSaleStatus.isChecked,
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


//
//        if (!(type == "" || binding.price.text.toString() == "" || binding.address.text.toString() == "" || binding.city.text.toString() == "" || binding.state.text.toString() == "" || binding.zipcode.text.toString() == "" || binding.country.text.toString() == "" || binding.surface.text.toString() == "" || binding.description.text.toString() == "" || binding.rooms.text.toString() == "" || binding.bedrooms.text.toString() == "" || binding.bathrooms.text.toString() == "" || binding.agent.text.toString() == "")){
//
//            binding.saveButton.isEnabled = false
//
////            binding.saveButton.isEnabled = true
////
////            binding.saveButton.setOnClickListener {0
////                viewModel.createProperty(
////                    type,
////                    binding.price.text.toString().toInt(),
////                    binding.address.text.toString(),
////                    binding.city.text.toString(),
////                    binding.state.text.toString(),
////                    binding.zipcode.text.toString().toInt(),
////                    binding.country.text.toString(),
////                    binding.surface.text.toString().toInt(),
////                    lat,
////                    lng,
////                    binding.description.text.toString(),
////                    binding.rooms.text.toString().toInt(),
////                    binding.bedrooms.text.toString().toInt(),
////                    binding.bathrooms.text.toString().toInt(),
////                    binding.agent.text.toString(),
////                    binding.checkboxSaleStatus.isChecked,
////                    binding.checkboxTrain.isChecked,
////                    binding.checkboxAirport.isChecked,
////                    binding.checkboxRestaurant.isChecked,
////                    binding.checkboxSchool.isChecked,
////                    binding.checkboxBus.isChecked,
////                    binding.checkboxPark.isChecked
////
////                )
////                startActivity(Intent(this, MainActivity::class.java))
////
////            }
//
//        }else{
//            binding.saveButton.isEnabled = true
//
//
////            binding.saveButton.isEnabled = false
//        }


    }
}
