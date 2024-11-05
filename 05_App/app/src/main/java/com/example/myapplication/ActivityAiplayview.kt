package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ActivityAiplayview : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ai_play_view)

        supportActionBar?.hide()
        window.statusBarColor = resources.getColor(android.R.color.black, null)


    }
}