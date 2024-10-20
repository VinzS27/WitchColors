package com.witchcolors.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.witchcolors.model.Item

@Dao
interface ItemDAO {

    @Insert
    suspend fun insertItem(item: Item)

    @Query("SELECT * FROM inventory_table")
    suspend fun getAllItems(): List<Item>

    @Query("DELETE FROM inventory_table WHERE name = :itemName")
    suspend fun deleteItemByName(itemName: String)
}
