package com.example.easydo.util

import com.example.easydo.model.Todo
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Validation states for the to-do item that the user is attempting to save.
 */
enum class TodoValidationStatus {
    Valid,
    Empty
}

/**
 * Validates the to-do that the user is attempting to save.
 */
@Singleton
class TextValidation @Inject constructor() {

    // returns true if this todo passes validation and can be saved to Room database
    fun validateTodo(todo: Todo): TodoValidationStatus {
        var validationStatus = TodoValidationStatus.Valid

        if (todo.title.isEmpty() && todo.description.isEmpty()) {
            validationStatus = TodoValidationStatus.Empty
        }

        return validationStatus
    }
}