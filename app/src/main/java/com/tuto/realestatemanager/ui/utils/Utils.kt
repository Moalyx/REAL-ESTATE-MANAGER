package com.tuto.realestatemanager.ui.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

object Utils {

    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @param dollars
     * @return
     */
    fun convertDollarToEuro(dollars: Int): Int {
        return (dollars * 0.812).roundToInt()
    }

    fun convertEuroToDollar(euros: Int): Int {
        return (euros / 0.812).roundToInt()
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @return
     */

    fun todayDate(): String {
        val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

}