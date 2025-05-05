package com.android.picmosaic

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DevelopersActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.developerspage)

        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            Log.e("Back", "Button is clicked")
            Toast.makeText(this, "Button is clicked", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        val buttonSettings = findViewById<ImageButton>(R.id.buttonSettings)
        buttonSettings.setOnClickListener {
            Log.e("Settings", "Button is clicked")
            Toast.makeText(this, "Button is clicked", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}