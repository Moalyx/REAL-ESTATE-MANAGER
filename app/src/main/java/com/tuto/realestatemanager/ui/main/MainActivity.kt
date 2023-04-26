package com.tuto.realestatemanager.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.tuto.realestatemanager.ui.createproperty.CreatePropertyActivity
import com.tuto.realestatemanager.ui.list.PropertyListFragment
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.ActivityMainBinding
import com.tuto.realestatemanager.ui.detail.DetailActivity
import com.tuto.realestatemanager.ui.detail.DetailsPropertyFragment
import com.tuto.realestatemanager.ui.editproperty.EditPropertyActivity
import com.tuto.realestatemanager.ui.editproperty.EditPropertyActivity.Companion.navigate
import com.tuto.realestatemanager.ui.map.MapFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewmodel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {

                R.id.map -> supportFragmentManager.beginTransaction()
                    .replace(binding.mainContainerList.id, MapFragment()).commit()

                R.id.list -> supportFragmentManager.beginTransaction()
                    .replace(binding.mainContainerList.id, PropertyListFragment()).commit()

            }
            true
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.mainContainerList.id, PropertyListFragment())
                .commit()
        }

        if (binding.mainContainerDetail != null && supportFragmentManager.findFragmentById(binding.mainContainerDetail.id) == null) {
            supportFragmentManager.beginTransaction()
                .add(
                    binding.mainContainerDetail.id,
                    DetailsPropertyFragment()
                )
                .commit()

            binding.bottomNav.setOnItemSelectedListener {
                when (it.itemId) {

                    R.id.map -> supportFragmentManager.beginTransaction()
                        .replace(binding.mainContainerList.id, MapFragment()).commit()

                    R.id.list -> supportFragmentManager.beginTransaction()
                        .replace(binding.mainContainerList.id, PropertyListFragment()).commit()

                }
                true
            }
        }

        viewmodel.navigateSingleLiveEvent.observe(this) {
            when (it) {
                MainViewAction.NavigateToDetailActivity -> startActivity(
                    Intent(
                        this,
                        DetailActivity::class.java
                    )
                )
            }
        }



    }

    override fun onResume() {
        super.onResume()
        viewmodel.onConfigurationChanged(resources.getBoolean(R.bool.isTablet))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.edit_property_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_property -> startActivity(navigate(this, 1))
        }
        return true
    }

}