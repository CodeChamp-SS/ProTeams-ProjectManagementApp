package com.saarthak.proteams.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.saarthak.proteams.R
import com.saarthak.proteams.SigninActivity
import com.saarthak.proteams.SignupActivity
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        signupB.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        signinB.setOnClickListener {
            startActivity(Intent(this, SigninActivity::class.java))
        }

    }
}