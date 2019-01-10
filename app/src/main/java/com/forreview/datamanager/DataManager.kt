package com.forreview.datamanager

import android.text.format.DateUtils
import com.forreview.database.DataInitializer
import com.forreview.helper.CoroutineExecutor
import com.forreview.helper.MeditationEvent
import com.forreview.helper.MeditationNavigationHelper
import com.forreview.helper.SchedulerProvider
import com.forreview.model.*
import com.forreview.model.*
import com.forreview.repository.MeditationRepository
import com.forreview.repository.StageRepository
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.withContext

interface DataManager {
    val meditationListObservable: Observable<MeditationsResult.MeditationList>
    val paidMeditationListObservable: Observable<MeditationsResult.MeditationList>
    val stageListObservable: Observable<StageResult.LoadStageList>
    val stageObservable: Observable<StageResult.LoadStage>
    val activeStageObservable: Observable<StageResult.ActiveStage>
    val activeMeditationObservable: Observable<MeditationsResult.ActiveMeditation>
    val activePaidMeditationObservable: Observable<MeditationsResult.ActivePaidMeditation>
    val statObservable: Observable<StatResult.LoadStat>
    val navigationRouteObservable: Observable<NavigationResult>
    val meditationAvailableObservable: Observable<MeditationsResult.IsAvailableMeditation>

    val testActiveMeditationObservable: Observable<MeditationsResult.ActiveMeditation>
    val testMeditationListObservable: Observable<MeditationsResult.MeditationList>


    suspend fun processAction(action: Action)
    fun loadActiveMeditation(isForce: Boolean = true): Meditation
    fun loadPaidActiveMeditation(isForce: Boolean): Meditation
    fun setActivePaidMeditation(meditation: Meditation)
    fun seedDataBase()
    fun setCompleted(activeMeditation: Meditation)
}

