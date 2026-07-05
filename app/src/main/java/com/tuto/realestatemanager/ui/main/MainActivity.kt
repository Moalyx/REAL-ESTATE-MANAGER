package com.tuto.realestatemanager.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.ActivityMainBinding
import com.tuto.realestatemanager.ui.detail.DetailsPropertyFragment
import com.tuto.realestatemanager.ui.list.PropertyListFragment
import com.tuto.realestatemanager.ui.map.MapFragment
import com.tuto.realestatemanager.ui.mortgagecalculator.MortgageCalculatorActivity
import com.tuto.realestatemanager.ui.search.SearchPropertyActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewmodel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.getBooleanExtra("property_created", false)) {
            Snackbar.make(
                binding.root,
                "Property successfully added",
                Snackbar.LENGTH_LONG
            ).show()
            intent.removeExtra("property_created")
        }

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

        if (binding.mainContainerDetail != null) {
            if (supportFragmentManager.findFragmentByTag("detail_fragment") == null) {
                supportFragmentManager.beginTransaction()
                    .replace(
                        binding.mainContainerDetail.id,
                        DetailsPropertyFragment(),
                        "detail_fragment"
                    )
                    .commit()
            }
        } else {
            val fragment = supportFragmentManager.findFragmentByTag("detail_fragment")
                ?: supportFragmentManager.fragments.find { it is DetailsPropertyFragment }

            if (fragment != null) {
                supportFragmentManager.beginTransaction().remove(fragment).commitNow()
                invalidateOptionsMenu()
            }
        }

        viewmodel.navigateSingleLiveEvent.observe(this) {
            when (it) {
                MainViewAction.NavigateToSearch -> startActivity(
                    Intent(
                        this,
                        SearchPropertyActivity::class.java
                    )
                )

                else -> {}
            }
        }

    }

    override fun onResume() {
        super.onResume()
        viewmodel.onConfigurationChanged(resources.getBoolean(R.bool.isTablet))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_property_menu, menu)

        val currencyItem = menu!!.findItem(R.id.currency)

        viewmodel.iconStatus.observe(this) { isDollar ->
            if (isDollar) {
                currencyItem.setIcon(R.drawable.euro)
            } else {
                currencyItem.setIcon(R.drawable.dollar)
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.bank_loan -> {
                startActivity(Intent(this, MortgageCalculatorActivity::class.java))
                true
            }

            R.id.search_property -> {
                viewmodel.navigateToSearch()
                true
            }

            R.id.currency -> {
                viewmodel.converterPrice()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}