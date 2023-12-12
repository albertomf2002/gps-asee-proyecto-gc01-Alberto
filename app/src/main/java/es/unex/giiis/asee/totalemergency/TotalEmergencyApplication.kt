package es.unex.giiis.asee.totalemergency

import android.app.Application
import es.unex.giiis.asee.totalemergency.util.AppContainer

class TotalEmergencyApplication : Application() {

    lateinit var appContainer: AppContainer

    override fun onCreate(){
        super.onCreate()
        appContainer = AppContainer(this)
    }
}