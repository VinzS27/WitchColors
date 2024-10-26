package com.witchcolors.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "item_table")
data class Item (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String = "",
    var price: Int = 0,
    var quantity: Int = 0,
    var img: String? = null,  // Campo per immagine (percorso o URL)
    ): Parcelable
{
    //TODO
}