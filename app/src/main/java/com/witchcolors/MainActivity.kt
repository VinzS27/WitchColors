package com.witchcolors

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
import android.widget.TextView
import com.witchcolors.DAO.GameDAO
import com.witchcolors.config.GameDatabase
import com.witchcolors.repository.GameRepository

class MainActivity : AppCompatActivity() {

    private lateinit var moneyText: TextView
    private lateinit var scoreText: TextView
    private lateinit var gameRep: GameRepository
    private lateinit var gameDAO: GameDAO
    private lateinit var worldsButton: ImageButton
    private lateinit var shopButton: ImageButton
    private lateinit var statsButton: ImageButton
    private lateinit var galleryButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ (API 30+)
            window.setDecorFitsSystemWindows(false) // Permette al layout di estendersi su tutto lo schermo
            val controller = window.insetsController
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
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
        worldsButton = findViewById(R.id.classicChallengeButton)
        shopButton = findViewById(R.id.shopButton)
        statsButton = findViewById(R.id.statsButton)
        galleryButton = findViewById(R.id.galleryButton)
        moneyText = findViewById(R.id.money)
        scoreText = findViewById(R.id.score)

        //init database
        gameDAO = GameDatabase.getDatabase(application).gameDao()
        gameRep = GameRepository(gameDAO)

        UpdateUI()

        galleryButton.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
        }

        worldsButton.setOnClickListener {
            val intent = Intent(this, WorldsActivity::class.java)
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
    }

    private fun UpdateUI() {
        gameRep.money.observe(this) { moneyValue ->
            moneyText.text = "$moneyValue"}
        gameRep.score.observe(this){ scoreValue ->
            scoreText.text = "$scoreValue"}
    }

    // Aggiorna la UI quando si torna alla schermata principale
    override fun onResume() {
        super.onResume()
            UpdateUI()
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