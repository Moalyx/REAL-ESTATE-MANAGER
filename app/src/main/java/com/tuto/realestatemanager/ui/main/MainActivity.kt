package com.tuto.realestatemanager.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.ActivityMainBinding
import com.tuto.realestatemanager.ui.detail.DetailActivity
import com.tuto.realestatemanager.ui.detail.DetailsPropertyFragment
import com.tuto.realestatemanager.ui.list.PropertyListFragment
import com.tuto.realestatemanager.ui.map.MapFragment
import com.tuto.realestatemanager.ui.mortgagecalcultator.MortgageCalculatorActivity
import com.tuto.realestatemanager.ui.search.SearchPropertyActivity
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

        viewmodel.navigateSingleLiveEvent.observe(this) { //todo a verifier ici
            when (it) {
//                MainViewAction.NavigateToDetailActivity -> startActivity(DetailActivity.navigate(
//                    this))

                MainViewAction.NavigateToSearch -> startActivity(Intent(this,
                    SearchPropertyActivity::class.java))
                else -> {}
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
        updateMenuIcon(menu!!.findItem(R.id.currency))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.bank_loan -> startActivity(Intent(this, MortgageCalculatorActivity::class.java))

            R.id.search_property -> viewmodel.navigateToSearch()

            R.id.currency -> {
                updateMenuIcon(item)
                viewmodel.converterPrice()
            }
        }
        return true
    }

    private fun updateMenuIcon(item: MenuItem) {
        viewmodel.iconStatus.observe(this) {
            if (it) item.setIcon(R.drawable.dollar) else item.setIcon(R.drawable.euro)
        }
    }

}