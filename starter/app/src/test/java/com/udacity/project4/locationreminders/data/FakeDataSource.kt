package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import java.lang.Exception

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders : MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {

//    Done: Create a fake data source to act as a double to the real data source

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        //Done("Return the reminders")
        reminders?.let { return Result.Success(ArrayList(it)) }
        return Result.Error(
            "Reminder not found"
        )
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
    //    Done("return the reminder with the id")
       val result = reminders?.firstOrNull { it.id == id }
        result?.let { return Result.Success(result) }
        return Result.Error("Reminder is not here")
    }

    override suspend fun deleteAllReminders() {
reminders?.clear()    }


}