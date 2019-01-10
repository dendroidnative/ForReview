package com.forreview.model

data class Stage(

    val id: String,

    val title: String,

    val subtitle: String,

    private val type: Type,

    val startMeditationIconPath: String,

    val staticBgPath: String,

    val dynamicBgPath: String,

    private var status: Status,

    private var currentDayNumber: Int,

    val daysCount: Int
) {

    fun getStatus() = status

    fun getType() = type

    fun getCurrentDayNumber() = currentDayNumber

    fun incrementCurrentDayNumber(): Boolean {
        if (currentDayNumber == daysCount - 1) {
            setCompleted()
            return true
        }

        currentDayNumber += 1
        return false
    }

    fun setActive() {
        status = Status.ACTIVE
        currentDayNumber = 0
    }

    fun setBlocked() {
        status = Status.BLOCKED
        currentDayNumber = 0
    }

    fun setCompleted() {
        status = Status.COMPLETED
        currentDayNumber = 0
    }

    fun setActiveDay(dayNumber: Int) {
        currentDayNumber = dayNumber
        status = Status.ACTIVE
    }

    enum class Status {
        BLOCKED, COMPLETED, ACTIVE
    }

    enum class Type {
        PAID, FREE
    }
}