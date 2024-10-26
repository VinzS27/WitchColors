package com.witchcolors.DAO

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.witchcolors.model.Item
import com.witchcolors.model.Player

@Dao
interface GameDAO {

    @Query("SELECT * FROM player_table LIMIT 1")
    fun getPlayer(): LiveData<List<Player>>

    @Query("SELECT money FROM player_table LIMIT 1")
    fun getMoney(): LiveData<Int?>

    @Query("SELECT score FROM player_table LIMIT 1")
    fun getScore(): LiveData<Int?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayer(player: Player)

    @Query("UPDATE player_table SET money = money + :Money WHERE id =:Id")
    suspend fun updatePlayerMoney(Id:Int, Money:Int)

    @Query("UPDATE player_table SET money = money + :Money, score = CASE WHEN score > :Score THEN score ELSE :Score END WHERE id = :Id")
    suspend fun updatePlayerMoneyScore(Id: Int, Money: Int, Score: Int)


    //-------------- ITEM DAO --------------------//

    @Query("SELECT * FROM item_table")
    fun getAllItems(): LiveData<List<Item>>

    @Query("SELECT * FROM item_table WHERE name = :itemName LIMIT 1")
    suspend fun getItemByName(itemName: String): Item?

    @Query("SELECT quantity FROM item_table WHERE name = :itemName")
    fun getQuantityByName(itemName: String): LiveData<Int?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItem(item: Item)

    @Query("UPDATE item_table SET quantity = quantity + :Quantity WHERE id =:Id")
    suspend fun updateItemQuantityById(Id:Int, Quantity:Int)

}
