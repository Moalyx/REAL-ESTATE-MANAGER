package com.tuto.realestatemanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

fun <T> LiveData<T>.observeForTesting(block: () -> Unit) {
    val observer = Observer<T> { }

    try {
        observeForever(observer)
        block()
    } finally {
        removeObserver(observer)
    }
}

fun <T> LiveData<T>.getOrAwaitValue(): T {
    var data: T? = null
    val latch = CountDownLatch(1)

    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            data = value
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    observeForever(observer)

    if (!latch.await(2, TimeUnit.SECONDS)) {
        removeObserver(observer)
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}

fun <T> LiveData<T>.observeForTesting(
    testScope: TestScope,
    pause: Boolean = false,
    block: (LiveData<T>) -> Unit
) {
    val observer = Observer<T> { }
    try {
        observeForever(observer)
        if (!pause) {
            testScope.runCurrent()
        }
        block(this)
    } finally {
        removeObserver(observer)
    }
}