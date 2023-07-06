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
    private var type: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchPropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUserParameters()
        setToolbar()
        setDropdownMenu()


        viewModel.getParametersLiveData().observe(this) {
            //todo rien a observer ici
        }

        viewModel.navigateSingleLiveEvent.observe(this) {
            //startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.onNavigateToMainActivity()
    }

    private fun setDropdownMenu() {
        val types = arrayOf("House", "Penthouse", "Duplex", "Loft", "Flat")
        val dropdownAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            R.layout.support_simple_spinner_dropdown_item, types
        )
        binding.typeDropdown.setAdapter(dropdownAdapter)
        binding.typeDropdown.threshold
        binding.typeDropdown.inputType = InputType.TYPE_NULL
    }

    private fun setUserParameters() {
        binding.typeDropdown.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                type = parent.getItemAtPosition(position).toString()
            }

        binding.validateParametersButton.setOnClickListener {

            viewModel.setParameters(
                type,
                binding.priceMin.text.toString(),
                binding.priceMax.text.toString(),
                binding.surfaceMinimum.text.toString(),
                binding.surfaceMaximum.text.toString(),
                binding.city.text.toString(),
                binding.checkboxtrTrain.isChecked,
                binding.checkboxAirport.isChecked,
                binding.checkboxRestaurant.isChecked,
                binding.checkboxSchool.isChecked,
                binding.checkboxBus.isChecked,
                binding.checkboxPark.isChecked

            )
            viewModel.onNavigateToMainActivity()
        }
    }
}