package es.unex.giiis.asee.totalemergency.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Localizaciones(
    @PrimaryKey val localizationId: String,
    val longitude: Double,
    val latitude: Double,
    val name: String,
    val telephone: Long,
)
