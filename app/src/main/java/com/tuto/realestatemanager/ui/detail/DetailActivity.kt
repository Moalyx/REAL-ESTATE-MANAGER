package com.tuto.realestatemanager.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tuto.realestatemanager.databinding.ActivityDetailBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

//    companion object {
//        const val KEY_PROPERTY_ID = "KEY_PROPERTY_ID"
//        fun navigate(context: Context, propertyId: Long): Intent {
//            val intent = Intent(context, DetailsPropertyFragment::class.java)
//            intent.putExtra(KEY_PROPERTY_ID, propertyId)
//            return intent
//        }
//    }

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