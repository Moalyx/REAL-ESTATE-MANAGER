package com.tuto.realestatemanager.ui.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object Utils {

    /**
     * Converts a real estate price from dollars to euros.
     * Keep this method because it is part of the original project requirements.
     */
    fun convertDollarToEuro(dollars: Int): Int {
        return (dollars * 0.92).roundToInt()
    }

    fun convertEuroToDollar(euros: Int): Int {
        return (euros / 0.92).roundToInt()
    }

    /**
     * Returns today's date using the expected display format.
     * Keep this method because it is part of the original project requirements.
     */
    fun todayDate(): String {

        val dateFormat: DateFormat =
            SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)

        return dateFormat.format(Date())
    }

    fun formatToUS(dateString: String): String {

        val inputFormat =
            SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)

        val outputFormat =
            SimpleDateFormat("MM/dd/yyyy", Locale.US)

        val date = inputFormat.parse(dateString) ?: return dateString

        return outputFormat.format(date)
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(network) ?: return false

            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo?.isConnected == true
        }
    }
}