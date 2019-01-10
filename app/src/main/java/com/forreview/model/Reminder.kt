package com.forreview.model

data class Reminder(
    var isSet: Boolean,
    var timeMillis: Long,
    var days: Days
) {

    enum class Days {
        WEEKDAYS, DAILY
    }
}