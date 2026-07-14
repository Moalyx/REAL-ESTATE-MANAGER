package com.tuto.realestatemanager.ui.mortgagecalculator

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.tuto.realestatemanager.databinding.ActivityMortgageCalculatorBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MortgageCalculatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMortgageCalculatorBinding

    private val viewModel by viewModels<MortgageCalculatorViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMortgageCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()
        setListeners()
        observeViewModel()
        configureBackNavigation()
    }

    private fun setListeners() {
        binding.housePrice.doAfterTextChanged { editable ->
            viewModel.setHousePrice(editable.toString())
        }

        binding.downPayment.doAfterTextChanged { editable ->
            viewModel.setDownPayment(editable.toString())
        }

        binding.rate.doAfterTextChanged { editable ->
            viewModel.setRate(editable.toString())
        }

        binding.duration.doAfterTextChanged { editable ->
            viewModel.setDuration(editable.toString())
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.loanAmount.collect { loanAmount ->
                        binding.loanAmount.text = loanAmount
                    }
                }

                launch {
                    viewModel.monthlyPayment.collect { monthlyPayment ->
                        binding.monthlyPayment.text = monthlyPayment
                    }
                }

                launch {
                    viewModel.viewAction.collect { action ->
                        when (action) {
                            MortgageViewAction.NavigateToMainActivity -> {
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun configureBackNavigation() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {
                    viewModel.onNavigateToMainActivity()
                }
            }
        )
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            viewModel.onNavigateToMainActivity()
        }
    }
}