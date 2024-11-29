package com.example.myapplication

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ActivityTamaguchiview : AppCompatActivity() {

    // mediaPlayer 객체 선언
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.tamaguchi_rule_view)
        supportActionBar?.hide()
        window.statusBarColor = resources.getColor(android.R.color.black, null)

        supportActionBar?.hide()
        window.statusBarColor = resources.getColor(android.R.color.black, null)

        // MediaPlayer 초기화
        mediaPlayer = MediaPlayer.create(this, R.raw.sound_stone5)


//        val boardView = findViewById<CustomBoardView1>(R.id.boardView)
//        val goButton = findViewById<ImageButton>(R.id.imageButton)
//        goButton.setOnClickListener {
//            boardView.placeStone()
//        }


    }


    fun onGoClick(view: View) {
        val boardView = findViewById<CustomBoardView1>(R.id.boardView)
        boardView.placeStone()
        mediaPlayer.start() // 효과음 재생
    }

    override fun onDestroy() {
        super.onDestroy()
        // Activity 종료 시 mediaPlayer 해제
        mediaPlayer.release()
    }
}