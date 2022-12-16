package com.tuto.realestatemanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.tuto.realestatemanager.databinding.ActivityCreatePropertyBinding

class CreatePropertyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_create_property)

        val binding = ActivityCreatePropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val types = arrayOf("House", "Penthouse", "Duplex", "Loft", "Flat" )
        val dropdowAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, types )
        binding.typeDropdown.setAdapter(dropdowAdapter)



    }
}