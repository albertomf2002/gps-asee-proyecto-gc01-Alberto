package es.unex.giiis.asee.totalemergency.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.unex.giiis.asee.totalemergency.data.model.Contact

@Dao
interface ContactDAO {

    @Query("SELECT * FROM Contact WHERE telephone = :first LIMIT 1")
    suspend fun findByTelephone(first: Long): Contact

    @Query("SELECT * FROM Contact WHERE contactId = :first LIMIT 1")
    suspend fun findById(first: Long): Contact

    @Query("SELECT * FROM Contact")
    suspend fun getAllContacts(): List<Contact>

    @Query("SELECT * FROM Contact WHERE userId = :first")
    suspend fun getAllContactsFromUser(first: Long): List<Contact>
    @Insert
    suspend fun insert(contact: Contact): Long

    @Query("DELETE FROM Contact WHERE contactId = :first")
    suspend fun deleteFromId(first: Long)

    @Query("DELETE FROM Contact WHERE userId = :first")
    suspend fun deleteFromUserId(first: Long)
}