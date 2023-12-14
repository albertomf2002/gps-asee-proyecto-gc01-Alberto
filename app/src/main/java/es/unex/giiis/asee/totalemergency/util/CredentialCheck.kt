package es.unex.giiis.asee.totalmergency.util

class CredentialCheck private constructor() {

    var fail: Boolean = false
    var msg: String = ""
    var error: CredentialError = CredentialError.PasswordError

    companion object{

        private val TAG = CredentialCheck::class.java.canonicalName
        private val MINCHARS = 4

        private val checks = arrayOf(
            CredentialCheck().apply {
                fail = false
                msg = CredentialMessage.CREDENTIALS_OK.message
                error = CredentialError.Success
            },
            CredentialCheck().apply {
                fail = true
                msg = CredentialMessage.INVALID_USERNAME.message
                error = CredentialError.UsernameError
            },
            CredentialCheck().apply {
                fail = true
                msg = CredentialMessage.INVALID_PASSWORD.message
                error = CredentialError.PasswordError
            },
            CredentialCheck().apply {
                fail = true
                msg = CredentialMessage.PASSWORDS_NOT_MATCH.message
                error = CredentialError.PasswordError
            }

        )

        fun login(username: String, password: String): CredentialCheck {
            return if (username.isBlank() || username.length < MINCHARS) checks[1]
            else if (password.isBlank() || password.length < MINCHARS) checks[2]
            else checks[0]
        }

        fun join(username: String, password: String, repassword: String): CredentialCheck {
            return if (username.isBlank() || username.length < MINCHARS) checks[1]
            else if (password.isBlank() || password.length < MINCHARS) checks[2]
            else if (password!=repassword) checks[3]
            else checks[0]
        }
    }

    enum class CredentialError {
        PasswordError, UsernameError, Success
    }

    enum class CredentialMessage(val message: String) {
        CREDENTIALS_OK("Your credentials are OK"),
        INVALID_USERNAME("Invalid username"),
        INVALID_PASSWORD("Invalid password"),
        PASSWORDS_NOT_MATCH("Passwords do not match")
    }

}