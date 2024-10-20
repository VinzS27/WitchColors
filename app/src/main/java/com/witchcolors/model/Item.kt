package com.witchcolors.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_table")
class Item {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var name: String = ""
    var type: String = ""
    var price: Int = 0

    constructor(name: String, type: String, price: Int) {
        this.name = name
        this.type = type
        this.price = price
    }
}