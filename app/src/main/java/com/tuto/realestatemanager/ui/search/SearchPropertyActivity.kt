package com.tuto.realestatemanager.ui.search

import android.os.Bundle
import android.text.InputType
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.ActivitySearchPropertyBinding
import com.tuto.realestatemanager.model.SearchParameters
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchPropertyActivity : AppCompatActivity() {

    private val viewModel by viewModels<SearchPropertyViewModel>()

    private lateinit var binding: ActivitySearchPropertyBinding

    private var type: String? = null
    private var soldStatus: Boolean? = null

    private companion object {
        const val STATUS_ALL = "All"
        const val STATUS_AVAILABLE = "Available"
        const val STATUS_SOLD = "Sold"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchPropertyBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)

        setToolbar()
        setDropdownMenus()
        setUserParameters()
        observeViewModel()
        setupOnBackPressed()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            viewModel.onNavigateToMainActivity()
        }
    }

    private fun setupOnBackPressed() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.onNavigateToMainActivity()
                }
            }
        )
    }

    private fun setDropdownMenus() {
        val types = resources.getStringArray(
            R.array.property_types
        )

        val typeDropdownAdapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout
                .support_simple_spinner_dropdown_item,
            types
        )

        binding.typeDropdown.setAdapter(typeDropdownAdapter)
        binding.typeDropdown.inputType = InputType.TYPE_NULL

        val statuses = arrayOf(
            STATUS_ALL,
            STATUS_AVAILABLE,
            STATUS_SOLD
        )

        val statusDropdownAdapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout
                .support_simple_spinner_dropdown_item,
            statuses
        )

        binding.soldStatusDropdown.setAdapter(
            statusDropdownAdapter
        )
        binding.soldStatusDropdown.inputType =
            InputType.TYPE_NULL
        binding.soldStatusDropdown.setText(
            STATUS_ALL,
            false
        )
    }

    private fun setUserParameters() {
        binding.typeDropdown.onItemClickListener =
            AdapterView.OnItemClickListener {
                    parent,
                    _,
                    position,
                    _ ->

                type = parent
                    .getItemAtPosition(position)
                    .toString()
            }

        binding.soldStatusDropdown.onItemClickListener =
            AdapterView.OnItemClickListener {
                    parent,
                    _,
                    position,
                    _ ->

                soldStatus = when (
                    parent
                        .getItemAtPosition(position)
                        .toString()
                ) {
                    STATUS_SOLD -> true
                    STATUS_AVAILABLE -> false
                    else -> null
                }
            }

        binding.validateParametersButton
            .setOnClickListener {
                val minimumPhotos = binding.minimumPhotos
                    .text
                    ?.toString()
                    ?.toIntOrNull()

                viewModel.setParameters(
                    type = type,
                    priceMinimum = binding.priceMin.text
                        .toString(),
                    priceMaximum = binding.priceMax.text
                        .toString(),
                    surfaceMinimum = binding.surfaceMinimum
                        .text
                        .toString(),
                    surfaceMaximum = binding.surfaceMaximum
                        .text
                        .toString(),
                    city = binding.city.text.toString(),
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
                        binding.checkboxPark.isChecked,
                    soldStatus = soldStatus,
                    minimumPhotos = minimumPhotos
                )

                viewModel.onNavigateToMainActivity()
            }

        binding.clearParametersButton
            .setOnClickListener {
                clearFields()
                viewModel.clearParameters()
                viewModel.onNavigateToMainActivity()
            }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.viewAction.collect { action ->
                        when (action) {
                            SearchViewAction
                                .NavigateToMainActivity -> {
                                finish()
                            }
                        }
                    }
                }

                launch {
                    viewModel.parametersStateFlow
                        .collect { parameters ->
                            parameters?.let {
                                displayParameters(it)
                            }
                        }
                }
            }
        }
    }

    private fun displayParameters(
        parameters: SearchParameters
    ) {
        type = parameters.type
        soldStatus = parameters.soldStatus

        binding.typeDropdown.setText(
            parameters.type.orEmpty(),
            false
        )
        binding.priceMin.setText(
            parameters.priceMinimum
                ?.toString()
                .orEmpty()
        )
        binding.priceMax.setText(
            parameters.priceMaximum
                ?.toString()
                .orEmpty()
        )
        binding.surfaceMinimum.setText(
            parameters.surfaceMinimum
                ?.toString()
                .orEmpty()
        )
        binding.surfaceMaximum.setText(
            parameters.surfaceMaximum
                ?.toString()
                .orEmpty()
        )
        binding.city.setText(
            parameters.city.orEmpty()
        )

        binding.checkboxTrain.isChecked =
            parameters.poiTrain
        binding.checkboxAirport.isChecked =
            parameters.poiAirport
        binding.checkboxRestaurant.isChecked =
            parameters.poiResto
        binding.checkboxSchool.isChecked =
            parameters.poiSchool
        binding.checkboxBus.isChecked =
            parameters.poiBus
        binding.checkboxPark.isChecked =
            parameters.poiPark

        binding.soldStatusDropdown.setText(
            when (parameters.soldStatus) {
                true -> STATUS_SOLD
                false -> STATUS_AVAILABLE
                null -> STATUS_ALL
            },
            false
        )

        binding.minimumPhotos.setText(
            parameters.minimumPhotos
                ?.toString()
                .orEmpty()
        )
    }

    private fun clearFields() {
        type = null
        soldStatus = null

        binding.typeDropdown.setText("", false)
        binding.priceMin.setText("")
        binding.priceMax.setText("")
        binding.surfaceMinimum.setText("")
        binding.surfaceMaximum.setText("")
        binding.city.setText("")

        binding.checkboxTrain.isChecked = false
        binding.checkboxAirport.isChecked = false
        binding.checkboxRestaurant.isChecked = false
        binding.checkboxSchool.isChecked = false
        binding.checkboxBus.isChecked = false
        binding.checkboxPark.isChecked = false

        binding.soldStatusDropdown.setText(
            STATUS_ALL,
            false
        )
        binding.minimumPhotos.setText("")
    }
}