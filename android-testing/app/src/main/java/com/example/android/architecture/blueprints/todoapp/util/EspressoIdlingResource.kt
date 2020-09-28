package com.example.android.architecture.blueprints.todoapp.util

import androidx.test.espresso.idling.CountingIdlingResource

/**
 * Created by suson on 9/27/20
 */
object EspressoIdlingResource  {
    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResouce = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResouce.increment()
    }

    fun decrement() {
        if (!countingIdlingResouce.isIdleNow) {
            countingIdlingResouce.decrement()
        }
    }
}

inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
    EspressoIdlingResource.increment()
    return try {
        function()
    } finally {
        EspressoIdlingResource.decrement()
    }
}