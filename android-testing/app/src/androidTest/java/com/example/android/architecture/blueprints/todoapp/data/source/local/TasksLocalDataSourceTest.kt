package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by suson on 9/27/20
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class TasksLocalDataSourceTest {

    // Executes each task synchronously
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var localDataSource: TasksLocalDataSource
    private lateinit var database: ToDoDatabase

    @Before
    fun setup() {
        // Using an in-memory database
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), ToDoDatabase::class.java)
                .allowMainThreadQueries()
                .build()

        localDataSource = TasksLocalDataSource(database.taskDao(), Dispatchers.Main)
    }

    @After
    fun clean() {
        database.close()
    }

    @Test
    fun saveTask_retrieveTask() {
        runBlocking {
            // given - a new task saved in the database
            val newTask = Task("title", "description", false)
            localDataSource.saveTask(newTask)

            // when - task retrieved by ID
            val result = localDataSource.getTask(newTask.id)

            // then - same task is returned
            assertThat(result.succeeded, `is`(true))
            result as Result.Success
            assertThat(result.data.title, `is`(newTask.title))
            assertThat(result.data.description, `is`(newTask.description))
            assertThat(result.data.isCompleted, `is`(newTask.isCompleted))
        }
    }

    @Test
    fun completeTask_retrievedTaskIsCompleted() {
        runBlocking {
            // given - a new task saved in the database
            val newTask = Task("title", "description", false)
            localDataSource.saveTask(newTask)

            localDataSource.completeTask(newTask.id)
            // retrieve the task

            val result = localDataSource.getTask(newTask.id)

            // then - same task is returned
            assertThat(result.succeeded, `is`(true))
            result as Result.Success
            assertThat(result.data.title, `is`(newTask.title))
            assertThat(result.data.description, `is`(newTask.description))
            assertThat(result.data.isCompleted, `is`(true))
        }
    }
}