package com.codewithngoc.instagallery.data.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
fun formatTimeAgo(createdAt: String): String {
    return try {
        val instant = Instant.parse(createdAt)
        val now = Instant.now()
        val minutes = ChronoUnit.MINUTES.between(instant, now)
        val hours = ChronoUnit.HOURS.between(instant, now)
        val days = ChronoUnit.DAYS.between(instant, now)

        when {
            minutes < 1 -> "Vừa xong"
            minutes < 60 -> "$minutes phút trước"
            hours < 24 -> "$hours giờ trước"
            days < 7 -> "$days ngày trước"
            else -> {
                val dateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
                "${dateTime.dayOfMonth}/${dateTime.monthValue}/${dateTime.year}"
            }
        }
    } catch (e: Exception) {
        "" // nếu parse lỗi thì trả về chuỗi rỗng
    }
}
