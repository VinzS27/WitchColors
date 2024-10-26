package com.witchcolors.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "gallery_table")

data class Gallery(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var name: String,
    var type: String,
    var reward: Int,
    var img: String,
) : Parcelable
{
    //TODO
}
