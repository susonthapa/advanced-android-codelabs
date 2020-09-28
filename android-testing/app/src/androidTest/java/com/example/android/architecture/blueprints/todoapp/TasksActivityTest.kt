package com.example.android.architecture.blueprints.todoapp

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.tasks.TasksActivity
import com.example.android.architecture.blueprints.todoapp.util.DataBindingIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.monitorActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by suson on 9/27/20
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class TasksActivityTest {
    private lateinit var repository: TasksRepository
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        repository = ServiceLocator.provideTasksRepository(getApplicationContext())
        runBlocking {
            repository.deleteAllTasks()
        }
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResouce)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResouce)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @After
    fun reset() {
        ServiceLocator.resetRepository()
    }

    @Test
    fun editTask() {
        runBlocking {
            // set initial state
            repository.saveTask(Task("title", "description"))

            // start up tasks screen
            val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
            dataBindingIdlingResource.monitorActivity(activityScenario)

            // click on the task on the list and verify that all the data is correct
            onView(withText("title")).perform(click())
            onView(withId(R.id.task_detail_title_text)).check(matches(withText("title")))
            onView(withId(R.id.task_detail_description_text)).check(matches(withText("description")))
            onView(withId(R.id.task_detail_complete_checkbox)).check(matches(not(isChecked())))

            // click on the edit button, edit and save
            onView(withId(R.id.edit_task_fab)).perform(click())
            onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("new title"))
            onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("new description"))
            onView(withId(R.id.save_task_fab)).perform(click())

            // verify task is displayed on screen in the task list
            onView(withText("new title")).check(matches(isDisplayed()))
            // verify previous task is not displayed
            onView(withText("title")).check(doesNotExist())

            activityScenario.close()
        }
    }

    @Test
    fun createOneTask_deleteTask() {
        // start task activity
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // add an active task by clicking on the fab and saving a new task
        onView(withId(R.id.add_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("title"))
        onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("description"))
        onView(withId(R.id.save_task_fab)).perform(click())

        // open the new task in a details view
        onView(withText("title")).perform(click())
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("title")))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("description")))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(not(isChecked())))

        // click the delete task in menu
        onView(withId(R.id.menu_delete)).perform(click())

        // verify it was deleted
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("title")).check(doesNotExist())

        activityScenario.close()

    }
}