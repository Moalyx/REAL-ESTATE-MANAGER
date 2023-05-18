package com.tuto.realestatemanager.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.R
import androidx.appcompat.app.AppCompatActivity
import com.tuto.realestatemanager.databinding.ActivitySearchPropertyBinding
import com.tuto.realestatemanager.model.SearchParameters
import com.tuto.realestatemanager.ui.main.MainActivity
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
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
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
                SearchParameters(
                    type,
                    Integer.parseInt(binding.priceMin.text.toString()),
                    Integer.parseInt(binding.priceMax.text.toString()),
                    Integer.parseInt(binding.surfaceMinimum.text.toString()),
                    Integer.parseInt(binding.surfaceMaximum.text.toString()),
                    binding.city.text.toString(),
                    binding.checkboxtrTrain.isChecked,
                    binding.checkboxAirport.isChecked,
                    binding.checkboxRestaurant.isChecked,
                    binding.checkboxSchool.isChecked,
                    binding.checkboxBus.isChecked,
                    binding.checkboxPark.isChecked
                )
            )
            startActivity(Intent(this, MainActivity::class.java))
            //onBackPressed()
        }
    }
}