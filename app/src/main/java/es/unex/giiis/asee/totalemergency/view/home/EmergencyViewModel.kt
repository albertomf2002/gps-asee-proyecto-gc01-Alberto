package es.unex.giiis.asee.totalemergency.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import es.unex.giiis.asee.totalemergency.TotalEmergencyApplication
import es.unex.giiis.asee.totalemergency.data.Repository
import es.unex.giiis.asee.totalmergency.api.APIError
import es.unex.giiis.asee.totalmergency.data.database.TotalEmergencyDatabase
import es.unex.giiis.asee.totalmergency.data.model.User
import es.unex.giiis.asee.totalmergency.data.model.VideoRecord
import es.unex.giiis.asee.totalmergency.view.home.HomeActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class EmergencyViewModel (
    private val repository: Repository,
) : ViewModel() {
    var user: User? = null

    init {

    }

    fun insertVideo(vr : VideoRecord){
        viewModelScope.launch {
            repository.insertVideo(vr)
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
                return EmergencyViewModel(
                    (application as TotalEmergencyApplication).appContainer.repository
                ) as T
            }
        }
    }
}