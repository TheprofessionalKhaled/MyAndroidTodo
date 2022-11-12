package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
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
    private val Reminder1 = ReminderDTO("Title1","Description1","Location1",10.0,10.0,"j")
    private val Reminder2 = ReminderDTO("Title2","Description2","Location2",15.0,15.0,"k")

    private val dataDao = listOf<ReminderDTO>(Reminder1,Reminder2).sortedBy { it.id } as Result.Success<*>
//    Done: Add testing implementation to the RemindersLocalRepository.kt


    private lateinit var remindersDao: RemindersDao
private lateinit var reminderDatabase : RemindersDatabase

    private lateinit var remindersLocalRepository: RemindersLocalRepository
    @Before
    fun createRepository(){
        reminderDatabase = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),RemindersDatabase::class.java)
            .allowMainThreadQueries().build()
        remindersLocalRepository = RemindersLocalRepository(reminderDatabase.reminderDao(),Dispatchers.Main)


    }
    val testDispatcher : TestCoroutineDispatcher = TestCoroutineDispatcher()
    @Before
    fun setupDispatcher(){
        Dispatchers.setMain(testDispatcher)
    }
    @After
    fun testDownDispatcer(){
        Dispatchers.resetMain()
    }
    @ExperimentalCoroutinesApi
    @Test
    fun getReminders() = runBlockingTest{
        val reminders = remindersLocalRepository.getReminders() as Result.Success
  assertThat(reminders, IsEqual(dataDao))
    }

    fun deleteReminders()= runBlockingTest {
        val reminders = remindersLocalRepository.deleteAllReminders()
        assertThat(reminders, `is`(nullValue()))
    }
    fun saveReminders()= runBlockingTest {
        val reminder1 = remindersLocalRepository.saveReminder(Reminder1) as Result.Success<*>
        val reminder2 = remindersLocalRepository.getReminder(Reminder1.id) as Result.Success
        assertThat(reminder2 , `is`(reminder1))
    }


}