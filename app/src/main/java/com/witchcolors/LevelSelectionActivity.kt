package com.witchcolors

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LevelSelectionActivity : AppCompatActivity() {
    private lateinit var worldName: TextView
    private lateinit var levelsGrid: GridLayout
    private lateinit var homeButton: ImageButton
    private lateinit var shopButton: ImageButton
    private lateinit var statsButton: ImageButton
    private lateinit var galleryButton: ImageButton

    private var worldIndex = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_selection)

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
        worldIndex = intent.getIntExtra("worldIndex", 0)
        worldName = findViewById(R.id.worldTitle)
        levelsGrid = findViewById(R.id.levelContainer)

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

        worldName.text = "Livelli del Mondo ${worldIndex + 1}"

        val levelsInWorld = 5
        for (level in 1..levelsInWorld) {
            val levelButton = Button(this).apply {
                text = "$level"
                setOnClickListener {
                    val intent = Intent(this@LevelSelectionActivity, GameActivity::class.java)
                    intent.putExtra("worldIndex", worldIndex)
                    intent.putExtra("level", level)
                    startActivity(intent)
                }
            }
            levelsGrid.addView(levelButton)
        }
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