package com.tuto.realestatemanager.ui.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UtilsTest {

    @Test
    fun convertDollarToEuro_shouldReturnExpectedEuroValue() {
        val result = Utils.convertDollarToEuro(100)

        assertEquals(92, result)
    }

    @Test
    fun convertEuroToDollar_shouldReturnExpectedDollarValue() {
        val result = Utils.convertEuroToDollar(92)

        assertEquals(100, result)
    }

    @Test
    fun todayDate_shouldReturnCurrentDateWithExpectedFormat() {
        val expectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date())

        val result = Utils.todayDate()

        assertEquals(expectedDate, result)
    }

    @Test
    fun formatToUS_shouldReturnDateWithUSFormat() {
        val result = Utils.formatToUS("15/01/2018")

        assertEquals("01/15/2018", result)
    }
}