package es.unex.giiis.asee.totalemergency.util

import android.content.Context
import es.unex.giiis.asee.totalemergency.data.Repository
import es.unex.giiis.asee.totalemergency.api.NetworkService
import es.unex.giiis.asee.totalemergency.data.database.TotalEmergencyDatabase

class AppContainer(context: Context?) {
    private val networkService = NetworkService()
    private val db = TotalEmergencyDatabase.getInstance(context!!)
    val repository = Repository(db!!.localizacionesDao(),db.userDao(),db.contactDAO(),db.videoDAO(),networkService)
}
