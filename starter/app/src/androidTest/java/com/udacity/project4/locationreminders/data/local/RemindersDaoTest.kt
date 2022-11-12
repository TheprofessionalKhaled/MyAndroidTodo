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
//Unit test the DAO
@SmallTest
class RemindersDaoTest {
@get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
//    Done: Add testing implementation to the RemindersDao.kt



    private lateinit var database: RemindersDatabase

        @Before
        fun initDb(){
            database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java).build()
        }

    fun closeDb() = database.close()
    @Test
    fun insertReminderAndGetById() = runBlockingTest {
        val reminder = ReminderDTO("Title1","Description1","Location1",10.0,10.0,"j")
        database.reminderDao().saveReminder(reminder)
       val loaded = database.reminderDao().getReminderById(reminder.id)

        assertThat<ReminderDTO>(loaded as ReminderDTO, notNullValue())
        assertThat(loaded?.id , `is` (reminder.id))
        assertThat(loaded?.title , `is` (reminder.title))
        assertThat(loaded?.description , `is` (reminder.description))
        assertThat(loaded?.latitude , `is` (reminder.latitude))
        assertThat(loaded?.longitude, `is` (reminder.longitude))
    }

}