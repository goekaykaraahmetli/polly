package com.polly.visuals

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class BarcodeBoxView(
    context: Context
) : View(context) {

    private val paint = Paint()

    private var mRect = RectF()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val cornerRadius = 10f

        paint.style = Paint.Style.STROKE
        paint.color = Color.GREEN
        paint.strokeWidth = 5f

        canvas?.drawRoundRect(mRect, cornerRadius, cornerRadius, paint)
    }



    fun setRect(rect: RectF) {
        mRect = rect
        invalidate()
        requestLayout()
    }


}