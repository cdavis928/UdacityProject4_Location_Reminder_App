package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.Result.Success
import com.udacity.project4.locationreminders.data.dto.Result.Error

//Use FakeDataSource that acts as a test double to the LocalDataSource

// Adding parameter of mutableListOf reminders
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) :
    ReminderDataSource {

    private var shouldReturnError = false

    fun setShouldReturnError(returnError: Boolean) {
        this.shouldReturnError = returnError
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Error("Reminders not found")
        } else {
            return Success(ArrayList(reminders))
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError) {
            return Error("Error")
        } else {
            val reminder = reminders?.find { it.id == id }
            return if (reminder != null) {
                Success(reminder)
            } else {
                Error("Reminder not found")
            }
        }
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }

}