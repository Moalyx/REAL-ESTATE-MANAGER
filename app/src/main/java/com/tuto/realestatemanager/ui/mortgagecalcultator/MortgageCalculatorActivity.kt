package com.tuto.realestatemanager.ui.mortgagecalcultator

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.tuto.realestatemanager.databinding.ActivityMortgageCalculatorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MortgageCalculatorActivity : AppCompatActivity() {

    private val viewModel by viewModels<MortgageCalculatorViewModel>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMortgageCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    }
}