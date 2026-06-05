package com.marketdata.sfmcdemo.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class EventEntry(
    val type: String,
    val name: String,
    val detail: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    fun formattedTime(): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
