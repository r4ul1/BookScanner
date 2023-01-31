package com.example.mybookscanner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
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
    }

    fun startScan(view: View) {
        val scanner = MyIntentIntegrator(this)
        scanner.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        scanner.setBeepEnabled(false)
        intent = Intent(this, MainActivity::class.java)
        scanner.startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (data != null) {
                val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                if (result.contents != null) {
                    resultTextView.text = result.contents
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