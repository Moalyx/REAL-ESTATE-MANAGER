package com.tuto.realestatemanager.ui.mortgagecalcultator

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.tuto.realestatemanager.databinding.ActivityMortgageCalculatorBinding
import com.tuto.realestatemanager.databinding.ActivitySearchPropertyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MortgageCalculatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMortgageCalculatorBinding
    private val viewModel by viewModels<MortgageCalculatorViewModel>()


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMortgageCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()

        binding.duration.doAfterTextChanged {
            viewModel.setDuration(it.toString())
        }

        binding.rate.doAfterTextChanged {
            viewModel.setRate(it.toString())
        }

        binding.totalAmount.doAfterTextChanged {
            viewModel.setAmount(it.toString())
        }

        viewModel.getMonthlyPayment.observe(this) {
            binding.mensualitPayement.text = it
        }

        viewModel.navigateSingleLiveEvent.observe(this) {
            finish()
        }


    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.onNavigateToMainActivity()
    }
}