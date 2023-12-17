package es.unex.giiis.asee.totalemergency.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import es.unex.giiis.asee.totalemergency.TotalEmergencyApplication
import es.unex.giiis.asee.totalemergency.data.Repository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: Repository,
) : ViewModel()   {


    private val _userCod = MutableLiveData<Long>()

    val userCod : LiveData<Long>
        get() = _userCod
    fun retrieveUserCod(name: String, password: String) {
        viewModelScope.launch {
            _userCod.value = repository.getUserFromCredentials(name, password)
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
                return LoginViewModel(
                    (application as TotalEmergencyApplication).appContainer.repository
                ) as T
            }
        }
    }
}