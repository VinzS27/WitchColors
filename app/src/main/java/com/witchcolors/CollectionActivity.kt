package com.witchcolors

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import com.witchcolors.DAO.GameDAO
import com.witchcolors.config.GameDatabase
import com.witchcolors.model.Collection
import com.witchcolors.model.Item
import com.witchcolors.repository.GameRepository
import com.witchcolors.utility.ColorsUtility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CollectionActivity : AppCompatActivity() {

    private lateinit var collectionGrid: GridLayout
    private lateinit var gameRep: GameRepository
    private lateinit var gameDAO: GameDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)
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
        collectionGrid = findViewById(R.id.collectionGrid)

        //Get from db
        gameDAO = GameDatabase.getDatabase(application).gameDao()
        gameRep = GameRepository(gameDAO)

        // Recupera la categoria selezionata
        val categoryName = intent.getStringExtra("CATEGORY_NAME")

        // Recupera e popola i dati
        if (categoryName != null) {
            populateCollections(categoryName)
        }
    }

    // Recupera le collezioni della categoria specifica
    private fun populateCollections(categoryName: String) {
        gameDAO.getAllCollectionByCategory(categoryName).observe(this) { collections ->
            if (collections != null && collections.isNotEmpty()) {
                populateCollectionGrid(collections)
            }
        }
    }

    private fun populateCollectionGrid(collections: List<Collection>) {
        collectionGrid.removeAllViews()
        for (obj in collections) {
            val button = ImageButton(this).apply {
                setBackgroundResource(android.R.color.transparent)
                if (obj.collected){
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 345
                        height = 455
                    }
                    setImageResource(ColorsUtility.getCollection256FromName(obj.name))
                    setOnClickListener {
                        showObjectDetails(obj) // Mostra i dettagli dell'oggetto
                    }
                }else{
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 345
                        height = 455
                    }
                    setImageResource(R.drawable.card_retro_256)
                }
            }
            collectionGrid.addView(button)
        }
    }

    private fun showObjectDetails(obj: Collection) {
        val dialog = Dialog(this)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_object_details)
        dialog.findViewById<ImageView>(R.id.objectImage).setImageResource(ColorsUtility.getCollectionFullScreenFromName(obj.name))
        dialog.show()
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