package com.witchcolors.View

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.View

class DarkView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val transparentPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) //cancella pixel
        isAntiAlias = true // bordi dettagliati
    }

    private var transparentHeight = 100f

    fun updateHolePosition(height: Float) {
        transparentHeight = height.coerceAtLeast(0f) // Assicurati che non sia negativo
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Salva lo stato del canvas e abilita un layer trasparente
        val saveLayer = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)

        // Disegna lo sfondo opaco (ad esempio, un colore nero semi-trasparente)
        canvas.drawColor(Color.argb(240, 0, 0, 0))

        // Disegna il buco trasparente
        canvas.drawRect(0f, transparentHeight, width.toFloat(), height.toFloat(), transparentPaint)

        // Ripristina lo stato del canvas
        canvas.restoreToCount(saveLayer)
    }
}

