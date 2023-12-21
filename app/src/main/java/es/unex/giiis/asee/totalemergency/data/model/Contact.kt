package es.unex.giiis.asee.totalemergency.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Contact(
    @PrimaryKey(autoGenerate = true) var contactId: Long? = null,
    val telephone: Long,
    val contactName: String,
    val userId: Long
) : Serializable
