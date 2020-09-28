package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by suson on 9/27/20
 */

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TasksDaoTest  {
    // execute each task synchronously
    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ToDoDatabase

    @Before
    fun initDB() {
        database = Room.inMemoryDatabaseBuilder(getApplicationContext(), ToDoDatabase::class.java)
                .allowMainThreadQueries()
                .build()
    }

    @After
    fun closeDB() {
        database.close()
    }

    @Test
    fun insertTaskAndGetById() {
        runBlockingTest {
            // given - insert a task
            val task = Task("title", "description")
            database.taskDao().insertTask(task)

            // when - get the task by the id from the database
            val loaded = database.taskDao().getTaskById(task.id)

            // then - the loaded data contains the expected values
            assertThat<Task>(loaded as Task, notNullValue())
            assertThat(loaded.id, `is`(task.id))
            assertThat(loaded.title, `is`(task.title))
            assertThat(loaded.description, `is`(task.description))
            assertThat(loaded.isCompleted, `is`(task.isCompleted))
        }
    }

    @Test
    fun updateTaskAndGetById() {
        runBlockingTest {
            // given - insert a task
            val task = Task("title", "description", false, "id1")
            database.taskDao().insertTask(task)

            // update the task by creating a new task with the same ID but different attributes
            database.taskDao().updateCompleted(task.id, true)

            // get the loaded task
            val loaded = database.taskDao().getTaskById(task.id)

            // then - the loaded data contains the expected values
            assertThat(loaded as Task, notNullValue())
            assertThat(loaded.id, `is`(task.id))
            assertThat(loaded.title, `is`(task.title))
            assertThat(loaded.description, `is`(task.description))
            assertThat(loaded.isCompleted, `is`(true))

        }
    }
}