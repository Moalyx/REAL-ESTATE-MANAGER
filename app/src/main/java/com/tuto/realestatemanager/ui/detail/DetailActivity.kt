package com.tuto.realestatemanager.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tuto.realestatemanager.ui.list.PropertyListFragment
import com.tuto.realestatemanager.databinding.ActivityDetailBinding
import com.tuto.realestatemanager.databinding.FragmentDetailsPropertyBinding
import com.tuto.realestatemanager.databinding.FragmentPropertyListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(binding.detailContainer.id, DetailsPropertyFragment())
        }

    }
}