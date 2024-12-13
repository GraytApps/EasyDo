package com.example.easydo.model

import com.example.easydo.room.AppDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepository @Inject constructor(
    private val database: AppDatabase
) {
    private val todoDao = database.todoDao()
    val todoItems: Flow<List<Todo>> = todoDao.getAll()

    fun addTodo(todo: Todo) {
        todoDao.insertAll(todo)
    }

    suspend fun getTodoById(todoId: String): Todo {
        return todoDao.getById(todoId)
    }

    fun delete(todo: Todo) {
        todoDao.delete(todo)
    }
}