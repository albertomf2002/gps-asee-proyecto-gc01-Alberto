package es.unex.giiis.asee.totalemergency.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import es.unex.giiis.asee.totalemergency.data.model.Localizaciones


@Dao
interface LocalizacionesDAO {

    @Query("SELECT * FROM Localizaciones")
    fun getAllUbications(): LiveData<List<Localizaciones>>

    @Query("SELECT Count(*) FROM Localizaciones")
    suspend fun getTotalUbications() : Long

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(ubications: List<Localizaciones>)

}