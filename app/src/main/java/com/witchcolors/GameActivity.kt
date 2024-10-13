package com.witchcolors

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private lateinit var colorToFind: TextView
    private lateinit var scoreText: TextView
    private lateinit var livesText: TextView
    private lateinit var moneyText: TextView
    private lateinit var timerText: TextView
    private lateinit var objectsLayout: LinearLayout
    private lateinit var returnButton: Button

    private var score = 0
    private var lives = 3
    private var money = 0
    private var currentLevel = 1
    private var targetColor: String = ""
    private var timer: CountDownTimer? = null
    private var timeLeft: Long = 60000 // 60 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Inizializzazione delle variabili
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

            // Controlla se è tempo di un potenziamento
            if (currentLevel % 10 == 0) {
                grantUpgrade()
            }
            // Cambio colore e oggetti
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
}

