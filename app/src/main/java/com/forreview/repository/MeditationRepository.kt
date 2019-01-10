package com.forreview.repository

import com.forreview.LiveResult
import com.forreview.database.MeditationDao
import com.forreview.database.mapper.MeditationMapper
import com.forreview.helper.PrefsHelper
import com.forreview.helper.StatResources
import com.forreview.model.Meditation
import com.forreview.model.MeditationsResult
import com.forreview.model.Stage
import com.forreview.model.Stat
import com.forreview.model.entity.MeditationEntity
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

interface MeditationRepository {

    val freeMeditationListSubject: Observable<MutableList<Meditation>>
    val paidMeditationListSubject: Observable<MutableList<Meditation>>
    val allMeditationListSubject: Observable<MutableList<Meditation>>
    val meditationForStage: Observable<MutableList<Meditation>>
    val freeActiveMeditationSubject: Observable<Meditation>
    val paidActiveMeditationSubject: Observable<Meditation>
    val lastMeditationTimeSubject: Observable<Long>

    val test: LiveResult<MeditationsResult.ActiveMeditation>
    val testList: LiveResult<MeditationsResult.MeditationList>

    fun loadFreeMeditationList(isForce: Boolean = false): MutableList<Meditation>
    fun loadPaidMeditationList(isForce: Boolean = false): MutableList<Meditation>

    fun loadFreeActiveMeditation(isForce: Boolean = false): Meditation
    fun loadPaidActiveMeditation(isForce: Boolean = false): Meditation
    fun loadMeditation(id: String): Meditation?
    fun meditationObservable(id: String): Single<Meditation>
    fun updateMeditation(meditation: Meditation)
    fun updateMeditationList(meditationList: MutableList<Meditation>)

    fun setActiveMeditation(meditation: Meditation)
    fun setActivePaidMeditation(meditation: Meditation)
    fun seed(meditationList: List<MeditationEntity>)
    fun getStatObservable(): Observable<Stat>
    fun setLastMeditationTime(timeMillis: Long)
    fun loadLastMeditationTime(isForce: Boolean = false): Long
    fun setCompletedMeditation(activeMeditation: Meditation)
}

