package es.unex.giiis.asee.tiviclone.view.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import es.unex.giiis.asee.tiviclone.R
import es.unex.giiis.asee.tiviclone.data.database.TotalEmergencyDatabase
import es.unex.giiis.asee.tiviclone.data.model.VideoRecord
import es.unex.giiis.asee.tiviclone.databinding.FragmentRecordRegistryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.RuntimeException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecordRegistryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecordRegistryFragment : Fragment() {

    private lateinit var listener: OnShowClickListener

    interface OnShowClickListener{
        fun onShowClick(video: VideoRecord)
    }


    private lateinit var db: TotalEmergencyDatabase

    private var _binding: FragmentRecordRegistryBinding? = null
    private val binding get() = _binding!!

    private var videos: List<VideoRecord>? = null

    private lateinit var adapter: RecordRegistryAdapter

    val scope = CoroutineScope(Job() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onAttach(context: android.content.Context) {
        super.onAttach(context)
        if(context is OnShowClickListener){
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnShowClickListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecordRegistryBinding.inflate(inflater, container, false)

        db = TotalEmergencyDatabase.getInstance((activity as HomeActivity).applicationContext)!!

        //Log.i("TOTAL_VIDEOS", "videos size: " + videos!!.size)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        GlobalScope.launch(Dispatchers.Main) {
            Log.i("AAA", "Before updating data")
            videos = db.videoDAO().getAllVideos()
            Log.i("AAA", "Size is: " + videos?.size)
            setUpRecyclerView()
        }

    }

    private fun setUpRecyclerView(){
        Log.i("AAA", "DONT CRASH is: " + videos?.size)
        adapter = RecordRegistryAdapter(videos = videos!!,
            onClick = {
                listener.onShowClick(it)
                Toast.makeText(context, "click on:" + it.uri, Toast.LENGTH_SHORT).show()
        },
            onLongClick = {
               Toast.makeText(context, "long click on:" + it.uri, Toast.LENGTH_SHORT).show()
        })

        with(binding){
            videoRegistry.layoutManager = LinearLayoutManager(context)
            videoRegistry.adapter = adapter
        }
        android.util.Log.d("RecordRegistryFragment", "SetUpRecyclerView")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecordRegistryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecordRegistryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}