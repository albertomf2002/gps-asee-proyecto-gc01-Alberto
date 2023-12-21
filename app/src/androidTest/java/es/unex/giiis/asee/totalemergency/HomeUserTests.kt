package es.unex.giiis.asee.totalemergency

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import es.unex.giiis.asee.totalemergency.data.Repository
import es.unex.giiis.asee.totalemergency.data.database.TotalEmergencyDatabase
import es.unex.giiis.asee.totalemergency.data.database.dao.UserDAO
import es.unex.giiis.asee.totalemergency.data.model.Localizaciones
import es.unex.giiis.asee.totalemergency.data.model.User
import es.unex.giiis.asee.totalemergency.view.home.HomeMenuViewModel
import es.unex.giiis.asee.totalemergency.view.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class HomeUserTests {

    private lateinit var userDao: UserDAO
    private lateinit var db: TotalEmergencyDatabase

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var homeViewModel : HomeViewModel

    @Mock
    private lateinit var repository: Repository


    @Test
    fun testHomeViewModel() = runTest{
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TotalEmergencyDatabase::class.java)
            .allowMainThreadQueries() // Only for testing
            .build()
        userDao = db.userDao()

        repository.updateUserDao(userDao)

        homeViewModel = HomeViewModel(repository)



        var user1 = User(1,"carlitos","Carlos","Jesus",
            "cj@gmail.com","1234","10000",
            "Caceres","España","123123123")

        runBlocking {
            userDao.insert(user1)
        }

        withContext(Dispatchers.Main){
            homeViewModel.obtenerUser(1)
        }


        val observer = Observer<User> {}
        val liveData = homeViewModel.user

        withContext(Dispatchers.Main) {
            liveData.observeForever(observer)
        }

        val values = liveData.value

        Assert.assertEquals(user1, values)

        withContext(Dispatchers.Main) {
            liveData.removeObserver(observer)
        }


    }
}