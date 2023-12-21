package es.unex.giiis.asee.totalemergency.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) var cod: Long? = null,
    val userName: String = "",
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val userPassword: String = "",
    val addres: String = "",
    val city: String = "",
    val country: String = "",
    val telephone: String = ""
) : Serializable