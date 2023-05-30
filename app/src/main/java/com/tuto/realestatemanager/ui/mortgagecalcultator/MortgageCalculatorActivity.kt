package com.tuto.realestatemanager.ui.mortgagecalcultator

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.tuto.realestatemanager.databinding.ActivityMortgageCalculatorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MortgageCalculatorActivity : AppCompatActivity() {

    private val viewModel by viewModels<MortgageCalculatorViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_mortgage_calculator)

        val binding = ActivityMortgageCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.duration.doAfterTextChanged {
            if (it.toString() == "") {
                viewModel.setDuration(0)
            } else {
                viewModel.setDuration(it.toString().toInt())
            }
        }

        binding.rate.doAfterTextChanged {
//            if (it.toString() == "") {
//                viewModel.setRate(0.0) //todo algo reporter dans le viewmodel
//            }else{
            viewModel.setRate(it.toString())
//            }
        }

        binding.totalAmount.doAfterTextChanged {
            if (it.toString() == "") {
                viewModel.setAmount(0.0)
            } else
                viewModel.setAmount(it.toString().toDouble())
        }

        viewModel.getMonthlyPayment.observe(this) {
            binding.mensualitPayement.text = it.toString()
        }
    }
}