package es.unex.giiis.asee.totalemergency.data.model

import android.net.Uri
import java.io.Serializable

data class Ubication(
    var uri: Uri? = null,
    val latitude: String = "",
    val longitude: String = ""


) : Serializable
