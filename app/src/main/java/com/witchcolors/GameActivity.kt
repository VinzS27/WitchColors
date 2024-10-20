package com.witchcolors

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.witchcolors.DAO.PlayerDAO
import com.witchcolors.config.GameDatabase
import com.witchcolors.model.Player
import com.witchcolors.repository.PlayerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity() {

    private lateinit var colorToFind: TextView
    private lateinit var scoreText: TextView
    private lateinit var livesText: TextView
    private lateinit var moneyText: TextView
    private lateinit var timerText: TextView
    private lateinit var objectsLayout: LinearLayout
    private lateinit var returnButton: Button
    private lateinit var playerRep: PlayerRepository
    private lateinit var playerDAO: PlayerDAO

    private var score = 0
    private var lives = 3
    private var money = 0
    private var currentLevel = 1
    private var targetColor: String = ""
    private var timer: CountDownTimer? = null
    private var timeLeft: Long = 30000 // 60 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game)

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

        //Get player from db
        playerDAO = GameDatabase.getDatabase(application).playerDao()
        playerRep = PlayerRepository(playerDAO)

        // Inizializzazione delle variabili xml
        colorToFind = findViewById(R.id.colorToFind)
        scoreText = findViewById(R.id.score)
        livesText = findViewById(R.id.lives)
        moneyText = findViewById(R.id.money)
        timerText = findViewById(R.id.timer)
        objectsLayout = findViewById(R.id.objectsLayout)
        returnButton = findViewById(R.id.returnButton)

        //Buttons
        returnButton.setOnClickListener {
            // Torna al menu principale
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //level start from 0
        startNewLevel()
    }

    private fun startNewLevel() {
        if (lives <= 0) {
            showGameOver()
            return
        }
        targetColor = getNewColor() // Funzione per ottenere un nuovo colore
        colorToFind.text = "Trova il colore: $targetColor"
        setupObjects() // Funzione per impostare gli oggetti colorati

        startTimer()
    }

    private fun getNewColor(): String {
        val colors = listOf("Rosso", "Blu", "Verde", "Giallo")
        return colors.random()
    }

    private fun setupObjects() {
        objectsLayout.removeAllViews()
        val colors = listOf("Rosso", "Blu", "Verde", "Giallo") // Colori disponibili
        for (color in colors) {
            val colorView = Button(this)
            colorView.text = color
            colorView.setBackgroundColor(getColorFromName(color)) // Funzione per ottenere il colore
            colorView.setOnClickListener {
                checkColor(color)
            }
            objectsLayout.addView(colorView)
        }
    }

    private fun checkColor(selectedColor: String) {
        if (selectedColor == targetColor) {
            score += 10
            money += 5
            scoreText.text = "Punteggio: $score"
            moneyText.text = "Soldi: $money"
            currentLevel++

            // potenziamento ogni 10 livelli
            if (currentLevel % 10 == 0) {
                grantUpgrade()
            }
            // Aggiorna le statistiche
            showVictory()
        } else {
            lives--
            livesText.text = "Vite: $lives"
            if (lives <= 0) {
                showGameOver()
            }
        }
    }

    private fun showVictory() {
        Toast.makeText(this, "Hai vinto! Un nuovo colore è stato generato!", Toast.LENGTH_SHORT).show()
        stopTimer()
        startNewLevel()
    }

    private fun grantUpgrade() {
        val upgrades = listOf("Potenziamento 1", "Potenziamento 2", "Potenziamento 3")
        val randomUpgrade = upgrades.random()
        Toast.makeText(this, "Hai ricevuto: $randomUpgrade!", Toast.LENGTH_SHORT).show()
        // Implementa la logica del potenziamento qui
        UpdateDatabase()
    }

    private fun startTimer() {
        timer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                timerText.text = "Tempo: ${timeLeft / 1000}"
            }

            override fun onFinish() {
                showGameOver()
            }
        }.start()
    }

    private fun stopTimer() {
        timer?.cancel()
        timeLeft = 30000 // Reset del timer
        timerText.text = "Tempo: ${timeLeft / 1000}"
    }

    private fun showGameOver() {
        stopTimer()
        Toast.makeText(this, "Tempo scaduto! Torna al menu.", Toast.LENGTH_SHORT).show()
        returnButton.visibility = View.VISIBLE
        UpdateDatabase()
        Toast.makeText(this, "Database updated", Toast.LENGTH_SHORT).show()
    }

    private fun getColorFromName(colorName: String): Int {
        return when (colorName) {
            "Rosso" -> Color.RED
            "Blu" -> Color.BLUE
            "Verde" -> Color.GREEN
            "Giallo" -> Color.YELLOW
            else -> Color.TRANSPARENT
        }
    }

    //Persistenza tra sessioni non funziona
    fun UpdateDatabase() {
        val currentMoney = playerRep.money
        //aggiorna con i valori attuali deve fare moneycurrent + money
        CoroutineScope(Dispatchers.IO).launch {
            val p = Player(id=1, money=money, score=score)
            playerRep.updatePlayer(p)
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

