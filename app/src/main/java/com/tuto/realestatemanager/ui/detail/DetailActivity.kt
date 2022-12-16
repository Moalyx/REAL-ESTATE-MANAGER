package com.tuto.realestatemanager.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.tuto.realestatemanager.CreatePropertyActivity
import com.tuto.realestatemanager.R
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.edit_property_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.edit_property2 -> startActivity(Intent(this, CreatePropertyActivity::class.java))
        }
        return true
    }

}