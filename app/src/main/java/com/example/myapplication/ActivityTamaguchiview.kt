package com.example.myapplication

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ActivityTamaguchiview : AppCompatActivity() {

    // mediaPlayer 객체 선언
    private lateinit var mediaPlayer: MediaPlayer
    // TextView 객체 선언
    private lateinit var statusText: TextView

    // 텍스트 목록 (예시)
    private val messages = listOf(
        "1. Put a black stone first. 그리고 차례를 넘깁니다.",
        "2. 첫번째 스왑. 백돌은 스왑을 선택할 수 있습니다. 스왑을 하시겠습니까?",
        "3. 이제 백돌은 첫수 주위 3x3안에 돌을 놓을 수 있습니다. 그리고 차례를 넘깁니다.",
        "4. 두번째 스왑. 흑돌은 스왑을 선택할 수 있습니다. 스왑을 하시겠습니까?",
        "5. 이제 흑돌은 첫수 주위 5x5안에 돌을 놓을 수 있습니다. 그리고 차례를 넘깁니다.",
        "6. 세번째 스왑. 백돌은 스왑을 선택할 수 있습니다. 스왑을 하시겠습니까?",
        "7. 이제 백돌은 첫수 주위 7x7안에 돌을 놓을 수 있습니다. 그리고 차례를 넘깁니다.",
        "8. 네번째 스왑. 흑돌은 스왑을 선택할 수 있습니다. 스왑을 하시겠습니까?",

        "9. 스왑을 하지 않을 경우, 흑돌은 10골을 고릅니다. 그리고 백돌이 10곳중 1곳을 선택합니다.",
        "10. 스왑을 했을 경우, 흑돌은 첫수 주위 9x9안에 돌을 놓습니다. 그리고 백돌이 다섯번째 스왑을 결정하게 됩니다."
    )

    // 현재 메시지 인덱스
    private var currentMessageIndex = 0

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

        // TextView 초기화
        statusText = findViewById(R.id.statusText)

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

        // 텍스트 변경
        currentMessageIndex = (currentMessageIndex + 1) % messages.size
        statusText.text = messages[currentMessageIndex]
    }

    override fun onDestroy() {
        super.onDestroy()
        // Activity 종료 시 mediaPlayer 해제
        mediaPlayer.release()
    }
}