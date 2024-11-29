package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ActivityTwoplayerview : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.two_player_view)  // 새로운 레이아웃 설정

        supportActionBar?.hide()
        window.statusBarColor = resources.getColor(android.R.color.black, null)


    }
}