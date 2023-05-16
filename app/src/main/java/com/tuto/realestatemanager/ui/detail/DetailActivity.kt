package com.tuto.realestatemanager.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tuto.realestatemanager.databinding.ActivityDetailBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.detailToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.detailContainer.id, DetailsPropertyFragment())
                .commit()
        }
    }
}