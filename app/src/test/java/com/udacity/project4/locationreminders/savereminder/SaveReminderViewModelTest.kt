package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    // Subject under test
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    // Use a fake data source to be injected into the viewmodel
    private lateinit var fakeReminderDataSource: FakeDataSource

    // You use this to help test LiveData
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup() {

        stopKoin()

        // We initialize the data source with no reminders
        fakeReminderDataSource = FakeDataSource()

        // We initialize the viewmodel
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeReminderDataSource)

    }

    // Create this function here so we can use it down below to make example reminders
    private fun getReminder(): ReminderDataItem {
        return ReminderDataItem(
            title = "title",
            description = "description",
            location = "location",
            latitude = 1.0,
            longitude = 1.0
        )
    }

    // Test to ensure the loading is working properly
    @Test
    fun checkLoading() = runBlockingTest {

        val reminderTest = getReminder()

        mainCoroutineRule.pauseDispatcher()

        saveReminderViewModel.validateAndSaveReminder(reminderTest)
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    // Test that the reminders are being saved
    @Test
    fun saveReminder() = runBlockingTest {
        val reminderTest = getReminder()
        saveReminderViewModel.saveReminder(reminderTest)
        assertThat(saveReminderViewModel.showToast.getOrAwaitValue(), `is`("Reminder Saved !"))
    }

    // Test to ensure snackbar is triggered when title is missing
    @Test
    fun saveReminder_noTitle_showSnackBarValue() = runBlockingTest {
        val reminderTest = ReminderDataItem(
            title = "",
            description = "description",
            location = "location",
            latitude = 10.0,
            longitude = 10.0
        )

        saveReminderViewModel.validateAndSaveReminder(reminderTest)
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), notNullValue())
    }

    // Test to ensure snackbar is triggered when location is missing
    @Test
    fun saveReminder_noLocation_showSnackBarValue() = runBlockingTest {
        val reminderTest = ReminderDataItem(
            title = "title",
            description = "description",
            location = "",
            latitude = 10.0,
            longitude = 10.0
        )

        saveReminderViewModel.validateAndSaveReminder(reminderTest)
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), notNullValue())
    }

}