package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

private lateinit var dataSource: FakeDataSource
    //Done: provide testing to the SaveReminderView and its live data objects
    private lateinit var saveReminderViewModel: SaveReminderViewModel



    @get:Rule
    var instantExecuterRule = InstantTaskExecutorRule()

    @Test
    fun saveReminderTest(){
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(),dataSource)
        saveReminderViewModel.saveReminder(reminderData =  ReminderDataItem("title","description","location",10.00,10.00,"j"))
        val reminderDescriptionValue = saveReminderViewModel.reminderDescription.getOrAwaitValue()
        val reminderTitleValue = saveReminderViewModel.reminderTitle.getOrAwaitValue()
        val reminderSelectedLocationValue = saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue()
        val reminderSelectedPoi =saveReminderViewModel.selectedPOI.getOrAwaitValue()
        val reminderLatitude = saveReminderViewModel.latitude.getOrAwaitValue()
        val reminderLongitute = saveReminderViewModel.longitude.getOrAwaitValue()
         assertThat(reminderDescriptionValue, `is`(not(nullValue())))
        assertThat(reminderTitleValue, `is`(not(nullValue())))
        assertThat(reminderLatitude, `is`(not(nullValue())))
        assertThat(reminderLongitute, `is`(not(nullValue())))
        assertThat(reminderSelectedLocationValue, `is`(not(nullValue())))
        assertThat(reminderSelectedPoi, `is`(not(nullValue())))
    }


}