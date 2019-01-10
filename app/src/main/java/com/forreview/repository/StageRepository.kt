package com.forreview.repository

import com.forreview.database.StagesDao
import com.forreview.database.mapper.StageMapper
import com.forreview.model.Stage
import com.forreview.model.entity.StageEntity
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

interface StageRepository {

    val stageListSubject: Observable<MutableList<Stage>>
    val activeStageSubject: Observable<Stage>
    val stageSubject: Observable<Stage>


    fun loadStageList(isForce: Boolean = false): MutableList<Stage>
    fun loadPaidStagesList(isForce: Boolean = false): MutableList<Stage>
    fun loadActiveStage(isForce: Boolean = false): Stage
    fun loadStage(title: String): Single<Stage>
    fun updateStage(stage: Stage)
    fun updateStageList(stageList: MutableList<Stage>)

    fun setActiveStage(stage: Stage)
    fun seed(stageList: List<StageEntity>)
}

class StageRepositoryImpl(
    private val stagesDao: StagesDao,
    private val stageMapper: StageMapper
) : StageRepository {

    override val stageListSubject = BehaviorSubject.create<MutableList<Stage>>()

    override val activeStageSubject = BehaviorSubject.create<Stage>()

    override val stageSubject = BehaviorSubject.create<Stage>()

    override fun loadStageList(isForce: Boolean): MutableList<Stage> {
        var cache = stageListSubject.value ?: mutableListOf()
        if (isForce || cache.isEmpty()) {
            cache = stagesDao.findFree().filter {
                it.type == StageEntity.Type.FREE
            }.let { entityList ->
                return@let stageMapper.fromEntityList(entityList)
            }
            stageListSubject.onNext(cache)
        }
        return cache
    }

    override fun loadPaidStagesList(isForce: Boolean): MutableList<Stage> {
        var cache = stageListSubject.value ?: mutableListOf()
        if (isForce || cache.isEmpty()) {
            cache = stagesDao.findFree().filter {
                it.type == StageEntity.Type.PAID
            }.let { entityList ->
                return@let stageMapper.fromEntityList(entityList)
            }
            stageListSubject.onNext(cache)
        }
        return cache
    }

    override fun loadActiveStage(isForce: Boolean): Stage {
        var stage: Stage? = activeStageSubject.value
        val activeStage: Stage
        if (isForce || stage == null) {
            val stageList = loadStageList(false)

            run loop@{
                stageList.forEach {
                    if (it.getStatus() == Stage.Status.ACTIVE) {
                        stage = it
                        return@loop
                    }
                }
            }

            activeStage = stage?.let { it } ?: run {
                stageList.first()
            }

            if (activeStage.id != activeStageSubject.value?.id) {
                activeStageSubject.onNext(activeStage)
            }
        } else {
            activeStage = stage!! //TODO
        }

        return activeStage
    }

    override fun loadStage(title: String): Single<Stage> {
        return Single.fromCallable {
            val stage = StageMapper().fromEntity(stagesDao.findStage(title))
            if (stage != null) {
                return@fromCallable stage
            }
            throw Exception("Meditation was not found")
        }
    }

    override fun updateStage(stage: Stage) {
        updateCache(stage)
        stagesDao.update(stageMapper.toEntity(stage))
    }

    override fun updateStageList(stageList: MutableList<Stage>) {
        updateCache(stageList)
        val stagesToDelete = stageMapper.toEntityList(stageList)
        stagesDao.delete(stagesToDelete)
        stagesDao.insert(stagesToDelete)
    }

    override fun setActiveStage(stage: Stage) {
        activeStageSubject.onNext(stage)
    }


    private fun updateCache(new: Stage) {
        if (activeStageSubject.value?.id == new.id) {
            activeStageSubject.onNext(new)
        }

        stageListSubject.value?.also { cacheList ->
            cacheList.forEachIndexed { index, cache ->
                if (cache.id == new.id) {
                    cacheList[index] = new
                    stageListSubject.onNext(cacheList)
                    return@also
                }
            }
        }
    }

    private fun updateCache(newList: MutableList<Stage>) {
        activeStageSubject.value?.also { cache ->
            newList.forEach { new ->
                if (cache.id == new.id) {
                    activeStageSubject.onNext(new)
                    return@also
                }
            }
        }
        stageListSubject.onNext(newList)
    }


    override fun seed(stageList: List<StageEntity>) {
        stagesDao.deleteAll()
        stagesDao.insert(stageList)
    }
}