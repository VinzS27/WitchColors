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
    fun updatePlayerMoney(Id:Int, Money:Int)

    @Query("UPDATE player_table SET score = score + :Score WHERE id =:Id")
    fun updatePlayerScore(Id:Int, Score:Int)

    //aggiorna i soldi + quelli guadagnati e lo score con quello attuale
    @Query("UPDATE player_table SET money = money + :Money, score = CASE WHEN score > :Score THEN score ELSE :Score END WHERE id = :Id")
    fun updatePlayerMoneyScore(Id: Int, Money: Int, Score: Int)
}
