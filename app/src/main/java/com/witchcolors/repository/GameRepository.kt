package com.witchcolors.repository

import androidx.lifecycle.LiveData
import com.witchcolors.DAO.GameDAO
import com.witchcolors.model.Item
import com.witchcolors.model.Player
import com.witchcolors.model.Collection

class GameRepository(private val gameDAO: GameDAO) {

    //PLAYER
    val getPlayer: LiveData<List<Player>> = gameDAO.getPlayer()
    val money: LiveData<Int?> = gameDAO.getMoney()
    val score: LiveData<Int?> = gameDAO.getScore()
    //ITEM
    val getAllItems: LiveData<List<Item>> = gameDAO.getAllItems()
    //COLLECTION
    val getAllCollection: LiveData<List<Collection>> = gameDAO.getAllCollections()

    //----------PLAYER REPOSITORY----------------

    suspend fun updatePlayerMoneyScore(Id:Int, Money:Int, Score:Int){
        gameDAO.updatePlayerMoneyScore(Id,Money,Score)
    }

    suspend fun updatePlayerMoney(Id:Int,Money:Int){
        gameDAO.updatePlayerMoney(Id,Money)
    }

    //------------- ITEM REPOSITORY ----------------

    suspend fun getItemByName(itemName: String){
        gameDAO.getItemByName(itemName)
    }

    fun getQuantityByName(itemName: String){
        gameDAO.getQuantityByName(itemName)
    }

    suspend fun updateItemQuantityById(Id:Int, Quantity:Int){
        gameDAO.updateItemQuantityById(Id,Quantity)
    }

    suspend fun buyItem(id: Int, itemId: Int, itemPrice: Int) {
        if(itemPrice.toString() <= money.toString()){
            updatePlayerMoney(id, -itemPrice) // Sottrae il costo
            updateItemQuantityById(itemId,1)
        }
    }

    //------------- COLLECTION REPOSITORY ----------------
    fun getAllCollectionByCategory(categoryName: String){
        gameDAO.getAllCollectionByCategory(categoryName)
    }
}