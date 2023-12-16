package es.unex.giiis.asee.totalmergency.view.home

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
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
import androidx.fragment.app.activityViewModels
import es.unex.giiis.asee.totalemergency.view.home.HomeViewModel
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import es.unex.giiis.asee.totalemergency.view.home.EmergencyViewModel
import es.unex.giiis.asee.totalmergency.data.database.TotalEmergencyDatabase
import es.unex.giiis.asee.totalmergency.data.model.User
import es.unex.giiis.asee.totalmergency.data.model.VideoRecord
import es.unex.giiis.asee.totalmergency.databinding.FragmentEmergencyBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.io.FilePermission
import java.util.Date
import java.util.Locale


/**
 * A simple [Fragment] subclass.
 * Use the [EmergencyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EmergencyFragment : Fragment() {

    private val homeViewModel: HomeViewModel by activityViewModels()

    private var _binding: FragmentEmergencyBinding? = null

    private lateinit var videoUri : Uri
    private val binding get() = _binding!!

    private val viewModel : EmergencyViewModel by viewModels { EmergencyViewModel.Factory }

    private val responseCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == RESULT_OK){

                //Pass the uri to view model, view model will pass it to Repository.
                //Manage files
                viewModel.retrieveUriData(result.data?.data!!, requireContext())

            }else if(result.resultCode == RESULT_CANCELED){
                Log.i("VIDEO_RECORD_TAG", "Video recording is cancelled")
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
        viewModel.obtainPermission(requireContext(), activity as HomeActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentEmergencyBinding.inflate(inflater, container, false)

        homeViewModel.user.observe(viewLifecycleOwner) { us ->
            viewModel.user = us
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListeners()

    }

    private fun observeCameraResponse(){
        viewModel.cameraResponse.observe(viewLifecycleOwner, Observer { response ->
            Log.d("OBSERVER", "The response is: [${response}]")
        })
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

                viewModel.cameraPermission.observe(viewLifecycleOwner, Observer { response ->
                    if(response){
                        Log.i("Acceso", "Existe acceso a la camara")


                        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                        responseCamera.launch(intent)
                        observeCameraResponse()
                    }else{
                        Log.i("Acceso", "NO Existe acceso a la camara")
                    }
                })
            }
        }
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