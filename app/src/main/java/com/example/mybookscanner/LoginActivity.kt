package com.example.mybookscanner

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.lang.StringBuilder
import java.net.InetAddress
import java.net.Socket

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.decorView.setOnTouchListener(object:OnSwipeTouchListener(this@LoginActivity){
            override fun onSwipeLeft() {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        })

        val shared_preferences = this.getSharedPreferences("data", Context.MODE_PRIVATE)
        var token = shared_preferences.getString("token", MainApplication.Companion.token)

        if(!(token.isNullOrEmpty())){
            val intent = Intent(this@LoginActivity, BarcodeScannerActivity::class.java)
            startActivity(intent)

            finish()
        }

        val bt = findViewById<Button>(R.id.btn_login)
        bt.setOnClickListener{

            var user_email = findViewById<EditText>(R.id.et_email).text.toString()
            var user_pw= findViewById<EditText>(R.id.et_password).text.toString()
            val address = InetAddress.getByName("ableytner.ddns.net")


            val salt_client = Socket(address.hostAddress, 20002)
            val salt_output = PrintWriter(salt_client.getOutputStream(), true)
            val salt_input = BufferedReader(InputStreamReader(salt_client.getInputStream()))


            var salt_data = mapOf(
                "email" to user_email
            )

            var salt_request = mapOf (
                "request" to "GET",
                "type" to "salt",
                "data" to salt_data
            )

            var salt_request_data = Klaxon().toJsonString(salt_request).replace("\\", "")

            salt_output.println(salt_request_data)

            Thread.sleep(100)
            var salt_return_data = salt_input.readLine()
            var salt_return_json = Parser.default().parse(StringBuilder(salt_return_data)) as JsonObject

            var salt = salt_return_json.obj("data")?.string("salt").toString().toByteArray()
            var hashed_pw = PasswordUtils.hash(user_pw, salt)


            val login_client = Socket(address.hostAddress, 20002)
            val login_output = PrintWriter(login_client.getOutputStream(), true)
            val login_input = BufferedReader(InputStreamReader(login_client.getInputStream()))


            var login_auth = mapOf(
                "type" to "password",
                "email" to user_email,
                "pw_hash" to hashed_pw.toString()
            )

            var login_request = mapOf (
                "request" to "GET",
                "type" to "token",
                "auth" to login_auth
            )

            var login_request_data = Klaxon().toJsonString(login_request).replace("\\", "")

            login_output.println(login_request_data)

            Thread.sleep(100)
            var login_return_data = login_input.readLine()
            var login_return_json = Parser.default().parse(StringBuilder(login_return_data)) as JsonObject


            assert(!(login_return_json["error"] as Boolean))
            MainApplication.Companion.token = login_return_json.obj("data")?.string("token").toString()

            val shared_preferences = this.getSharedPreferences("data", Context.MODE_PRIVATE)
            val editor = shared_preferences.edit()
            editor.putString("token", MainApplication.Companion.token)
            editor.apply()

            println(MainApplication.Companion.token)


            val intent = Intent(this@LoginActivity, BarcodeScannerActivity::class.java)
            startActivity(intent)

            finish()
        }

        var policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)
    }
}