package es.unex.giiis.asee.totalmergency.view.home

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import es.unex.giiis.asee.totalemergency.view.home.HomeViewModel
import es.unex.giiis.asee.totalmergency.R
import es.unex.giiis.asee.totalmergency.data.database.TotalEmergencyDatabase
import es.unex.giiis.asee.totalmergency.data.model.User
import es.unex.giiis.asee.totalmergency.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

private const val TAG = "ProfileFragment"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    private val homeViewModel: HomeViewModel by activityViewModels()

    private lateinit var user: User

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val args: ProfileFragmentArgs by navArgs()

    private lateinit var db : TotalEmergencyDatabase


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeViewModel.user.observe(viewLifecycleOwner) { us ->
            user = us
        }

        db = TotalEmergencyDatabase.getInstance((activity as HomeActivity).applicationContext)!!

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        with(binding){
            UsernameTextShow.text = "${user.userName}"
            NameTextShow.text = "${user.name}"
            LastnameTextShow.text = "${user.lastName}"
            EmailTextShow.text = "${user.email}"

            modificarCuenta.setOnClickListener {
                val controlaor = (activity as HomeActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                controlaor.navController.navigate(R.id.profileUpdaterFragment)
            }

        }
    }

    fun setupUI(user: User?){

    }

    companion object {
        const val USER_INFO = HomeActivity.USER_INFO;
        @JvmStatic
        fun newInstance(param1: User, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    //user_arg = param1
                    //putString(ARG_PARAM2, param2)
                    println("AAAA")
                }
            }
    }
}