package com.dinder.rihla.rider.ui.custom_views // ktlint-disable experimental:package-name

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.dinder.rihla.rider.R
import com.dinder.rihla.rider.common.Constants.NUMBER_OF_SEATS_ROWS
import com.dinder.rihla.rider.data.model.Seat
import com.dinder.rihla.rider.data.model.SquareBound
import com.dinder.rihla.rider.utils.SeatState
import com.dinder.rihla.rider.utils.SeatUtils

class SeatsView : View {
    private var seats: MutableMap<String, SeatState> = SeatUtils.emptySeats.toMutableMap()

    private var paint: Paint = Paint()
    private var textPaint: Paint = Paint()
    private var _bounds: MutableMap<Int, SquareBound> = mutableMapOf()
    private val _space = 20f
    private var onSeatSelectedListener: ((List<Seat>) -> Unit)? = null

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun setOnSeatSelectedListener(listener: (List<Seat>) -> Unit) {
        this.onSeatSelectedListener = listener
    }

    fun setSeats(seats: Map<String, SeatState>) {
        this.seats = seats.toMutableMap()
        invalidate()
    }

    fun getSeats() = seats

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val eventAction = event?.action

        // Click Coordinates
        val x = event?.x
        val y = event?.y

        when (eventAction) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_UP -> {
                val index: Int? = getClickEventSeatNumber(x!!, y!!)
                Log.i("SeatView", "Coordinates: $x, $y")
                Log.i("SeatView", "Index: $index")
                if (index != null) {
                    onSeatClicked(index)
                }
            }
            MotionEvent.ACTION_MOVE -> {}
        }

        invalidate()
        return true
    }

    private fun initializeSeat(
        seatNumber: Int,
        left: Float,
        right: Float,
        top: Float,
        bottom: Float
    ) {
        paint.color = getSeatColor(seatNumber)

        val bound = SquareBound(seatNumber, left, right, top, bottom)
        _bounds[seatNumber] = bound
    }

    private fun getClickEventSeatNumber(x: Float, y: Float): Int? {
        var index: Int? = null
        _bounds.values.forEach { bound ->
            val withinHeight = bound.top <= y && bound.bottom >= y
            val withinWidth = bound.left <= x && bound.right >= x
            if (withinHeight && withinWidth) {
                index = bound.index
                return@forEach
            }
        }
        return index
    }

    private fun onSeatClicked(seatNumber: Int) {
        when (seats["$seatNumber"]) {
            SeatState.SELECTED -> unSelectSeat(seatNumber)
            SeatState.UNBOOKED -> selectSeat(seatNumber)
            SeatState.UN_SELECTED -> selectSeat(seatNumber)
            else -> Unit
        }
    }

    private fun selectSeat(seatNumber: Int) {
        seats["$seatNumber"] = SeatState.SELECTED
        onSeatSelectedListener?.invoke(SeatUtils.getSelectedSeats(seats))
    }

    private fun unSelectSeat(seatNumber: Int) {
        seats["$seatNumber"] = SeatState.UN_SELECTED
        onSeatSelectedListener?.invoke(SeatUtils.getSelectedSeats(seats))
    }

    private fun getSeatColor(seatNumber: Int): Int {
        return when (seats["$seatNumber"]) {
            SeatState.UNBOOKED -> {
                resources.getColor(R.color.orange, context.theme)
            }

            SeatState.UN_SELECTED -> {
                resources.getColor(R.color.orange, context.theme)
            }

            SeatState.SELECTED -> {
                resources.getColor(R.color.green, context.theme)
            }

            else -> {
                Color.GRAY
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        val availableWidth = width - paddingRight - paddingLeft
        val _squareSize = (availableWidth - (_space * (7 - 1))) / 7
        val _textSize = 0.34f * _squareSize
        var startX = paddingLeft.toFloat()
        var startY = (height - 12 * _squareSize - NUMBER_OF_SEATS_ROWS * _space) / 2

        paint.color = Color.GRAY
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.color = Color.WHITE
        textPaint.textSize = _textSize

        for (i in 0..NUMBER_OF_SEATS_ROWS) {
            for (j in 0..7) {
                val seats = mutableListOf(1, 2, 4, 5).also {
                    if (i == NUMBER_OF_SEATS_ROWS) {
                        it.addAll(2, listOf(3))
                    }
                }

                if (seats.contains(j)) {
                    val seatNumber = (4 * i + seats.indexOf(j) + 1)
                    val left = startX
                    val right = startX + _squareSize
                    val top = startY
                    val bottom = startY + _squareSize

                    initializeSeat(seatNumber, left, right, top, bottom)

                    canvas?.drawRect(
                        left,
                        top,
                        right,
                        bottom,
                        paint
                    )

                    canvas!!.drawText(
                        seatNumber.toString(),
                        startX + _squareSize / 2,
                        (startY + _squareSize / 2 - (textPaint.descent() + textPaint.ascent()) / 2),
                        textPaint
                    )
                }
                startX += _squareSize + _space
            }
            startX = paddingLeft.toFloat()
            startY += _squareSize + _space
        }
    }
}
