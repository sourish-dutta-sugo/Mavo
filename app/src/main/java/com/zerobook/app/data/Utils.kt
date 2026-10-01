package com.zerobook.app.data

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Utils {

    val INDIAN_STATES = listOf(
        "Jammu & Kashmir" to "01",
        "Himachal Pradesh" to "02",
        "Punjab" to "03",
        "Chandigarh" to "04",
        "Uttarakhand" to "05",
        "Haryana" to "06",
        "Delhi" to "07",
        "Rajasthan" to "08",
        "Uttar Pradesh" to "09",
        "Bihar" to "10",
        "Sikkim" to "11",
        "Arunachal Pradesh" to "12",
        "Nagaland" to "13",
        "Manipur" to "14",
        "Mizoram" to "15",
        "Tripura" to "16",
        "Meghalaya" to "17",
        "Assam" to "18",
        "West Bengal" to "19",
        "Jharkhand" to "20",
        "Odisha" to "21",
        "Chhattisgarh" to "22",
        "Madhya Pradesh" to "23",
        "Gujarat" to "24",
        "Daman & Diu" to "25",
        "Dadra & Nagar Haveli" to "26",
        "Maharashtra" to "27",
        "Andhra Pradesh" to "28",
        "Karnataka" to "29",
        "Goa" to "30",
        "Lakshadweep" to "31",
        "Kerala" to "32",
        "Tamil Nadu" to "33",
        "Puducherry" to "34",
        "Andaman & Nicobar Islands" to "35",
        "Telangana" to "36",
        "Andhra Pradesh (New)" to "37",
        "Ladakh" to "38"
    )

    fun formatIndianCurrency(amount: Double): String =
        (if (amount < 0) "-" else "") + "₹" + DecimalFormat("#,##,##0.00").format(Math.abs(amount))

    fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date(timestamp))
    }

    fun parseShorthandDate(input: String): Long? {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return null
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val normalized = trimmed.replace(".", "/").replace("-", "/")
        val yearSuffix = "/$currentYear"
        val tryPatterns: (String) -> Long? = { value ->
            val candidates = listOf(
                "dd/MM/yyyy" to value,
                "d/M/yyyy" to value,
                "dd/MM/yy" to value,
                "M/d/yy" to value,
                "MM/dd/yyyy" to value,
                "M/d/yyyy" to value,
                "dd/MM" to value,
                "d/M" to value,
                "MM/dd" to value,
                "M/d" to value,
                "dd/MM" to value + yearSuffix,
                "d/M" to value + yearSuffix,
                "MM/dd" to value + yearSuffix,
                "M/d" to value + yearSuffix
            )
            var result: Long? = null
            for ((pattern, v) in candidates) {
                val parsed = runCatching {
                    SimpleDateFormat(pattern, Locale.ENGLISH).parse(v)?.time
                }.getOrNull() ?: continue
                val cal = Calendar.getInstance().apply { timeInMillis = parsed }
                val day = cal.get(Calendar.DAY_OF_MONTH)
                val month = cal.get(Calendar.MONTH) + 1
                val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                if (day in 1..maxDay && month in 1..12) {
                    result = parsed
                    break
                }
            }
            result
        }
        tryPatterns(normalized)?.let { return it }
        return null
    }

}
