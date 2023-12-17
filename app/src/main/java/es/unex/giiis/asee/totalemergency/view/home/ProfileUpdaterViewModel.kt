package es.unex.giiis.asee.totalemergency.view.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.preference.PreferenceManager
import es.unex.giiis.asee.totalemergency.TotalEmergencyApplication
import es.unex.giiis.asee.totalemergency.data.Repository
import es.unex.giiis.asee.totalmergency.data.model.User
import kotlinx.coroutines.launch

class ProfileUpdaterViewModel (
    private val repository: Repository
) : ViewModel() {

    var user: User? = null

    private val _userView = MutableLiveData<User>()
    val userView : LiveData<User>
        get() = _userView

    fun updateUser(){
        _userView.value = user
    }

    fun modifyUser(userMod: User, context : Context){
        viewModelScope.launch {
            user = userMod
            updateUser()
            repository.modifyUser(userMod)

            val setPreferences = PreferenceManager.getDefaultSharedPreferences(context).edit()
            //setPreferences.putBoolean("rememberme", true)
            setPreferences.putString("username", userMod.userName)
            setPreferences.putString("password", userMod.userPassword)
            setPreferences.apply()
        }
    }

    fun deleteUser(context: Context){
        viewModelScope.launch {
            repository.deleteUser(user!!)

            val setPreferences = PreferenceManager.getDefaultSharedPreferences(context).edit()
            setPreferences.putBoolean("rememberme", false)
            setPreferences.putString("username", "")
            setPreferences.putString("password", "")
            setPreferences.apply()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return ProfileUpdaterViewModel(
                    (application as TotalEmergencyApplication).appContainer.repository
                ) as T
            }
        }
    }
}