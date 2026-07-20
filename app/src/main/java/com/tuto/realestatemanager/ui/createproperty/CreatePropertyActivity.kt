package com.tuto.realestatemanager.ui.createproperty

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.ActivityCreatePropertyBinding
import com.tuto.realestatemanager.ui.addphoto.AddPhotoActivity
import com.tuto.realestatemanager.ui.addpicturecamera.AddPictureCameraActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreatePropertyActivity : AppCompatActivity() {

    private lateinit var binding:
            ActivityCreatePropertyBinding

    private val viewModel by
    viewModels<CreatePropertyViewModel>()

    private var lat: Double? = null
    private var lng: Double? = null
    private var onePhotoAtLeast = false
    private var isUpdatingAddressFromAutocomplete = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityCreatePropertyBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        binding.tvDateOfPublication.visibility =
            View.GONE

        binding.titleDate.visibility =
            View.GONE

        binding.checkboxSaleStatus.visibility =
            View.GONE

        configureTypeDropdown()
        configureAddressSearch()
        configureAddressTextWatchers()

        val searchAdapter =
            configurePredictionList()

        val photoAdapter =
            configurePhotoList()

        observeViewModel(
            searchAdapter = searchAdapter,
            photoAdapter = photoAdapter
        )

        configurePhotoButtons()
        configureSaveButton()
        configureDismissButton()
        configureBackNavigation()
    }

    private fun configureTypeDropdown() {
        val types =
            resources.getStringArray(
                R.array.property_types
            )

        val dropdownAdapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout
                .support_simple_spinner_dropdown_item,
            types
        )

        binding.typeDropdown.setAdapter(
            dropdownAdapter
        )

        binding.typeDropdown.inputType =
            InputType.TYPE_NULL
    }

    private fun configureAddressSearch() {
        binding.searchview.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(
                    query: String?
                ): Boolean {
                    return false
                }

                override fun onQueryTextChange(
                    query: String?
                ): Boolean {
                    viewModel.onAddressSearchChanged(
                        query
                    )

                    return false
                }
            }
        )
    }

    private fun configureAddressTextWatchers() {
        val addressTextWatcher =
            object : TextWatcher {

                override fun beforeTextChanged(
                    text: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) = Unit

                override fun onTextChanged(
                    text: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (
                        !isUpdatingAddressFromAutocomplete
                    ) {
                        lat = null
                        lng = null
                    }
                }

                override fun afterTextChanged(
                    editable: Editable?
                ) = Unit
            }

        binding.address.addTextChangedListener(
            addressTextWatcher
        )

        binding.city.addTextChangedListener(
            addressTextWatcher
        )

        binding.zipcode.addTextChangedListener(
            addressTextWatcher
        )

        binding.state.addTextChangedListener(
            addressTextWatcher
        )

        binding.country.addTextChangedListener(
            addressTextWatcher
        )
    }

    private fun configurePredictionList():
            SearchAdapter {

        val searchAdapter = SearchAdapter(
            object :
                SearchAdapter.OnSearchClickListener {

                override fun onPredictionClicked(
                    id: String
                ) {
                    viewModel
                        .onSetAutocompleteAddressId(id)

                    binding.searchview.clearFocus()

                    binding.searchview.setQuery(
                        "",
                        false
                    )
                }
            }
        )

        binding.predictionRecyclerview
            .layoutManager =
            LinearLayoutManager(this)

        binding.predictionRecyclerview.adapter =
            searchAdapter

        return searchAdapter
    }

    private fun configurePhotoList():
            CreatePropertyPhotoAdapter {

        val photoAdapter =
            CreatePropertyPhotoAdapter {
                    temporaryPhoto ->

                viewModel.deleteTemporaryPhoto(
                    temporaryPhoto
                )
            }

        binding.createUpdatePhotoRecyclerview
            .layoutManager =
            LinearLayoutManager(this)

        binding.createUpdatePhotoRecyclerview
            .adapter =
            photoAdapter

        return photoAdapter
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel(
        searchAdapter: SearchAdapter,
        photoAdapter: CreatePropertyPhotoAdapter
    ) {
        lifecycleScope.launch {
            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                launch {
                    viewModel.viewAction.collect {
                            action ->

                        when (action) {
                            CreateViewAction
                                .NavigateToMainActivity -> {

                                setResult(RESULT_OK)
                                finish()
                            }
                        }
                    }
                }

                launch {
                    viewModel.placeDetailViewState
                        .collect { placeDetail ->

                            placeDetail?.let { state ->
                                isUpdatingAddressFromAutocomplete =
                                    true

                                binding.address.setText(
                                    "${state.number} ${state.address}"
                                )

                                binding.zipcode.setText(
                                    state.zipCode
                                )

                                binding.state.setText(
                                    state.state
                                )

                                binding.country.setText(
                                    state.country
                                )

                                binding.city.setText(
                                    state.city
                                )

                                lat = state.lat
                                lng = state.lng

                                isUpdatingAddressFromAutocomplete =
                                    false
                            }
                        }
                }

                launch {
                    viewModel.predictionListViewState
                        .collect { predictions ->

                            if (predictions.isEmpty()) {
                                binding
                                    .predictionRecyclerview
                                    .visibility =
                                    View.GONE
                            } else {
                                binding
                                    .predictionRecyclerview
                                    .visibility =
                                    View.VISIBLE

                                searchAdapter.submitList(
                                    predictions
                                )

                                binding
                                    .predictionRecyclerview
                                    .post {
                                        binding
                                            .predictionRecyclerview
                                            .requestLayout()
                                    }
                            }
                        }
                }

                launch {
                    var toastAlreadyShown = false

                    viewModel.hasInternetStateFlow
                        .collect { hasInternet ->

                            if (
                                hasInternet == false &&
                                !toastAlreadyShown
                            ) {
                                Toast.makeText(
                                    this@CreatePropertyActivity,
                                    getString(
                                        R.string
                                            .please_enter_an_address_manually
                                    ),
                                    Toast.LENGTH_SHORT
                                ).show()

                                toastAlreadyShown = true
                            }

                            if (hasInternet == true) {
                                toastAlreadyShown = false
                            }
                        }
                }

                launch {
                    viewModel.temporaryPhotoStateFlow
                        .collect { temporaryPhotos ->

                            onePhotoAtLeast =
                                temporaryPhotos.isNotEmpty()

                            photoAdapter.submitList(
                                temporaryPhotos
                            )
                        }
                }
            }
        }
    }

    private fun configurePhotoButtons() {
        binding.addPictureButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddPhotoActivity::class.java
                )
            )
        }

        binding.takePictureButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddPictureCameraActivity::class.java
                )
            )
        }
    }

    private fun configureSaveButton() {
        binding.saveButton.setOnClickListener {
            if (!onePhotoAtLeast) {
                Toast.makeText(
                    this,
                    getString(
                        R.string
                            .please_add_at_least_one_photo
                    ),
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val type =
                binding.typeDropdown.text.toString()

            val price =
                binding.price.text.toString()

            val address =
                binding.address.text.toString()

            val city =
                binding.city.text.toString()

            val state =
                binding.state.text.toString()

            val zipcode =
                binding.zipcode.text.toString()

            val country =
                binding.country.text.toString()

            val surface =
                binding.surface.text.toString()

            val description =
                binding.description.text.toString()

            val rooms =
                binding.rooms.text.toString()

            val bedrooms =
                binding.bedrooms.text.toString()

            val bathrooms =
                binding.bathrooms.text.toString()

            val agent =
                binding.agent.text.toString()

            if (
                type.isEmpty() ||
                price.isEmpty() ||
                address.isEmpty() ||
                city.isEmpty() ||
                state.isEmpty() ||
                zipcode.isEmpty() ||
                country.isEmpty() ||
                surface.isEmpty() ||
                description.isEmpty() ||
                rooms.isEmpty() ||
                bedrooms.isEmpty() ||
                bathrooms.isEmpty() ||
                agent.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    getString(
                        R.string
                            .please_fill_all_the_required_fields
                    ),
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val priceInt =
                price.toIntOrNull()

            val zipcodeInt =
                zipcode.toIntOrNull()

            val surfaceInt =
                surface.toIntOrNull()

            val roomsInt =
                rooms.toIntOrNull()

            val bedroomsInt =
                bedrooms.toIntOrNull()

            val bathroomsInt =
                bathrooms.toIntOrNull()

            if (
                priceInt == null ||
                zipcodeInt == null ||
                surfaceInt == null ||
                roomsInt == null ||
                bedroomsInt == null ||
                bathroomsInt == null
            ) {
                Toast.makeText(
                    this,
                    getString(
                        R.string
                            .please_enter_valid_numbers
                    ),
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            viewModel.createProperty(
                type = type,
                price = priceInt,
                address = address,
                city = city,
                state = state,
                zipcode = zipcodeInt,
                country = country,
                surface = surfaceInt,
                lat = lat,
                lng = lng,
                description = description,
                room = roomsInt,
                bedroom = bedroomsInt,
                bathroom = bathroomsInt,
                agent = agent,
                isSold =
                    binding.checkboxSaleStatus.isChecked,
                poiTrain =
                    binding.checkboxTrain.isChecked,
                poiAirport =
                    binding.checkboxAirport.isChecked,
                poiResto =
                    binding.checkboxRestaurant.isChecked,
                poiSchool =
                    binding.checkboxSchool.isChecked,
                poiBus =
                    binding.checkboxBus.isChecked,
                poiPark =
                    binding.checkboxPark.isChecked
            )
        }
    }

    private fun configureDismissButton() {
        binding.dismissButton.setOnClickListener {
            viewModel.clearTemporaryPhotos()
            finish()
        }
    }

    private fun configureBackNavigation() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.clearTemporaryPhotos()
                    finish()
                }
            }
        )
    }
}