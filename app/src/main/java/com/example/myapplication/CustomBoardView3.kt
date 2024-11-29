// 패키지 정의 및 필요한 임포트 구문은 유지합니다.
package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.util.Log
//import android.widget.TextView
import android.widget.Toast
import kotlin.random.Random

//import com.chaquo.python.Python
//import com.chaquo.python.PyObject

class CustomBoardView3 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint()
    private val boardSize = 15  // 15x15 바둑판
    private val boardPadding = 40f  // 여백
    private var cellSize: Float = 0f

    private val stones = mutableListOf<Pair<Pair<Int, Int>, Int>>()  // (좌표, 돌 색상)
    private var currentColor = Color.BLACK  // 현재 돌 색상

    // 화점을 추가할 위치 목록
    private val starPoints = listOf(
        Pair(3, 3), Pair(3, 11), Pair(11, 3), Pair(11, 11), Pair(7, 7)
    )

    init {
        paint.isAntiAlias = true
        paint.strokeWidth = 5f
        paint.textSize = 40f // 텍스트 크기 설정
    }

    //디버깅용 시작

    //디버깅용 끝

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.playbord)
        val boardWidth = width.toFloat()
        val boardHeight = height.toFloat()
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, boardWidth.toInt(), boardWidth.toInt(), false)
        val topPosition = (boardHeight - boardWidth) / 2

        canvas.drawBitmap(scaledBitmap, 0f, topPosition, null)

        val padding = 60
        cellSize = (boardWidth - padding * 2) / (boardSize - 1)

        paint.color = Color.BLACK

        for (i in 0 until boardSize) {
            val startX = padding.toFloat()
            val startY = topPosition + padding + i * cellSize
            val stopX = boardWidth - padding
            canvas.drawLine(startX, startY, stopX, startY, paint)

            // 라인좌표
            canvas.drawText((i + 1).toString(), startX - 55f, startY + 15f, paint)
            canvas.drawText((i + 1).toString(), boardWidth - padding + 10f, startY + 15f, paint)

        }

        for (i in 0 until boardSize) {
            val startX = padding + i * cellSize
            val startY = topPosition + padding
            val stopY = topPosition + boardWidth - padding
            canvas.drawLine(startX, startY, startX, stopY, paint)

            // 라인좌표
            val letter = ('A' + i).toString()
            canvas.drawText(letter, startX - 10f, startY - 10f, paint)
            canvas.drawText(letter, startX - 10f, stopY + 40f, paint)

        }

        for (point in starPoints) {
            val centerX = padding + point.first * cellSize
            val centerY = topPosition + padding + point.second * cellSize
            paint.color = Color.BLACK
            canvas.drawCircle(centerX, centerY, cellSize / 8, paint)
        }

        for ((position, color) in stones) {
            paint.color = color
            val centerX = padding + position.first * cellSize
            val centerY = topPosition + padding + position.second * cellSize
            canvas.drawCircle(centerX, centerY, cellSize / 2.5f, paint)
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = ((event.x - boardPadding) / cellSize).toInt()
            val y = ((event.y - boardPadding - (height - width) / 2) / cellSize).toInt()

            if (x in 0 until boardSize && y in 0 until boardSize) {
                if (stones.none { it.first == Pair(x, y) }) {
                    stones.add(Pair(Pair(x, y), currentColor))

                    if (checkWinCondition(x, y)) {
                        Toast.makeText(context, "${if (currentColor == Color.BLACK) "Black" else "White"} wins!", Toast.LENGTH_SHORT).show()
                        resetBoard()
                    } else {
                        currentColor = if (currentColor == Color.BLACK) Color.WHITE else Color.BLACK
                        // AI 턴 실행
                        if (currentColor == Color.WHITE) {
                            aiMove()
                        }
                    }
                    invalidate()

                }
            }
        }

        return true
    }


    // AI 돌 놓기
    private fun aiMove() {
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until boardSize) {
            for (j in 0 until boardSize) {
                if (stones.none { it.first == Pair(i, j) }) {
                    emptyCells.add(Pair(i, j))
                }
            }
        }
        if (emptyCells.isNotEmpty()) {
            val (aiX, aiY) = emptyCells[Random.nextInt(emptyCells.size)]
            stones.add(Pair(Pair(aiX, aiY), currentColor))
            if (checkWinCondition(aiX, aiY)) {
                Toast.makeText(context, "White wins!", Toast.LENGTH_SHORT).show()
                resetBoard()
            } else {
                currentColor = Color.BLACK
            }
        }

        // Chaquopy를 사용하여 Python 코드 실행
//        val python = Python.getInstance()
//        try {
//            val numpy = python.getModule("numpy")  // NumPy 모듈 가져오기
//
//            // NumPy 배열 생성
//            val npArray = numpy.callAttr("array", listOf(1, 2, 3, 4, 5))
//
//            // 배열을 Logcat에 출력
//            Log.d("PythonResult", "NumPy Array: $npArray")
//        } catch (e: Exception) {
//            Log.e("PythonError", "Error calling Python code: ${e.message}")
//        }
    }

    private fun checkWinCondition(x: Int, y: Int): Boolean {
        val directions = listOf(
            Pair(1, 0), Pair(0, 1), Pair(1, 1), Pair(1, -1)
        )

        val colorToCheck = stones.find { it.first == Pair(x, y) }?.second ?: return false

        for ((dx, dy) in directions) {
            var count = 1

            for (dir in listOf(1, -1)) {
                var nx = x + dx * dir
                var ny = y + dy * dir
                while (nx in 0 until boardSize && ny in 0 until boardSize &&
                    stones.any { it.first == Pair(nx, ny) && it.second == colorToCheck }) {
                    count++
                    if (count == 5) return true
                    nx += dx * dir
                    ny += dy * dir
                }
            }
        }
        return false
    }

    private fun resetBoard() {
        stones.clear()
        currentColor = Color.BLACK
        invalidate()
    }
}
