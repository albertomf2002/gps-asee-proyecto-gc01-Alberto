package es.unex.giiis.asee.totalmergency.view.home

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import es.unex.giiis.asee.totalmergency.data.database.TotalEmergencyDatabase
import es.unex.giiis.asee.totalmergency.data.model.VideoRecord
import es.unex.giiis.asee.totalmergency.databinding.FragmentEmergencyBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.io.FilePermission


/**
 * A simple [Fragment] subclass.
 * Use the [EmergencyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EmergencyFragment : Fragment() {

    private var _binding: FragmentEmergencyBinding? = null

    private lateinit var videoUri : Uri
    private val binding get() = _binding!!

    private lateinit var db: TotalEmergencyDatabase

    val scope = CoroutineScope(Job() + Dispatchers.Main)

    private val responseCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == RESULT_OK){

                val calendar: Calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val dateTime: String = dateFormat.format(calendar.time)

                videoUri = result.data?.data!!
                val path = getPath(videoUri)

                Log.i("VIDEO_RECORD_TAG", "Other form of path: ${videoUri.path}")
                Log.i("VIDEO_RECORD_TAG", "Video is recorded and available at uri: ${videoUri}")
                Log.i("VIDEO_RECORD_TAG", "Video is recorded and available at path: ${path}")

                val videoFile = createVideoFile(dateTime)

                videoUri.let{ uri ->
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    inputStream?.use { input ->
                        videoFile?.outputStream().use { output ->
                            input.copyTo(output!!)
                        }
                    }
                }

                Log.i("VIDEO_RECORD_TAG", "AUTHORITY: ${videoUri.authority}")

                Log.i("DATE TIME", "The date is: ${dateTime}")

                val vr = VideoRecord(videoId= null, path= "$path", userId= (activity as HomeActivity).getUser().cod!!, date=dateTime)
                scope.launch {
                    insertNewVideo(vr)
                }
            }else if(result.resultCode == RESULT_CANCELED){
                Log.i("VIDEO_RECORD_TAG", "Video recording is cancelled")
            }else{
                Log.i("VIDEO_RECORD_TAG", "Something bad happened, i guess")
            }
        }

    private fun createVideoFile(timeStamp : String) : File? {

        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES)

        return storageDir?.let {
            File.createTempFile(
                "VIDEO_${timeStamp}_",
                ".mp4",
                it
            ).apply {
                // Save a file path for use with the captured video URI
                videoUri = FileProvider.getUriForFile(
                    (activity as HomeActivity),
                    "es.unex.giiis.asee.totalmergency.fileprovider",
                    this
                )
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        scope
        if(isFrontCameraPresent()){
            Log.i("notice", "Camera is detected")
            getCameraPermission()
        }else {
            if (isBackCameraPresent()) {
                Log.i("notice", "Back camera is detected")
                getCameraPermission()
            } else {
                Log.i("notice", "No camera is detected")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentEmergencyBinding.inflate(inflater, container, false)

        db = TotalEmergencyDatabase.getInstance((activity as HomeActivity).applicationContext)!!

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListeners()
        with(binding){


        }
    }

    fun getPath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor = context?.contentResolver?.query(uri, projection, null, null, null)
        cursor?.use {
            Log.i("Cursor", "Trying to fetch the data")
            if(it.moveToFirst()){
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }

    fun setUpListeners() {

        with(binding){
            //emergencyButton.onKeyLongPress(3)
            emergency.setOnClickListener {
                if (isFrontCameraPresent()) {
                    Log.i("suceso", "hay acceso a la cámara")
                    //TODO: Añadir -> Hora, minutos y segundos al momento que fue grabado.  EL TIEMPO DEBE SER ANTES DE GRABAR
                    val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                    responseCamera.launch(intent)
                    //startActivityForResult(intent, 101)
                } else {
                    Log.e("error", "No hay acceso a la cámara")
                }
            }
        }
    }

    private fun isBackCameraPresent(): Boolean {
        return context?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_EXTERNAL)?: false
    }
    fun isFrontCameraPresent() : Boolean {
        return context?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)?: false
    }

    fun getCameraPermission() {
        Log.i("Permission request", "Permission request called")
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED
        ) {
            Log.i("Permission request", "Permission request successfully called")
            ActivityCompat.requestPermissions(activity as HomeActivity, arrayOf(Manifest.permission.CAMERA), 100)
        }

    }

    private suspend fun insertNewVideo(vr: VideoRecord){
        db.videoDAO().insert(vr)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EmergencyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EmergencyFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}