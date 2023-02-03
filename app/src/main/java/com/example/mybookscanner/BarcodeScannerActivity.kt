package com.example.mybookscanner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.lang.StringBuilder
import java.net.InetAddress
import java.net.Socket


class BarcodeScannerActivity : AppCompatActivity() {

    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scanner)

        resultTextView = findViewById(R.id.resultTextView)

        val bt = findViewById<Button>(R.id.scanButton)
        bt.setOnClickListener{
            Toast.makeText(this, "Scan started", Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

        var policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)

        val bt2 = findViewById<Button>(R.id.listButton)
        bt2.setOnClickListener{
            val intent = Intent(this@BarcodeScannerActivity, BookView::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (data != null) {
                val data_str = data?.getStringExtra("result")
                if (data_str != null) {
                    var title = sendBarcode(data_str)
                    resultTextView.text = title
                } else {
                    Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun sendBarcode(barcode:String): String? {

        val shared_preferences = this.getSharedPreferences("data", Context.MODE_PRIVATE)
        var token = shared_preferences.getString("token", MainApplication.Companion.token)

        val data = mapOf(
            "barcode" to barcode
        )

        val auth = mapOf(
            "type" to "token",
            "token" to token
        )

        val request = mapOf (
            "request" to "GET",
            "type" to "book",
            "auth" to auth,
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

        if(return_json["error"] as Boolean){
            return "This book is not in our library mate ;)"
        }

        return return_json.obj("data")?.string("title")
    }

    companion object {
        private const val REQUEST_CODE = 100
    }
}