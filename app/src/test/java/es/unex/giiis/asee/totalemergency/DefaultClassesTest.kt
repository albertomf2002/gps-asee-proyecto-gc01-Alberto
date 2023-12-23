package es.unex.giiis.asee.totalemergency

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

        assertEquals(1, contact1.contactId)
        assertEquals(2, contact2.contactId)
        assertEquals(3, contact3.contactId)

        assertEquals(123456123L, contact1.telephone)
        assertEquals(999888777L, contact2.telephone)
        assertEquals(111222333L, contact3.telephone)

        assertEquals("Juan", contact1.contactName)
        assertEquals("Carlos", contact2.contactName)
        assertEquals("Miguel", contact3.contactName)

        assertEquals(1, contact1.userId)
        assertEquals(2, contact2.userId)
        assertEquals(1, contact3.userId)
    }
}