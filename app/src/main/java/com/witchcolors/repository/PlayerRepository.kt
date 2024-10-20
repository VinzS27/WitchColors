package com.witchcolors.repository

import androidx.lifecycle.LiveData
import com.witchcolors.DAO.PlayerDAO
import com.witchcolors.model.Player

class PlayerRepository(private val playerDAO: PlayerDAO) {

    val getPlayer: LiveData<List<Player>> = playerDAO.getPlayer()

    // Funzione per ottenere il valore di money
    val money: LiveData<Int?> = playerDAO.getMoney()

    // Funzione per ottenere il valore di score
    val score: LiveData<Int?> = playerDAO.getScore()

    suspend fun insertPlayer(player: Player){
        playerDAO.insertPlayer(player)
    }

    suspend fun updatePlayer(player: Player){
        playerDAO.updatePlayer(player)
    }

    fun updatePlayerMoneyScore(Id:Int, Money:Int, Score:Int){
        playerDAO.updatePlayerMoneyScore(Id,Money,Score)
    }

    fun updatePlayerMoney(Id:Int,Money:Int){
        playerDAO.updatePlayerMoney(Id,Money)
    }

    fun updatePlayerScore(Id:Int,Score: Int){
        playerDAO.updatePlayerScore(Id,Score)
    }

}