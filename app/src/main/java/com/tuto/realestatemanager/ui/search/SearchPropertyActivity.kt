package com.tuto.realestatemanager.ui.search

import android.os.Bundle
import android.text.InputType
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.R
import androidx.appcompat.app.AppCompatActivity
import com.tuto.realestatemanager.databinding.ActivitySearchPropertyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchPropertyActivity : AppCompatActivity() {

    private val viewModel by viewModels<SearchPropertyViewModel>()
    private lateinit var binding: ActivitySearchPropertyBinding

    private var type: String? = null
    private var soldStatus: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchPropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()
        setDropdownMenus()
        setUserParameters()
        observeParameters()
        observeNavigation()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setDropdownMenus() {
        val types = arrayOf("House", "Penthouse", "Duplex", "Loft", "Flat")
        val typeDropdownAdapter = ArrayAdapter(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            types
        )

        binding.typeDropdown.setAdapter(typeDropdownAdapter)
        binding.typeDropdown.inputType = InputType.TYPE_NULL

        val statuses = arrayOf("All", "Available", "Sold")
        val statusDropdownAdapter = ArrayAdapter(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            statuses
        )

        binding.soldStatusDropdown.setAdapter(statusDropdownAdapter)
        binding.soldStatusDropdown.inputType = InputType.TYPE_NULL
        binding.soldStatusDropdown.setText("All", false)
    }

    private fun setUserParameters() {
        binding.typeDropdown.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                type = parent.getItemAtPosition(position).toString()
            }

        binding.soldStatusDropdown.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                soldStatus = when (parent.getItemAtPosition(position).toString()) {
                    "Sold" -> true
                    "Available" -> false
                    else -> null
                }
            }

        binding.validateParametersButton.setOnClickListener {
            val minimumPhotos = binding.minimumPhotos.text
                ?.toString()
                ?.toIntOrNull()

            viewModel.setParameters(
                type = type,
                priceMinimum = binding.priceMin.text.toString(),
                priceMaximum = binding.priceMax.text.toString(),
                surfaceMinimum = binding.surfaceMinimum.text.toString(),
                surfaceMaximum = binding.surfaceMaximum.text.toString(),
                city = binding.city.text.toString(),
                poiTrain = binding.checkboxTrain.isChecked,
                poiAirport = binding.checkboxAirport.isChecked,
                poiResto = binding.checkboxRestaurant.isChecked,
                poiSchool = binding.checkboxSchool.isChecked,
                poiBus = binding.checkboxBus.isChecked,
                poiPark = binding.checkboxPark.isChecked,
                soldStatus = soldStatus,
                minimumPhotos = minimumPhotos
            )

            viewModel.onNavigateToMainActivity()
        }

        binding.clearParametersButton.setOnClickListener {
            clearFields()
            viewModel.clearParameters()
            viewModel.onNavigateToMainActivity()
        }
    }

    private fun observeParameters() {
        viewModel.getParametersLiveData().observe(this) { parameters ->
            parameters ?: return@observe

            type = parameters.type
            soldStatus = parameters.soldStatus

            binding.typeDropdown.setText(parameters.type.orEmpty(), false)
            binding.priceMin.setText(parameters.priceMinimum?.toString().orEmpty())
            binding.priceMax.setText(parameters.priceMaximum?.toString().orEmpty())
            binding.surfaceMinimum.setText(parameters.surfaceMinimum?.toString().orEmpty())
            binding.surfaceMaximum.setText(parameters.surfaceMaximum?.toString().orEmpty())
            binding.city.setText(parameters.city.orEmpty())

            binding.checkboxTrain.isChecked = parameters.poiTrain
            binding.checkboxAirport.isChecked = parameters.poiAirport
            binding.checkboxRestaurant.isChecked = parameters.poiResto
            binding.checkboxSchool.isChecked = parameters.poiSchool
            binding.checkboxBus.isChecked = parameters.poiBus
            binding.checkboxPark.isChecked = parameters.poiPark

            binding.soldStatusDropdown.setText(
                when (parameters.soldStatus) {
                    true -> "Sold"
                    false -> "Available"
                    null -> "All"
                },
                false
            )

            binding.minimumPhotos.setText(
                parameters.minimumPhotos?.toString().orEmpty()
            )
        }
    }

    private fun observeNavigation() {
        viewModel.navigateSingleLiveEvent.observe(this) {
            finish()
        }
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

        binding.soldStatusDropdown.setText("All", false)
        binding.minimumPhotos.setText("")
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.onNavigateToMainActivity()
    }
}