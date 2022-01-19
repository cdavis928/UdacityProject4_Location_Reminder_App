package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RemindersDaoTest {

    private lateinit var remindersDatabase: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Setting up database
    @Before
    fun initDb() {
        remindersDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    // Close the database
    @After
    fun closeDb() = remindersDatabase.close()


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
    fun insertReminderAndFindById() = runBlockingTest {
        // GIVEN - save a reminder
        val testReminder = getReminder()
        remindersDatabase.reminderDao().saveReminder(testReminder)

        // WHEN - load the reminder by id from the database
        val loaded = remindersDatabase.reminderDao().getReminderById(testReminder.id)

        // THEN - the loaded data contains the expected values
        assertThat<ReminderDTO>(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id, `is`(testReminder.id))
        assertThat(loaded.title, `is`(testReminder.title))
        assertThat(loaded.description, `is`(testReminder.description))
        assertThat(loaded.location, `is`(testReminder.location))
        assertThat(loaded.latitude, `is`(testReminder.latitude))
        assertThat(loaded.longitude, `is`(testReminder.longitude))
    }
}