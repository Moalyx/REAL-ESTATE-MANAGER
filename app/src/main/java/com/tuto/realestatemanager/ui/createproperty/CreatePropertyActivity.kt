package com.tuto.realestatemanager.ui.createproperty

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView.OnQueryTextListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.ActivityCreatePropertyBinding
import com.tuto.realestatemanager.ui.addphoto.AddPhotoActivity
import com.tuto.realestatemanager.ui.addpicturecamera.AddPictureCameraActivity
import com.tuto.realestatemanager.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreatePropertyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePropertyBinding
    private val viewModel by viewModels<CreatePropertyViewModel>()
    private var lat: Double = 0.0
    private var lng: Double = 0.0

    @SuppressLint("SetTextI18n")
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

        viewModel.predictionListViewState.observe(this) {
            val recyclerview: RecyclerView = binding.predictionRecyclerview
            val adapter = SearchAdapter(object : SearchAdapter.OnSearchClickListener {
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

        val adapter = CreatePropertyPhotoAdapterTwo()
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