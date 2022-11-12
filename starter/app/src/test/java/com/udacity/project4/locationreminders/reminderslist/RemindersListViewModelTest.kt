package com.udacity.project4.locationreminders.reminderslist

import android.service.autofill.Validators.not
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.Event
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.MatcherAssert.assertThat


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

private lateinit var dataSource : FakeDataSource
@get:Rule
var instantExecuterRule = InstantTaskExecutorRule()
    //Done: provide testing to the RemindersListViewModel and its live data objects

    @Test
    fun loadReminder_setsNewReminderEvent(){
        val remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),dataSource)
        remindersListViewModel.loadReminders()
        val reminderValue = remindersListViewModel.remindersList.getOrAwaitValue()
        assertThat(reminderValue, `is`(not(null))
        )
   }
}