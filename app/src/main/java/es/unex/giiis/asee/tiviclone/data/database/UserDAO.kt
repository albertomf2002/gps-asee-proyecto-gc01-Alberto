package es.unex.giiis.asee.tiviclone.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.unex.giiis.asee.tiviclone.data.model.User


@Dao
interface UserDAO {

    @Query("SELECT * FROM User WHERE cod = :first LIMIT 1")
    suspend fun findByCod(first: Long): User

    @Query("SELECT cod FROM User WHERE userName LIKE :first AND userPassword LIKE :second LIMIT 1")
    suspend fun findByLogin(first: String, second: String): Long

    @Query("SELECT * FROM User")
    abstract fun getAllUsers(): List<User>

    @Insert
    suspend fun insert(user: User): Long

}