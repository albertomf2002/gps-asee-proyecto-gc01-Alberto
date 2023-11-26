package es.unex.giiis.asee.tiviclone.data.database

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import es.unex.giiis.asee.tiviclone.data.model.VideoRecord

@Dao
interface VideoRecordDAO {

    @Query("SELECT * FROM VideoRecord WHERE uri LIKE :first LIMIT 1")
    suspend fun findByUri(first: String): VideoRecord

    @Query("SELECT * FROM VideoRecord WHERE videoId = :first LIMIT 1")
    suspend fun findById(first: Long): VideoRecord

    @Query("SELECT * FROM VideoRecord")
    suspend fun getAllVideos(): List<VideoRecord>

    @Insert
    suspend fun insert(video: VideoRecord): Long

    @Query("DELETE FROM VideoRecord WHERE videoId = :first")
    suspend fun deleteFromId(first: Long)
}