package es.unex.giiis.asee.totalmergency.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import es.unex.giiis.asee.totalmergency.data.database.TotalEmergencyDatabase

import es.unex.giiis.asee.totalmergency.databinding.ActivityJoinBinding
import es.unex.giiis.asee.totalmergency.data.model.User
import es.unex.giiis.asee.totalmergency.util.CredentialCheck
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class JoinActivity : AppCompatActivity() {

    private lateinit var db: TotalEmergencyDatabase
    private lateinit var binding: ActivityJoinBinding

    companion object {

        const val COD = "JOIN_COD"
        const val USERNAME = "JOIN_USERNAME"
        const val PASS = "JOIN_PASS"
        fun start(
            context: Context,
            responseLauncher: ActivityResultLauncher<Intent>
        ) {
            val intent = Intent(context, JoinActivity::class.java)
            responseLauncher.launch(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TotalEmergencyDatabase.getInstance(applicationContext)!!

        //views initialization and listeners
        setUpUI()
        setUpListeners()
    }

    private fun setUpUI() {
        //get attributes from xml using binding
    }

    private fun setUpListeners() {
        with(binding) {
            btRegister.setOnClickListener {
                join()
            }
        }
    }

    private fun join() {
        with(binding) {
            val check = CredentialCheck.join(
                etUsername.text.toString(),
                etPassword.text.toString(),
                etRepassword.text.toString()
            )
            if (check.fail) notifyInvalidCredentials(check.msg) else {
                lifecycleScope.launch {
                    val user = User(null, etUsername.text.toString(),
                        userPassword = etPassword.text.toString(), name = etRealname.text.toString(),
                        lastName = etLastname.text.toString(), email = etEmail.text.toString(),
                        addres = etAddress.text.toString(), city = etCity.text.toString(),
                        country = etCountry.text.toString(), telephone = etPhone.text.toString()
                    )
                    val cod = db?.userDao()?.insert(user)
                    navigateBackWithResult (
                        cod = cod!!,
                        username = etUsername.text.toString(),
                        pass = etPassword.text.toString()
                    )
                }
            }
        }
    }


    private fun navigateBackWithResult(cod: Long, username: String, pass: String) {
        val intent = Intent().apply {
            putExtra(COD, cod)
            putExtra(USERNAME, username)
            putExtra(PASS, pass)
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun notifyInvalidCredentials(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}