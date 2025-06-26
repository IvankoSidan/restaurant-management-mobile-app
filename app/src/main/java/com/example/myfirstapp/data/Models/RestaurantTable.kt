package com.example.myfirstapp.data.Models

import com.example.myfirstapp.data.Enums.TableStatus
import com.example.myfirstapp.data.Enums.TableType

data class RestaurantTable(
    val idTable: Long = 0,
    val number: String,
    val capacity: Int,
    val floor: Int,
    var status: TableStatus,
    val name: String?,
    val location: String?,
    val description: String?,
    val x: Float,
    val y: Float,
    val table_type: TableType,
    val imagePath: String?
)