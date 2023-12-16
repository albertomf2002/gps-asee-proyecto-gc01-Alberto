package es.unex.giiis.asee.totalmergency.view.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import es.unex.giiis.asee.totalemergency.view.home.HomeViewModel
import es.unex.giiis.asee.totalmergency.data.database.TotalEmergencyDatabase
import es.unex.giiis.asee.totalmergency.data.model.User
import es.unex.giiis.asee.totalmergency.databinding.FragmentProfileUpdaterBinding
import es.unex.giiis.asee.totalmergency.view.LoginActivity
import kotlinx.coroutines.launch


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileUpdaterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileUpdaterFragment : Fragment() {

    private val homeViewModel: HomeViewModel by activityViewModels()

    private lateinit var user: User

    private var _binding : FragmentProfileUpdaterBinding? = null
    private val binding get() = _binding!!

    private lateinit var db : TotalEmergencyDatabase

    private var popping : Boolean = false


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
        _binding = FragmentProfileUpdaterBinding.inflate(inflater, container, false)

        homeViewModel.user.observe(viewLifecycleOwner) { us ->
            user = us
        }

        db = TotalEmergencyDatabase.getInstance((activity as HomeActivity).applicationContext)!!

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            popup.visibility = View.GONE
        }
        setUpListener()
    }


    private fun setUpListener(){

        with(binding){

            borrar.setOnClickListener {
                popping = !popping
                popup.visibility = if(popping) View.VISIBLE else View.GONE
                Log.i("PRE-ELIMINAR", "El valor es: ${popping}")
            }
            buttonDelete.setOnClickListener {

                //TODO: Borrado en cascada

            }
            modificar.setOnClickListener {
                editUsername.text.clear()
                editName.text.clear()
                editLastname.text.clear()
                editEmail.text.clear()
            }


            buttonDelete.setOnClickListener {
                Log.i("ELIMINAR", "Borrando usuario y su informacion")
                lifecycleScope.launch{
                    val cod = user.cod!!
                    db.userDao().deleteByCod(cod)

                    db.videoDAO().deleteFromUserId(cod) //TODO: Clear videos from memory

                    db.contactDAO().deleteFromUserId(cod)
                    startActivity(Intent(context, LoginActivity::class.java))
                }

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
         * @return A new instance of fragment ProfileUpdaterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileUpdaterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}