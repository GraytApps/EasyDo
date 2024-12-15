package com.example.easydo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easydo.model.Todo
import com.example.easydo.model.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val todoItems: List<Todo> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val todoRepository: TodoRepository
) : ViewModel() {

    var todoForContextMenu: Todo? = null

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    init {
        viewModelScope.launch {
            todoRepository.todoItems.collect { todos ->

                // Update view with latest to-do items
                _homeUiState.update { uiState ->
                    val sortedTodoItems = todos.sortedBy { it.creationDate }
                    uiState.copy(
                        todoItems = sortedTodoItems
                    )
                }
            }
        }
    }

    /**
     * Sets the to-do item that the context menu will be shown for.
     *
     * @param todo the to-do item that the user long clicked on
     */
    fun onContextMenuBeingShownFor(todo: Todo) {
        todoForContextMenu = todo
    }

    /**
     * Deletes a to-do item
     *
     * @param todo the to-do that should be deleted
     */
    fun delete(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.delete(todo = todo)
        }
    }

    /**
     * Sets the to-do item as [completed]
     *
     * @param completed the completed status of the [todo] item
     */
    fun setTodoCompleted(todo: Todo, completed: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedTodo = todo.copy(completed = completed)
            todoRepository.addTodo(updatedTodo)
        }
    }
}