package com.saarthak.proteams.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import com.saarthak.proteams.MainActivity
import com.saarthak.proteams.R
import com.saarthak.proteams.util.FireStoreClass

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // to go to intro act after few seconds
        Handler().postDelayed({
            val curUserId = FireStoreClass().getCurUserId()
            Log.d("id", "onCreate: $curUserId")

            // if curUser != null => redirect to main act instead of intro act
            if (curUserId.isEmpty()) startActivity(Intent(this, IntroActivity::class.java))
            else startActivity(Intent(this, MainActivity::class.java))

            finish()
        }, 2500)
        
    }
}