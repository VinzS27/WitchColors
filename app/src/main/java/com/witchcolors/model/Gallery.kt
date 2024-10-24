package com.witchcolors.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "gallery_table",
    foreignKeys = [ForeignKey(
        entity = Player::class,
        parentColumns = ["id"],
        childColumns = ["playerGalleryId"],
        onDelete = ForeignKey.CASCADE // Se elimini un player, gli oggetti collegati vengono eliminati
    )],
    indices = [Index(value = ["playerGalleryId"])] // Aggiungiamo un indice per migliorare le prestazioni delle query
)

data class Gallery(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var name: String,
    var type: String,
    var reward: Int,
    var img: String,
    var playerGalleryId: Int // Foreign key che collega la galleria al giocatore
) : Parcelable
{
    //TODO
}
