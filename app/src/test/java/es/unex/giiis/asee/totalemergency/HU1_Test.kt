package es.unex.giiis.asee.totalemergency


import androidx.arch.core.executor.testing.InstantTaskExecutorRule

import es.unex.giiis.asee.totalemergency.data.Repository

import es.unex.giiis.asee.totalemergency.data.model.Localizaciones
import es.unex.giiis.asee.totalemergency.view.home.HomeMenuViewModel
import es.unex.giiis.asee.totalemergency.view.home.HomeViewModel
import es.unex.giiis.asee.totalemergency.data.api.CentrosSalud

import es.unex.giiis.asee.totalemergency.data.model.User
import es.unex.giiis.asee.totalemergency.data.toLoc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain

import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever


@RunWith(MockitoJUnitRunner::class)
class HU1_Test {

    private lateinit var viewModel : HomeMenuViewModel
    private lateinit var homeViewModel : HomeViewModel

    @Mock
    private lateinit var repository: Repository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)

        viewModel = HomeMenuViewModel(repository)
        homeViewModel = HomeViewModel(repository)


    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        //webServer?.shutdown()
        Dispatchers.resetMain()  // Reset the Main dispatcher to the original state
    }

    //Comprobar que no se instancien a nulo, importante pues el setup "podría" fallar y se necesita checkear
    @Test
    fun viewModel_initialization(){
        assertNotNull(viewModel)
        assertNotNull(homeViewModel)
    }

    @Test
    fun testUser_viewmodel(){
        viewModel.user = User(1,"carlitos","Carlos","Juan",
            "cj@gmail.com","1234","caceres","Caceres",
            "España","123123123")

        assertNotNull(viewModel.user)
        assertEquals(viewModel.user?.name, "Carlos")
        assertEquals(viewModel.user?.lastName, "Juan")
        assertEquals(viewModel.user?.userName, "carlitos")
        assertEquals(viewModel.user?.email, "cj@gmail.com")
    }

    @Test
    fun mappearDatos() = runBlockingTest{
        //Ejemplo, simula la obtencion correcta desde la API

        // Example of mocking a failed API response
        val testCentrosSalud = mutableListOf<CentrosSalud>()

        for (i in 1..10) {
            val barrio = "ID:$i"
            val codigoPostal = "CP$i"
            val numero = i
            val web = "http://example.com/$i"
            val calle = "Calle$i"
            val parroquia = "Parroquia$i"
            val lon =  10 + i*0.2/* your double value here */
            val id = i
            val telefono = "123456789"
            val nombre = "Centro_$i"
            val lat = -4 + i*0.2 /* your double value here */

            val centroSalud = CentrosSalud(barrio, codigoPostal, numero, web, calle, parroquia, lon, id, telefono, nombre, lat)
            testCentrosSalud.add(centroSalud)
        }
        val mappedCentrosSalud = mutableListOf<Localizaciones>()

        for (i in 1..10) {
            mappedCentrosSalud.add(testCentrosSalud.get(i-1).toLoc())
        }


        val testLocations = mutableListOf<Localizaciones>()
        for (i in 1..10) {
            val localizationId = "ID:$i"
            val longitude =  10 + i*0.2
            val latitude = -4 + i*0.2
            val name = "Centro_$i"
            val telephone = 123456789L // Replace with your desired telephone number
            val location = Localizaciones(localizationId, longitude, latitude, name, telephone)
            testLocations.add(location)
        }

        for (i in 1..10) {
            assertEquals(mappedCentrosSalud.get(i-1),testLocations.get(i-1))
        }
    }
}