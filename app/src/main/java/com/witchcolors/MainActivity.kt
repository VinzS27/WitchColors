package com.witchcolors

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.LiveData
import com.witchcolors.DAO.PlayerDAO
import com.witchcolors.config.GameDatabase
import com.witchcolors.model.Player
import com.witchcolors.repository.PlayerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var moneyText: TextView
    private lateinit var scoreText: TextView
    private lateinit var playerRep: PlayerRepository
    private lateinit var playerDAO: PlayerDAO
    private lateinit var classicChallengeButton: Button
    private lateinit var shopButton: Button

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
        classicChallengeButton = findViewById(R.id.classicChallengeButton)
        shopButton = findViewById(R.id.shopButton)
        moneyText = findViewById(R.id.money)
        scoreText = findViewById(R.id.score)

        //init database
        playerDAO = GameDatabase.getDatabase(application).playerDao()
        playerRep = PlayerRepository(playerDAO)

        initializePlayer()
        UpdateUI()

        classicChallengeButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        shopButton.setOnClickListener {
            val intent = Intent(this, ShopActivity::class.java)
            startActivity(intent)
        }
    }
    private fun initializePlayer() {
        // Coroutine per eseguire operazioni di database in background
        CoroutineScope(Dispatchers.IO).launch {
            val player: LiveData<List<Player>> = playerRep.getPlayer
             //Se non c'è alcun giocatore, crea un nuovo giocatore con valori di default
            if (player == null) {
                val defaultPlayer = Player(money = 1000, score = 0) // Valori di default
                playerRep.insertPlayer(defaultPlayer)
            }
        }
    }

    private fun UpdateUI() {
        playerRep.money.observe(this) { moneyValue ->
            moneyText.text = "Soldi: $moneyValue"}
        playerRep.score.observe(this){ scoreValue ->
            scoreText.text = "Score: $scoreValue"}
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

    //Aggiungere funzione che quando si chiude la schermata i soldi si guadagnano ugualmente
}