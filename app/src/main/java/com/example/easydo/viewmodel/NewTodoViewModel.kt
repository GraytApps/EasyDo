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
    val editingTodo: Todo? = null
)

@HiltViewModel
class NewTodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val textValidation: TextValidation
) : ViewModel() {

    private val _newTodoUiState = MutableStateFlow(NewTodoUiState())
    val newTodoUiState: StateFlow<NewTodoUiState> = _newTodoUiState.asStateFlow()

    /**
     * Saves a to-do item
     * @param title the title of the to-do
     * @param description the description of the to-do
     * @param completed whether or not this to-do item has been completed.
     * It is not possible for the user to set newly created to-do items to completed when initially creating them.
     *
     * @return The status of the to-do item that the user attempted to save. If the result is not [TodoValidationStatus.Valid],
     * an alert dialog should be shown explaining to the user what went wrong.
     */
    fun saveTodo(title: String, description: String, completed: Boolean): TodoValidationStatus {
        val newTodo = Todo(
            title = title,
            description = description,
            completed = completed
        )

        // When editing an existing to-do item, set its ID to match the item being edited.
        // This ensures the updated values overwrite the existing item, rather than creating a new one.
        _newTodoUiState.value.editingTodo?.let {
            newTodo.uid = it.uid
        }

        // Before saving, we ensure that the to-do is valid and can be saved
        val textValidation = textValidation.validateTodo(newTodo)
        if (textValidation == TodoValidationStatus.Valid) {
            viewModelScope.launch(Dispatchers.IO) {
                todoRepository.addTodo(newTodo)
            }
        }

        return textValidation
    }

    /**
     * Sets the to-do item that the user is editing.
     *
     * @param todoId the id of the to-do item. The id is used to access the To-do object from Room.
     */
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

    /**
     * Sets the to-do item as completed. This change will not be persisted until [saveTodo] is called.
     *
     * @param completed whether or not the to-do item is completed
     */
    fun setTodoCompleted(completed: Boolean) {
        _newTodoUiState.value.editingTodo?.let {
            it.completed = completed
        }
    }
}