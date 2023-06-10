/*
 * Copyright (c) 2023 trian.app.
 */

package app.trian.core.ui.extensions

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

fun BigDecimal.formatToRupiah(show: Boolean = true): String {
    return if (show) {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        format.maximumFractionDigits = 0
        format.format(this)
    } else "Rp*********"
}

fun BigDecimal.formatDecimal(): String = try {
    val decimalFormat = DecimalFormat("#,###")
    decimalFormat.format(this)
} catch (e: Exception) {
    ""
}


