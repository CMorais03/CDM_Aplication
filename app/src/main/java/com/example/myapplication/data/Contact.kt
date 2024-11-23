package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    val name: String = "",
    val phone: String = "",
    var favourite: Int = 0,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)


