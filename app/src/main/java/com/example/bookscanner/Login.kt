package com.example.bookscanner

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.login_activity.*
import java.util.Scanner

class Login : AppCompatActivity() {

    @SuppressLint("WrongViewCast", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        signin.setOnClickListener {
            signin.setTextColor(Color.parseColor("#FFFFFF"))
            signin.setBackgroundColor(Color.parseColor("#FF2729C3"))
            signup.setTextColor(Color.parseColor("#FF2729C3"))
            signup.setBackgroundResource(R.color.white)
            signin_signup_txt.text = "Sign In"
            signin_signup_btn.text = "Sign In"
            forgot_password.visibility = View.VISIBLE
        }

        signup.setOnClickListener {
            signup.setTextColor(Color.parseColor("#FFFFFF"))
            signup.setBackgroundColor(Color.parseColor("#FF2729C3"))
            signin.setTextColor(Color.parseColor("#FF2729C3"))
            signin.setBackgroundResource(R.color.white)
            signin_signup_txt.text = "Sign Up"
            signin_signup_btn.text = "Sign Up"
            forgot_password.visibility = View.INVISIBLE
        }

        title = "ScannerApp"
        val button = findViewById<Button>(R.id.signin_signup_btn)

        button.setOnClickListener{
            val intent = Intent(this, com.example.bookscanner.ScannerActivity ::class.java)
            startActivity(intent)
            finish()
        }
    }
}