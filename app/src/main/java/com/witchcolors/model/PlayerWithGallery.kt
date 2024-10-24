package com.witchcolors.model

import androidx.room.Embedded
import androidx.room.Relation

//Pojo class 2
data class PlayerWithGallery(
    @Embedded val player: Player, // Include i dati del player
    @Relation(
        parentColumn = "id",       // Colonna che collega Player e Galleria
        entityColumn = "playerGalleryId"  // Colonna nella Galleria che memorizza l'id del player
    )
    val gallery: List<Gallery> // Lista degli oggetti della galleria appartenenti al Player
)
