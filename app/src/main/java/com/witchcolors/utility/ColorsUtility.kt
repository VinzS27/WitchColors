package com.witchcolors.utility

import android.graphics.Color

object ColorsUtility {

    private val colorMap = mapOf(
        "Rosso" to Color.RED,
        "Blu" to Color.BLUE,
        "Verde" to Color.GREEN,
        "Giallo" to Color.YELLOW,
        "Rosa" to Color.parseColor("#FFC0CB"),
        "Nero" to Color.BLACK,
        "Celeste" to Color.CYAN,
        "Arancione" to Color.parseColor("#FF5722"),
        "Viola" to Color.parseColor("#AE52D5"),
        "Bianco" to Color.WHITE
    )

    fun getColorFromName(colorName: String): Int {
        return colorMap[colorName] ?: Color.TRANSPARENT
    }

    fun getRandomColorName(): String {
        return colorMap.keys.random()
    }
}