class DataManagerImpl(
    private val meditationRepository: MeditationRepository,
    private val stageRepository: StageRepository,
    private val dataInitializer: DataInitializer,
    private val executor: CoroutineExecutor,
    private val schedulerProvider: SchedulerProvider,
    private val navigationHelper: MeditationNavigationHelper
) : DataManager {

    override fun setCompleted(activeMeditation: Meditation) {
        meditationRepository.setCompletedMeditation(activeMeditation)
    }

    override fun setActivePaidMeditation(meditation: Meditation) {
        navigationHelper.activeMeditation = meditation
        meditationRepository.setActivePaidMeditation(meditation)
    }

    override fun seedDataBase() {
        val (stageList, meditationList) = dataInitializer.initMeditationWithStages()
        stageRepository.seed(stageList)
        meditationRepository.seed(meditationList)
    }

    override val meditationListObservable: Observable<MeditationsResult.MeditationList>
        get() = meditationRepository.freeMeditationListSubject
            .map { MeditationsResult.MeditationList.Success(it) }

    override val paidMeditationListObservable: Observable<MeditationsResult.MeditationList>
        get() = meditationRepository.paidMeditationListSubject
            .map { MeditationsResult.MeditationList.Success(it) }

    override val activeMeditationObservable: Observable<MeditationsResult.ActiveMeditation>
        get() = meditationRepository.freeActiveMeditationSubject
            .map { MeditationsResult.ActiveMeditation.Success(it) }

    override val activePaidMeditationObservable: Observable<MeditationsResult.ActivePaidMeditation>
        get() = meditationRepository.paidActiveMeditationSubject
            .map { MeditationsResult.ActivePaidMeditation.Success(it) }

    override val stageListObservable: Observable<StageResult.LoadStageList>
        get() = stageRepository.stageListSubject
            .map { StageResult.LoadStageList.Success(it) }

    override val activeStageObservable: Observable<StageResult.ActiveStage>
        get() = stageRepository.activeStageSubject
            .map { StageResult.ActiveStage.Success(it) }

    override val statObservable: Observable<StatResult.LoadStat>
        get() = meditationRepository.getStatObservable()
            .subscribeOn(schedulerProvider.io())
            .map { StatResult.LoadStat.Success(it) }

    override val stageObservable: Observable<StageResult.LoadStage>
        get() = stageRepository.stageSubject
            .map { StageResult.LoadStage.Success(it) }

    override val meditationAvailableObservable: Observable<MeditationsResult.IsAvailableMeditation>
        get() = meditationRepository.lastMeditationTimeSubject
            .map { MeditationsResult.IsAvailableMeditation.Success(!DateUtils.isToday(it)) }

    override val testActiveMeditationObservable: Observable<MeditationsResult.ActiveMeditation>
        get() = meditationRepository.test.resultObservable


    override val testMeditationListObservable: Observable<MeditationsResult.MeditationList>
        get() = meditationRepository.testList.resultObservable

    override val navigationRouteObservable: Observable<NavigationResult> = navigationHelper.navigationActionObservable

    override suspend fun processAction(action: Action) = withContext(executor.io()) {
        when (action) {
            is StageAction -> {
                when (action) {
                    is StageAction.LoadStageList -> {
                        loadStageList(action.isForce)
                    }
                    is StageAction.LoadStage -> {
                        loadStage(action.title)
                    }
                    is StageAction.FindActiveStage -> {
                        loadActiveStage(action.isForce)
                    }
                }
            }
            is MeditationAction -> {
                when (action) {
                    is MeditationAction.LoadMeditationList -> {
                        loadFreeMeditationList(action.isForce)
                    }
                    is MeditationAction.LoadPaidMeditationList -> {
                        loadPaidMeditationList(action.isForce)
                    }
                    is MeditationAction.LoadMeditation -> {
                        loadMeditation(action.id)
                    }
                    is MeditationAction.UpdateMeditationAction -> {
                        updateMeditation(action.meditation)
                    }
                    is MeditationAction.LoadActiveMeditation -> {
                        loadActiveMeditation(action.isForce)
                    }
                    is MeditationAction.LoadPaidActiveMeditation -> {
                        loadPaidActiveMeditation(action.isForce)
                    }
                    is MeditationAction.GoNextMeditation -> {
                        goNextMeditation()
                    }
                    is MeditationAction.JumpToMeditation -> {
                        jumpToMeditation(action.meditation)
                    }
                    is MeditationAction.IsMeditationAvailable -> {
                        loadLastMeditationTime(action.isForce)
                    }
                    is MeditationAction.SetCompleted -> {
                        setCompleted(action.meditation)
                    }


                    is MeditationAction.Stop -> {
                        navigationHelper.routeEvent(MeditationEvent.Stop)
                    }
                    is MeditationAction.Complete -> {
                        navigationHelper.routeEvent(MeditationEvent.Complete)
                    }
                    is MeditationAction.GoMeditation -> {
                        navigationHelper.routeEvent(MeditationEvent.GoMeditation)
                    }
                    is MeditationAction.Next -> {
                        navigationHelper.routeEvent(MeditationEvent.FromWakeUpToPostMeditation)
                    }
                    is MeditationAction.SelectDuration -> {
                        navigationHelper.routeEvent(MeditationEvent.SelectDuration(action.audio))
                    }
                    is MeditationAction.NextPostCard -> {
                        navigationHelper.routeEvent(MeditationEvent.NextPostCard(action.position))
                    }
                    is MeditationAction.NextPreCard -> {
                        navigationHelper.routeEvent(MeditationEvent.NextPreCard(action.position))
                    }
                }
            }
        }
        return@withContext
    }


    private fun loadStageList(isForce: Boolean = false): MutableList<Stage> {
        return stageRepository.loadStageList(isForce)
    }

    private fun loadStage(title: String): Single<Stage> {
        return stageRepository.loadStage(title)
    }

    private fun loadActiveStage(isForce: Boolean = false): Stage {
        return stageRepository.loadActiveStage(isForce)
    }

    private fun updateStage(stage: Stage) {
        stageRepository.updateStage(stage)
    }


    private fun loadFreeMeditationList(isForce: Boolean = false): MutableList<Meditation> {
        return meditationRepository.loadFreeMeditationList(isForce)
    }

    private fun loadPaidMeditationList(isForce: Boolean = false): MutableList<Meditation> {
        return meditationRepository.loadPaidMeditationList(isForce)
    }

    private fun loadMeditation(id: String): Meditation? {
        return meditationRepository.loadMeditation(id)
    }

    private fun updateMeditation(meditation: Meditation) {
        meditationRepository.updateMeditation(meditation)
    }

    override fun loadActiveMeditation(isForce: Boolean): Meditation {
        val meditation = meditationRepository.loadFreeActiveMeditation(isForce)
        navigationHelper.activeMeditation = meditation
        return meditation
    }

    override fun loadPaidActiveMeditation(isForce: Boolean): Meditation {
        val meditation = meditationRepository.loadPaidActiveMeditation(isForce)
        navigationHelper.activeMeditation = meditation
        return meditation
    }

    private fun loadLastMeditationTime(isForce: Boolean = false) {
        meditationRepository.loadLastMeditationTime(isForce)
    }


    private fun goNextMeditation() {
        val meditation = loadActiveMeditation(false)
        meditation.setCompleted()
        val isStageCompleted = meditation.stage.incrementCurrentDayNumber()

        updateStage(meditation.stage)
        updateMeditation(meditation)

        meditationRepository.setLastMeditationTime(meditation.timeCompletedMillis)

        if (isStageCompleted) {
            val stageList = loadStageList()
            var hasNextStage = false
            run loop@{
                stageList.forEachIndexed { index, stage ->
                    if (meditation.stage.id == stage.id) {
                        val nextStage = stageList.getOrNull(index + 1)
                        if (nextStage != null) {
                            nextStage.setActive()
                            updateStage(nextStage)
                            stageRepository.setActiveStage(nextStage)

                            hasNextStage = true
                        }
                        return@loop
                    }
                }
            }
            if (!hasNextStage) {
                dropAllMeditationProgress()
            }
        }
        loadActiveMeditation(true)
    }

    private fun dropAllMeditationProgress() {
        val meditationList = loadFreeMeditationList()
        val stageList = loadStageList()

        meditationList.forEachIndexed { index, meditation ->
            meditation.unComplete()
        }

        stageList.forEachIndexed { index, stage ->
            if (index == 0) {
                stage.setActive()
            } else {
                stage.setBlocked()
            }
        }

        stageRepository.updateStageList(stageList)
        meditationRepository.updateMeditationList(meditationList)
        stageRepository.setActiveStage(stageList.first())
        meditationRepository.setActiveMeditation(meditationList.first())
    }

    private fun jumpToMeditation(toMeditation: Meditation) {
        val meditationList = loadFreeMeditationList()
        val stageSet = mutableSetOf<Stage>()
        var isActiveFound = false
        meditationList.forEach { meditation ->
            when {
                meditation.stage.id == toMeditation.stage.id -> when {
                    meditation.id == toMeditation.id -> {
                        isActiveFound = true
                        meditation.unComplete()
                        meditation.stage.setActiveDay(toMeditation.dayNumber)
                    }
                    isActiveFound -> {
                        meditation.unComplete()
                        meditation.stage.setActiveDay(toMeditation.dayNumber)
                    }
                    else -> {
                        meditation.setCompleted()
                        meditation.stage.setActiveDay(toMeditation.dayNumber)
                    }
                }
                isActiveFound -> {
                    meditation.unComplete()
                    meditation.stage.setBlocked()
                }
                else -> {
                    meditation.setCompleted()
                    meditation.stage.setCompleted()
                }
            }

            stageSet.add(meditation.stage)
        }
        stageRepository.updateStageList(stageSet.toMutableList())
        meditationRepository.updateMeditationList(meditationList)
        stageRepository.setActiveStage(toMeditation.stage)
        meditationRepository.setActiveMeditation(toMeditation)
    }
}