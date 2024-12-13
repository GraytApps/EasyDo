package com.example.easydo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easydo.model.Todo
import com.example.easydo.model.TodoRepository
import com.example.easydo.util.TextValidation
import com.example.easydo.util.TodoValidationStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class NewTodoUiState(
    val editingTodo: Todo? = null,
    val validationStatus: TodoValidationStatus? = null
)

@HiltViewModel
class NewTodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val textValidation: TextValidation
) : ViewModel() {

    private val _newTodoUiState = MutableStateFlow(NewTodoUiState())
    val newTodoUiState: StateFlow<NewTodoUiState> = _newTodoUiState.asStateFlow()

    fun saveTodo(title: String, description: String, completed: Boolean = false): Boolean {
        val newTodo = Todo(
            title = title,
            description = description,
            completed = false
        )

        // If the user was editing an existing item, we ensure that the old item will get overwritten
        // with new values instead of creating a new item.
        _newTodoUiState.value.editingTodo?.let {
            newTodo.uid = it.uid
            newTodo.completed = it.completed
        }

        // Before saving, ensure that the todo is valid and can be saved
        if (textValidation.validateTodo(newTodo) != TodoValidationStatus.Valid) {
            _newTodoUiState.update { uiState ->
                uiState.copy(
                    validationStatus = TodoValidationStatus.Empty
                )
            }
            return false
        }

        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.addTodo(newTodo)
        }

        return true
    }

    fun editTodo(todoId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val editingTodo = todoRepository.getTodoById(todoId)

            withContext(Dispatchers.Main) {
                _newTodoUiState.update { uiState ->
                    uiState.copy(
                        editingTodo = editingTodo
                    )
                }
            }
        }
    }

    // No item needs to be passed since the NewTodoFragment can only edit one single item - the one that was passed in
    // and we already have a reference to it (editingTodo)
    fun setTodoCompleted(completed: Boolean) {
        _newTodoUiState.value.editingTodo?.let {
            viewModelScope.launch(Dispatchers.IO) {
                it.completed = completed
                todoRepository.addTodo(it)
            }
        }
    }

    fun validationAlertShown() {
        _newTodoUiState.update { uiState ->
            uiState.copy(
                validationStatus = null
            )
        }
    }
}