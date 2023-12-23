package es.unex.giiis.asee.totalemergency

import es.unex.giiis.asee.totalemergency.data.model.Localizaciones
import es.unex.giiis.asee.totalemergency.data.model.User
import es.unex.giiis.asee.totalemergency.data.model.VideoRecord
import android.app.Activity
import android.content.Context
import es.unex.giiis.asee.totalemergency.data.Repository
import es.unex.giiis.asee.totalemergency.data.model.Contact
import es.unex.giiis.asee.totalemergency.util.CredentialCheck
import es.unex.giiis.asee.totalemergency.view.home.EmergencyViewModel
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

import org.junit.Assert.*
import org.mockito.kotlin.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DefaultClassesTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun username_and_password_validator(){
        //Sirve para comparar la clase credential check, no es parte de las HU pero es importante.

        //Username and password >= 4 length
        assertEquals(CredentialCheck.login("Carlos", "1234").fail, false)
        assertEquals(CredentialCheck.login("Carlos", "1234").msg, CredentialCheck.CredentialMessage.CREDENTIALS_OK.message)
        assertEquals(CredentialCheck.login("Carlos", "1234").error, CredentialCheck.CredentialError.Success)

        //Incorrect password
        assertEquals(CredentialCheck.login("Carlos", "123").fail, true)
        assertEquals(CredentialCheck.login("Carlos", "123").msg, CredentialCheck.CredentialMessage.INVALID_PASSWORD.message)
        assertEquals(CredentialCheck.login("Carlos", "123").error, CredentialCheck.CredentialError.PasswordError)

        //Incorrect username
        assertEquals(CredentialCheck.login("Ca", "12345").fail, true)
        assertEquals(CredentialCheck.login("Ca", "12345").msg, CredentialCheck.CredentialMessage.INVALID_USERNAME.message)
        assertEquals(CredentialCheck.login("Ca", "12345").error, CredentialCheck.CredentialError.UsernameError)

        //Incorrect password and username
        assertEquals(CredentialCheck.login("Car", "123").fail, true)
        assertEquals(CredentialCheck.login("Car", "123").msg, CredentialCheck.CredentialMessage.INVALID_USERNAME.message)
        assertEquals(CredentialCheck.login("Car", "123").error, CredentialCheck.CredentialError.UsernameError)
    }

    @Test
    fun contactTest(){
        val contact1 = Contact(1, 123456123L, "Juan", 1)
        val contact2 = Contact(2, 999888777L, "Carlos", 2)
        val contact3 = Contact(3, 111222333L, "Miguel", 1)

        assertEquals(1L, contact1.contactId)
        assertEquals(2L, contact2.contactId)
        assertEquals(3L, contact3.contactId)

        assertEquals(123456123L, contact1.telephone)
        assertEquals(999888777L, contact2.telephone)
        assertEquals(111222333L, contact3.telephone)

        assertEquals("Juan", contact1.contactName)
        assertEquals("Carlos", contact2.contactName)
        assertEquals("Miguel", contact3.contactName)

        assertEquals(1L, contact1.userId)
        assertEquals(2L, contact2.userId)
        assertEquals(1L, contact3.userId)
    }

    @Test
    fun userTests(){
        val user1 = User(1L, "paquito", "Paco", "Quito", "pq@gmail.com",
            "1234", "casa", "Caceres", "España", "123123")
        val user2 = User(2L, "carlitos", "Carlos", "Juan", "cj@gmail.com",
            "1234", "casa", "Caceres", "España", "333333")
        val user3 = User(3L, "miguelin", "Miguel", "Jose", "mj@gmail.com",
            "6789", "casa", "Caceres", "España", "789987")

        assertEquals(1L, user1.cod)
        assertEquals(2L, user2.cod)
        assertEquals(3L, user3.cod)

        assertEquals("paquito", user1.userName)
        assertEquals("carlitos", user2.userName)
        assertEquals("miguelin", user3.userName)

        assertEquals("Paco", user1.name)
        assertEquals("Carlos", user2.name)
        assertEquals("Miguel", user3.name)

        assertEquals("Quito", user1.lastName)
        assertEquals("Juan", user2.lastName)
        assertEquals("Jose", user3.lastName)

        assertEquals("pq@gmail.com", user1.email)
        assertEquals("cj@gmail.com", user2.email)
        assertEquals("mj@gmail.com", user3.email)

        assertEquals("1234", user1.userPassword)
        assertEquals("1234", user2.userPassword)
        assertEquals("6789", user3.userPassword)

        assertEquals("casa", user1.addres)
        assertEquals("casa", user2.addres)
        assertEquals("casa", user3.addres)

        assertEquals("Caceres", user1.city)
        assertEquals("Caceres", user2.city)
        assertEquals("Caceres", user3.city)

        assertEquals("España", user1.country)
        assertEquals("España", user2.country)
        assertEquals("España", user3.country)

        assertEquals("123123", user1.telephone)
        assertEquals("333333", user2.telephone)
        assertEquals("789987", user3.telephone)


    }

    @Test
    fun videoTests(){
        val video1 = VideoRecord(1L, "storage/video/0", 1L, "3-8-2022")
        val video2 = VideoRecord(2L, "storage/video/1", 2L, "17-8-2022")
        val video3 = VideoRecord(3L, "storage/video/2", 1L, "8-9-2022")

        assertEquals(1L, video1.videoId)
        assertEquals(2L, video2.videoId)
        assertEquals(3L, video3.videoId)

        assertEquals("storage/video/0", video1.path)
        assertEquals("storage/video/1", video2.path)
        assertEquals("storage/video/2", video3.path)

        assertEquals(1L, video1.userId)
        assertEquals(2L, video2.userId)
        assertEquals(1L, video3.userId)

        assertEquals("3-8-2022", video1.date)
        assertEquals("17-8-2022", video2.date)
        assertEquals("8-9-2022", video3.date)


    }

    @Test
    fun locTests(){
        val loc1 = Localizaciones("ID:1", 1.0, 1.0, "Centro salud", 123123L)
        val loc2 = Localizaciones("ID:2", -1.0, 0.5, "Hospital", 887766L)


        assertEquals("ID:1", loc1.localizationId)
        assertEquals("ID:2", loc2.localizationId)

        assertEquals(1.0, loc1.longitude, 0.005)
        assertEquals(-1.0, loc2.longitude, 0.005)

        assertEquals(1.0, loc1.latitude, 0.005)
        assertEquals(0.5, loc2.latitude, 0.005)

        assertEquals("Centro salud", loc1.name)
        assertEquals("Hospital", loc2.name)

        assertEquals(123123L, loc1.telephone)
        assertEquals(887766L, loc2.telephone )

    }
}