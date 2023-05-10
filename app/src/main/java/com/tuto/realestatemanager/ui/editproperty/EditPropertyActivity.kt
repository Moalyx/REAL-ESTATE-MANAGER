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
import com.tuto.realestatemanager.ui.addphoto.AddPhotoActivity
import com.tuto.realestatemanager.ui.addpicturecamera.AddPictureCameraActivity
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

    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private val viewModel by viewModels<EditPropertyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityCreatePropertyBinding.inflate(layoutInflater)
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

        binding.addPictureButton.setOnClickListener {
            startActivity(Intent(this, AddPhotoActivity::class.java))

        }

        binding.takePictureButton.setOnClickListener {
            startActivity(Intent(this, AddPictureCameraActivity::class.java))
        }


        val adapter = EditPropertyPhotoAdapter()
        val recyclerView: RecyclerView = binding.createUpdatePhotoRecyclerview
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

//        viewModel.photoEntity.observe(this) {
//            adapter.submitList(it)
//        }

        viewModel.detailPropertyLiveData.observe(this) {
            binding.typeDropdown.setText(it.type)
            binding.description.setText(it.description, TextView.BufferType.EDITABLE)
            binding.price.setText(it.price.toString())
            binding.surface.setText(it.surface.toString(), TextView.BufferType.EDITABLE).toString()
            binding.rooms.setText(it.room.toString(), TextView.BufferType.EDITABLE)
            binding.bedrooms.setText(it.bedroom.toString(), TextView.BufferType.EDITABLE)
//            binding.bedrooms.setText(it.bedroom.toString(), TextView.BufferType.EDITABLE)
            binding.bathrooms.setText(it.bathroom.toString(), TextView.BufferType.EDITABLE)
            binding.address.setText(it.address, TextView.BufferType.EDITABLE)
            binding.city.setText(it.city, TextView.BufferType.EDITABLE)
            binding.state.setText(it.state, TextView.BufferType.EDITABLE)
            binding.zipcode.setText(it.zipcode.toString())
            binding.country.setText(it.country, TextView.BufferType.EDITABLE)
            binding.date.setText(it.saleSince)
            lat = it.lat
            lng = it.lng
            viewModel.isChecked(binding.checkboxAirport, it.poiAirport)
            viewModel.isChecked(binding.checkboxBus, it.poiBus)
            viewModel.isChecked(binding.checkboxPark, it.poiPark)
            viewModel.isChecked(binding.checkboxSchool, it.poiSchool)
            viewModel.isChecked(binding.checkboxRestaurant, it.poiResto)
            viewModel.isChecked(binding.checkboxtrTrain, it.poiTrain)

            adapter.submitList(it.photoList) //todo momentanement commenté pour trouver une solution

            binding.saveButton.setOnClickListener {
                viewModel.updateProperty(
                    propertyId,
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
                    binding.agent.text.toString(),
                    binding.checkboxSaleStatus.isChecked,
                    binding.checkboxtrTrain.isChecked,
                    binding.date.text.toString(),
                    binding.checkboxAirport.isChecked,
                    binding.checkboxRestaurant.isChecked,
                    binding.checkboxSchool.isChecked,
                    binding.checkboxBus.isChecked,
                    binding.checkboxPark.isChecked
                )
                finish()
            }
        }
    }
}