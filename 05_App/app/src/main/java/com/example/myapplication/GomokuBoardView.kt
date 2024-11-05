package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.graphics.BitmapFactory
import android.graphics.Bitmap

class CustomBoardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint()
    private val boardSize = 15  // 15x15 바둑판
    private val boardPadding = 40f  // 여백
    private var cellSize: Float = 0f

    private val stones = mutableListOf<Pair<Pair<Int, Int>, Int>>()  // (좌표, 돌 색상)

    private var currentColor = Color.BLACK  // 현재 돌 색상

    init {
        paint.isAntiAlias = true
        paint.color = currentColor
        paint.strokeWidth = 5f
        paint.textSize = 40f // 텍스트 크기 설정
        paint.color = Color.BLACK // 텍스트 색상
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // PNG 이미지를 배경으로 설정
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.playbord)

        // 바둑판 크기와 동일한 크기로 이미지 크기를 조정
        val boardWidth = width.toFloat()  // View의 가로 크기
        val boardHeight = height.toFloat() // View의 세로 크기

        // 이미지의 폭은 화면 전체에 맞추고, 높이는 화면의 가운데에 위치시킴
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, boardWidth.toInt(), boardWidth.toInt(), false)
        val topPosition = (boardHeight - boardWidth) / 2  // 화면 가운데에 맞추기 위한 높이 계산

        // 이미지를 화면의 폭에 맞추고, 가운데 높이에 배치
        canvas.drawBitmap(scaledBitmap, 0f, topPosition, null)

        // 오목판 그리기 (텍스트 공간을 고려하여)
        val padding = 60 // 텍스트 공간을 위한 패딩
        cellSize = (boardWidth - padding * 2) / boardSize // 셀 크기 계산

        //test
        canvas.drawText(boardWidth.toString(), 50f, 50f, paint)

        // 가로선 그리기
        for (i in 0..boardSize) {
            val startX = padding.toFloat()
            val startY = topPosition + padding + i * cellSize
            val stopX = boardWidth - padding
            canvas.drawLine(startX, startY, stopX, startY, paint) // 수평선

            // 숫자 그리기 (왼쪽)
            if (i < boardSize) {
                canvas.drawText((i + 1).toString(), startX - 55f, startY+45f, paint) // 왼쪽 숫자
                canvas.drawText((i + 1).toString(), boardWidth-padding+10f, startY+45f, paint)
            }
        }

        // 세로선 그리기
        for (i in 0..boardSize) {
            val startX = padding + i * cellSize
            val startY = topPosition + padding
            val stopY = topPosition + boardWidth - padding
            canvas.drawLine(startX, startY, startX, stopY, paint) // 수직선

            // 알파벳 그리기 (왼쪽)
            if (i < boardSize) {
                val letter = ('A' + i).toString() // 알파벳 계산
                canvas.drawText(letter, startX + 15f, startY - 10f, paint) // 왼쪽 알파벳
                canvas.drawText(letter, startX + 15f, topPosition + boardWidth-20f, paint) // 왼쪽 알파벳
            }
        }
    }
}
