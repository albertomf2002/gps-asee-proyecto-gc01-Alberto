package es.unex.giiis.asee.totalmergency

import es.unex.giiis.asee.totalmergency.util.CredentialCheck
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun username_and_password_validator(){

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
}