package es.unex.giiis.asee.totalemergency.data

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import es.unex.giiis.asee.totalemergency.data.database.dao.ContactDAO
import es.unex.giiis.asee.totalemergency.data.database.dao.LocalizacionesDAO
import es.unex.giiis.asee.totalemergency.data.database.dao.UserDAO
import es.unex.giiis.asee.totalemergency.data.database.dao.VideoRecordDAO
import es.unex.giiis.asee.totalmergency.api.APIError
import es.unex.giiis.asee.totalmergency.api.UbicationAPI
import es.unex.giiis.asee.totalmergency.data.model.Contact
import es.unex.giiis.asee.totalmergency.data.model.User
import es.unex.giiis.asee.totalmergency.data.model.VideoRecord
import es.unex.giiis.asee.totalmergency.data.toLoc
import es.unex.giiis.asee.totalmergency.view.home.HomeActivity
import java.io.File
import java.util.Date
import java.util.Locale


class Repository (
    private val localizacionesDao: LocalizacionesDAO,
    private val userDao: UserDAO,
    private val contactDao: ContactDAO,
    private val videoRecordDao: VideoRecordDAO,
    private val networkService: UbicationAPI
) {
    private var lastUpdateTimeMillis: Long = 0L

    fun systemDate() : String{
        return SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    }

    val localizaciones = localizacionesDao.getAllUbications()

    suspend fun obtenerContactos(cod : Long) : List<Contact>{
        return contactDao.getAllContactsFromUser(cod)
    }

    suspend fun guardarContacto(contact: Contact) {
        contactDao.insert(contact)
    }
    suspend fun insertVideo(vr : VideoRecord){
        videoRecordDao.insert(vr)
    }

    suspend fun getAllVideos(cod: Long) : List<VideoRecord> {
        return videoRecordDao.getAllVideosFromUser(cod)
    }

    suspend fun deleteVideo(vr : Long){
        videoRecordDao.deleteFromId(vr)
    }

    suspend fun getUserFromCod(cod: Long) : User{
        return userDao.findByCod(cod)
    }
    suspend fun modifyUser(user: User){
        userDao.modifyUser(user)
    }

    suspend fun deleteUser(user: User){
        userDao.deleteByCod(user.cod!!)
        contactDao.deleteFromUserId(user.cod!!)
        videoRecordDao.deleteFromUserId(user.cod!!)
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

    /**
     * Esta funcion del repositorio se encarga de crear video
     * según el FileProvider.
     *
     * @param uri Uri del video a almacenar
     * @param fileName nombre con el cual guardar el video
     * @param fileUser codigo del usuario asociado al video.
     * @param timeStam fecha en la cual se grabó el video
     * @param context Contexto desde el cual se llamó
     *
     * @return Long : Retorna el código identificador dentro de la base de datos.
     * */
    suspend fun FP_CrearVideo(uri:Uri, fileName : String, fileUser : Long, timeStamp:String, context: Context) : Long{

        uri.let { it ->
            val folderName = "es.unex.giiis.asee.totalemergency.videos"
            val folderPath = context.getDir(folderName, Context.MODE_PRIVATE).absolutePath
            val videoFile = File(folderPath, fileName)
            val inputStream = context.contentResolver.openInputStream(it)
            inputStream?.use { input ->
                videoFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            return tryStoreVideo(videoFile.path!!, fileUser, timeStamp)
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
            Log.d("Repository", "PRE DANGER")
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

    fun askPhonePermission(context: Context, activity: Activity) {
        val phonePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)

        if (phonePermission == PackageManager.PERMISSION_DENIED) {
            // Phone call permission is not granted
            // Request the permission
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CALL_PHONE), 100)
        } else {
            // Phone call permission is already granted, proceed with making a call
            // Your code to start a call goes here
        }
    }

    fun askLocationPermission(context: Context, activity: Activity){
        val locationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)

        if (locationPermission == PackageManager.PERMISSION_DENIED) {
            // Location permission is not granted
            // Request the permission
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 103)
        } else {
            // Location permission is already granted, proceed with accessing location
            // Your code to access location goes here
        }
    }
    fun askStoragePermission(context: Context, activity: Activity){

        // Check if the app has permissions to read and write external storage
        val readPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        val writePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (readPermission == PackageManager.PERMISSION_DENIED || writePermission == PackageManager.PERMISSION_DENIED) {
            // Permissions for reading or writing external storage are not granted
            // Request both permissions

            val permissionsToRequest = mutableListOf<String>()

            // Check and add read external storage permission if needed
            if (readPermission == PackageManager.PERMISSION_DENIED) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

            // Check and add write external storage permission if needed
            if (writePermission == PackageManager.PERMISSION_DENIED) {
                permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

            // Request permissions
            ActivityCompat.requestPermissions(activity, permissionsToRequest.toTypedArray(), 102)
        } else {
            // Both permissions are already granted, proceed with accessing photos and videos
            // Your code to access photos and videos goes here
        }
    }

    suspend fun insertUser(user: User) : Long {
        return userDao.insert(user)
    }

    suspend fun getUserFromCredentials(name: String, password: String) : Long {
        return userDao.findByLogin(name, password)
    }

    companion object {
        private const val MIN_TIME_FROM_LAST_FETCH_MILLIS: Long = 30000
    }
}
