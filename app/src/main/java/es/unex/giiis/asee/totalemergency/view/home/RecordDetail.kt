package es.unex.giiis.asee.totalmergency.view.home

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecordDetail.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecordDetail : Fragment() {

    private var _binding: FragmentRecordDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel : RecordDetailViewModel by viewModels { RecordDetailViewModel.Factory }

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
        viewModel.video = args.VideoUri
        viewModel.path = viewModel.video?.path


        _binding = FragmentRecordDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            //videoView.setVideoURI(uri)
            val db = TotalEmergencyDatabase.getInstance((activity as HomeActivity).applicationContext)!!

            if(viewModel.path == null){
                lifecycleScope.launch {
                    //db.videoDAO().deleteFromId(video?.videoId!!)
                }
            }else {
                videoView.setVideoPath(viewModel.path)
                videoView.start()
            }

            eliminarVideo.setOnClickListener {
                videoView.stopPlayback()

                val fdelete = File(viewModel.path!!)
                try {
                    //fdelete.delete()
                    //FileUtils.forceDelete(fdelete)
                    Log.i("DELETE", "Video has been successfully deleted")
                    lifecycleScope.launch {
                       // db.videoDAO().deleteFromId(video?.videoId!!)
                    }
                } catch (e: Exception){
                    Log.e("DELETE", "ERROR: Video cant be deleted, reason: ${e.message}")
                }

                /*
                lifecycleScope.launch {

                    if (fdelete.exists()) //Should be always true
                    {
                        try {
                            //fdelete.delete()
                            db.videoDAO().deleteFromId(video?.videoId!!)
                        } catch (e: Exception){
                            Log.e("DELETE", "ERROR: Video cant be deleted, reason: ${e.message}")
                        }

                    }else{
                        Log.i("DELETE", "Video path doesn't exists")
                    }
                }
                */
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
         * @return A new instance of fragment RecordDetail.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecordDetail().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}