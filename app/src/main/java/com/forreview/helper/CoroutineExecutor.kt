package com.forreview.helper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.rx2.asCoroutineDispatcher

private const val THREAD_COUNT = 4

interface CoroutineExecutor {
    fun io(): CoroutineDispatcher

    fun net(): CoroutineDispatcher

    fun ui(): CoroutineDispatcher
}

class AppCoroutineExecutor(val schedulerProvider: SchedulerProvider):
    CoroutineExecutor {

    override fun io() = schedulerProvider.io().asCoroutineDispatcher()

    override fun net() = schedulerProvider.net().asCoroutineDispatcher()

    override fun ui() = schedulerProvider.ui().asCoroutineDispatcher()

}