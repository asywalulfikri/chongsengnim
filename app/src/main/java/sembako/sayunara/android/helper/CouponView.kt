package sembako.sayunara.android.helper


import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.FrameLayout

import androidx.annotation.AttrRes
import sembako.sayunara.android.R


class CouponView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                           @AttrRes defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private var semiCirclePaint: Paint? = null
    private var dottedLinePaint: Paint? = null

    private var widthh : Int = 0
    private var heightt : Int = 0

    private var semiCircleRadius = 10f
    private var semiCircleGap = 10f
    private var semiCircleColor = getContext().resources.getColor(R.color.colorApp)
    private var drawSemiCircleLeft: Boolean = false
    private var drawSemiCircleRight: Boolean = false
    private var drawSemiCircleTop: Boolean = false
    private var drawSemiCircleBottom: Boolean = false
    private var semiCircleNumberInX: Int = 0
    private var semiCircleNumberInY: Int = 0
    private var remainderCircleXHalf: Float = 0.toFloat()
    private var remainderCircleYHalf: Float = 0.toFloat()

    private var dottedLineWidth = 20f
    private var dottedLineHeight = 2f
    private var dottedLineGap = 5f
    private var dottedLineMargin = 10f
    private val dottedLineColor = getContext().resources.getColor(R.color.colorApp)
    private var drawDottedLineLeft: Boolean = false
    private var drawDottedLineRight: Boolean = false
    private var drawDottedLineTop: Boolean = false
    private var drawDottedLineBottom: Boolean = false
    private var dottedLineNumberInX: Int = 0
    private var dottedLineNumberInY: Int = 0
    private var remainderLineXHalf: Float = 0.toFloat()
    private var remainderLineYHalf: Float = 0.toFloat()

    init {
        initAttrs(context, attrs)
        initPaint()
    }

    // @formatter:off
    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CouponView)
        semiCircleRadius = typedArray.getDimensionPixelOffset(R.styleable.CouponView_SemiCircleRadius, semiCircleRadius.toInt()).toFloat()
        semiCircleGap = typedArray.getDimensionPixelOffset(R.styleable.CouponView_SemiCircleGap, semiCircleGap.toInt()).toFloat()
        semiCircleColor = typedArray.getColor(R.styleable.CouponView_SemiCircleColor, semiCircleColor)
        drawSemiCircleLeft = typedArray.getBoolean(R.styleable.CouponView_drawSemiCircleLeft, false)
        drawSemiCircleRight = typedArray.getBoolean(R.styleable.CouponView_drawSemiCircleRight, false)
        drawSemiCircleTop = typedArray.getBoolean(R.styleable.CouponView_drawSemiCircleTop, false)
        drawSemiCircleBottom = typedArray.getBoolean(R.styleable.CouponView_drawSemiCircleBottom, false)

        dottedLineWidth = typedArray.getDimensionPixelOffset(R.styleable.CouponView_dottedLineWidth, dottedLineWidth.toInt()).toFloat()
        dottedLineHeight = typedArray.getDimensionPixelOffset(R.styleable.CouponView_dottedLineHeight, dottedLineHeight.toInt()).toFloat()
        dottedLineGap = typedArray.getDimensionPixelOffset(R.styleable.CouponView_dottedLineGap, dottedLineGap.toInt()).toFloat()
        dottedLineMargin = typedArray.getDimensionPixelOffset(R.styleable.CouponView_dottedLineMargin, dottedLineMargin.toInt()).toFloat()
        drawDottedLineLeft = typedArray.getBoolean(R.styleable.CouponView_drawDottedLineLeft, drawDottedLineLeft)
        drawDottedLineRight = typedArray.getBoolean(R.styleable.CouponView_drawDottedLineRight, drawDottedLineRight)
        drawDottedLineTop = typedArray.getBoolean(R.styleable.CouponView_drawDottedLineTop, drawDottedLineTop)
        drawDottedLineBottom = typedArray.getBoolean(R.styleable.CouponView_drawDottedLineBottom, drawDottedLineBottom)
        typedArray.recycle()
    }

    // @formatter:on
    private fun initPaint() {
        semiCirclePaint = Paint()
        semiCirclePaint!!.isAntiAlias = true
        semiCirclePaint!!.isDither = true
        semiCirclePaint!!.style = Paint.Style.FILL
        semiCirclePaint!!.color = semiCircleColor

        dottedLinePaint = Paint()
        dottedLinePaint!!.isAntiAlias = true
        dottedLinePaint!!.isDither = true
        dottedLinePaint!!.style = Paint.Style.STROKE
        dottedLinePaint!!.strokeCap = Paint.Cap.ROUND
        dottedLinePaint!!.strokeWidth = dottedLineHeight
        dottedLinePaint!!.color = dottedLineColor
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        widthh = w
        heightt = h
        initSemiCircleNumber()
    }

    // @formatter:off
    private fun initSemiCircleNumber() {
        if (drawSemiCircleTop || drawSemiCircleBottom) {
            // 为了美观 其实和结束为止应该为gap 而不是圆 所以画的时候最前和最后要预留一段
            // 两边各预留的位置等于= gap + remainderXHalf
            remainderCircleXHalf = (widthh - semiCircleGap) % (semiCircleGap + 2 * semiCircleRadius) / 2
            semiCircleNumberInX = ((widthh - semiCircleGap) / (semiCircleGap + 2 * semiCircleRadius)).toInt()
        }
        if (drawSemiCircleLeft || drawSemiCircleRight) {
            remainderCircleYHalf = (heightt - semiCircleGap) % (semiCircleGap + 2 * semiCircleRadius) / 2
            semiCircleNumberInY = ((heightt - semiCircleGap) / (semiCircleGap + 2 * semiCircleRadius)).toInt()
        }

        if (drawDottedLineTop || drawDottedLineBottom) {
            remainderLineXHalf = (widthh + dottedLineGap - dottedLineMargin * 2) % (dottedLineGap + dottedLineWidth) / 2f
            dottedLineNumberInX = ((widthh + dottedLineGap - dottedLineMargin * 2) / (dottedLineGap + dottedLineWidth)).toInt()
        }

        if (drawDottedLineLeft || drawDottedLineRight) {
            remainderLineYHalf = (heightt + dottedLineGap - dottedLineMargin * 2) % (dottedLineGap + dottedLineWidth) / 2
            dottedLineNumberInY = ((heightt + dottedLineGap - dottedLineMargin * 2) / (dottedLineGap + dottedLineWidth)).toInt()
        }
    }

    // @formatter:off
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 画半圆
        if (drawSemiCircleTop) {
            for (i in 0 until semiCircleNumberInX) {
                val centerX = remainderCircleXHalf + semiCircleGap + semiCircleRadius + (semiCircleGap + semiCircleRadius * 2) * i
                canvas.drawCircle(centerX, 0f, semiCircleRadius, semiCirclePaint!!)
            }
        }

        if (drawSemiCircleBottom) {
            for (i in 0 until semiCircleNumberInX) {
                val centerX = remainderCircleXHalf + semiCircleGap + semiCircleRadius + (semiCircleGap + semiCircleRadius * 2) * i
                canvas.drawCircle(centerX, height.toFloat(), semiCircleRadius, semiCirclePaint!!)
            }
        }

        if (drawSemiCircleLeft) {
            for (i in 0 until semiCircleNumberInY) {
                val centerY = remainderCircleYHalf + semiCircleGap + semiCircleRadius + (semiCircleGap + semiCircleRadius * 2) * i
                canvas.drawCircle(0f, centerY, semiCircleRadius, semiCirclePaint!!)
            }
        }

        if (drawSemiCircleTop) {
            for (i in 0 until semiCircleNumberInY) {
                val centerY = remainderCircleYHalf + semiCircleGap + semiCircleRadius + (semiCircleGap + semiCircleRadius * 2) * i
                canvas.drawCircle(width.toFloat(), centerY, semiCircleRadius, semiCirclePaint!!)
            }
        }

        if (drawDottedLineTop) {
            for (i in 0 until dottedLineNumberInX) {
                val startX = dottedLineMargin + remainderLineXHalf + (dottedLineWidth + dottedLineGap) * i
                val startY = dottedLineMargin
                val stopY = dottedLineMargin
                canvas.drawLine(startX, startY, startX + dottedLineWidth, stopY, dottedLinePaint!!)
            }
        }

        if (drawDottedLineBottom) {
            for (i in 0 until dottedLineNumberInX) {
                val startX = dottedLineMargin + remainderLineXHalf + (dottedLineWidth + dottedLineGap) * i
                val startY = heightt - dottedLineMargin
                val stopY = heightt - dottedLineMargin
                canvas.drawLine(startX, startY, startX + dottedLineWidth, stopY, dottedLinePaint!!)
            }
        }

        if (drawDottedLineLeft) {
            for (i in 0 until dottedLineNumberInY) {
                val startX = dottedLineMargin
                val stopX = dottedLineMargin
                val startY = dottedLineMargin + remainderLineYHalf + (dottedLineWidth + dottedLineGap) * i
                canvas.drawLine(startX, startY, stopX, startY + dottedLineWidth, dottedLinePaint!!)
            }
        }

        if (drawDottedLineRight) {
            for (i in 0 until dottedLineNumberInY) {
                val startX = widthh - dottedLineMargin
                val stopX = widthh - dottedLineMargin
                val startY = dottedLineMargin + remainderLineYHalf + (dottedLineWidth + dottedLineGap) * i
                canvas.drawLine(startX, startY, stopX, startY + dottedLineWidth, dottedLinePaint!!)
            }
        }
    }
}