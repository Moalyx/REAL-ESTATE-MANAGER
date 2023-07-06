package com.tuto.realestatemanager.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tuto.realestatemanager.databinding.ActivityDetailBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private val viewmodel by viewModels<DetailViewModel>()

    companion object {
        fun navigate(context: Context) = Intent(context, DetailActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.detailToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        binding.detailToolbar.setNavigationOnClickListener {
            onBackPressed()
        }


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.detailContainer.id, DetailsPropertyFragment())
                .commit()
        }

        viewmodel.navigateSingleLiveEvent.observe(this){
            when(it) {
                DetailViewAction.NavigateToMainActivity -> finish()
                else -> {}
            }
        }
    }


    override fun onBackPressed() { //todo voir si on ajoute ici plutot la fleche de retour dans la actionbar
        super.onBackPressed()
        viewmodel.onNavigateToMainActivity()
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val inflater = menuInflater
//        inflater.inflate(R.menu.fragment_detail_menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//           R.id.edit_property -> viewmodel.onNavigateToEditActivity()
//        }
//        return true
//    }



}