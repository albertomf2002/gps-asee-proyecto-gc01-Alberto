package es.unex.giiis.asee.totalemergency.data

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import es.unex.giiis.asee.totalemergency.data.database.dao.ContactDAO
import es.unex.giiis.asee.totalemergency.data.database.dao.LocalizacionesDAO
import es.unex.giiis.asee.totalemergency.data.database.dao.UserDAO
import es.unex.giiis.asee.totalemergency.data.database.dao.VideoRecordDAO
import es.unex.giiis.asee.totalmergency.api.APIError
import es.unex.giiis.asee.totalmergency.api.UbicationAPI
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

    suspend fun tryUpdateRecentLocationCache() {
        if (shouldUpdateLocationCache()) fetchRecentUbications()
    }

    suspend fun tryStoreVideo(path : String, userId : Long, date : String) : Long{
        val vr = VideoRecord(videoId= null, path=path, userId=userId, date=date)
        return videoRecordDao.insert(vr)
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
