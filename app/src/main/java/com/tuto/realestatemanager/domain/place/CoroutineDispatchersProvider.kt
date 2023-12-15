package com.tuto.realestatemanager.domain.place

import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext
@Singleton
class CoroutineDispatchersProvider @Inject constructor() {
    val main: CoroutineContext = Dispatchers.Main
    val io: CoroutineContext = Dispatchers.IO
}