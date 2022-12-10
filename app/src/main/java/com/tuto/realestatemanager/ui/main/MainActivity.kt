package com.tuto.realestatemanager.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.replace
import android.widget.FrameLayout
import androidx.activity.viewModels
import com.tuto.realestatemanager.CreatePropertyActivity
import com.tuto.realestatemanager.ui.list.PropertyListFragment
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.ActivityMainBinding
import com.tuto.realestatemanager.ui.detail.DetailActivity
import com.tuto.realestatemanager.ui.detail.DetailsPropertyFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewmodel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createActivty?.setOnClickListener(){
            startActivity(Intent(this, CreatePropertyActivity::class.java))
        }

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(binding.mainContainerList.id, PropertyListFragment())
                .commit()
        }

        if(binding.mainContainerDetail != null && supportFragmentManager.findFragmentById(binding.mainContainerDetail.id) == null){
            supportFragmentManager.beginTransaction()
                .add(
                    binding.mainContainerDetail.id,
                    DetailsPropertyFragment()
                )
                .commit()
        }

        viewmodel.navigateSingleLiveEvent.observe(this){
            when(it){
                MainViewAction.NavigateToDetailActivity -> startActivity(Intent(this, DetailActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewmodel.onConfigurationChanged(resources.getBoolean(R.bool.isTablet))
    }
}