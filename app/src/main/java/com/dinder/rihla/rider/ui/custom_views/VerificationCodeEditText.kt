package com.dinder.rihla.rider.ui.custom_views

import android.R
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Editable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem

class VerificationCodeEditText : androidx.appcompat.widget.AppCompatEditText {
    // Spacing and sizing
    private var _space: Float = 24.0F // Default size
    private var _charSize: Float = 0.0F
    private var _numberOfCharacters = 6
    private var _lineSpacing: Float = 8F

    // Theming
    private var _lineStroke = 1F
    private lateinit var _linePaint: Paint
    private val _states: Array<IntArray> = arrayOf(
        intArrayOf(R.attr.state_selected), // selected
        intArrayOf(R.attr.state_focused), // selected
        intArrayOf(-R.attr.state_selected) // sel
    )
    private val _colors: IntArray = intArrayOf(Color.GREEN, Color.BLACK, Color.GRAY)
    private val colorStates = ColorStateList(_states, _colors)
    private var _listener: OnClickListener? = null

    private fun getColorForState(state: IntArray) =
        colorStates.getColorForState(state, Color.GRAY)

    private fun updateColorForLines(next: Boolean) {
        if (isFocused) {
            _linePaint.color = getColorForState(intArrayOf(R.attr.state_focused))
            if (next) {
                _linePaint.color = getColorForState(intArrayOf(R.attr.state_selected))
            }
        } else {
            _linePaint.color =
                getColorForState(intArrayOf(-R.attr.state_focused))
        }
    }

    private fun setThemeColors() {
        val outValue = TypedValue()
        context.theme.resolveAttribute(
            R.attr.colorControlActivated,
            outValue,
            true
        )
        val colorActivated = outValue.data
        _colors[0] = colorActivated

        context.theme.resolveAttribute(
            R.attr.colorPrimaryDark,
            outValue,
            true
        )
        val colorDark = outValue.data
        _colors[1] = colorDark

        context.theme.resolveAttribute(
            R.attr.colorControlHighlight,
            outValue,
            true
        )
        val colorHighlight = outValue.data
        _colors[2] = colorHighlight
    }

    constructor(context: Context?) : super(context!!) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        setBackgroundResource(0)
        setThemeColors()
        val density = context.resources.displayMetrics.density
        _space *= density // convert to pixels
        _lineSpacing *= density

        _lineStroke *= density
        _linePaint = Paint(paint)
        _linePaint.strokeWidth = _lineStroke

        // Disable copy paste
        super.setCustomSelectionActionModeCallback(object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
            }
        })

        super.setOnClickListener { v -> _listener?.onClick(v) }
    }

    override fun setCustomInsertionActionModeCallback(actionModeCallback: ActionMode.Callback?) {
        throw RuntimeException("setCustomSelectionActionModeCallback() not supported")
    }

    override fun onDraw(canvas: Canvas?) {
        val availableWidth = width - paddingRight - paddingLeft
        _charSize =
            if (_space < 0) (availableWidth / (_numberOfCharacters * 2 - 1)).toFloat() else (availableWidth - (_space * (_numberOfCharacters - 1))) / _numberOfCharacters

        var startX = paddingLeft.toFloat()
        val bottom = (height - paddingBottom).toFloat()

        for (i in 0.._numberOfCharacters) {
            updateColorForLines(i == text?.length)
            canvas?.drawLine(startX, bottom, (startX + _charSize), bottom, _linePaint)

            if (text?.length!! > i) {
                val middle = startX + _charSize / 2
                canvas?.drawText(
                    text as Editable,
                    i,
                    i + 1,
                    (middle - textSize * 0.5 / 2).toFloat(),
                    bottom - _lineSpacing,
                    paint
                )
            }

            startX += if (_space < 0) {
                _charSize * 2
            } else {
                _charSize + _space
            }
        }
    }
}
