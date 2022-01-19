package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    // Add testing implementation to the RemindersLocalRepository.kt
    private lateinit var localDataBase: RemindersDatabase

    // Class under test
    private lateinit var remindersLocalRepository: RemindersLocalRepository

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        localDataBase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        remindersLocalRepository =
            RemindersLocalRepository(localDataBase.reminderDao(), Dispatchers.Main)
    }

    // Close the database after we are finished testing
    @After
    fun cleanUp() {
        localDataBase.close()
    }

    private fun getReminder(): ReminderDTO {
        return ReminderDTO(
            title = "title",
            description = "description",
            location = "location",
            latitude = 10.0,
            longitude = 10.0
        )
    }

    @Test
    fun saveReminder_retrievesReminder() = runBlocking {
        // GIVEN - new reminder saved in the database
        val testReminder = getReminder()
        remindersLocalRepository.saveReminder(testReminder)

        // WHEN - reminder retrieved by ID
        val result = remindersLocalRepository.getReminder(testReminder.id)

        // THEN - the same reminder is returned
        assertThat(result is Result.Success, `is`(true))
        result as Result.Success

        assertThat(result.data.title, `is`(testReminder.title))
        assertThat(result.data.description, `is`(testReminder.description))
        assertThat(result.data.latitude, `is`(testReminder.latitude))
        assertThat(result.data.longitude, `is`(testReminder.longitude))
        assertThat(result.data.location, `is`(testReminder.location))
    }

    @Test
    fun deleteAllReminders_getRemindersById() = runBlocking {
        // GIVEN - a new task in the persistent repository
        val reminder = getReminder()
        remindersLocalRepository.saveReminder(reminder)
        remindersLocalRepository.deleteAllReminders()

        // WHEN - completed in the persistent repository
        val result = remindersLocalRepository.getReminder(reminder.id)

        // THEN - task can be retrieved from persistent repository and is complete
        assertThat(result is Result.Error, `is`(true))
        result as Result.Error
        assertThat(result.message, `is`("Reminder not found!"))
    }

}