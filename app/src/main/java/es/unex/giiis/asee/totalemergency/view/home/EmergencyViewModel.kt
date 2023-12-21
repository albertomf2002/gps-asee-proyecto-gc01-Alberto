package es.unex.giiis.asee.totalemergency.view.home

import android.app.Activity
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
import es.unex.giiis.asee.totalemergency.api.APIError
import es.unex.giiis.asee.totalemergency.data.database.TotalEmergencyDatabase
import es.unex.giiis.asee.totalemergency.data.model.User
import es.unex.giiis.asee.totalemergency.data.model.VideoRecord
import es.unex.giiis.asee.totalemergency.view.home.HomeActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date
import java.util.Locale

class EmergencyViewModel (
    private val repository: Repository,
) : ViewModel() {
    var user: User? = null

    private val _cameraResponse = MutableLiveData<Pair<Long, String>>()
    val cameraResponse: LiveData<Pair<Long, String>>
        get() = _cameraResponse

    private val _cameraPermission = MutableLiveData<Boolean>()
    val cameraPermission: LiveData<Boolean>
        get() = _cameraPermission

    fun retrieveUriData(uri: Uri, context: Context){
        viewModelScope.launch {
            // Get the variables
            val videoUri = uri
            val timeStamp = repository.systemDate()
            val videoFileName = "VIDEO_${timeStamp}.mp4"

            val codInserted = repository.FP_CrearVideo(videoUri, videoFileName, user?.cod!!, timeStamp, context)

            _cameraResponse.value = Pair(codInserted,if(codInserted != -1L) "Camera response, video inserted with cod:${codInserted}" else "Video failed to be created")
        }
    }



    init {

    }

    fun obtainPermission(context: Context, activity : Activity){
        if(repository.isFrontCameraPresent(context) || repository.isBackCameraPresent(context)){
            repository.requestCameraPermission(context, activity)
            _cameraPermission.value = true
        }else{
            _cameraPermission.value = false
        }


    }

    fun insertVideo(vr : VideoRecord){
        viewModelScope.launch {
            repository.insertVideo(vr)
        }
    }

    fun obtainStoragePermission(context: Context, activity: HomeActivity) {
        repository.askStoragePermission(context, activity)
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