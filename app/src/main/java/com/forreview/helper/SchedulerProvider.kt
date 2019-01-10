package com.forreview.helper

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

interface SchedulerProvider {
    fun net(): Scheduler

    fun io(): Scheduler

    fun ui(): Scheduler
}

class ImmediateSchedulerProvider : SchedulerProvider {
    override fun net(): Scheduler = Schedulers.trampoline()

    override fun io(): Scheduler = Schedulers.trampoline()

    override fun ui(): Scheduler = Schedulers.trampoline()
}


class AppSchedulerProvider : SchedulerProvider {
    override fun net(): Scheduler = Schedulers.io()

    override fun io(): Scheduler = Schedulers.single()

    override fun ui(): Scheduler = AndroidSchedulers.mainThread()
}
