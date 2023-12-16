package es.unex.giiis.asee.totalemergency.view.home

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.util.Log
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

    private val _cameraResponse = MutableLiveData<Pair<Long, String>>()
    val cameraResponse: LiveData<Pair<Long, String>>
        get() = _cameraResponse

    fun retrieveUriData(uri: Uri, context: Context){
        viewModelScope.launch {
            // Get the variables
            val videoUri = uri
            val path = repository.getPath(videoUri, context)

            Log.i("VIDEO_RECORD_TAG", "Video is recorded and available at path: ${path}")
            //Sacar la fecha
            val calendar: Calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val dateTime: String = dateFormat.format(calendar.time)

            Log.i("DATE TIME", "The date is: ${dateTime}")
            // Patron repository se encarga de manejar la BD.
            val codInserted = repository.tryStoreVideo(path!!, user?.cod!!, dateTime)
            /*
             Actualizo la respuesta.
             La vista vinculada (?) (EmergencyFragment, pero puede ser cualquier otra que lo llegase a requerir)
                * Recibe respuesta.
             */

            _cameraResponse.value = Pair<Long, String>(codInserted,if(codInserted != -1L) "Camera response, video inserted with cod:${codInserted}" else "Video failed to be created")

        }
    }



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