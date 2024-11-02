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
import androidx.lifecycle.LiveData
import com.witchcolors.model.Item
import com.witchcolors.utility.ColorsUtility
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
    private lateinit var gameBoard: GameBoard

    private var score = 0
    private var lives = 3
    private var money = 0
    private var currentLevel = 1
    private var targetColor: String = ""
    private var timer: CountDownTimer? = null
    private var timeLeft: Long = 30000 // 60 seconds
    private var ReviveStatus: Boolean = false
    private var hasReviveToken: Boolean = false
    private var rows: Int = 3
    private var cols: Int = 6
    private var emptyCell: Int = 9

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
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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

        // Creazione della griglia di colori 3x3
        gameBoard = GameBoard(rows, cols, emptyCell)

        //level start from 0
        startNewLevel()

        //Check if player have the revive token
        CoroutineScope(Dispatchers.IO).launch {
            val item: Item? = gameDAO.getItemByName(itemName = "Resurrection_Token")
            if (item != null) {
                if (item.quantity > 0) {
                    hasReviveToken = true
                }
            }
        }
    }

    //***bloccare il tasto indietro***
    private fun startNewLevel() {
        if (lives <= 0) {
            showGameOver()
            return
        }
        gameBoard.resetBoard()
        val fullCells = gameBoard.getFullCells()
        val (randomRow, randomCol) = fullCells.random()

        targetColor = gameBoard.getColorAt(randomRow, randomCol)
        colorToFind.text = "Trova il colore: $targetColor"

        drawBoard()
        startTimer()
    }

    private fun drawBoard() {
        objectsLayout.removeAllViews()

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val color = gameBoard.getColorAt(i, j)
                if (color != "") {
                    val colorView = Button(this)
                    colorView.text = color
                    colorView.setBackgroundColor(ColorsUtility.getColorFromName(color))

                    val params = setParams()
                    colorView.layoutParams = params

                    colorView.setOnClickListener {
                        checkColor(color)
                    }
                    objectsLayout.addView(colorView)
                } else {
                    val emptyView = View(this)
                    emptyView.layoutParams = GridLayout.LayoutParams().apply {
                        width = 100  // Imposta dimensioni per lo spazio vuoto
                        height = 100
                    }
                    objectsLayout.addView(emptyView)
                }
            }
        }
    }

    private fun setParams(): GridLayout.LayoutParams {
        val params = GridLayout.LayoutParams()
        params.setMargins(25, 10, 30, 10) // Margine opzionale per distanziare i bottoni
        return params
    }

    private fun checkColor(selectedColor: String) {
        if (selectedColor == targetColor) {
            score += 10
            money += 5
            scoreText.text = "$score"
            moneyText.text = "$money"
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
                livesText.text = "$lives"
                showGameOver()
            }else {
                livesText.text = "$lives"
            }
        }
    }

    private fun showVictory() {
        //aggiungere un qualcosa che fa capire che hai vinto
        stopTimer()
        startNewLevel()
    }

    private fun showGameOver() {
        stopTimer()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Game Over")
        builder.setMessage("Tempo scaduto!")

        // Se hai un "Gettone Rinascita"
        if (hasReviveToken && !ReviveStatus) {
            builder.setPositiveButton("Rinascita(1 volta)") { dialog, which ->
                useReviveToken()
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

        updateMoneyScore()
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

        updateMoneyScore()
    }

    private fun startTimer() {
        timer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                timerText.text = "${timeLeft / 1000}"
            }

            override fun onFinish() {
                showGameOver()
            }
        }.start()
    }

    private fun stopTimer() {
        timer?.cancel()
        timeLeft = 30000 // Reset del timer
        timerText.text = "${timeLeft / 1000}"
    }

    private fun useReviveToken() {
        // Aggiungi una vita o resetta lo stato di gioco
        lives = 1
        livesText.text = "$lives"
        ReviveStatus = true

        CoroutineScope(Dispatchers.IO).launch {
            val item: Item? = gameDAO.getItemByName(itemName = "Resurrection_Token")
            if(item != null) {
                if((item.quantity > 0) == true) {
                    gameRep.updateItemQuantityById(Id=item.id,Quantity=-1)
                }
            }
        }
    }

    private fun updateMoneyScore() {
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

