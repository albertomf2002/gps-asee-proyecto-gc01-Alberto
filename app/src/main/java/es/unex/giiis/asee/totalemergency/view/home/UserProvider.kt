package es.unex.giiis.asee.totalemergency.view.home

import es.unex.giiis.asee.totalmergency.data.model.User

interface UserProvider {
    fun getUser(): User
}