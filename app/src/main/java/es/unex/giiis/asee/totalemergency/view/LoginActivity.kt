package es.unex.giiis.asee.totalmergency.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import es.unex.giiis.asee.totalmergency.data.database.TotalEmergencyDatabase

import es.unex.giiis.asee.totalmergency.databinding.ActivityLoginBinding
import es.unex.giiis.asee.totalmergency.data.model.User
import es.unex.giiis.asee.totalmergency.util.CredentialCheck
import es.unex.giiis.asee.totalmergency.view.home.HomeActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: TotalEmergencyDatabase
    private var guardar: Boolean = false

    private val responseLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                with(result.data) {
                    val name = this?.getStringExtra(JoinActivity.USERNAME).orEmpty()
                    val password = this?.getStringExtra(JoinActivity.PASS).orEmpty()

                    with(binding) {
                        etPassword.setText(password)
                        etUsername.setText(name)
                    }

                    Toast.makeText(
                        this@LoginActivity,
                        "New user ($name/$password) created",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //view binding and set content view
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //database initialization
        db = TotalEmergencyDatabase.getInstance(applicationContext)!!

        //views initialization and listeners
        setUpUI()
        setUpListeners()

        readSettings()
    }

    private fun readSettings(){
        val preferences = PreferenceManager.getDefaultSharedPreferences(this).all

        val rememberme = preferences["rememberme"] as Boolean? ?: false
        val username = preferences["username"] as String? ?: ""
        val password = preferences["password"] as String? ?: ""

        Log.d("PREFERENCES", "The main preferences are: $rememberme, $username, $password")

        if (rememberme) {
            Log.d("PREFERENCES", "Preferences are being applied: $rememberme, $username, $password")
            binding.etUsername.setText(username)
            binding.etPassword.setText(password)
            checkLogin()
        }
    }

    private fun setSettings(context: Context){
        with(binding) {
            if (rememberMe.isChecked) {
                val setPreferences = PreferenceManager.getDefaultSharedPreferences(context).edit()
                setPreferences.putBoolean("rememberme", true)
                setPreferences.putString("username", etUsername.text.toString())
                setPreferences.putString("password", etPassword.text.toString())
                setPreferences.apply()
            }
        }
    }

    private fun setUpUI() {
        //get attributes from xml using binding
        with(binding) {
            guardar = rememberMe.isChecked
        }
    }

    private fun setUpListeners() {
        with(binding) {

            btLogin.setOnClickListener {
                checkLogin()
            }

            btRegister.setOnClickListener {
                navigateToJoin()
            }

            rememberMe.setOnClickListener{
                //guardar = rememberMe.isChecked
            }


        }
    }

    private fun checkLogin() {
        val check = CredentialCheck.login(binding.etUsername.text.toString(), binding.etPassword.text.toString())
        if (!check.fail) {
            lifecycleScope.launch {
                val codUser = db?.userDao()?.findByLogin(binding.etUsername.text.toString(), binding.etPassword.text.toString())

                if (codUser != null) {
                    if(check.fail) {
                        notifyInvalidCredentials(check.msg)
                    }else {
                        Log.i("User data", "User cod is retrieved: " + codUser)
                        //REMEMBER ME
                        setSettings(binding.root.context)

                        navigateToHomeActivity(codUser, check.msg)
                    }
                }
                else
                    notifyInvalidCredentials("Invalid username")
            }
        }
        else
            notifyInvalidCredentials(check.msg)
    }

    private fun guardarAutologin(remember: Boolean){

    }

    private fun navigateToHomeActivity(codUser: Long, msg: String) {
       // Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        HomeActivity.start(this, codUser)
    }

    private fun navigateToJoin() {
        JoinActivity.start(this, responseLauncher)
    }


    private fun notifyInvalidCredentials(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}