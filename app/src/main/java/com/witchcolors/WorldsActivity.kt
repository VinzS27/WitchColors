package com.witchcolors

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WorldsActivity : AppCompatActivity() {
    private lateinit var homeButton: ImageButton
    private lateinit var shopButton: ImageButton
    private lateinit var statsButton: ImageButton
    private lateinit var galleryButton: ImageButton
    private lateinit var leftArrowButton: ImageButton
    private lateinit var rightArrowButton: ImageButton
    private lateinit var worldImageButton: ImageButton
    private lateinit var worldName: TextView

    private val worldImages = listOf(
        R.drawable.mondo_casa,
        R.drawable.mondo_castello,
        R.drawable.challenge1
        // Aggiungi altre immagini di mondi
    )
    private val worldNames = listOf("CASA", "CASTELLO", "FORESTA") // Nome per ciascun mondo
    private var currentWorldIndex = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worlds)

        //FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ (API 30+)
            window.setDecorFitsSystemWindows(false) // Permette al layout di estendersi su tutto lo schermo
            val controller = window.insetsController
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }else{
            // Android <=10  (API < 30)
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    )
        }

        //Init variable
        homeButton = findViewById(R.id.homeButton)
        shopButton = findViewById(R.id.shopButton)
        statsButton = findViewById(R.id.statsButton)
        galleryButton = findViewById(R.id.galleryButton)
        worldImageButton = findViewById(R.id.worldImageButton)
        worldName = findViewById(R.id.worldTitle)
        leftArrowButton = findViewById(R.id.leftArrowButton)
        rightArrowButton = findViewById(R.id.rightArrowButton)
        
        // Mostra l’immagine e titolo del mondo attuale
        updateWorldDisplay(worldImageButton, worldName)

        galleryButton.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        shopButton.setOnClickListener {
            val intent = Intent(this, ShopActivity::class.java)
            startActivity(intent)
        }

        statsButton.setOnClickListener {
            val intent = Intent(this, WitchStatsActivity::class.java)
            startActivity(intent)
        }

        // Gestisci clic della freccia sinistra
        leftArrowButton.setOnClickListener {
            if (currentWorldIndex > 0) {
                currentWorldIndex--
                updateWorldDisplay(worldImageButton, worldName)
            }
        }

        // Gestisci clic della freccia destra
        rightArrowButton.setOnClickListener {
            if (currentWorldIndex < worldImages.size - 1) {
                currentWorldIndex++
                updateWorldDisplay(worldImageButton, worldName)
            }
        }

        // Pulsante per selezionare il mondo corrente
        worldImageButton.setOnClickListener {
            val intent = Intent(this, LevelSelectionActivity::class.java)
            intent.putExtra("worldIndex", currentWorldIndex)
            startActivity(intent)
        }

        // Disabilita le frecce ai limiti
        updateArrowButtons(leftArrowButton, rightArrowButton)
    }

    // Aggiorna immagine e titolo del mondo
    private fun updateWorldDisplay(worldImageView: ImageButton, worldName: TextView) {
        worldImageView.setImageResource(worldImages[currentWorldIndex])
        worldName.text = worldNames[currentWorldIndex]
        updateArrowButtons(findViewById(R.id.leftArrowButton), findViewById(R.id.rightArrowButton))
    }

    // Abilita/disabilita le frecce quando siamo ai limiti
    private fun updateArrowButtons(leftArrowButton: ImageButton, rightArrowButton: ImageButton) {
        leftArrowButton.isEnabled = currentWorldIndex > 0
        rightArrowButton.isEnabled = currentWorldIndex < worldImages.size - 1
    }

    //FULLSCREEN RESET ON TOUCH
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.setDecorFitsSystemWindows(false)
                val controller = window.insetsController
                if (controller != null) {
                    controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                    controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        )
            }
        }
    }
}