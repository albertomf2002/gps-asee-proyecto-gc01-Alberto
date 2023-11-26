package es.unex.giiis.asee.tiviclone.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.unex.giiis.asee.tiviclone.data.model.Contact

@Dao
interface ContactDAO {

    @Query("SELECT * FROM Contact WHERE telephone = :first LIMIT 1")
    suspend fun findByTelephone(first: Long): Contact

    @Query("SELECT * FROM Contact WHERE contactId = :first LIMIT 1")
    suspend fun findById(first: Long): Contact

    @Query("SELECT * FROM Contact")
    suspend fun getAllContacts(): List<Contact>

    @Insert
    suspend fun insert(contact: Contact): Long

    @Query("DELETE FROM Contact WHERE contactId = :first")
    suspend fun deleteFromId(first: Long)
}