package com.witchcolors

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.witchcolors.DAO.GameDAO
import com.witchcolors.config.GameDatabase
import com.witchcolors.repository.GameRepository

class GalleryActivity : AppCompatActivity() {

    private lateinit var moneyText: TextView
    private lateinit var scoreText: TextView
    private lateinit var gameRep: GameRepository
    private lateinit var gameDAO: GameDAO
    private lateinit var shopButton: ImageButton
    private lateinit var statsButton: ImageButton
    private lateinit var homeButton: ImageButton
    private lateinit var objectsGrid: GridLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

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
        shopButton = findViewById(R.id.shopButton)
        statsButton = findViewById(R.id.statsButton)
        homeButton = findViewById(R.id.homeButton)
        moneyText = findViewById(R.id.money)
        scoreText = findViewById(R.id.score)
        objectsGrid = findViewById(R.id.objectsContainer)

        //init database
        gameDAO = GameDatabase.getDatabase(application).gameDao()
        gameRep = GameRepository(gameDAO)

        UpdateUI()
        setupNavigationButtons()
        loadCollectionCategories()
    }

    private fun setupNavigationButtons() {
        homeButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        shopButton.setOnClickListener {
            startActivity(Intent(this, ShopActivity::class.java))
        }

        statsButton.setOnClickListener {
            startActivity(Intent(this, WitchStatsActivity::class.java))
        }
    }

    private fun setGridParams(): GridLayout.LayoutParams {
        val params = GridLayout.LayoutParams()
        params.setMargins(10, 10, 10, 10)
        return params
    }

    private fun loadCollectionCategories() {
        val collectionCategories = listOf(
            R.drawable.category_strega to "strega",
            R.drawable.category_casa to "casa",
            R.drawable.category_castello to "castello",
            /*R.drawable.category_casa to "giardino",
            R.drawable.category_casa to "secret",
            R.drawable.category_casa to "alternativo",
            R.drawable.category_casa to "oggetti",
            R.drawable.category_casa to "villain",
            R.drawable.category_casa to "storie"*/
        )

        for ((imageRes, categoryName) in collectionCategories) {
            val button = ImageButton(this).apply {
                layoutParams = setGridParams()
                setBackgroundResource(android.R.color.transparent)
                setImageResource(imageRes)
                setOnClickListener {
                    val intent = Intent(this@GalleryActivity, CollectionActivity::class.java)
                    intent.putExtra("CATEGORY_NAME", categoryName)
                    startActivity(intent)
                }
            }
            objectsGrid.addView(button)
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