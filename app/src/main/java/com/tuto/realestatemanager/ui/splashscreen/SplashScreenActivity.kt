package com.tuto.realestatemanager.ui.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tuto.realestatemanager.databinding.ActivitySplashScreenBinding
import com.tuto.realestatemanager.ui.main.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logo.alpha = 0.0f

        //binding.logo.animate().rotation(1.5f)

        binding.logo.animate().setDuration(500).alpha(1f).rotation(360.0f).scaleX(2.0f).scaleY(2.0f).withEndAction{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}