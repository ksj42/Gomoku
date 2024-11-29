package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

class CustomBoardView2 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint()
    private val boardSize = 15  // 15x15 바둑판
    private val boardPadding = 40f  // 여백
    private var cellSize: Float = 0f

    private val stones = mutableListOf<Pair<Pair<Int, Int>, Int>>()  // (좌표, 돌 색상)
    private var currentColor = Color.BLACK  // 현재 돌 색상

    private val starPoints = listOf(
        Pair(3, 3), Pair(3, 11), Pair(11, 3), Pair(11, 11), Pair(7, 7)
    )

    private var pendingStone: Pair<Pair<Int, Int>, Int>? = null // 미리보기 돌

    // 타이머 관련 변수
    private val turnTimeLimit = 30 * 1000L  // 30초 -> 밀리초 단위
    private var remainingTime = turnTimeLimit // 남은 시간 (밀리초)
    private val timerHandler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable
    private var currentPlayer = "Black"

    // 시간바 관련 변수
    private var progressBarWidth: Float = 0f
    private var maxTimeWidth: Float = 0f
    private val frameInterval = 50L // 50ms마다 갱신 (부드러운 애니메이션을 위해 짧은 간격)

    // 바둑돌 관련 함수
    private lateinit var blackStoneBitmap: Bitmap
    private lateinit var whiteStoneBitmap: Bitmap

    init {
        paint.isAntiAlias = true
        paint.strokeWidth = 5f
        paint.textSize = 40f

        // 바둑돌 이미지 로드
        val blackStone = BitmapFactory.decodeResource(resources, R.drawable.stone_black)
        val whiteStone = BitmapFactory.decodeResource(resources, R.drawable.stone_white)

        blackStoneBitmap = blackStone
        whiteStoneBitmap = whiteStone

        // 타이머 Runnable 정의
        timerRunnable = object : Runnable {
            override fun run() {
                if (remainingTime > 0) {
                    remainingTime -= frameInterval // 50ms씩 감소
                    progressBarWidth = (remainingTime.toFloat() / turnTimeLimit) * maxTimeWidth
                    invalidate() // 화면 갱신
                    timerHandler.postDelayed(this, frameInterval) // 50ms마다 반복
                } else {
                    remainingTime = 0 // 잔여 시간 0으로 고정
                    progressBarWidth = 0f // 진행 바 완전히 비우기
                    invalidate() // 화면 갱신
                    Toast.makeText(
                        context,
                        "$currentPlayer ran out of time! Switching turns.",
                        Toast.LENGTH_SHORT
                    ).show()
                    switchPlayer() // 턴 전환
                    resetTimer() // 타이머 리셋
                }
            }
        }
        resetTimer() // 초기 타이머 시작
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // PNG 이미지를 배경으로 설정
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.playbord)
        val boardWidth = width.toFloat()
        val boardHeight = height.toFloat()
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, boardWidth.toInt(), boardWidth.toInt(), false)
        val topPosition = (boardHeight - boardWidth) / 2

        canvas.drawBitmap(scaledBitmap, 0f, topPosition, null)

        // 오목판 그리기
        val padding = 60
        cellSize = (boardWidth - padding * 2) / (boardSize - 1)

        paint.color = Color.BLACK

        for (i in 0 until boardSize) {
            val startX = padding.toFloat()
            val startY = topPosition + padding + i * cellSize
            val stopX = boardWidth - padding
            canvas.drawLine(startX, startY, stopX, startY, paint)

            if (i < boardSize) {
                canvas.drawText((i + 1).toString(), startX - 55f, startY + 15f, paint)
                canvas.drawText((i + 1).toString(), boardWidth - padding + 10f, startY + 15f, paint)
            }
        }

        for (i in 0 until boardSize) {
            val startX = padding + i * cellSize
            val startY = topPosition + padding
            val stopY = topPosition + boardWidth - padding
            canvas.drawLine(startX, startY, startX, stopY, paint)

            if (i < boardSize) {
                val letter = ('A' + i).toString()
                canvas.drawText(letter, startX - 10f, startY - 10f, paint)
                canvas.drawText(letter, startX - 10f, stopY + 40f, paint)
            }
        }

        for (point in starPoints) {
            val centerX = padding + point.first * cellSize
            val centerY = topPosition + padding + point.second * cellSize
            paint.color = Color.BLACK
            canvas.drawCircle(centerX, centerY, cellSize / 8, paint)
        }


        // 바둑돌 이미지 크기 조정
        val stoneSize = cellSize
        val scaledBlackStone = Bitmap.createScaledBitmap(blackStoneBitmap, stoneSize.toInt(), stoneSize.toInt(), true)
        val scaledWhiteStone = Bitmap.createScaledBitmap(whiteStoneBitmap, stoneSize.toInt(), stoneSize.toInt(), true)

        //if(pendingStone != null){
        for ((position, color) in stones) {
            val centerX = padding + position.first * cellSize - stoneSize / 2
            val centerY = topPosition + padding + position.second * cellSize - stoneSize / 2
            val stoneBitmap = if (color == Color.BLACK) scaledBlackStone else scaledWhiteStone
            canvas.drawBitmap(stoneBitmap, centerX, centerY, null)
        }
        //}

        // 미리보기 돌 그리기
        pendingStone?.let {
            val (x, y) = it.first
            val centerX = padding + x * cellSize - stoneSize / 2
            val centerY = topPosition + padding + y * cellSize - stoneSize / 2
            paint.color = it.second
            paint.alpha = 128 // 투명도 설정 (50%)
            canvas.drawCircle(centerX + stoneSize / 2, centerY + stoneSize / 2, stoneSize / 2, paint)
            paint.alpha = 255 // 투명도 초기화
        }

        // 시간바 크기 및 위치 계산
        val barHeight = 30f
        val barPadding = 0f
        val barLeft = 0f // 왼쪽은 cellSize부터 시작
        val barRight = width.toFloat() // 오른쪽도 cellSize만큼 떨어짐
        val barTop = topPosition - barHeight - barPadding // 위쪽 위치
        val barBottom = topPosition - barPadding // 아래쪽 위치

        // 최대 시간 바 너비 계산 (화면 양 끝 여백 포함)
        maxTimeWidth = barRight - barLeft

        // 전체 시간 바 (회색 배경)
        paint.color = Color.GRAY
        canvas.drawRect(barLeft, barTop, barRight, barBottom, paint)


        // 남은 시간에 따른 진행 바
        paint.color = if (currentColor == Color.BLACK) Color.BLACK else Color.WHITE
        canvas.drawRect(barLeft, barTop, barLeft + progressBarWidth, barBottom, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = ((event.x - boardPadding) / cellSize).toInt()
            val y = ((event.y - boardPadding - (height - width) / 2) / cellSize).toInt()

            if (x in 0 until boardSize && y in 0 until boardSize) {
                if (stones.none { it.first == Pair(x, y) }) {
//                    stones.add(Pair(Pair(x, y), currentColor))
//                    if (checkWinCondition(x, y)) {
//                        Toast.makeText(context, "$currentPlayer wins!", Toast.LENGTH_SHORT).show()
//                        resetBoard()
//                    } else {
//                        switchPlayer()
//                        resetTimer()
//                    }
                    pendingStone = Pair(Pair(x, y), currentColor)
                    invalidate()
                }
            }
        }
        return true
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

    private fun switchPlayer() {
        currentColor = if (currentColor == Color.BLACK) Color.WHITE else Color.BLACK
        currentPlayer = if (currentColor == Color.BLACK) "Black" else "White"
    }

    private fun resetBoard() {
        stones.clear()
        currentColor = Color.BLACK
        currentPlayer = "Black"
        resetTimer()
        invalidate()
    }

    fun placeStone() {
        pendingStone?.let {
            stones.add(it)
            if (checkWinCondition(it.first.first, it.first.second)) {
                Toast.makeText(context, "Player ${if (currentColor == Color.BLACK) "Black" else "White"} wins!", Toast.LENGTH_SHORT).show()
                resetBoard()
                resetTimer()
            } else {
                switchPlayer()
                resetTimer()
            }
            pendingStone = null // 미리보기 돌 제거
            invalidate()
        }
    }

    // 플레이어 교체
