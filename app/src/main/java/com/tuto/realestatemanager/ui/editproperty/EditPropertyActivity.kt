package com.tuto.realestatemanager.ui.editproperty

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.R
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import com.tuto.realestatemanager.databinding.ActivityCreatePropertyBinding
import com.tuto.realestatemanager.ui.addphoto.AddPhotoActivity
import com.tuto.realestatemanager.ui.addpicturecamera.AddPictureCameraActivity
import com.tuto.realestatemanager.ui.createproperty.SearchAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPropertyActivity : AppCompatActivity() {

    private var type = ""
    private var onePhotoAtLeast = false
    private var isUpdatingAddressFromAutocomplete = false
    private var isBindingPropertyDetails = false

    companion object {
        const val XXX = "XXX"
        const val KEY_EDIT = "edit_property"
        const val KEY_PROPERTY_ID = "KEY_PROPERTY_ID"
        fun navigate(context: Context, propertyId: Long): Intent {
            val intent = Intent(context, EditPropertyActivity::class.java)
            intent.putExtra(KEY_PROPERTY_ID, propertyId)
            return intent
        }
    }
    private var previousDateOfSale: String = "Not yet sold"
    private var wasSold: Boolean = false

    private var lat: Double? = null
    private var lng: Double? = null

    private val viewModel by viewModels<EditPropertyViewModel>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityCreatePropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.typeDropdown.inputType = InputType.TYPE_NULL

        val searchView = binding.searchview

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.onAddressSearchChanged(query)
                return false
            }
        })

        val addressTextWatcher = object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isUpdatingAddressFromAutocomplete && !isBindingPropertyDetails) {
                    lat = null
                    lng = null
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) = Unit
        }

        binding.address.addTextChangedListener(addressTextWatcher)
        binding.city.addTextChangedListener(addressTextWatcher)
        binding.zipcode.addTextChangedListener(addressTextWatcher)
        binding.state.addTextChangedListener(addressTextWatcher)
        binding.country.addTextChangedListener(addressTextWatcher)

        viewModel.placeDetailViewState.observe(this) {
            isUpdatingAddressFromAutocomplete = true

            binding.address.setText("${it.number} ${it.address}")
            binding.zipcode.setText(it.zipCode)
            binding.state.setText(it.state)
            binding.country.setText(it.country)
            binding.city.setText(it.city)

            lat = it.lat
            lng = it.lng

            isUpdatingAddressFromAutocomplete = false
        }

        val searchAdapter = SearchAdapter(object : SearchAdapter.OnSearchClickListener {
            override fun onPredictionClicked(id: String) {
                viewModel.onSetAutocompleteAddressId(id)
                binding.searchview.clearFocus()
                searchView.setQuery("", false)
            }
        })

        binding.predictionRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.predictionRecyclerview.adapter = searchAdapter

        viewModel.predictionListViewState.observe(this) { predictions ->

            if (predictions.isNullOrEmpty()) {
                binding.predictionRecyclerview.visibility = View.GONE
            } else {
                binding.predictionRecyclerview.visibility = View.VISIBLE
                searchAdapter.submitList(predictions)

                binding.predictionRecyclerview.post {
                    binding.predictionRecyclerview.requestLayout()
                }
            }
        }

        val types = arrayOf("House", "Penthouse", "Duplex", "Loft", "Flat")
        val dropdownAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            R.layout.support_simple_spinner_dropdown_item, types
        )
        binding.typeDropdown.setAdapter(dropdownAdapter)
        binding.typeDropdown.threshold

        binding.typeDropdown.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                type = parent.getItemAtPosition(position).toString()
            }

        val propertyId = intent.getLongExtra(KEY_PROPERTY_ID, -1)
        viewModel.setPropertyId(propertyId)

        binding.addPictureButton.setOnClickListener {
            val intent = Intent(this, AddPhotoActivity::class.java)
            intent.putExtra("XXX", XXX)
            intent.putExtra(KEY_EDIT, propertyId)
            startActivity(intent)
        }

        binding.takePictureButton.setOnClickListener {
            val intent = Intent(this, AddPictureCameraActivity::class.java)
            intent.putExtra("XXX", XXX)
            intent.putExtra(KEY_EDIT, propertyId)
            startActivity(intent)
        }

        val adapter = EditPropertyPhotoAdapter(
            object : EditPropertyPhotoAdapter.OnDeletePhotoListener {
                override fun onDeletePhotoListener(photo: EditPropertyPhotoViewState) {
                    viewModel.onDeleteEditPhoto(photo)
                }
            }
        )
        val recyclerView: RecyclerView = binding.createUpdatePhotoRecyclerview
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.getAllPhotoLiveData.observe(this) {photos ->
            onePhotoAtLeast = photos.isNotEmpty()
            adapter.submitList(photos)
        }

        viewModel.detailPropertyLiveData.observe(this) {
            isBindingPropertyDetails = true

            type = it.type
            binding.typeDropdown.setText(it.type, false)
            binding.description.setText(it.description, TextView.BufferType.EDITABLE)
            binding.price.setText(it.price.toString())
            binding.surface.setText(it.surface.toString(), TextView.BufferType.EDITABLE).toString()
            binding.rooms.setText(it.room.toString(), TextView.BufferType.EDITABLE)
            binding.bedrooms.setText(it.bedroom.toString(), TextView.BufferType.EDITABLE)
            binding.bathrooms.setText(it.bathroom.toString(), TextView.BufferType.EDITABLE)
            binding.address.setText(it.address, TextView.BufferType.EDITABLE)
            binding.city.setText(it.city, TextView.BufferType.EDITABLE)
            binding.state.setText(it.state, TextView.BufferType.EDITABLE)
            binding.zipcode.setText(it.zipcode.toString())
            binding.country.setText(it.country, TextView.BufferType.EDITABLE)
            binding.date.setText(it.saleSince)
            binding.agent.setText(it.agent)

            lat = it.lat
            lng = it.lng

            previousDateOfSale = it.dateOfSale
            wasSold = it.isSold

            viewModel.isChecked(binding.checkboxAirport, it.poiAirport)
            viewModel.isChecked(binding.checkboxBus, it.poiBus)
            viewModel.isChecked(binding.checkboxPark, it.poiPark)
            viewModel.isChecked(binding.checkboxSchool, it.poiSchool)
            viewModel.isChecked(binding.checkboxRestaurant, it.poiResto)
            viewModel.isChecked(binding.checkboxTrain, it.poiTrain)
            viewModel.isChecked(binding.checkboxSaleStatus, it.isSold)

            isBindingPropertyDetails = false
        }

        binding.saveButton.setOnClickListener {

            if (!onePhotoAtLeast) {
                Toast.makeText(this, "please add at least one photo", Toast.LENGTH_SHORT).show()
            } else {

                type = binding.typeDropdown.text.toString()

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

                if (
                    type.isEmpty() || price.isEmpty() || address.isEmpty() || city.isEmpty() ||
                    state.isEmpty() || zipcode.isEmpty() || country.isEmpty() || surface.isEmpty() ||
                    description.isEmpty() || rooms.isEmpty() || bedrooms.isEmpty() ||
                    bathrooms.isEmpty() || agent.isEmpty()
                ) {
                    Toast.makeText(this, "Please fill all the required fields", Toast.LENGTH_SHORT).show()
                } else {

                    val priceInt = price.toIntOrNull()
                    val zipcodeInt = zipcode.toIntOrNull()
                    val surfaceInt = surface.toIntOrNull()
                    val roomsInt = rooms.toIntOrNull()
                    val bedroomsInt = bedrooms.toIntOrNull()
                    val bathroomsInt = bathrooms.toIntOrNull()

                    if (
                        priceInt == null ||
                        zipcodeInt == null ||
                        surfaceInt == null ||
                        roomsInt == null ||
                        bedroomsInt == null ||
                        bathroomsInt == null
                    ) {
                        Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    viewModel.updateProperty(
                        propertyId,
                        type,
                        priceInt,
                        address,
                        city,
                        state,
                        zipcodeInt,
                        country,
                        surfaceInt,
                        lat,
                        lng,
                        description,
                        roomsInt,
                        bedroomsInt,
                        bathroomsInt,
                        agent,
                        binding.checkboxSaleStatus.isChecked,
                        wasSold,
                        previousDateOfSale,
                        binding.checkboxTrain.isChecked,
                        binding.date.text.toString(),
                        binding.checkboxAirport.isChecked,
                        binding.checkboxRestaurant.isChecked,
                        binding.checkboxSchool.isChecked,
                        binding.checkboxBus.isChecked,
                        binding.checkboxPark.isChecked
                    )

//                    viewModel.onNavigateToDetailActivity()
                }
            }
        }

        binding.dismissButton.setOnClickListener {
            viewModel.clearTemporaryPhotos()
            finish()
        }

        viewModel.navigateSingleLiveEvent.observe(this) {
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        viewModel.clearTemporaryPhotos()
        super.onBackPressed()
    }

}