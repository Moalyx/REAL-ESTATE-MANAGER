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
     * Conversion of a real estate property price (Dollars to Euros).
     * NOTE: DO NOT DELETE, TO BE SHOWN DURING THE SOUTENANCE.
     *
     * @param dollars price in dollars
     * @return price in euros
     */
    fun convertDollarToEuro(dollars: Int): Int {
        return (dollars * 0.92).roundToInt()
    }

    /**
     * Conversion of a real estate property price (Euros to Dollars).
     *
     * @param euros price in euros
     * @return price in dollars
     */
    fun convertEuroToDollar(euros: Int): Int {
        return (euros / 0.92).roundToInt()
    }

    /**
     * Conversion of today's date into a more appropriate format.
     * NOTE: DO NOT DELETE, TO BE SHOWN DURING THE SOUTENANCE.
     *
     * @return formatted date string
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