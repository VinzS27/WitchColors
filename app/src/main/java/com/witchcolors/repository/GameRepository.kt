package com.witchcolors.repository

import androidx.lifecycle.LiveData
import com.witchcolors.DAO.GameDAO
import com.witchcolors.model.Item
import com.witchcolors.model.Player
import com.witchcolors.model.PlayerWithGallery
import com.witchcolors.model.PlayerWithItems

class GameRepository(private val gameDAO: GameDAO) {

    //PLAYER
    val getPlayer: LiveData<List<Player>> = gameDAO.getPlayer()
    val money: LiveData<Int?> = gameDAO.getMoney()
    val score: LiveData<Int?> = gameDAO.getScore()

    //ITEM
    val getAllItems: LiveData<List<Item>> = gameDAO.getAllItems()

    //----------PLAYER REPOSITORY----------------

    suspend fun updatePlayerMoneyScore(Id:Int, Money:Int, Score:Int){
        gameDAO.updatePlayerMoneyScore(Id,Money,Score)
    }

    suspend fun updatePlayerMoney(Id:Int,Money:Int){
        gameDAO.updatePlayerMoney(Id,Money)
    }

    //------------- ITEM REPOSITORY ----------------

    suspend fun getItemByName(itemName: String){
        gameDAO.getItemIdByName(itemName)
    }

    suspend fun assignItemToPlayer(itemId: Int, playerId: Int){
        gameDAO.assignItemToPlayer(itemId,playerId)
    }

    suspend fun buyItem(id: Int, itemId: Int, itemPrice: Int) {
        if(itemPrice.toString() <= money.toString()){
            updatePlayerMoney(id, -itemPrice) // Sottrae il costo
            assignItemToPlayer(itemId, id)// Assegna l'item al giocatore
        }
    }

    suspend fun removeItemFromInventory(playerId: Int, itemId: Int, amount: Int) {
        gameDAO.removeItemFromInventory(playerId, itemId, amount)
    }

    //-----------PLAYER WITH ITEMS--------------------
    suspend fun getPlayerWithItems(playerItemId: Int): PlayerWithItems {
        return gameDAO.getPlayerWithItems(playerItemId)
    }

    //-----------PLAYER WITH GALLERY--------------------
    suspend fun getPlayerWithGallery(playerGalleryId: Int): PlayerWithGallery {
        return gameDAO.getPlayerWithGallery(playerGalleryId)
    }
}