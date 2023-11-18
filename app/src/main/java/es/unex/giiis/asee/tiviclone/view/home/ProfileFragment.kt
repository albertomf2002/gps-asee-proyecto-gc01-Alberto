package es.unex.giiis.asee.tiviclone.view.home

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import es.unex.giiis.asee.tiviclone.data.model.User
import es.unex.giiis.asee.tiviclone.databinding.FragmentProfileBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

private const val TAG = "ProfileFragment"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val args: ProfileFragmentArgs by navArgs()

    private var user:User = User()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val parentActivity = (activity as HomeActivity)
        user = parentActivity.getUser()

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        with(binding){
            UsernameText.text = "AAA: ${user.name} BBB"
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