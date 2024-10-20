package com.witchcolors.config

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.witchcolors.DAO.ItemDAO
import com.witchcolors.DAO.PlayerDAO
import com.witchcolors.model.Item
import com.witchcolors.model.Player

@Database(entities = [Player::class, Item::class], version = 1, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDAO
    abstract fun itemDao(): ItemDAO

    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null

        fun getDatabase(context: Context): GameDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "player_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
