package com.example.mybookscanner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator

class MyIntentIntegrator(activity: Activity) : IntentIntegrator(activity) {

    public override fun startActivityForResult(intent: Intent?, code: Int) {
        super.startActivityForResult(intent, code)
    }
}


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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (data != null) {
                val data_str = data?.getStringExtra("result")
                if (data_str != null) {
                    resultTextView.text = data_str
                } else {
                    Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 100
    }
}