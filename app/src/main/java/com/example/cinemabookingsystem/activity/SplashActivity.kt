package com.example.cinemabookingsystem.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.cinemabookingsystem.constant.GlobalFunction
import com.example.cinemabookingsystem.databinding.ActivitySplashBinding
import com.example.cinemabookingsystem.prefs.DataStoreManager
import com.example.cinemabookingsystem.util.StringUtil

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activitySplashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(activitySplashBinding.root)
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ goToNextActivity() }, 2000)
    }

    private fun goToNextActivity() {
        if (DataStoreManager.getUser() != null && !StringUtil.isEmpty(DataStoreManager.getUser()?.email)) {
            GlobalFunction.gotoMainActivity(this)
        } else {
            GlobalFunction.startActivity(this, SignInActivity::class.java)
        }
        finish()
    }
}