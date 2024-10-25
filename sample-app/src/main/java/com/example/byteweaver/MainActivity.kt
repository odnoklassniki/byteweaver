package com.example.byteweaver

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.ac_main)

        val text1 = findViewById<TextView>(android.R.id.text1)
        text1.text = getOriginalText()
    }

    companion object {
        @JvmStatic
        fun getOriginalText(): String {
            return "Call replacement failed"
        }

        @JvmStatic
        private fun getReplacementText(self: Companion): String {
            return "Call replacement successful"
        }
    }
}
