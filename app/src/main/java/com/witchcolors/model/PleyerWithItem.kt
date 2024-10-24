package com.witchcolors.model

import androidx.room.Embedded
import androidx.room.Relation

//Pojo class 1
data class PlayerWithItems(
    @Embedded val player: Player,
    @Relation(
        parentColumn = "id", // Colonna nella tabella Player
        entityColumn = "playerItemId" // Colonna nella tabella Item che si riferisce al Player
    )
    val items: List<Item>
)