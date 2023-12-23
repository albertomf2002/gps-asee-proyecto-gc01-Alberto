package es.unex.giiis.asee.totalemergency

import android.app.Activity
import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import es.unex.giiis.asee.totalemergency.data.Repository
import es.unex.giiis.asee.totalemergency.view.home.EmergencyViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class Prueba {


    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertEquals("es.unex.giiis.asee.totalemergency", appContext.packageName)
    }
}