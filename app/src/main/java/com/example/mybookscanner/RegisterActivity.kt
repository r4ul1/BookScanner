package com.example.mybookscanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import com.beust.klaxon.json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.lang.StringBuilder
import java.net.InetAddress
import java.net.Socket


class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        window.decorView.setOnTouchListener(object : OnSwipeTouchListener(this@RegisterActivity) {
            override fun onSwipeRight() {
                finish()
            }
        })

        val bt = findViewById<Button>(R.id.btn_register)
        bt.setOnClickListener{
            var user_email = findViewById<EditText>(R.id.et_email).text.toString()
            var user_pw= findViewById<EditText>(R.id.et_password).text.toString()
            var salt = PasswordUtils.generateSalt()
            var hashed_pw = PasswordUtils.hash(user_pw, salt)


            val data = mapOf(
                "email" to user_email,
                "pw_hash" to hashed_pw.toString(),
                "salt" to salt.toString()
            )

            val request = mapOf (
                "request" to "PUT",
                "type" to "user",
                "data" to data
            )

            val request_data = Klaxon().toJsonString(request).replace("\\", "")

            var address = InetAddress.getByName("ableytner.ddns.net")
            val client = Socket(address.hostAddress, 20002)
            val output = PrintWriter(client.getOutputStream(), true)
            val input = BufferedReader(InputStreamReader(client.getInputStream()))

            output.println(request_data)

            Thread.sleep(100)
            var return_data = input.readLine()
            var return_json = Parser.default().parse(StringBuilder(return_data)) as JsonObject


            assert(!(return_json["error"] as Boolean))
            //println(return_json.obj("data")?.int("user_id"))


            finish()
        }

        var policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)
    }
}