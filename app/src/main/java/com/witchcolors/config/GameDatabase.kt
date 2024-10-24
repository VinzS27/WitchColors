package com.witchcolors.config

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.witchcolors.DAO.GameDAO
import com.witchcolors.model.Gallery
import com.witchcolors.model.Item
import com.witchcolors.model.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Player::class, Item::class, Gallery::class], version = 1, exportSchema = false)
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
            dao.insertItem(Item(name = "Resurrection_Token", price = 100, quantity = 1))
            dao.insertPlayer(Player(id = 1, money = 1000, score = 0))
            // Aggiungi altri oggetti dello shop qui
        }
    }
}
