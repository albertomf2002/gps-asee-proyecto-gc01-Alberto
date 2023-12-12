package es.unex.giiis.asee.totalemergency.data.database.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import es.unex.giiis.asee.totalmergency.data.model.VideoRecord

@Dao
interface VideoRecordDAO {

    @Query("SELECT * FROM VideoRecord WHERE uri LIKE :first LIMIT 1")
    suspend fun findByUri(first: String): VideoRecord

    @Query("SELECT * FROM VideoRecord WHERE videoId = :first LIMIT 1")
    suspend fun findById(first: Long): VideoRecord

    @Query("SELECT * FROM VideoRecord")
    suspend fun getAllVideos(): List<VideoRecord>

    @Query("SELECT * FROM VideoRecord WHERE userId = :first")
    suspend fun getAllVideosFromUser(first: Long): List<VideoRecord>

    @Insert
    suspend fun insert(video: VideoRecord): Long

    @Query("DELETE FROM VideoRecord WHERE videoId = :first")
    suspend fun deleteFromId(first: Long)

    @Query("DELETE FROM VideoRecord WHERE userId = :first")
    suspend fun deleteFromUserId(first:Long)
}