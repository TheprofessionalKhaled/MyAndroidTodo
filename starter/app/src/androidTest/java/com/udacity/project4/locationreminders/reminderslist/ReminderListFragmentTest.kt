package com.udacity.project4.locationreminders.reminderslist

import android.content.Context
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.RemindersDao
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val Reminder1 = ReminderDTO("Title1","Description1","Location1",10.0,10.0,"j")
    private val Reminder2 = ReminderDTO("Title2","Description2","Location2",15.0,15.0,"k")
    private lateinit var reminderLocalRepository : RemindersLocalRepository
    private val dataDao = listOf<ReminderDTO>(Reminder1,Reminder2).sortedBy { it.id } as Result.Success<*>

    private lateinit var reminderListViewModer : RemindersListViewModel
    private lateinit var remindersDao: RemindersDao

//    Done: test the navigation of the fragments.

//    Done: test the displayed data on the UI.

    fun activeReminder_DisplayedInUI(){


        launchFragmentInContainer<ReminderListFragment>(Bundle(),R.style.AppTheme)
        Thread.sleep(200)
        onView(withId(R.id.reminderTitle)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.reminderDescription)).check(ViewAssertions.matches(isDisplayed()))



    }
    fun initRepository(){
        reminderLocalRepository = RemindersLocalRepository(remindersDao)
    }

    fun clickReminder_navigateToDetail() = runBlockingTest {
        reminderLocalRepository.saveReminder(Reminder1)
        reminderLocalRepository.saveReminder(Reminder2)
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(),R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!,navController)

        }
        onView(withId(R.id.addReminderFAB)).perform(RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
            hasDescendant(withText("title")), click()
        ))
        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()


        )

    }



//    Done: add testing for the error messages.
    fun loadRemindersWhenRemindersAreUnavailable_callErrorToDisplay(){
        reminderLocalRepository.setReturnError(true)
    reminderListViewModer.loadReminders()
    assertThat(reminderListViewModer.empty.getOrAwaitValue(), `is`(true))
    assertThat(reminderListViewModer.error.getOrAwaitValue(), `is`(true))

}
}