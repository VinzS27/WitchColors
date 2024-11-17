package com.witchcolors

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.witchcolors.DAO.GameDAO
import com.witchcolors.model.Item
import com.witchcolors.model.Player
import com.witchcolors.config.GameDatabase
import com.witchcolors.repository.GameRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShopActivity : AppCompatActivity() {

    private lateinit var moneyText: TextView
    private lateinit var reviveButton: ImageButton
    private lateinit var scoreButton: ImageButton
    private lateinit var poisonButton: ImageButton
    private lateinit var iceButton: ImageButton
    private lateinit var gameRep: GameRepository
    private lateinit var gameDAO: GameDAO

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        //FULLSCREEN
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

        //init variable
        moneyText = findViewById(R.id.moneyTextView)
        reviveButton = findViewById(R.id.reviveButton)
        iceButton = findViewById(R.id.icePotionButton)
        poisonButton = findViewById(R.id.poisonPotionButton)
        scoreButton = findViewById(R.id.scorePotionButton)

        //Get player from db
        gameDAO = GameDatabase.getDatabase(application).gameDao()
        gameRep = GameRepository(gameDAO)

        UpdateUI()

        reviveButton.setOnClickListener {
            val item = "Resurrection_Token"
            buyItems(item)
        }
        iceButton.setOnClickListener {
            val item = "Gelo"
            buyItems(item)
        }
        poisonButton.setOnClickListener {
            val item = "Veleno"
            buyItems(item)
        }
        scoreButton.setOnClickListener {
            val item = "Double_Score"
            buyItems(item)
        }
    }

    // Aggiorna la UI quando si torna alla schermata principale
    override fun onResume() {
        super.onResume()
        UpdateUI()
    }

    private fun UpdateUI() {
        gameRep.money.observe(this) { moneyValue ->
            moneyText.text = "$moneyValue"}
    }

    private fun buyItems(itemName:String) {
        CoroutineScope(Dispatchers.IO).launch {
            val item: Item? = gameDAO.getItemByName(itemName = itemName)
            if(item != null) {
                gameRep.buyItem(1, item.id, item.price)
            }
        }
        UpdateUI()
    }

    // FULLSCREEN RESET ON TOUCH
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