//    private fun switchPlayer() {
//        currentPlayer = if (currentPlayer == "Black") "White" else "Black"
//        invalidate() // UI 갱신
//    }

    // 타이머 리셋
    private fun resetTimer() {
        remainingTime = turnTimeLimit
        progressBarWidth = maxTimeWidth // 시간 바 초기화
        timerHandler.removeCallbacks(timerRunnable)
        timerHandler.postDelayed(timerRunnable, frameInterval) // 타이머 시작
    }


    // View가 제거될 때 타이머 정리
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        timerHandler.removeCallbacks(timerRunnable) // 타이머 중단
    }
}


//// 패키지 정의 및 필요한 임포트 구문은 유지합니다.
//package com.example.myapplication
//
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
//import android.util.AttributeSet
//import android.view.MotionEvent
//import android.view.View
//import android.graphics.BitmapFactory
//import android.graphics.Bitmap
//import android.widget.Toast
//
//class CustomBoardView2 @JvmOverloads constructor(
//    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
//) : View(context, attrs, defStyleAttr) {
//
//    private val paint = Paint()
//    private val boardSize = 15  // 15x15 바둑판
//    private val boardPadding = 40f  // 여백
//    private var cellSize: Float = 0f
//
//    private val stones = mutableListOf<Pair<Pair<Int, Int>, Int>>()  // (좌표, 돌 색상)
//    private var currentColor = Color.BLACK  // 현재 돌 색상
//
//    // 화점을 추가할 위치 목록
//    private val starPoints = listOf(
//        Pair(3, 3), Pair(3, 11), Pair(11, 3), Pair(11, 11), Pair(7, 7)
//    )
//
//    init {
//        paint.isAntiAlias = true
//        paint.strokeWidth = 5f
//        paint.textSize = 40f // 텍스트 크기 설정
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//
//        // PNG 이미지를 배경으로 설정
//        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.playbord)
//        val boardWidth = width.toFloat()  // View의 가로 크기
//        val boardHeight = height.toFloat() // View의 세로 크기
//        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, boardWidth.toInt(), boardWidth.toInt(), false)
//        val topPosition = (boardHeight - boardWidth) / 2  // 화면 가운데에 맞추기 위한 높이 계산
//
//        // 이미지를 화면의 폭에 맞추고, 가운데 높이에 배치
//        canvas.drawBitmap(scaledBitmap, 0f, topPosition, null)
//
//        // 오목판 그리기
//        val padding = 60 // 텍스트 공간을 위한 패딩
//        cellSize = (boardWidth - padding * 2) / (boardSize - 1) // 셀 크기 계산 (교차점에 맞추기 위해 -1)
//
//        paint.color = Color.BLACK // 선과 텍스트 색상 설정
//
//        // 가로선 그리기
//        for (i in 0 until boardSize) {
//            val startX = padding.toFloat()
//            val startY = topPosition + padding + i * cellSize
//            val stopX = boardWidth - padding
//            canvas.drawLine(startX, startY, stopX, startY, paint) // 수평선
//
//            // 숫자 그리기 (왼쪽)
//            if (i < boardSize) {
//                canvas.drawText((i + 1).toString(), startX - 55f, startY + 15f, paint)
//                canvas.drawText((i + 1).toString(), boardWidth - padding + 10f, startY + 15f, paint)
//            }
//        }
//
//        // 세로선 그리기
//        for (i in 0 until boardSize) {
//            val startX = padding + i * cellSize
//            val startY = topPosition + padding
//            val stopY = topPosition + boardWidth - padding
//            canvas.drawLine(startX, startY, startX, stopY, paint) // 수직선
//
//            // 알파벳 그리기
//            if (i < boardSize) {
//                val letter = ('A' + i).toString()
//                canvas.drawText(letter, startX - 10f, startY - 10f, paint)
//                canvas.drawText(letter, startX - 10f, stopY + 40f, paint)
//            }
//        }
//
//        // 화점 그리기
//        for (point in starPoints) {
//            val centerX = padding + point.first * cellSize
//            val centerY = topPosition + padding + point.second * cellSize
//            paint.color = Color.BLACK // 화점 색상은 검은색
//            canvas.drawCircle(centerX, centerY, cellSize / 8, paint) // 작은 원을 그려 화점을 표시
//        }
//
//        // 돌 그리기
//        for ((position, color) in stones) {
//            paint.color = color // 돌 색상 설정
//            val centerX = padding + position.first * cellSize
//            val centerY = topPosition + padding + position.second * cellSize
//            canvas.drawCircle(centerX, centerY, cellSize / 2.5f, paint)
//        }
//    }
//
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        if (event.action == MotionEvent.ACTION_DOWN) {
//            val x = ((event.x - boardPadding) / cellSize).toInt()
//            val y = ((event.y - boardPadding - (height - width) / 2) / cellSize).toInt()
//
//            // 터치가 바둑판 내에 있는지 확인
//            if (x in 0 until boardSize && y in 0 until boardSize) {
//                // 빈 칸인지 확인하고 돌 놓기
//                if (stones.none { it.first == Pair(x, y) }) {
//                    stones.add(Pair(Pair(x, y), currentColor))
//                    if (checkWinCondition(x, y)) {
//                        Toast.makeText(context, "${if (currentColor == Color.BLACK) "Black" else "White"} wins!", Toast.LENGTH_SHORT).show()
//                        resetBoard()
//                    } else {
//                        currentColor = if (currentColor == Color.BLACK) Color.WHITE else Color.BLACK
//                    }
//                    invalidate()
//                }
//            }
//        }
//        return true
//    }
//
//    // 승리 조건 확인
//    private fun checkWinCondition(x: Int, y: Int): Boolean {
//        val directions = listOf(
//            Pair(1, 0),  // 수평
//            Pair(0, 1),  // 수직
//            Pair(1, 1),  // 대각선 \
//            Pair(1, -1)  // 대각선 /
//        )
//
//        val colorToCheck = stones.find { it.first == Pair(x, y) }?.second ?: return false
//
//        for ((dx, dy) in directions) {
//            var count = 1
//
//            // 두 방향에서 연속된 돌 개수 세기
//            for (dir in listOf(1, -1)) {
//                var nx = x + dx * dir
//                var ny = y + dy * dir
//                while (nx in 0 until boardSize && ny in 0 until boardSize &&
//                    stones.any { it.first == Pair(nx, ny) && it.second == colorToCheck }) {
//                    count++
//                    if (count == 5) return true
//                    nx += dx * dir
//                    ny += dy * dir
//                }
//            }
//        }
//        return false
//    }
//
//    // 게임 보드 초기화
//    private fun resetBoard() {
//        stones.clear()
//        currentColor = Color.BLACK
//        invalidate()
//    }
//}
