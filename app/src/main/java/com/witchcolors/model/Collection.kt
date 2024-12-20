package com.witchcolors.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "collection_table")

data class Collection(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var name: String,
    var category: String,
    var description: String,
    var rarity: Int,
    var collected: Boolean,
) : Parcelable

