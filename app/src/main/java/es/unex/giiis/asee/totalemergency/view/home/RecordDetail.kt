package es.unex.giiis.asee.totalmergency.view.home

import android.app.Activity
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
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
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

    private var _binding: FragmentRecordDetailBinding? = null
    private val binding get() = _binding!!

    private var video: VideoRecord? = null

    //private var uri: Uri? = null
    private var path: String? = null
    private lateinit var fdelete: File

    private val args: RecordDetailArgs by navArgs()

    private val responseDocument =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK) {

                MediaScannerConnection.scanFile((activity as HomeActivity).applicationContext,
                    arrayOf<String>(fdelete.getAbsolutePath()),
                    null,
                    OnScanCompletedListener { new_path, uri ->
                        //DocumentFile.fromSingleUri(requireContext().applicationContext,uri)!!.delete();

                        //requireContext().applicationContext.contentResolver.delete(uri, null, null)
                        val contentResolver = requireContext().contentResolver

                        val cursor = contentResolver.query(uri, null, null, null, null)
                        cursor?.use { cursor ->
                            if (cursor.moveToFirst()) {
                                val columnIndex1 =
                                    cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                                val columnIndex2 = cursor.getColumnIndex(OpenableColumns.SIZE)
                                val displayName =
                                    if (columnIndex1 != -1) cursor.getString(columnIndex1) else -1
                                val size =
                                    if (columnIndex2 != -1) cursor.getLong(columnIndex2) else -1
                                // Add more attributes you need here
                                Log.i("FILE_DATA", "Name: ${displayName}")
                                // Perform operations on the document using DocumentFile
                                val documentFile = DocumentFile.fromSingleUri(requireContext(), uri)
                                if (documentFile != null && documentFile.exists()) {
                                    // Perform operations such as read, write, delete, etc., on the document
                                    // Example: val inputStream: InputStream? = contentResolver.openInputStream(uri)
                                    // Example: val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
                                    // Example: documentFile.delete()
                                    if(documentFile.delete()){
                                        Log.i("documentFile", "DELETE")
                                    }else{
                                        Log.i("documentFile", "NOT DELETE")
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }



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

        Log.i("PATH:", "The path data ${path} is checked")

        _binding = FragmentRecordDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Log.i("PATH", "The path is: ${path}")
        //var mediaPlayer = MediaPlayer.create(context, uri)

        with(binding){
            //videoView.setVideoURI(uri)
            val db = TotalEmergencyDatabase.getInstance((activity as HomeActivity).applicationContext)!!

            if(path == null){
                lifecycleScope.launch {
                    db.videoDAO().deleteFromId(video?.videoId!!)
                }
            }else {
                videoView.setVideoPath(path)
                videoView.start()
            }

            eliminarVideo.setOnClickListener {
                videoView.stopPlayback()

                Log.d("INTENT", "REQUEST OPEN DOCUMENT")

                lifecycleScope.launch {
                    db.videoDAO().deleteFromId(video?.videoId!!)
                }

                /*
                MediaScannerConnection.scanFile((activity as HomeActivity).applicationContext,
                    arrayOf<String>(fdelete.getAbsolutePath()),
                    null,
                    OnScanCompletedListener { new_path, uri ->


                    }
                )
                */
                /*
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "video/a*" // Set MIME type here, for example, "image/a*" for images
                responseDocument.launch(intent)
                Log.d("INTENT", "RESPONSE OPEN DOCUMENT")
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

                }
            }
    }
}