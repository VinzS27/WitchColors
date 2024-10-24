package com.witchcolors.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "player_table")
data class Player(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var money: Int = 0,
    var score: Int = 0,
    ): Parcelable
{

}
