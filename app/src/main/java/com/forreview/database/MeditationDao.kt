package com.forreview.database

import android.database.Cursor
import androidx.room.*
import com.forreview.model.entity.MeditationEntity

@Dao
interface MeditationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<MeditationEntity>)

    @Query("select * from MeditationEntity")
    fun findAll(): MutableList<MeditationEntity>

    @Query("select * from MeditationEntity where meditationId = :id")
    fun find(id: String): MeditationEntity?

    @Query("delete from MeditationEntity")
    fun deleteAll()

    @Query("select * from MeditationEntity inner join StageEntity on MeditationEntity.stageId = StageEntity.stageId")
    fun getAllCursor(): Cursor

    @Query("select * from MeditationEntity inner join StageEntity on MeditationEntity.stageId = StageEntity.stageId where meditationId = :id")
    fun getOneCursor(id: String): Cursor

    @Update
    fun update(entity: MeditationEntity)

    @Delete
    fun delete(list:MutableList<MeditationEntity>): Int
}