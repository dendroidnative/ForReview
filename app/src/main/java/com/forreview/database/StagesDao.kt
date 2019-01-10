package com.forreview.database

import androidx.room.*
import com.forreview.model.entity.StageEntity

@Dao
interface StagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<StageEntity>)

    @Query("select * from StageEntity")
    fun findFree(): MutableList<StageEntity>

    @Query("select * from StageEntity where stageTitle = :title")
    fun findStage(title: String): StageEntity?

    @Query("delete from StageEntity")
    fun deleteAll()

    @Update
    fun update(entity: StageEntity)

    @Delete
    fun delete(stageList: List<StageEntity>)
}