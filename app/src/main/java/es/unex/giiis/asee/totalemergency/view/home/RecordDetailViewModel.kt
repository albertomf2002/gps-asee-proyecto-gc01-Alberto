package es.unex.giiis.asee.totalemergency.view.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import es.unex.giiis.asee.totalemergency.TotalEmergencyApplication
import es.unex.giiis.asee.totalemergency.data.Repository
import es.unex.giiis.asee.totalemergency.data.model.VideoRecord
import kotlinx.coroutines.launch
import java.io.File

class RecordDetailViewModel(
    private val repository: Repository,
) : ViewModel() {


    var path : String? = null
    var video : VideoRecord? = null

    fun deleteVideo(video: VideoRecord?) {
        viewModelScope.launch {
            repository.deleteVideo(video?.videoId!!)
        }
    }

    fun deleteFile(fdelete: File, video: VideoRecord?) {
        viewModelScope.launch {
            if(fdelete.delete()){
                Log.i("DELETE", "It was succesfully deleted")
                repository.deleteVideo(video?.videoId!!)
            }else{
                Log.i("DELETE", "It was NOT succesfully deleted")
            }

        }
    }

    init {

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
                return RecordDetailViewModel(
                    (application as TotalEmergencyApplication).appContainer.repository
                ) as T
            }
        }
    }
}