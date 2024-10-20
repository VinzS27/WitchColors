package com.witchcolors

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.witchcolors.DAO.PlayerDAO
import com.witchcolors.model.Item
import com.witchcolors.model.Player
import com.witchcolors.config.GameDatabase
import com.witchcolors.repository.PlayerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShopActivity : AppCompatActivity() {

    private lateinit var moneyText: TextView
    private lateinit var skinButton: Button
    private lateinit var reviveButton: Button
    private lateinit var playerRep: PlayerRepository
    private lateinit var playerDAO: PlayerDAO
    private lateinit var player: Player

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

        moneyText = findViewById(R.id.moneyTextView)
        skinButton = findViewById(R.id.skinButton)
        reviveButton = findViewById(R.id.reviveButton)

        UpdateUI()

        // Acquista una skin
        skinButton.setOnClickListener {
            val skin = Item(name = "Skin speciale", type = "Skin", price = 50)
            //buyItem(skin)
        }

        // Acquista un oggetto per resuscitare
        reviveButton.setOnClickListener {
            val reviveItem = Item(name = "Revive", type = "Oggetto speciale", price = 100)
            //buyItem(reviveItem)
        }
    }

    private fun UpdateUI() {
        playerRep.money.observe(this) { moneyValue ->
            moneyText.text = "Soldi: $moneyValue"}
    }

    private suspend fun buyItem(item: Item) {
        //logica d'acquisto
    }

    private suspend fun savePlayerData() {
        //logica di aggiornamento money e inventario db
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


