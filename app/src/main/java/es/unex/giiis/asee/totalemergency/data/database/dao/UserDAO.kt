package es.unex.giiis.asee.totalemergency.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import es.unex.giiis.asee.totalemergency.data.model.User


@Dao
interface UserDAO {

    @Query("SELECT * FROM User WHERE cod = :first LIMIT 1")
    suspend fun findByCod(first: Long): User

    @Query("SELECT cod FROM User WHERE userName LIKE :first AND userPassword LIKE :second LIMIT 1")
    suspend fun findByLogin(first: String, second: String): Long

    @Query("SELECT * FROM User")
    abstract fun getAllUsers(): List<User>

    @Query("DELETE FROM User WHERE cod = :first")
    suspend fun deleteByCod(first: Long)

    @Update
    suspend fun modifyUser(user : User)

    @Insert
    suspend fun insert(user: User): Long

}