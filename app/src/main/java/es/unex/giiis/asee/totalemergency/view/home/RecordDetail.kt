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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
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

    private var video: VideoRecord? = null

    private var uri: Uri? = null

    private val args: RecordDetailArgs by navArgs()

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        video = args.VideoUri
        uri = Uri.parse(video?.uri)
        Log.i("URI:", "The uri data ${uri} is checked")
        Log.i("URI:", "The uri authority is: ${uri?.authority}")

        _binding = FragmentRecordDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun justGonnaTry(): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor = context?.contentResolver?.query(uri!!, projection, null, null, null)
        cursor?.use {
            Log.i("Cursor", "Trying to fetch the data")
            if(it.moveToFirst()){
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }
    private fun iterateMediaFiles() {
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED
        )

        val cursor = context?.contentResolver?.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        cursor?.use { cursor ->
            Log.i("CURSOR", "Iteration")
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                val displayName =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
                val data =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                val size =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))
                val dateAdded =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED))

                Log.d(
                    "TRY", "Video - ID: $id, Name: $displayName, Path: $data, Size: $size, Date Added: $dateAdded"
                )
            }
        }
    }
    fun getRealPath(): String?{

        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        if(isKitKat){
            Log.i("KITKAT", "API SDK is higher than KitKat")
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":")
            val type = split[0]
            Log.i("KITKAT", "API SDK is higher than KitKat")

            var contentUri:Uri? = null

            if("video".equals(type)){
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

            val selection = "_id=?"
            val selectionArgs = arrayOf<String>(split[1])

            return getDataColumn(selection, selectionArgs)
        }
        Log.i("TAG", "PASSED KIT KAT")
        return "AAA"
    }

    fun getDataColumn(selection: String, selectionArgs: Array<String>) : String?{

        val column = "_data"
        var cursor : Cursor? = null
        val projection = arrayOf(column)

        Log.i("Cursor", "Trying to open cursor")
        try {
            cursor = context?.contentResolver?.query(uri!!, projection, selection, selectionArgs, null)
            if(cursor != null && cursor.moveToFirst()){
                val column_index = cursor.getColumnIndexOrThrow(column)
                Log.i("Cursor", "The cursor path is: ${cursor.getString(column_index)}")
                return cursor.getString(column_index)
            }
            Log.i("Cursor", "Failed to fetch video")
        } finally {
            if (cursor != null){
                cursor.close()
            }
        }
        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iterateMediaFiles()
        val path = justGonnaTry()
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

                val fdelete = File(path!!)
                try {
                    //fdelete.delete()
                    FileUtils.forceDelete(fdelete)
                    Log.i("DELETE", "Video has been successfully deleted")
                    lifecycleScope.launch {
                        db.videoDAO().deleteFromId(video?.videoId!!)
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