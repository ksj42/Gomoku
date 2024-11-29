package com.example.myapplication

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class ActivityTwoplayerview : AppCompatActivity() {

    // mediaPlayer 객체 선언
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.two_player_view)  // 새로운 레이아웃 설정

        supportActionBar?.hide()
        window.statusBarColor = resources.getColor(android.R.color.black, null)

        // MediaPlayer 초기화
        mediaPlayer = MediaPlayer.create(this, R.raw.sound_stone5)
    }

    fun onGoClick(view: View) {
        val boardView = findViewById<CustomBoardView2>(R.id.boardView) // 코드 들고오기
        boardView.placeStone()
        mediaPlayer.start() // 효과음 재생
    }

    override fun onDestroy() {
        super.onDestroy()
        // Activity 종료 시 mediaPlayer 해제
        mediaPlayer.release()
    }
}

