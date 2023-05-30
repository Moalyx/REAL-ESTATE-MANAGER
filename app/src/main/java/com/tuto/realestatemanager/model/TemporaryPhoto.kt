package com.tuto.realestatemanager.model

import kotlin.math.abs
import kotlin.random.Random

data class TemporaryPhoto(
    val id: Long = abs(Random.nextLong()),
    val title: String,
    val uri: String,
)
