package es.unex.giiis.asee.totalemergency.data

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import es.unex.giiis.asee.totalemergency.data.database.dao.ContactDAO
import es.unex.giiis.asee.totalemergency.data.database.dao.LocalizacionesDAO
import es.unex.giiis.asee.totalemergency.data.database.dao.UserDAO
import es.unex.giiis.asee.totalemergency.data.database.dao.VideoRecordDAO
import es.unex.giiis.asee.totalmergency.api.APIError
import es.unex.giiis.asee.totalmergency.api.UbicationAPI
import es.unex.giiis.asee.totalmergency.data.model.User
import es.unex.giiis.asee.totalmergency.data.model.VideoRecord
import es.unex.giiis.asee.totalmergency.data.toLoc
import es.unex.giiis.asee.totalmergency.view.home.HomeActivity


class Repository (
    private val localizacionesDao: LocalizacionesDAO,
    private val userDao: UserDAO,
    private val contactDao: ContactDAO,
    private val videoRecordDao: VideoRecordDAO,
    private val networkService: UbicationAPI
) {
    private var lastUpdateTimeMillis: Long = 0L

    val localizaciones = localizacionesDao.getAllUbications()

    suspend fun insertVideo(vr : VideoRecord){
        videoRecordDao.insert(vr)
    }

    suspend fun getUserFromCod(cod: Long) : User{
        return userDao.findByCod(cod)
    }

    suspend fun deleteContactFromCod(cod : Long){
        contactDao.deleteFromId(cod)
    }
    suspend fun tryUpdateRecentLocationCache() {
        if (shouldUpdateLocationCache()) fetchRecentUbications()
    }

    suspend fun tryStoreVideo(path : String, userId : Long, date : String) : Long{
        val vr = VideoRecord(videoId= null, path=path, userId=userId, date=date)
        return videoRecordDao.insert(vr)
    }


    fun requestCameraPermission(context: Context, activity : Activity) {
        Log.i("Repository: Permission request", "Permission request called")
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED)
        {
            Log.i("Repository: Permission request", "Permission request successfully called")
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), 100)
        }
    }

    fun isBackCameraPresent(context: Context): Boolean {
        return context?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_EXTERNAL)?: false
    }
    fun isFrontCameraPresent(context: Context) : Boolean {
        return context?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)?: false
    }


    fun getPath(uri: Uri, context: Context): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor = context.contentResolver?.query(uri, projection, null, null, null)
        cursor?.use {
            Log.i("Cursor", "Trying to fetch the data")
            if(it.moveToFirst()){
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }

    private suspend fun fetchRecentUbications() {
        Log.d("Repository", "Fetching data from ubications")
        try {
            val localizaciones = networkService.getAllUbications().map { it.toLoc() }
            Log.d("Repository", "Data has been read: ${localizaciones.size}")
            localizacionesDao.insertAll(localizaciones)
            Log.d("Repository", "Data stored")
            lastUpdateTimeMillis = System.currentTimeMillis()
        } catch (cause: Throwable) {
            throw APIError("Unable to fetch data from API", cause)
        }
    }

    private suspend fun shouldUpdateLocationCache(): Boolean {
        val lastFetchTimeMillis = lastUpdateTimeMillis
        val timeFromLastFetch = System.currentTimeMillis() - lastFetchTimeMillis
        return timeFromLastFetch > MIN_TIME_FROM_LAST_FETCH_MILLIS || localizacionesDao.getTotalUbications() == 0L
    }

    companion object {
        private const val MIN_TIME_FROM_LAST_FETCH_MILLIS: Long = 30000
    }
}
