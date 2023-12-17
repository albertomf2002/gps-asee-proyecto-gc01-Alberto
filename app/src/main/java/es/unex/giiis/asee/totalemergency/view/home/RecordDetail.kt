package es.unex.giiis.asee.totalmergency.view.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.OnScanCompletedListener
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import es.unex.giiis.asee.totalemergency.view.home.RecordDetailViewModel
import es.unex.giiis.asee.totalmergency.R
import es.unex.giiis.asee.totalmergency.data.database.TotalEmergencyDatabase
import es.unex.giiis.asee.totalmergency.data.model.VideoRecord
import es.unex.giiis.asee.totalmergency.databinding.FragmentRecordDetailBinding
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 * Use the [RecordDetail.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecordDetail : Fragment() {

    private val viewModel : RecordDetailViewModel by viewModels { RecordDetailViewModel.Factory }

    private var _binding: FragmentRecordDetailBinding? = null
    private val binding get() = _binding!!

    private var video: VideoRecord? = null

    //private var uri: Uri? = null
    private var path: String? = null
    private lateinit var fdelete: File


    private val args: RecordDetailArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        video = args.VideoUri

        path = video?.path
        fdelete = File(path!!)

        viewModel.video = args.VideoUri
        viewModel.path = viewModel.video?.path

        Log.i("PATH", "The path data ${path} is checked")
        Log.i("FILE", "The file path ${fdelete.path} is checked")

        _binding = FragmentRecordDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            //videoView.setVideoURI(uri)
            val db = TotalEmergencyDatabase.getInstance((activity as HomeActivity).applicationContext)!!

            if(viewModel.path == null){
                viewModel.deleteVideo(video)

            }else {
                videoView.setVideoPath(viewModel.path)
                videoView.start()
            }

            eliminarVideo.setOnClickListener {
                videoView.stopPlayback()

                Log.d("INTENT", "REQUEST OPEN DOCUMENT")

                viewModel.deleteFile(fdelete, video)
            }
        }
    }
    private fun getFileUriFromPath(filePath: String): Uri? {
        val file = File(filePath)
        return if (file.exists()) {
            FileProvider.getUriForFile(
                requireContext(),
                "es.unex.giiis.asee.totalmergency.fileprovider",
                file
            )
        } else {
            null
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecordDetail.
         */

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecordDetail().apply {
                arguments = Bundle().apply {

                }
            }
    }
}