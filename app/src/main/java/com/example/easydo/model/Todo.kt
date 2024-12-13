package com.example.easydo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "todo")
data class Todo(
    @PrimaryKey var uid: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "completed") var completed: Boolean,
    @ColumnInfo(name = "creation_date") var creationDate: Date = Date()
)