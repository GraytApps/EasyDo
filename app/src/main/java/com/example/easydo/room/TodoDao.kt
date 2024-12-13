package com.example.easydo.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.easydo.model.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo")
    fun getAll(): Flow<List<Todo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg todo: Todo)

    @Query("SELECT * FROM todo WHERE uid = :todoId")
    suspend fun getById(todoId: String): Todo

    @Delete
    fun delete(todo: Todo)
}