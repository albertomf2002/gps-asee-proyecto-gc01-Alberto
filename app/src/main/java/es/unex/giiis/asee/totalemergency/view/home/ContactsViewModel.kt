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
import es.unex.giiis.asee.totalemergency.data.model.Contact
import es.unex.giiis.asee.totalemergency.data.model.User
import kotlinx.coroutines.launch

class ContactsViewModel(
    private val repository: Repository
) : ViewModel()  {

    var user : User? = null

    private val _contactos = MutableLiveData<List<Contact>?>()
    val contactos: LiveData<List<Contact>?>
        get() = _contactos

    fun obtenerListado(){
        viewModelScope.launch {
            Log.i("ContactosViewModel", "Contacts being fetched of user: ${user?.cod}")
            _contactos.value = repository.obtenerContactos(user?.cod!!)
            Log.i("ContactosViewModel", "Contact fetched: ${_contactos.value?.size}")
        }
    }

    fun guardarContacto(contact: Contact){
        viewModelScope.launch {
            repository.guardarContacto(contact)
        }
    }

    fun borrarContact(cod : Long){
        viewModelScope.launch {
            repository.deleteContactFromCod(cod)
        }
    }

    fun requirePermisson(context: Context, activity: Activity) {
        // Check if the app has phone call permission
        repository.askPhonePermission(context, activity)
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
                return ContactsViewModel(
                    (application as TotalEmergencyApplication).appContainer.repository
                ) as T
            }
        }
    }
}