class MeditationRepositoryImpl(
    private val meditationDao: MeditationDao,
    private val meditationMapper: MeditationMapper,
    private val statResources: StatResources,
    private val prefsHelper: PrefsHelper
) : MeditationRepository {
    override val meditationForStage: Observable<MutableList<Meditation>> =
        BehaviorSubject.create<MutableList<Meditation>>()

    override val freeMeditationListSubject = BehaviorSubject.create<MutableList<Meditation>>()

    override val paidMeditationListSubject = BehaviorSubject.create<MutableList<Meditation>>()

    override val allMeditationListSubject = BehaviorSubject.create<MutableList<Meditation>>()

    override val freeActiveMeditationSubject = BehaviorSubject.create<Meditation>()

    override val paidActiveMeditationSubject = BehaviorSubject.create<Meditation>()

    override val lastMeditationTimeSubject = BehaviorSubject.create<Long>()

    override val test = LiveResult.create<MeditationsResult.ActiveMeditation>()

    override val testList = LiveResult.create<MeditationsResult.MeditationList>()

    private fun testr() {
        test.applyLoading(MeditationsResult.ActiveMeditation.InFlight(true))

        try {
            val meditation = loadFreeActiveMeditation()
            test.applyLoading(MeditationsResult.ActiveMeditation.Success(meditation))
        } catch (exc: Exception) {
            test.applyLoading(MeditationsResult.ActiveMeditation.Failure(exc))
        }


        test.applyLoading(MeditationsResult.ActiveMeditation.InFlight(false))
    }

    override fun setCompletedMeditation(activeMeditation: Meditation) {
        activeMeditation.setCompleted()
        meditationDao.update(meditationMapper.toEntity(activeMeditation))
        loadPaidMeditationList(true)
    }

    override fun loadFreeMeditationList(isForce: Boolean): MutableList<Meditation> {
        return loadMeditations(
            isForce,
            freeMeditationListSubject,
            isPaid = false
        )
    }

    override fun loadPaidMeditationList(isForce: Boolean): MutableList<Meditation> {
        return loadMeditations(
            isForce,
            paidMeditationListSubject,
            isPaid = true
        )
    }

    private fun loadAllMeditationList(isForce: Boolean): MutableList<Meditation> {
        return loadMeditations(
            isForce,
            allMeditationListSubject,
            isPaid = null
        )
    }

    private fun loadMeditations(
        isForce: Boolean,
        meditationListSubject: BehaviorSubject<MutableList<Meditation>>,
        isPaid: Boolean?
    ): MutableList<Meditation> {
        var cache = meditationListSubject.value ?: mutableListOf()
        var filteredCache: MutableList<Meditation>
        if (isForce || cache.isEmpty()) {
            cache = meditationDao.getAllCursor().let { cursor ->
                meditationMapper.listFromCursor(cursor)
            }
            filteredCache = filterMeditations(cache, isPaid)
            meditationListSubject.onNext(filteredCache)
        }
        filteredCache = filterMeditations(cache, isPaid)
        return filteredCache
    }

    private fun filterMeditations(cache: MutableList<Meditation>, isPaid: Boolean?): MutableList<Meditation> {
        return cache.filter {
            when (isPaid) {
                true -> {
                    it.stage.getType() == Stage.Type.PAID
                }
                false -> {
                    it.stage.getType() == Stage.Type.FREE
                }
                null -> {
                    true
                }
            }
        }.toMutableList()
    }

    override fun loadFreeActiveMeditation(isForce: Boolean): Meditation {
        var meditation: Meditation? = freeActiveMeditationSubject.value
        val activeMeditation: Meditation
        if (isForce || meditation == null) {

            val meditationList = loadFreeMeditationList(isForce)
            run loop@{
                meditationList.forEach { item ->
                    if (item.stage.getStatus() == Stage.Status.ACTIVE
                        && !item.isCompleted()
                        && item.stage.getType() == Stage.Type.FREE
                    ) {
                        meditation = item
                        return@loop
                    }
                }
            }

            activeMeditation = meditation?.let { it } ?: run {
                meditationList.first()
            }

            if (activeMeditation.id != freeActiveMeditationSubject.value?.id) {
                freeActiveMeditationSubject.onNext(activeMeditation)
            }
        } else {
            activeMeditation = meditation!! //TODO
        }
        return activeMeditation
    }

    override fun loadPaidActiveMeditation(isForce: Boolean): Meditation {
        var meditation: Meditation? = paidActiveMeditationSubject.value
        val activeMeditation: Meditation
        if (isForce || meditation == null) {

            val meditationList = loadPaidMeditationList(true)

            run loop@{
                meditationList.forEach { item ->
                    if (item.stage.getStatus() == Stage.Status.ACTIVE
                        && !item.isCompleted()
                        && item.stage.getType() == Stage.Type.PAID
                    ) {
                        meditation = item
                        return@loop
                    }
                }
            }

            activeMeditation = meditation?.let { it } ?: run {
                meditationList.first()
            }

            if (activeMeditation.id != paidActiveMeditationSubject.value?.id) {
                paidActiveMeditationSubject.onNext(activeMeditation)
            }
        } else {
            activeMeditation = meditation!! //TODO
        }
        return activeMeditation
    }

    override fun meditationObservable(id: String): Single<Meditation> {
        return Single.fromCallable {
            val meditation = loadMeditation(id)
            if (meditation != null) {
                return@fromCallable meditation
            }
            throw Exception("Meditation was not found")
        }
    }

    override fun loadMeditation(id: String): Meditation? {
        val cachedList = freeMeditationListSubject.value
        if (cachedList == null) {
            meditationDao.getOneCursor(id).let { cursor ->
                if (cursor.moveToFirst()) {
                    return meditationMapper.fromCursor(cursor)
                }
            }
        } else {
            cachedList.forEach { meditation ->
                if (meditation.id == id) {
                    return meditation
                }
            }
        }

        return null
    }

    override fun updateMeditation(meditation: Meditation) {
        updateCache(meditation)
        meditationDao.update(meditationMapper.toEntity(meditation))
    }

    override fun updateMeditationList(meditationList: MutableList<Meditation>) {
        updateCache(meditationList)
        val meditationsToDelete = meditationMapper.toEntityList(meditationList)
        meditationDao.delete(meditationsToDelete)
        meditationDao.insert(meditationsToDelete)
    }

    override fun setActiveMeditation(meditation: Meditation) {
        freeActiveMeditationSubject.onNext(meditation)
    }

    override fun setActivePaidMeditation(meditation: Meditation) {
        paidActiveMeditationSubject.onNext(meditation)
    }

    override fun setLastMeditationTime(timeMillis: Long) {
        prefsHelper.setLastMeditationTime(timeMillis)
        lastMeditationTimeSubject.onNext(timeMillis)
    }

    override fun loadLastMeditationTime(isForce: Boolean): Long {
        var cached = lastMeditationTimeSubject.value ?: 0
        if (cached == 0L || isForce) {
            cached = prefsHelper.getLastMeditationTime()
            lastMeditationTimeSubject.onNext(cached)
        }
        return cached
    }

    override fun seed(meditationList: List<MeditationEntity>) {
        meditationDao.deleteAll()
        meditationDao.insert(meditationList)
    }

    override fun getStatObservable(): Observable<Stat> {
        return Observable.fromCallable {
            var prevMeditation: Meditation? = null
            var prevLongestStreak = 0

            var totalMeditations = 0
            var longestStreak = 0
            var totalMeditationTimeMillis = 0L
            loadFreeMeditationList(true).forEach { meditation ->
                if (meditation.isCompleted()) {
                    totalMeditations += 1
                    totalMeditationTimeMillis += meditation.duration

                    if (prevMeditation?.isCompletedAtLeastYesterdayRelativeTo(meditation.timeCompletedMillis) == true) {
                        longestStreak += 1
                        if (longestStreak > prevLongestStreak) {
                            prevLongestStreak = longestStreak
                        }
                    } else {
                        if (longestStreak > prevLongestStreak) {
                            prevLongestStreak = longestStreak
                        }
                        longestStreak = 0
                    }
                    prevMeditation = meditation
                }
            }

            if (prevLongestStreak != 0) {
                prevLongestStreak += 1
            }

            return@fromCallable Stat(
                totalMeditations = totalMeditations,
                longestStreak = prevLongestStreak,
                vibe = statResources.getVibe(totalMeditations),
                achievementIconResId = statResources.getAchievementIconResId(totalMeditations),
                streakIconResId = statResources.getStreakIconResId(totalMeditations),
                totalMeditationTimeMillis = totalMeditationTimeMillis
            )
        }
    }


    private fun updateCache(new: Meditation) {
        if (freeActiveMeditationSubject.value?.id == new.id) {
            freeActiveMeditationSubject.onNext(new)
        }

        freeMeditationListSubject.value?.also { cachedList ->
            cachedList.forEachIndexed { index, cache ->
                if (cache.stage.id == new.stage.id) {
                    if (cache.id == new.id) {
                        cachedList[index] = new
                    } else {
                        cache.stage = new.stage
                    }
                }
            }
            freeMeditationListSubject.onNext(cachedList)
        }
    }

    private fun updateCache(newList: MutableList<Meditation>) {
        freeActiveMeditationSubject.value?.also { cache ->
            newList.forEach { new ->
                if (cache.id == new.id) {
                    freeActiveMeditationSubject.onNext(new)
                    return@also
                }
            }
        }
        freeMeditationListSubject.onNext(newList)
    }
}