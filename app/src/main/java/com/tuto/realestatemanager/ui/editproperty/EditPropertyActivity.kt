package com.tuto.realestatemanager.ui.editproperty

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.R
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuto.realestatemanager.databinding.ActivityCreatePropertyBinding
import com.tuto.realestatemanager.databinding.ActivityEditPropertyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPropertyActivity : AppCompatActivity() {

    companion object {
        const val KEY_PROPERTY_ID = "KEY_PROPERTY_ID"
        fun navigate(context: Context, propertyId: Long): Intent {
            val intent = Intent(context, EditPropertyActivity::class.java)
            intent.putExtra(KEY_PROPERTY_ID, propertyId)
            return intent
        }
    }

    private val viewModel by viewModels<EditPropertyViewModel>()
    private lateinit var binding: ActivityEditPropertyBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityEditPropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var type = ""

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

        viewModel.detailPropertyLiveData.observe(this) {
            binding.typeDropdown.setText(it.type, TextView.BufferType.EDITABLE)
            binding.description.setText(it.description, TextView.BufferType.EDITABLE)
            binding.price.setText(it.price.toString(), TextView.BufferType.EDITABLE)
            binding.surface.setText(it.surface.toString(), TextView.BufferType.EDITABLE).toString()
            binding.rooms.setText(it.room.toString(), TextView.BufferType.EDITABLE)
            binding.bedrooms.setText(it.bedroom.toString(), TextView.BufferType.EDITABLE)
            binding.bedrooms.setText(it.bedroom.toString(), TextView.BufferType.EDITABLE)
            binding.bathrooms.setText(it.bathroom.toString(), TextView.BufferType.EDITABLE)
            binding.country.setText(it.county, TextView.BufferType.EDITABLE)
//            if (it.poiAirport) {
//                binding.checkboxAirport.isChecked = true
//            }
            viewModel.isChecked(binding.checkboxAirport, it.poiAirport)
            viewModel.isChecked(binding.checkboxBus, it.poiBus)
            viewModel.isChecked(binding.checkboxPark, it.poiPark)
            viewModel.isChecked(binding.checkboxSchool, it.poiSchool)
            viewModel.isChecked(binding.checkboxRestaurant, it.poiResto)
            viewModel.isChecked(binding.checkboxtrTrain, it.poiTrain)

            val adapter = EditPropertyPhotoAdapter()
            val recyclerView: RecyclerView = binding.createUpdatePhotoRecyclerview
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)
            adapter.submitList(it.photoList)

            binding.saveButton.setOnClickListener() {
                viewModel.updateProperty(
                    propertyId,
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
                    binding.checkboxPark.isChecked,
                    photoUri = "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg"
                )
                finish()
            }




        }



    }
}