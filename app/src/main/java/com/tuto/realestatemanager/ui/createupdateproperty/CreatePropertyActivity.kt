package com.tuto.realestatemanager.ui.createupdateproperty

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.ActivityCreatePropertyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePropertyActivity : AppCompatActivity() {

    companion object{
        val KEY_PROPERTY_ID = "KEY_PROPERTY_ID"
        fun navigate(context: Context, propertyId: Long): Intent{
            val intent = Intent(context, CreatePropertyActivity::class.java)
            intent.putExtra(KEY_PROPERTY_ID, propertyId)
            return intent
        }
    }

    private lateinit var binding: ActivityCreatePropertyBinding
    private val viewModel by viewModels<CreateUpdatePropertyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_create_property)


        val binding = ActivityCreatePropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val types = arrayOf("House", "Penthouse", "Duplex", "Loft", "Flat" )
        val dropdownAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, types )
        binding.typeDropdown.setAdapter(dropdownAdapter)
        binding.typeDropdown.threshold

        //binding.typeDropdown.getSelectedItem().toString()

        binding.typeDropdown.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                viewModel.onTypeSelected(parent.getItemAtPosition(position).toString())
            }
        binding.typeDropdown.inputType = InputType.TYPE_NULL

        binding.addPictureButton.setOnClickListener(){
            Intent(Intent.ACTION_PICK).data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        //viewModel.onTypeSelected(binding.typeDropdown.text.toString())
//        try {
//        viewModel.onPriceChanged(Integer.parseInt(binding.price.text.toString()))
//        }catch (e:NumberFormatException) {
//            println("not a number");
//        }
        //viewModel.onCountyChanged(binding.country.text.toString())
//        try {
//        viewModel.onSurfaceChanged(Integer.parseInt(binding.surface.text.toString()))
//        }catch (e:NumberFormatException) {
//            println("not a number");
//        }


        binding.saveButton.setOnClickListener(){
            viewModel.createProperty(
                "PENTHOUSE",
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
                photoUrl = "https://pic.le-cdn.com/thumbs/1024x768/04/8/properties/Property-b2660000000001e2000857b5fd0a-31614642.jpg"
            )
            finish()

        }

    }


//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun configureDatePicker(){
//        val c = Calendar.getInstance()
//        val year = c.get(Calendar.YEAR)
//        val month = c.get(Calendar.MONTH)
//        val day = c.get(Calendar.DAY_OF_MONTH)
//        LocalDate.now()
//
//
//        binding.date.setOnClickListener() {
//            DatePickerDialog(this, this, year, month, day).show()
//        }
//
    

}