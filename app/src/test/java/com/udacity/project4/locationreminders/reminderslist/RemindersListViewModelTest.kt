package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsNot.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    // Subject under test
    private lateinit var remindersListViewModel: RemindersListViewModel

    // Use a fake data source to be injected into the viewmodel
    private lateinit var fakeReminderDataSource: FakeDataSource

    // You use this to help test LiveData
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupRemindersListViewModel() {

        stopKoin()

        //Initialize firebase
        //Added per solution in https://knowledge.udacity.com/questions/501735
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())

        // We initialize the data source with no reminders
        fakeReminderDataSource = FakeDataSource()

        // We initialize the viewmodel
        remindersListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeReminderDataSource
        )

    }

    @Test
    fun loadReminders_resultNotEmpty() = runBlockingTest {

        // Add a fake reminder to the data source for testing
        val reminder1 = ReminderDTO(
            "title1", "description1",
            "location1", 1.0, 1.0, "000"
        )
        fakeReminderDataSource.saveReminder(reminder1)

        // Load the reminders from the database
        remindersListViewModel.loadReminders()

        // The list is not empty
        assertThat(remindersListViewModel.remindersList.getOrAwaitValue().isEmpty(), `is`(false))

        // Progress indicator will not be shown
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))

        // No Data screen will not be shown
        assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), `is`(false))

    }

    @Test
    fun noData_resultIsEmpty() = runBlockingTest {

        // Delete all reminders
        fakeReminderDataSource.deleteAllReminders()

        // Loading reminders without any reminders to load
        remindersListViewModel.loadReminders()

        // List is empty
        assertThat(remindersListViewModel.remindersList.getOrAwaitValue().isEmpty(), `is`(true))

        // No Data screen will be shown
        assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), `is`(true))

    }

    @Test
    fun loadRemindersWhenNoneAvailable_returnError() = runBlockingTest {
        fakeReminderDataSource.setShouldReturnError(true)
        remindersListViewModel.loadReminders()
        assertThat(
            remindersListViewModel.showSnackBar.getOrAwaitValue(),
            `is`("Reminders not found")
        )
    }

    @Test
    fun loadReminders_showLoading() = runBlockingTest {
        // Pause dispatcher
        mainCoroutineRule.pauseDispatcher()

        // Load reminders in viewmodel
        remindersListViewModel.loadReminders()

        // Progress indicator is shown
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))

        // Execute pending coroutines
        mainCoroutineRule.resumeDispatcher()

        // Progress indicator is hidden, No Data screen is shown
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), `is`(true))

    }


}