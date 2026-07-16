package com.tuto.realestatemanager.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.ActivityMainBinding
import com.tuto.realestatemanager.ui.detail.DetailsPropertyFragment
import com.tuto.realestatemanager.ui.list.PropertyListFragment
import com.tuto.realestatemanager.ui.map.MapFragment
import com.tuto.realestatemanager.ui.mortgagecalculator.MortgageCalculatorActivity
import com.tuto.realestatemanager.ui.search.SearchPropertyActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel>()

    private var currencyMenuItem: MenuItem? = null

    private var tabletCreatePropertyFab:
            FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        tabletCreatePropertyFab =
            findViewById(R.id.create_property_tablet)

        configureTabletFab()

        setSupportActionBar(binding.toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setDisplayShowTitleEnabled(false)
        }

        configureBottomNavigation()

        if (savedInstanceState == null) {
            displayPropertyList()
        } else {
            binding.root.post {
                updateTabletFabVisibility()
            }
        }

        configureMasterDetail()

        observeViewModel()
    }

    private fun configureTabletFab() {
        tabletCreatePropertyFab?.setOnClickListener {
            val propertyListFragment =
                supportFragmentManager.findFragmentById(
                    binding.mainContainerList.id
                ) as? PropertyListFragment

            propertyListFragment
                ?.onCreatePropertyClicked()
        }
    }

    private fun configureBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener {
                menuItem ->

            when (menuItem.itemId) {
                R.id.map -> {
                    tabletCreatePropertyFab?.hide()

                    supportFragmentManager
                        .beginTransaction()
                        .replace(
                            binding.mainContainerList.id,
                            MapFragment()
                        )
                        .commit()
                }

                R.id.list -> {
                    displayPropertyList()
                }
            }

            true
        }
    }

    private fun displayPropertyList() {
        tabletCreatePropertyFab?.hide()

        supportFragmentManager
            .beginTransaction()
            .replace(
                binding.mainContainerList.id,
                PropertyListFragment()
            )
            .runOnCommit {
                tabletCreatePropertyFab?.show()
            }
            .commit()
    }

    private fun updateTabletFabVisibility() {
        val currentFragment =
            supportFragmentManager.findFragmentById(
                binding.mainContainerList.id
            )

        if (currentFragment is PropertyListFragment) {
            tabletCreatePropertyFab?.show()
        } else {
            tabletCreatePropertyFab?.hide()
        }
    }

    private fun configureMasterDetail() {
        val detailContainer = binding.mainContainerDetail

        if (detailContainer != null) {
            if (
                supportFragmentManager.findFragmentByTag(
                    DETAIL_FRAGMENT_TAG
                ) == null
            ) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(
                        detailContainer.id,
                        DetailsPropertyFragment(),
                        DETAIL_FRAGMENT_TAG
                    )
                    .commit()
            }
        } else {
            val detailFragment =
                supportFragmentManager.findFragmentByTag(
                    DETAIL_FRAGMENT_TAG
                ) ?: supportFragmentManager.fragments.find {
                    it is DetailsPropertyFragment
                }

            if (detailFragment != null) {
                supportFragmentManager
                    .beginTransaction()
                    .remove(detailFragment)
                    .commitNow()

                invalidateOptionsMenu()
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                launch {
                    viewModel.viewAction.collect { action ->
                        when (action) {
                            MainViewAction.NavigateToSearch -> {
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        SearchPropertyActivity::class.java
                                    )
                                )
                            }

                            MainViewAction.NavigateToDetailActivity -> Unit
                        }
                    }
                }

                launch {
                    viewModel.iconStatus.collect { isDollar ->
                        updateCurrencyIcon(isDollar)
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(
        menu: Menu?
    ): Boolean {
        menu ?: return false

        menuInflater.inflate(
            R.menu.edit_property_menu,
            menu
        )

        currencyMenuItem =
            menu.findItem(R.id.currency)

        updateCurrencyIcon(
            viewModel.iconStatus.value
        )

        return true
    }

    private fun updateCurrencyIcon(
        isDollar: Boolean
    ) {
        currencyMenuItem?.setIcon(
            if (isDollar) {
                R.drawable.euro
            } else {
                R.drawable.dollar
            }
        )
    }

    override fun onOptionsItemSelected(
        item: MenuItem
    ): Boolean {
        return when (item.itemId) {
            R.id.bank_loan -> {
                startActivity(
                    Intent(
                        this,
                        MortgageCalculatorActivity::class.java
                    )
                )

                true
            }

            R.id.search_property -> {
                viewModel.navigateToSearch()
                true
            }

            R.id.currency -> {
                viewModel.converterPrice()
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onDestroy() {
        currencyMenuItem = null
        tabletCreatePropertyFab = null

        super.onDestroy()
    }

    private companion object {
        private const val DETAIL_FRAGMENT_TAG =
            "detail_fragment"
    }
}