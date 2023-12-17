package es.unex.giiis.asee.totalemergency.view.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import es.unex.giiis.asee.totalemergency.TotalEmergencyApplication
import es.unex.giiis.asee.totalemergency.data.Repository
import es.unex.giiis.asee.totalmergency.data.model.Contact
import es.unex.giiis.asee.totalmergency.data.model.User
import kotlinx.coroutines.launch


class HomeViewModel(
    private val repository: Repository
) : ViewModel()
{
    private val _user_cod = MutableLiveData<Long>()
    private val _user = MutableLiveData<User>(null)
    val user: LiveData<User>
        get() = _user

    private val _navigateToShow = MutableLiveData<Contact>()

    val navigateToShow : LiveData<Contact>
        get() = _navigateToShow

    fun onClickContact(contact: Contact, context: Context){
        _navigateToShow.value = contact

        Log.i("CALL", "Starting a call with another phone")
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:" + contact.telephone)
        context.startActivity(callIntent)

    }

    fun onDeleteClickCall(contact: Contact, context: Context) {
        viewModelScope.launch {
            repository.deleteContactFromCod(contact.contactId!!)
        }
    }

    fun onClickDelete(contact: Contact) {
        //Nothing, yet
    }



    fun obtenerUser(cod : Long){
        viewModelScope.launch {
            _user.value = repository.getUserFromCod(cod)
        }
    }

    fun navitageToProfileUpdater(){

    }


    companion object {

        val Factory : ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return HomeViewModel(
                    (application as TotalEmergencyApplication).appContainer.repository,
                ) as T
            }
        }
    }

}