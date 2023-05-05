package com.tuto.realestatemanager

import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class TestCoroutineRule : TestRule {

    val testCoroutineDispatcher = StandardTestDispatcher()
    private val testCoroutineScope = TestScope(testCoroutineDispatcher)

    override fun apply(base: Statement, description: Description): Statement = object : Statement() {
        @Throws(Throwable::class)
        override fun evaluate() {
            Dispatchers.setMain(testCoroutineDispatcher)

            base.evaluate()

            Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        }
    }

//    fun runTest(block: suspend TestScope.() -> Unit) = testCoroutineScope.runTest {
//        block()
//    }
//
//    fun getTestCoroutineDispatcherProvider() = mockk<CoroutineDispatcherProvider> {
//        every { main } returns testCoroutineDispatcher
//        every { io } returns testCoroutineDispatcher
//    }
}