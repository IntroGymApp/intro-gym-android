package ru.lonelywh1te.introgym.db.dao

import androidx.room.Dao
import androidx.room.Query
import ru.lonelywh1te.introgym.db.entity.TagEntity

@Dao
interface TagDao {

    @Query("select * from tag")
    fun getTags(): List<TagEntity>

}