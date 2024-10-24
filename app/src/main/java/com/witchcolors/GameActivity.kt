package com.witchcolors

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.witchcolors.DAO.GameDAO
import com.witchcolors.config.GameDatabase
import com.witchcolors.repository.GameRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.appcompat.app.AlertDialog
import com.witchcolors.model.Item
import kotlinx.coroutines.cancel

class GameActivity : AppCompatActivity() {

    private lateinit var colorToFind: TextView
    private lateinit var scoreText: TextView
    private lateinit var livesText: TextView
    private lateinit var moneyText: TextView
    private lateinit var timerText: TextView
    private lateinit var objectsLayout: GridLayout
    private lateinit var gameRep: GameRepository
    private lateinit var gameDAO: GameDAO
    private lateinit var colors: List<String>

    private var score = 0
    private var lives = 3
    private var money = 0
    private var currentLevel = 1
    private var targetColor: String = ""
    private var timer: CountDownTimer? = null
    private var timeLeft: Long = 30000 // 60 seconds
    private var ReviveStatus: Boolean = false

    @SuppressLint("MissingInflatedId")
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
        gameDAO = GameDatabase.getDatabase(application).gameDao()
        gameRep = GameRepository(gameDAO)

        // Inizializzazione delle variabili xml
        colorToFind = findViewById(R.id.colorToFind)
        scoreText = findViewById(R.id.score)
        livesText = findViewById(R.id.lives)
        moneyText = findViewById(R.id.money)
        timerText = findViewById(R.id.timer)
        objectsLayout = findViewById(R.id.objectsLayout)
        colors = listOf("Rosso", "Blu", "Verde", "Giallo", "Rosa", "Nero", "Celeste", "Arancione", "Viola", "Bianco")

        //level start from 0
        startNewLevel()
    }

    private fun startNewLevel() {
        if (lives <= 0) {
            showGameOver()
            return
        }
        targetColor = colors.random() // Funzione per ottenere un nuovo colore
        colorToFind.text = "Trova il colore: $targetColor"
        setupObjects() // Funzione per impostare gli oggetti colorati

        startTimer()
    }

    private fun setupObjects() {
        objectsLayout.removeAllViews()
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
            }else{
                showVictory()
            }

        } else {
            lives--
            if (lives <= 0) {
                livesText.text = "Vite: $lives"
                showGameOver()
            }else {
                livesText.text = "Vite: $lives"
            }
        }
    }

    private fun getColorFromName(colorName: String): Int {
        return when (colorName) {
            "Rosso" -> Color.RED
            "Blu" -> Color.BLUE
            "Verde" -> Color.GREEN
            "Giallo" -> Color.YELLOW
            "Rosa" -> Color.parseColor("#FFC0CB")
            "Bianco" -> Color.WHITE
            "Viola" -> Color.parseColor("#AE52D5")
            "Nero" -> Color.BLACK
            "Celeste" -> Color.CYAN
            "Arancione" -> Color.parseColor("#FF5722")
            else -> Color.TRANSPARENT
        }
    }

    private fun showVictory() {
        //aggiungere un qualcosa che fa capire che hai vinto
        stopTimer()
        startNewLevel()
    }

    private fun showGameOver() {
        stopTimer()
        val hasReviveToken = checkReviveToken()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Game Over")
        builder.setMessage("Tempo scaduto!")

        // Se hai un "Gettone Rinascita"
        if (hasReviveToken) {
            builder.setPositiveButton("Rinascita(1 volta)") { dialog, which ->
                //useReviveToken()
                ReviveStatus = true
                dialog.dismiss()
                startNewLevel() // Riprende il gioco dal livello corrente
            }
        }

        // Torna al menu
        builder.setNegativeButton("Torna al Menu") { dialog, which ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // impedisce la chiusura
        builder.setCancelable(false)
        builder.show()

        UpdateMoneyScore()
    }

    private fun grantUpgrade() {
        stopTimer()
        val upgrades = listOf("Potenziamento 1", "Potenziamento 2", "Potenziamento 3")
        val randomUpgrade = upgrades.random()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Potenziamento")
        builder.setMessage("Hai ricevuto: $randomUpgrade")

        builder.setPositiveButton("Avanti") { dialog, which ->
            dialog.dismiss()
            startNewLevel()
        }
        builder.setNegativeButton("Torna al Menu") { dialog, which ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Impedisce la chiusura
        builder.setCancelable(false)
        builder.show()

        UpdateMoneyScore()
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

    // NON FUNZIONA PERCHE' IL CHECK E' SEMPRE FALSE DATO CHE IL TRUE é DENTRO UNA COROUTINE
    private fun checkReviveToken(): Boolean {
        //Se sei già stato resuscitato una volta
        if (ReviveStatus == true) {
            return false
        }
        var check = false

        CoroutineScope(Dispatchers.IO).launch {
            val item: Item? = gameDAO.getItemByName(itemName = "Resurrection_Token")
            if(item != null) {
                if(item.playerItemId?.equals(1) == true) {
                    check = true
                    cancel()
                }
            }
        }
        return check
    }

    /*private fun useReviveToken() {
       CoroutineScope(Dispatchers.IO).launch {
            val player = gameRep.getPlayer().value
            player?.let {
                // Rimuovi il gettone rinascita dall'inventario
                it.inventory.remove("Gettone Rinascita")

                // Aggiungi una vita o resetta lo stato di gioco
                lives = 1
                livesText.text = "Vite: $lives"

                // Aggiorna il giocatore nel database
                gameRep.updatePlayer(it)
            }
        }
    }*/

    fun UpdateMoneyScore() {
        //aggiorna con i valori attuali deve fare moneycurrent + money
        CoroutineScope(Dispatchers.IO).launch {
            gameRep.updatePlayerMoneyScore(Id = 1, Money = money, Score = score )
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

