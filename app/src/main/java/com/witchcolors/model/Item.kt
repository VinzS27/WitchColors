package com.witchcolors.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
        tableName = "item_table",
        foreignKeys = [ForeignKey(
            entity = Player::class,
            parentColumns = ["id"],
            childColumns = ["playerItemId"],
            onDelete = ForeignKey.CASCADE // Se elimini un player, gli oggetti collegati vengono eliminati
    )],
    indices = [Index(value = ["playerItemId"])] // Aggiungiamo un indice per migliorare le prestazioni delle query
)
data class Item (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String = "",
    var price: Int = 0,
    var quantity: Int = 0,
    var img: String? = null,  // Campo per immagine (percorso o URL)
    var playerItemId: Int? = null
    ): Parcelable
{
    //TODO
}