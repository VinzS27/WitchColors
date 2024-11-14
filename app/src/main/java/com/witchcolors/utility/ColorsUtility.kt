package com.witchcolors.utility

import android.graphics.Color
import com.witchcolors.R

object ColorsUtility {

    val drawableMap = mapOf(
        /*"sfera" to mapOf(
            "rosso" to R.drawable.sfera_rosso,
            "verde" to R.drawable.sfera_verde,
            "giallo" to R.drawable.sfera_giallo,
            "celeste" to R.drawable.sfera_celeste,
            "blu" to R.drawable.sfera_blu,
            "viola" to R.drawable.sfera_viola,
            "arancione" to R.drawable.sfera_arancione,
            "nero" to R.drawable.sfera_nero,
            "rosa" to R.drawable.sfera_rosa,
            "bianco" to R.drawable.sfera_bianco
        ),
        "mela" to mapOf(
            "rosso" to R.drawable.mela_rosso,
            "verde" to R.drawable.mela_verde,
            "giallo" to R.drawable.mela_giallo,
            "celeste" to R.drawable.mela_celeste,
            "blu" to R.drawable.mela_blu,
            "viola" to R.drawable.mela_viola,
            "arancione" to R.drawable.mela_arancione,
            "nero" to R.drawable.mela_nero,
            "rosa" to R.drawable.mela_rosa,
            "bianco" to R.drawable.mela_bianco
        ),
        "spada" to mapOf(
            "rosso" to R.drawable.spada_rosso,
            "verde" to R.drawable.spada_verde,
            "giallo" to R.drawable.spada_giallo,
            "celeste" to R.drawable.spada_celeste,
            "blu" to R.drawable.spada_blu,
            "viola" to R.drawable.spada_viola,
            "arancione" to R.drawable.spada_arancione,
            "nero" to R.drawable.spada_nero,
            "rosa" to R.drawable.spada_rosa,
            "bianco" to R.drawable.spada_bianco
        ),
        "pozione" to mapOf(
            "rosso" to R.drawable.pozione_rosso,
            "verde" to R.drawable.pozione_verde,
            "giallo" to R.drawable.pozione_giallo,
            "celeste" to R.drawable.pozione_celeste,
            "blu" to R.drawable.pozione_blu,
            "viola" to R.drawable.pozione_viola,
            "arancione" to R.drawable.pozione_arancione,
            "nero" to R.drawable.pozione_nero,
            "rosa" to R.drawable.pozione_rosa,
            "bianco" to R.drawable.pozione_bianco
        ),*/
        "cappello" to mapOf(
            "rosso" to R.drawable.cappello_rosso,
            "verde" to R.drawable.cappello_verde,
            "giallo" to R.drawable.cappello_giallo,
            "celeste" to R.drawable.cappello_celeste,
            "blu" to R.drawable.cappello_blu,
            "viola" to R.drawable.cappello_viola,
            "arancione" to R.drawable.cappello_arancione,
            "nero" to R.drawable.cappello_nero,
            "rosa" to R.drawable.cappello_rosa,
            "bianco" to R.drawable.cappello_bianco
        )/*,
        "fungo" to mapOf(
            "rosso" to R.drawable.fungo_rosso,
            "verde" to R.drawable.fungo_verde,
            "giallo" to R.drawable.fungo_giallo,
            "celeste" to R.drawable.fungo_celeste,
            "blu" to R.drawable.fungo_blu,
            "viola" to R.drawable.fungo_viola,
            "arancione" to R.drawable.fungo_arancione,
            "nero" to R.drawable.fungo_nero,
            "rosa" to R.drawable.fungo_rosa,
            "bianco" to R.drawable.fungo_bianco
        )*/
    )

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

    fun getDrawableForObjectAndColor(objectType: String, colorName: String): Int? {
        return drawableMap[objectType]?.get(colorName.lowercase())
    }

    fun getColorFromName(colorName: String): Int {
        return colorMap[colorName] ?: Color.TRANSPARENT
    }

    fun getRandomColorName(): String {
        return colorMap.keys.random()
    }
}
