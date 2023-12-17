package es.unex.giiis.asee.totalmergency.view.home

import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import es.unex.giiis.asee.totalemergency.view.home.HomeViewModel
import es.unex.giiis.asee.totalemergency.view.home.RecordRegistryViewModel
import es.unex.giiis.asee.totalmergency.R
import es.unex.giiis.asee.totalmergency.data.database.TotalEmergencyDatabase
import es.unex.giiis.asee.totalmergency.data.model.User
import es.unex.giiis.asee.totalmergency.data.model.VideoRecord
import es.unex.giiis.asee.totalmergency.databinding.FragmentRecordRegistryBinding
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

    private val homeViewModel: HomeViewModel by activityViewModels()

    private val viewModel : RecordRegistryViewModel by viewModels { RecordRegistryViewModel.Factory }

    private var _binding: FragmentRecordRegistryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RecordRegistryAdapter

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
        _binding = FragmentRecordRegistryBinding.inflate(inflater, container, false)

        viewModel.obtainStoragePermission(requireContext(), requireActivity())

        homeViewModel.user.observe(viewLifecycleOwner) { us ->
            viewModel.user = us
            viewModel.obtainVideos()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.videos.observe(viewLifecycleOwner){ it ->
            setUpRecyclerView()

            if(it == null || it.isEmpty()){
                //Error checking
                binding.nodata.visibility = View.VISIBLE
                binding.nodataaux.visibility = View.VISIBLE
            }else{
                binding.nodata.visibility = View.GONE
                binding.nodataaux.visibility = View.GONE
            }
        }

    }

    private fun setUpRecyclerView(){
        Log.i("AAA", "DONT CRASH is: " + viewModel.videos.value?.size)
        adapter = RecordRegistryAdapter(videos = viewModel.videos.value!!,
            onClick = {
                homeViewModel.onShowClick(it)
                Toast.makeText(context, "click on:" + it.path, Toast.LENGTH_SHORT).show()
        },
            onLongClick = {
               Toast.makeText(context, "long click on:" + it.path, Toast.LENGTH_SHORT).show()
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