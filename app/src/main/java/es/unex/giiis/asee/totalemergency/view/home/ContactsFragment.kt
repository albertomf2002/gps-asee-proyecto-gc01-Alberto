package es.unex.giiis.asee.totalmergency.view.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import es.unex.giiis.asee.totalemergency.view.home.HomeViewModel
import es.unex.giiis.asee.totalmergency.R
import es.unex.giiis.asee.totalmergency.data.database.TotalEmergencyDatabase
import es.unex.giiis.asee.totalmergency.data.model.Contact
import es.unex.giiis.asee.totalmergency.data.model.User
import es.unex.giiis.asee.totalmergency.databinding.FragmentContactsBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.RuntimeException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ContactsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactsFragment : Fragment() {

    private lateinit var listener: OnShowClickListener

    private val homeViewModel: HomeViewModel by activityViewModels()
    interface OnShowClickListener{
        fun onShowClickCall(contact: Contact)
        fun onDeleteClickCall(contact: Contact)
    }

    private lateinit var user: User

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ContactsAdapter

    private lateinit var db: TotalEmergencyDatabase

    private var contacts: List<Contact>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onAttach(context: Context) {
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

        _binding = FragmentContactsBinding.inflate(inflater, container, false)

        db = TotalEmergencyDatabase.getInstance((activity as HomeActivity).applicationContext)!!

        homeViewModel.user.observe(viewLifecycleOwner) { us ->
            user = us
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            botonInsertar.setOnClickListener {
                val contacto = Contact(null, insertarTelefono.text.toString().toLong(), nombreContacto.text.toString(), userId = user.cod!!)
                lifecycleScope.launch {

                    Log.i("Contacto", "INSERTANDO NUEVO CONTACTO")
                    db.contactDAO().insert(contacto)
                    insertarTelefono.text = null
                    nombreContacto.text = null

                    contacts = db.contactDAO().getAllContactsFromUser(user.cod!!)

                    setUpRecyclerView()

                }
            }
        }

        GlobalScope.launch {
            contacts = db.contactDAO().getAllContactsFromUser(user.cod!!)

            setUpRecyclerView()
        }
    }

    private fun setUpRecyclerView() {
        Log.i("AAA", "DONT CRASH is: " + contacts?.size)
        adapter = ContactsAdapter(contacts = contacts!!,
            onClick = {
                listener.onShowClickCall(it)
                Toast.makeText(context, "click on:" + it.contactName, Toast.LENGTH_SHORT).show()
            },
            onLongClick = {
                listener.onDeleteClickCall(it)
                Toast.makeText(context, "long click on:" + it.contactName, Toast.LENGTH_SHORT).show()
            })

        with(binding){
            listadoContactos.layoutManager = LinearLayoutManager(context)
            listadoContactos.adapter = adapter
        }
        android.util.Log.d("ContactsFragment", "SetUpRecyclerView")
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ContactsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ContactsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}