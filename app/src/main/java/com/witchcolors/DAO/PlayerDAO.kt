package com.witchcolors.DAO

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.witchcolors.model.Player

@Dao
interface PlayerDAO {

    @Query("SELECT * FROM player_table LIMIT 1")
    fun getPlayer(): LiveData<List<Player>>

    // Query specifica per ottenere solo il valore di money
    @Query("SELECT money FROM player_table LIMIT 1")
    fun getMoney(): LiveData<Int?>

    // Query specifica per ottenere solo il valore di score
    @Query("SELECT score FROM player_table LIMIT 1")
    fun getScore(): LiveData<Int?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayer(player: Player)

    @Update
    suspend fun updatePlayer(player: Player)

    @Query("UPDATE player_table SET money = money + :Money WHERE id =:Id")
    fun updatePlayerMoney(Money:Int, Id:Int)

    @Query("UPDATE player_table SET score = score + :Score WHERE id =:Id")
    fun updatePlayerScore(Score:Int, Id:Int)

}
