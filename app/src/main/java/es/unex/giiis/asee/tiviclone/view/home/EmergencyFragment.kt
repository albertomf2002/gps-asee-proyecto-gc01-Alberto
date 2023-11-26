package es.unex.giiis.asee.tiviclone.view.home

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import es.unex.giiis.asee.tiviclone.R
import es.unex.giiis.asee.tiviclone.data.database.TotalEmergencyDatabase
import es.unex.giiis.asee.tiviclone.data.database.VideoRecordDAO
import es.unex.giiis.asee.tiviclone.data.model.User
import es.unex.giiis.asee.tiviclone.data.model.VideoRecord
import es.unex.giiis.asee.tiviclone.databinding.FragmentEmergencyBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.DEFAULT_CONCURRENCY
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EmergencyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EmergencyFragment : Fragment() {

    private var _binding: FragmentEmergencyBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: TotalEmergencyDatabase

    val scope = CoroutineScope(Job() + Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

        scope
        if(isFrontCameraPresent()){
            Log.i("notice", "Camera is detected")
            getCameraPermission()
        }else{
            Log.i("notice", "No camera is detected")
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

    fun setUpListeners() {

        with(binding){
            //emergencyButton.onKeyLongPress(3)
            emergencyButton.setOnClickListener {
                if(isFrontCameraPresent()) {
                    Log.i("suceso", "hay acceso a la cámara")
                    val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                    startActivityForResult(intent, 101)
                }else{
                    Log.e("error", "No hay acceso a la cámara")
                }
            }

            watchRegistry.setOnClickListener {
                Log.i("Emergency Fragment", "Navigating to recordRegistry RecyclerView")
                view?.findNavController()?.navigate(R.id.recordRegistryFragment)
            }


            callButton.setOnClickListener {
                Log.i("Emergency Fragment", "Starting a call with another phone")
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:" + 123456789)
                startActivity(callIntent)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 101){

            if(resultCode == RESULT_OK){
                val videoPath = data?.data
                Log.i("VIDEO_RECORD_TAG", "Video is recorded and available at path: $videoPath")
                val vr = VideoRecord(videoId = null, uri = "$videoPath")
                scope.launch {
                    insertNewVideo(vr)
                }
            }else if(resultCode == RESULT_CANCELED){
                Log.i("VIDEO_RECORD_TAG", "Video recording is cancelled")
            }else{
                Log.i("VIDEO_RECORD_TAG", "Something bad happened, i guess")
            }
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