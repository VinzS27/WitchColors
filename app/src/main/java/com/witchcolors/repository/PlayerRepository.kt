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

    suspend fun updatePlayerMoney(Money:Int, Id:Int){
        playerDAO.updatePlayerMoney(Money, Id)
    }

    suspend fun updatePlayerScore(Score:Int, Id:Int){
        playerDAO.updatePlayerScore(Score, Id)
    }

}