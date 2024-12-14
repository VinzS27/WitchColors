package com.witchcolors

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.witchcolors.DAO.GameDAO
import com.witchcolors.config.GameDatabase
import com.witchcolors.repository.GameRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.appcompat.app.AlertDialog
import com.witchcolors.View.DarkView
import com.witchcolors.model.Item
import com.witchcolors.utility.ColorsUtility
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class GameActivity : AppCompatActivity() {

    private lateinit var colorToFind: TextView
    private lateinit var scoreText: TextView
    private lateinit var livesText: TextView
    private lateinit var moneyText: TextView
    private lateinit var timerText: TextView
    private lateinit var levelsText: TextView
    private lateinit var comboText: TextView
    private lateinit var potionScoreButton: ImageButton
    private lateinit var potionPoisonButton: ImageButton
    private lateinit var potionFreezeButton: ImageButton
    private lateinit var objectsLayout: GridLayout
    private lateinit var gameRep: GameRepository
    private lateinit var gameDAO: GameDAO
    private lateinit var gameGrid: GameGrid
    private lateinit var darkView: DarkView
    private lateinit var effectOverlay: FrameLayout
    private lateinit var fullscreenEffect: ImageView

    private var score = 0
    private var lives = 3
    private var money = 0
    private var currentLevel = 0
    private var scoreCombo = 0
    private var level = 0
    private var maxLevel = 0
    private var worldIndex = 0
    private var darkJob: Job? = null //Job per ciclo buio luce
    private var swapJob: Job? = null // Job per gestire il timer di scambio
    private var swapSpeed: Long = 0
    private val bombAnimationJobs = mutableListOf<Job>() //job per intervallo di scoppio delle bombe
    private val activeBombButtons = mutableListOf<ImageButton>() //tiene traccia delle bombe attive
    private val objectsButton = mutableListOf<View>() // Lista per tenere traccia degli oggetti nella griglia
    private var targetColor: String = ""
    private var timer: CountDownTimer? = null
    private var timeLeft: Long = 0
    private var ReviveStatus: Boolean = false
    private var hasReviveToken: Boolean = false
    private var hasPoisonPotion: Boolean = false
    private var hasFreezePotion: Boolean = false
    private var hasDoubleScorePotion: Boolean = false
    private var rows: Int = 5 //TODO diminuire dimensioni img
    private var cols: Int = 4
    private var emptyCell: Int = 16

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

        // Init xml
        colorToFind = findViewById(R.id.colorToFind)
        scoreText = findViewById(R.id.score)
        livesText = findViewById(R.id.lives)
        moneyText = findViewById(R.id.money)
        timerText = findViewById(R.id.timer)
        levelsText = findViewById(R.id.levels)
        comboText = findViewById(R.id.combo)
        objectsLayout = findViewById(R.id.objectsLayout)
        potionScoreButton = findViewById(R.id.powerScore)
        potionFreezeButton = findViewById(R.id.powerIce)
        potionPoisonButton = findViewById(R.id.powerPoison)
        fullscreenEffect = findViewById(R.id.fullscreenEffects)

        //witch spells
        effectOverlay = findViewById(R.id.effectOverlay)
        darkView = DarkView(this, null).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        effectOverlay.addView(darkView)

        //powers
        potionPoisonButton.setOnClickListener {
            if (hasPoisonPotion) {
                usePoisonEffect()
            }
        }

        potionFreezeButton.setOnClickListener {
            if (hasFreezePotion) {
                useFreezeEffect()
            }
        }

        potionScoreButton.setOnClickListener {
            if (hasDoubleScorePotion) {
                useDoubleScoreEffect()
            }
        }

        //Get current world and current level for settings
        worldIndex = intent.getIntExtra("worldIndex", 0)
        level = intent.getIntExtra("level", 0)
        maxLevel = level*10

        // Setting colors grid
        gameGrid = GameGrid(rows, cols, emptyCell, level, worldIndex)
        timeLeft = gameGrid.getInitialTimer()

        // Check player utils
        CoroutineScope(Dispatchers.IO).launch {
            val itemsToCheck = mapOf(
                "Resurrection_Token" to { hasReviveToken = true },
                "Veleno" to { activatePoison() },
                "Gelo" to { activateFreeze() },
                "Double_Score" to { activateDoubleScore() }
            )

            itemsToCheck.forEach { (itemName, action) ->
                val item = gameDAO.getItemByName(itemName)
                if (item != null && item.quantity > 0) {
                    action.invoke() // Esegui l'azione associata
                }
            }
        }

        //level start from 0
        startNewLevel() //TODO mettere un caricamento prima di cominciare il livello
    }

    private fun startNewLevel() {
        if (lives <= 0) {
            showGameOver()
            return
        }
        gameGrid.resetBoard()

        val fullCells = gameGrid.getFullCells()
        val (randomRow, randomCol) = fullCells.random()
        //così il target è sempre una cella piena
        targetColor = gameGrid.getColorAt(randomRow, randomCol)
        colorToFind.text = "Trova il colore $targetColor"
        levelsText.text = "$currentLevel/$maxLevel"


        drawGrid()
        increaseDifficulty()
        if (scoreCombo == 3 && (1..100).random() <= gameGrid.getSpellProbability()) {
            castSpells() // lancia un incantesimo
        }
        startTimer()
    }

    private fun castSpells(){
        val spell = gameGrid.getWitchSpell()
        when (spell) {
            "buio" -> activateDarkMode()
            "nebbia" -> activateFog()
//            "mescola" -> activateShuffle()
//            "cassa" -> activeBoxs()
        }
    }

    private fun increaseDifficulty() {
        // Attiva gli scambi automatici per i livelli 5 e successivi
        if (currentLevel >= 5) {
            startAutomaticSwaps()
        }

        //testo di colore random
        if (currentLevel >= 10) {
            val randomColorName = ColorsUtility.getRandomColorName()
            colorToFind.setTextColor(ColorsUtility.getColorFromName(randomColorName))
            swapSpeed=500
        }
    }

    private fun startNightAnimation() {
        darkJob = CoroutineScope(Dispatchers.Main).launch {
            val totalTime = 2000L // Tempo per portare la tendina giù
            val frameDuration = 16L // Durata di un frame (circa 60 fps)
            val totalFrames = totalTime / frameDuration
            var currentFrame = 0

            while (isActive) {
                val progress = (currentFrame.toFloat() / totalFrames)

                // Calcola l'altezza trasparente per far scendere la tendina
                val maxHeight = darkView.height.toFloat()
                val height = maxHeight * progress // Tendina scende

                darkView.updateHolePosition(height)

                // Verifica se la tendina è completamente giù
                if (progress >= 1f) {
                    fadeOutDarkViewAnimation() // Dissolvenza tendina
                    fadeInButtonsAnimation(2000,1f)   // comparsa bottoni
                    break
                } else {
                    currentFrame++
                }
                delay(frameDuration)
            }
        }

    }

    private fun stopNightAnimation() {
        effectOverlay.visibility = View.GONE
        darkJob?.cancel()
        fadeInDarkViewAnimation()
        fadeInButtonsAnimation(0,1f)   // comparsa bottoni
        effectOverlay.removeView(darkView)
    }

    private fun activateDarkMode(){
        darkView = DarkView(this, null).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        darkView.alpha = 1f
        effectOverlay.addView(darkView)
        effectOverlay.visibility = View.VISIBLE
        startNightAnimation()
        fadeOutButtonsAnimation(2000, 0f)
    }

    private fun activateFog() {
        fullscreenEffect.visibility = View.VISIBLE
        fadeOutViewAnimation(9000) //9s
        fadeOutButtonsAnimation(500, 0.5f)
    }

    private fun stopFullscreenEffect(){
        fadeInViewAnimation()
        fullscreenEffect.visibility = View.GONE
        fadeInButtonsAnimation(0,1f)   // comparsa bottoni
    }

    //scomparsa tendina scura
    private fun fadeOutDarkViewAnimation() {
        darkView.animate()
            .alpha(0f) // Imposta trasparenza a 0
            .setDuration(2000L) // Durata 2 secondi
            .withEndAction {
                darkView.alpha = 1f // Resetta
                effectOverlay.visibility = View.GONE
            }
            .start()
    }

    //comparsa tendina scura
    private fun fadeInDarkViewAnimation() {
        darkView.animate()
            .alpha(1f) // Imposta trasparenza a 0
            .setDuration(0L) // Durata 2 secondi
            .start()
    }

    //scomparsa bottoni
    private fun fadeOutButtonsAnimation(duration:Long, alpha:Float) {
        objectsButton.forEach { button ->
            button.alpha = 1f // Imposta inizialmente trasparenza a 1
            button.animate()
                .alpha(alpha) // Gradualmente invisibili
                .setDuration(duration) // Durata 1 secondo
                .withEndAction {
                    button.alpha = alpha // Rimane invisibile
                }
                .start()
        }
    }

    //comparsa bottoni
    private fun fadeInButtonsAnimation(duration:Long, alpha:Float) {
        objectsButton.forEach { button ->
            button.alpha = 0f // Imposta inizialmente trasparenza a 0
            button.animate()
                .alpha(alpha) // Gradualmente visibili
                .setDuration(duration) // Durata 2 secondi
                .withEndAction {
                    button.alpha = alpha // Resetta la trasparenza per il prossimo ciclo
                }
                .start()
        }
    }

    //effetti fullscreen
    private fun fadeOutViewAnimation(duration:Long) {
        fullscreenEffect.animate()
            .alpha(0f) // Imposta trasparenza a 0
            .setDuration(duration)
            .withEndAction {
                fullscreenEffect.alpha = 1f // Resetta la trasparenza per il prossimo ciclo
                fullscreenEffect.visibility = View.GONE
            }
            .start()
    }

    //effetti fullscreen
    private fun fadeInViewAnimation() {
        fullscreenEffect.animate()
            .alpha(1f) // Imposta trasparenza a 1
            .setDuration(0)
            .start()
    }

    private fun startAutomaticSwaps() {
        // Avvia uno scambio automatico ogni secondo
        swapJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                performSwapWithAnimation()
                delay(1000-swapSpeed) //scambia questi due per far partire subito l'animazione
            }
        }
    }

    private fun stopAutomaticSwaps() {
        swapJob?.cancel()
    }

    private fun performSwapWithAnimation() {
        // Seleziona due posizioni casuali
        val row1 = (0 until rows).random()
        val col1 = (0 until cols).random()
        var row2: Int
        var col2: Int

        do {
            row2 = (0 until rows).random()
            col2 = (0 until cols).random()
        } while (row1 == row2 && col1 == col2)

        // Anima lo scambio degli ImageButton
        val button1 = getButtonAt(row1, col1)
        val button2 = getButtonAt(row2, col2)

        if (button1 != null && button2 != null) {
            animateSwap(button1, button2)
        }
    }

    //animazione che muove i bottoni
    private fun animateSwap(button1: ImageButton, button2: ImageButton) {
        // Animazione per il primo bottone
        button1.animate()
            .x(button2.x)
            .y(button2.y)
            .setDuration(500)
            .start()

        // Animazione per il secondo bottone
        button2.animate()
            .x(button1.x)
            .y(button1.y)
            .setDuration(500)
            .start()
    }

    private fun animateBomb(button: ImageButton) {
        activeBombButtons.add(button) // Aggiungi il bottone alla lista delle bombe attive
        val job = CoroutineScope(Dispatchers.Main).launch {
            repeat(5) { // Lampeggia 5 volte
                button.alpha = if (button.alpha == 1f) 0.5f else 1f
                delay(1000)
            }
            effectBomb() // Attiva l'effetto bomba dopo l'animazione
        }
        bombAnimationJobs.add(job) // Aggiungi il Job alla lista
    }

    private fun stopBombAnimation() {
        bombAnimationJobs.forEach { it.cancel() } // Cancella ogni Job attivo
        bombAnimationJobs.clear() // Svuota la lista

        // Disabilita tutti i bottoni delle bombe
        activeBombButtons.forEach { button ->
            button.alpha = 1f
            button.setImageResource(R.drawable.button_crack192)
            button.isEnabled = false
        }
        activeBombButtons.clear() // Svuota la lista dei bottoni
    }

    private fun effectBomb() {
        checkColor("bomba")
        stopBombAnimation()
        MediaPlayer.create(this, R.raw.explosion).start()
        val shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake)
        objectsLayout.startAnimation(shakeAnimation)
    }

    // Recupera il bottone nella posizione specifica del layout della griglia
    private fun getButtonAt(row: Int, col: Int): ImageButton? {
        val index = row * cols + col //ottengo sempre un buttone dentro la griglia
        return objectsLayout.getChildAt(index) as? ImageButton
    }

    //Imposta la matrice degli oggetti
    private fun drawGrid() {
        objectsLayout.removeAllViews()
        objectsButton.clear() // Pulisce la lista dei bottoni ad ogni nuova griglia
        var params: GridLayout.LayoutParams

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val color = gameGrid.getColorAt(i, j)
                if (color != "") { //se la cella non è vuota
                    val colorView = ImageButton(this)
                    val objectType = gameGrid.getRandomObjects()

                    val drawableResource =
                        ColorsUtility.getDrawableForObjectAndColor(objectType, color)
                    if (drawableResource != null) {
                        colorView.setImageResource(drawableResource)
                        colorView.foreground = getDrawable(R.drawable.ripple_effect) // effetto al click
                    }
                    params = setGridParams()
                    colorView.layoutParams = params
                    colorView.setBackgroundResource(R.drawable.sfondo192)

                    colorView.setOnClickListener {
                        checkColor(color)
                    }
                    objectsLayout.addView(colorView) // aggiungi il bottone alla griglia
                    objectsButton.add(colorView) // aggiungi il bottone alla lista

                } else {
                    val emptyView = ImageButton(this)
                    emptyView.foreground = getDrawable(R.drawable.ripple_effect)
                    //cella bomba
                    if ((1..100).random() <= gameGrid.getBombProbability()) {
                        ColorsUtility.getSpecialDrawableForObjectAndColor("bomba", targetColor)
                            ?.let { emptyView.setImageResource(it) }
                        animateBomb(emptyView)
                        emptyView.setOnClickListener {
                            effectBomb()
                        }
                    //cella vuota
                    } else {
                        emptyView.setOnClickListener {
                            checkColor(color)
                        }
                    }
                    params = setGridParams()
                    emptyView.layoutParams = params
                    emptyView.setBackgroundResource(R.drawable.sfondo192)
                    objectsLayout.addView(emptyView)
                    objectsButton.add(emptyView)
                }
            }
        }
    }

    //Parametri iniziali di ogni griglia
    private fun setGridParams(): GridLayout.LayoutParams {
        val params = GridLayout.LayoutParams()
        params.width = 192  // dimensioni spazio vuoto
        params.height = 192
        params.setMargins(5, 5, 5, 5)
        return params
    }

    private fun checkColor(selectedColor: String) {
        if (selectedColor == targetColor) {
            scoreCombo = (scoreCombo+1).coerceAtMost(3)
            updateUI(5, 10, 0)
            currentLevel++
            stopTimer()
            stopAutomaticSwaps()
            stopBombAnimation()
            stopNightAnimation()
            stopFullscreenEffect()

            // evento ogni 10 livelli
            if (currentLevel % 10 == 0) {
                gameGrid.updateEmptyCells(2) //aumenta gli oggetti di 2
                timeLeft = gameGrid.reduceTime(2000, timeLeft)
                grantPowerUp() //ottiene un potenziamento
            } else {
                startNewLevel()
            }
        } else {
            scoreCombo = 0
            updateUI(0, 0, -1)
            if (lives <= 0) {
                livesText.text = "0"
                showGameOver()
            }
        }
    }

    private fun showGameOver() {
        stopTimer()
        stopAutomaticSwaps()
        stopBombAnimation()
        stopNightAnimation()
        stopFullscreenEffect()

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

    private fun grantPowerUp() {
        if (currentLevel >= maxLevel) {
            finish()
        }else{
            val randomPowerup = gameGrid.getRandomPower()
            when (randomPowerup) {
                "Veleno" -> activatePoison()
                "Gelo" -> activateFreeze()
                "Double_Score" -> activateDoubleScore()
                "Magic_Dust" -> updateUI(100, 0, 0)
                "ExtraLife" -> updateUI(0, 0, 1)
                "ExtraTime" -> timeLeft+=5000L
            }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Bonus Raggiunto!")
            builder.setMessage("Hai ricevuto: $randomPowerup")

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
        }
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
        timerText.text = "${timeLeft / 1000}"
    }

    private fun useReviveToken() {
        // Aggiungi una vita o resetta lo stato di gioco
        lives = 1
        livesText.text = "$lives"
        ReviveStatus = true

        CoroutineScope(Dispatchers.IO).launch {
            val item: Item? = gameDAO.getItemByName(itemName = "Resurrection_Token")
            if (item != null) {
                if ((item.quantity > 0) == true) {
                    gameRep.updateItemQuantityById(Id = item.id, Quantity = -1)
                }
            }
        }
    }

    private fun updateMoneyScore() {
        //aggiorna con i valori attuali deve fare moneycurrent + money
        CoroutineScope(Dispatchers.IO).launch {
            gameRep.updatePlayerMoneyScore(Id = 1, Money = money, Score = score)
        }
    }

    private fun updateUI(m: Int, s: Int, l: Int) {
        money += m
        score += s*scoreCombo
        lives += l
        moneyText.text = "$money"
        scoreText.text = "$score"
        livesText.text = "$lives"
        comboText.text = "combo x$scoreCombo"
    }

    private fun activateFreeze() {
        hasFreezePotion = true
        potionFreezeButton.setImageResource(R.drawable.button_potion_ice)
    }

    private fun useFreezeEffect() {
        hasFreezePotion = false
        potionFreezeButton.setImageResource(R.drawable.button_potion_grey)

        stopTimer()
        stopAutomaticSwaps()
        stopBombAnimation()

        CoroutineScope(Dispatchers.IO).launch {
            val item: Item? = gameDAO.getItemByName(itemName = "Gelo")
            if (item != null) {
                if ((item.quantity > 0) == true) {
                    gameRep.updateItemQuantityById(Id = item.id, Quantity = -1)
                }
            }
        }
    }

    private fun activateDoubleScore() {
        hasDoubleScorePotion = true
        potionScoreButton.setImageResource(R.drawable.button_potion_score)
    }

    private fun useDoubleScoreEffect() {
        hasDoubleScorePotion = false
        potionScoreButton.setImageResource(R.drawable.button_potion_grey)
        updateUI(0, score, 0)
        CoroutineScope(Dispatchers.IO).launch {
            val item: Item? = gameDAO.getItemByName(itemName = "Double_Score")
            if (item != null) {
                if ((item.quantity > 0) == true) {
                    gameRep.updateItemQuantityById(Id = item.id, Quantity = -1)
                }
            }
        }
    }

    private fun activatePoison() {
        hasPoisonPotion = true
        potionPoisonButton.setImageResource(R.drawable.button_potion_poison)
    }

    private fun usePoisonEffect() {
        hasPoisonPotion = false
        potionPoisonButton.setImageResource(R.drawable.button_potion_grey)
        checkColor(targetColor)
        CoroutineScope(Dispatchers.IO).launch {
            val item: Item? = gameDAO.getItemByName(itemName = "Veleno")
            if (item != null) {
                if ((item.quantity > 0) == true) {
                    gameRep.updateItemQuantityById(Id = item.id, Quantity = -1)
                }
            }
        }
    }

    // Ferma il timer in caso di chiusura dell'attività
    override fun onDestroy() {
        super.onDestroy()
        stopAutomaticSwaps()
        stopTimer()
        stopBombAnimation()
        stopNightAnimation()
        stopFullscreenEffect()
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
                    controller.systemBarsBehavior =
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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

