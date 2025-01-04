package com.witchcolors.config

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.witchcolors.DAO.GameDAO
import com.witchcolors.model.Collection
import com.witchcolors.model.Item
import com.witchcolors.model.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Player::class, Item::class, Collection::class], version = 1, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {

    abstract fun gameDao(): GameDAO

    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null

        fun getDatabase(context: Context): GameDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "game_db"
                )
                    .addCallback(roomCallback) // Aggiungi la callback qui
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        // Definisci la callback per popolare il database
        private val roomCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    INSTANCE?.let { populateDatabase(it.gameDao()) }
                }
            }
        }

        // Metodo per popolare il database con dati iniziali
        suspend fun populateDatabase(dao: GameDAO) {
            // Inserisci gli item dello shop senza un giocatore associato
            dao.insertItem(Item(name = "Resurrection_Token", price = 100, quantity = 0))
            dao.insertItem(Item(name = "Gelo", price = 100, quantity = 0))
            dao.insertItem(Item(name = "Veleno", price = 100, quantity = 0))
            dao.insertItem(Item(name = "Double_Score", price = 100, quantity = 0))

            //Player
            dao.insertPlayer(Player(id = 1, money = 10000, score = 0))

            // Collezione strega
            dao.insertCollection(Collection(name = "felicità", category = "strega",
                rarity = 1, collected = true))
            dao.insertCollection(Collection(name = "tristezza", category = "strega",
                rarity = 1, collected = false))
            dao.insertCollection(Collection(name = "rabbia", category = "strega",
                rarity = 1, collected = false))
            dao.insertCollection(Collection(name = "paura", category = "strega",
                rarity = 1, collected = false))
            dao.insertCollection(Collection(name = "sorpresa", category = "strega",
                rarity = 1, collected = false))
            dao.insertCollection(Collection(name = "disgusto", category = "strega",
                rarity = 1, collected = false))
            dao.insertCollection(Collection(name = "vergogna", category = "strega",
                rarity = 1, collected = false))
            dao.insertCollection(Collection(name = "orgoglio", category = "strega",
                rarity = 1, collected = false))
            dao.insertCollection(Collection(name = "ansia", category = "strega",
                rarity = 1, collected = false))

            //Collezione mondo casa
            dao.insertCollection(Collection(name = "felicità", category = "casa",
                rarity = 1, collected = false))
            dao.insertCollection(Collection(name = "tristezza", category = "casa",
                rarity = 1, collected = false))
            dao.insertCollection(Collection(name = "rabbia", category = "casa",
                rarity = 1, collected = false))
            dao.insertCollection(Collection(name = "paura", category = "casa",
                rarity = 1, collected = false))
            dao.insertCollection(Collection(name = "sorpresa", category = "casa",
                rarity = 1, collected = false))
            dao.insertCollection(Collection(name = "disgusto", category = "casa",
                rarity = 1, collected = false))
            dao.insertCollection(Collection(name = "vergogna", category = "casa",
                rarity = 1, collected = false))
            dao.insertCollection(Collection(name = "orgoglio", category = "casa",
                rarity = 1, collected = false))
            dao.insertCollection(Collection(name = "ansia", category = "casa",
                rarity = 1, collected = false))

            //Collezione mondo castello
            dao.insertCollection(Collection(name = "felicità", category = "castello",
                rarity = 1, collected = false))
            dao.insertCollection(Collection(name = "tristezza", category = "castello",
                rarity = 1, collected = false))
            dao.insertCollection(Collection(name = "rabbia", category = "castello",
                rarity = 1, collected = false))
            dao.insertCollection(Collection(name = "paura", category = "castello",
                rarity = 1, collected = false))
            dao.insertCollection(Collection(name = "sorpresa", category = "castello",
                rarity = 1, collected = false))
            dao.insertCollection(Collection(name = "disgusto", category = "castello",
                rarity = 1, collected = false))
            dao.insertCollection(Collection(name = "vergogna", category = "castello",
                rarity = 1, collected = false))
            dao.insertCollection(Collection(name = "orgoglio", category = "castello",
                rarity = 1, collected = false))
            dao.insertCollection(Collection(name = "ansia", category = "castello",
                rarity = 1, collected = false))
        }
    }
}
