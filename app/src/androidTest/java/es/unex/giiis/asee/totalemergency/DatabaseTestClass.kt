package es.unex.giiis.asee.totalemergency

import android.content.Context
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import es.unex.giiis.asee.totalemergency.data.database.TotalEmergencyDatabase
import es.unex.giiis.asee.totalemergency.data.database.dao.ContactDAO
import es.unex.giiis.asee.totalemergency.data.database.dao.LocalizacionesDAO
import es.unex.giiis.asee.totalemergency.data.database.dao.UserDAO
import es.unex.giiis.asee.totalemergency.data.database.dao.VideoRecordDAO
import es.unex.giiis.asee.totalemergency.data.model.Contact
import es.unex.giiis.asee.totalemergency.data.model.Localizaciones
import es.unex.giiis.asee.totalemergency.data.model.User
import es.unex.giiis.asee.totalemergency.data.model.VideoRecord
import kotlinx.coroutines.test.runTest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.internal.wait

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4::class)
class DatabaseTestClass {


    private lateinit var db: TotalEmergencyDatabase
    private lateinit var userDao: UserDAO
    private lateinit var videoDao: VideoRecordDAO
    private lateinit var contactDao: ContactDAO
    private lateinit var localizacionesDAO: LocalizacionesDAO

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun iniciarDB(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TotalEmergencyDatabase::class.java)
            .allowMainThreadQueries() // Only for testing
            .build()
        userDao = db.userDao()
        videoDao = db.videoDAO()
        localizacionesDAO = db.localizacionesDao()
        contactDao = db.contactDAO()
    }

    @After
    fun eliminarDb(){
        db.close()
    }

    @Test
    fun testUserDao() = runTest{
        var user1 = User(1,"carlitos","Carlos","Jesus",
            "cj@gmail.com","1234","10000",
            "Caceres","España","123123123")
        var user2 = User(2,"paquito","Paco","Quito",
            "pq@gmail.com","4321","10001",
            "Caceres","España","123456789")
        userDao.insert(user1)
        userDao.insert(user2)



        val userItem = userDao.findByCod(user1.cod!!)

        assertEquals(userItem, user1)
        assertEquals(2, userDao.getAllUsers().size)

        val user2copy = user2.copy()

        user2.email = "paco@gmail.com"
        userDao.modifyUser(user2)

        val userItem2 = userDao.findByCod(2)

        assertEquals(userItem2, user2)
        assertNotEquals(userItem2, user2copy)

    }

    @Test
    fun videoDao() = runTest{
        val vr1 = VideoRecord(1, "path/1", 2, "13-2-2023")
        val vr3 = VideoRecord(3, "path/3", 2, "18-2-2023")
        val vr2 = VideoRecord(2, "path/2", 1, "14-2-2023")
        videoDao.insert(vr1)
        videoDao.insert(vr2)
        videoDao.insert(vr3)

        assertEquals(3, videoDao.getAllVideos().size)
        val test1 = videoDao.findById(1)
        assertEquals(test1, vr1)
        val test2 = videoDao.findByPath("path/3")
        assertEquals(2, test2.userId)   //Check the same owner, for example

        videoDao.deleteFromUserId(2)
        assertEquals(1, videoDao.getAllVideos().size)

        videoDao.deleteFromId(2)
        assertEquals(0, videoDao.getAllVideos().size)

    }

    @Test
    fun localizationDao() = runTest{
        val loc1 = Localizaciones("ID:1", 1.0, 1.0, "localizacion", 123456789L)
        val loc2 = Localizaciones("ID:2", -1.0, 0.3, "centro", 3232L)
        val list = mutableListOf<Localizaciones>()
        list.add(loc1)
        list.add(loc2)

        assertEquals(2, list.size)
        runBlocking {
            localizacionesDAO.insertAll(list)
        }

        delay(200)

        assertEquals(2L,localizacionesDAO.getTotalUbications())

        val observer = Observer<List<Localizaciones>> {}
        val liveData = localizacionesDAO.getAllUbications()

        withContext(Dispatchers.Main) {
            liveData.observeForever(observer)
        }

        val values = liveData.value
        assertEquals(2, values?.size)
        assertEquals(loc1, values?.get(0))
        assertEquals(loc2, values?.get(1))

        withContext(Dispatchers.Main) {
            liveData.removeObserver(observer)
        }
    }

    @Test
    fun contactDao() = runTest{
        val contact1 = Contact(1, 123123123L, "Carlos", 1)
        val contact2 = Contact(2, 123456789L, "Paco", 2)

        contactDao.insert(contact1)
        contactDao.insert(contact2)

        assertEquals(2, contactDao.getAllContacts().size)

        contactDao.deleteFromUserId(1)

        assertEquals(1,contactDao.getAllContacts().size)
        assertEquals(0,contactDao.getAllContactsFromUser(1).size)

    }

    @Test
    fun safeUserDelete() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TotalEmergencyDatabase::class.java)
            .allowMainThreadQueries() // Only for testing
            .build()
        videoDao = db.videoDAO()
        userDao = db.userDao()
        contactDao = db.contactDAO()

        val vr1 = VideoRecord(1, "path/1", 2, "13-2-2023")
        val vr3 = VideoRecord(3, "path/3", 2, "18-2-2023")
        val vr2 = VideoRecord(2, "path/2", 1, "14-2-2023")
        videoDao.insert(vr1)
        videoDao.insert(vr2)
        videoDao.insert(vr3)

        val contact1 = Contact(1, 123123123L, "Carlos", 2)
        val contact2 = Contact(2, 123456789L, "Paco", 1)

        contactDao.insert(contact1)
        contactDao.insert(contact2)


        var user = User(2,"carlitos","Carlos","Jesus",
            "cj@gmail.com","1234","10000",
            "Caceres","España","123123123")
        userDao.insert(user)


        //
        val cod = userDao.findByLogin("carlitos","1234")

        videoDao.deleteFromUserId(cod)
        contactDao.deleteFromUserId(cod)
        userDao.deleteByCod(cod)

        assertEquals(1, videoDao.getAllVideos().size)
        assertEquals(1, contactDao.getAllContacts().size)
        assertEquals(0, userDao.getAllUsers().size)

    }
}