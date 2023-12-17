package es.unex.giiis.asee.totalemergency.view.home

import android.app.Activity
import android.content.Context
import android.text.Editable.Factory
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import es.unex.giiis.asee.totalemergency.TotalEmergencyApplication
import es.unex.giiis.asee.totalemergency.data.Repository
import es.unex.giiis.asee.totalmergency.api.APIError
import es.unex.giiis.asee.totalmergency.data.model.User
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeMenuViewModel (
    private val repository: Repository
) : ViewModel() {
    var user: User? = null
    val localizaciones = repository.localizaciones

    private val _toast = MutableLiveData<String?>()
    val toast: LiveData<String?>
        get() = _toast


    private fun refresh(){
        launchDataLoad { repository.tryUpdateRecentLocationCache()}
    }

    init {
        refresh()
    }

    fun askLocationPermission(context: Context, activity: Activity){
        repository.askLocationPermission(context, activity)
    }


    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try{
                block()
            } catch (error: APIError){
                _toast.value = error.message
            }
        }
    }

    fun onToastShown() {
        _toast.value = null
    }


    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory{
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return HomeMenuViewModel(
                    (application as TotalEmergencyApplication).appContainer.repository,
                ) as T
            }
        }
    }
}