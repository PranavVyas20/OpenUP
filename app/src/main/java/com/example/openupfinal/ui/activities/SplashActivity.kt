package com.example.openupfinal.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.open_up.ui.activities.HomeActivity
import com.example.open_up.ui.activities.LoginRegActivity
import com.example.openupfinal.R
import com.example.openupfinal.data.Post
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        Handler().postDelayed({
            var intent1 = Intent(this, LoginRegActivity::class.java)
            val intent2 = Intent(this,HomeActivity::class.java)
            if(auth.currentUser!=null) {
               intent1  = intent2
            }
            startActivity(intent1)
            finish()
        }, 2000) // 3000 is the delayed time in milliseconds.
    }

    }
