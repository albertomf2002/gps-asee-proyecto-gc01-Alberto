package es.unex.giiis.asee.totalemergency

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import es.unex.giiis.asee.totalemergency.data.Repository
import es.unex.giiis.asee.totalemergency.data.database.dao.UserDAO
import es.unex.giiis.asee.totalemergency.view.home.HomeMenuViewModel
import es.unex.giiis.asee.totalemergency.view.home.HomeViewModel
import es.unex.giiis.asee.totalemergency.api.NetworkService
import es.unex.giiis.asee.totalemergency.data.database.TotalEmergencyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class API_Test {

    private lateinit var viewModel : HomeMenuViewModel
    private lateinit var homeViewModel : HomeViewModel

    private lateinit var testDb : TotalEmergencyDatabase
    private lateinit var userDao: UserDAO

    @Mock
    private lateinit var repository: Repository

    @Mock
    private lateinit var mockContext: Context

    private lateinit var webServer : MockWebServer

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        webServer = MockWebServer()
        webServer.start()

        viewModel = HomeMenuViewModel(repository)
        homeViewModel = HomeViewModel(repository)
    }

    @After
    fun tearDown() {
        webServer.shutdown()
        Dispatchers.resetMain()  // Reset the Main dispatcher to the original state
    }



    @Test
    fun apiCallConnection() = runTest{

        val mockUrl = webServer.url("/").toString()

        val testNetworkService = NetworkService(mockUrl)

        val jsonResponse = """
        [
            {
                "barrio": "Barrio1",
                "codigoPostal": "CP001",
                "numero": 1,
                "web": "http://example.com/1",
                "calle": "Calle1",
                "parroquia": "Parroquia1",
                "lon": -8.72,
                "id": 1,
                "telefono": "123456789",
                "nombre": "Centro Salud 1",
                "lat": 42.23
            },
            {
                "barrio": "Barrio2",
                "codigoPostal": "CP002",
                "numero": 2,
                "web": "http://example.com/2",
                "calle": "Calle2",
                "parroquia": "Parroquia2",
                "lon": -8.73,
                "id": 2,
                "telefono": "987654321",
                "nombre": "Centro Salud 2",
                "lat": 42.24
            }
        ]
        """.trimIndent()  // Replace with the actual expected JSON response


        webServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))
        // This is where you need to make sure that networkService is using this mockUrl

        try {
            val list = testNetworkService.getNetworkService().getAllUbications(mockUrl)

            Assert.assertEquals(2, list.size)
        }catch (e : Exception){
            Assert.assertEquals(true, false)
        }
    }

    @Test
    fun apiFailureTest() = runTest {
        try {
            repository.tryUpdateRecentLocationCache()
            Assert.assertEquals(true, false)
        }catch (cause: Throwable){
            Assert.assertEquals(cause.message,"Unable to fetch data from API")
        }
    }
}