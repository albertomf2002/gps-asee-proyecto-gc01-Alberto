package es.unex.giiis.asee.totalemergency.view.home

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import es.unex.giiis.asee.totalemergency.TotalEmergencyApplication
import es.unex.giiis.asee.totalemergency.data.Repository
import es.unex.giiis.asee.totalmergency.data.model.User
import es.unex.giiis.asee.totalmergency.data.model.VideoRecord
import es.unex.giiis.asee.totalmergency.view.home.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RecordRegistryViewModel(
    private val repository: Repository,
) : ViewModel() {

    var user : User? = null

    private val _videos = MutableLiveData<List<VideoRecord>>()
    val videos : LiveData<List<VideoRecord>>
        get() = _videos

    fun obtainStoragePermission(context: Context, activity: Activity) {
        repository.askStoragePermission(context, activity)
    }

    fun obtainVideos() {
        viewModelScope.launch() {
            _videos.value = repository.getAllVideos(user?.cod!!)
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
                return RecordRegistryViewModel(
                    (application as TotalEmergencyApplication).appContainer.repository
                ) as T
            }
        }
    }
}