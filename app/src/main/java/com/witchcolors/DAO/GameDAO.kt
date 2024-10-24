package com.witchcolors.DAO

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.witchcolors.model.Item
import com.witchcolors.model.Player
import com.witchcolors.model.PlayerWithGallery
import com.witchcolors.model.PlayerWithItems

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

    @Query("SELECT id FROM item_table WHERE name = :itemName LIMIT 1")
    fun getItemIdByName(itemName: String): Int?

    @Query("SELECT * FROM item_table WHERE name = :itemName AND playerItemId = :playerId LIMIT 1")
    suspend fun getItemByNameAndPlayerId(itemName: String, playerId: Int): Item?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItem(item: Item)

    @Query("UPDATE item_table SET quantity = quantity + :amount WHERE name = :itemName AND playerItemId = :playerId")
    suspend fun addItemToInventory(playerId: Int, itemName: String, amount: Int)

    @Query("UPDATE item_table SET quantity = quantity - :amount WHERE id = :itemId AND playerItemId = :playerId")
    suspend fun removeItemFromInventory(playerId: Int, itemId: Int, amount: Int)

    //----------- PLAYER WITH ITEM DAO -------------//

    @Transaction // Importante per le relazioni
    @Query("SELECT * FROM player_table WHERE id = :playerItemId")
    suspend fun getPlayerWithItems(playerItemId: Int): PlayerWithItems

    @Query("UPDATE item_table SET playerItemId = :playerId WHERE id = :itemId")
    suspend fun assignItemToPlayer(itemId: Int, playerId: Int)

    //----------- PLAYER WITH GALLERY DAO -------------//

    @Transaction // Importante per le relazioni
    @Query("SELECT * FROM player_table WHERE id = :playerGalleryId")
    suspend fun getPlayerWithGallery(playerGalleryId: Int): PlayerWithGallery
}
