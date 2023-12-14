package es.unex.giiis.asee.totalmergency.view.home

import android.content.Context
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import es.unex.giiis.asee.totalemergency.TotalEmergencyApplication
import es.unex.giiis.asee.totalemergency.data.Repository
import es.unex.giiis.asee.totalemergency.data.model.Localizaciones


import es.unex.giiis.asee.totalmergency.R
import es.unex.giiis.asee.totalmergency.api.APICallback
import es.unex.giiis.asee.totalmergency.api.APIError
import es.unex.giiis.asee.totalmergency.api.UbicationAPI
import es.unex.giiis.asee.totalmergency.data.api.CentrosSalud
import es.unex.giiis.asee.totalmergency.api.getNetworkService
import es.unex.giiis.asee.totalmergency.data.database.TotalEmergencyDatabase
import es.unex.giiis.asee.totalmergency.data.model.Ubication
import es.unex.giiis.asee.totalmergency.databinding.FragmentHomeMenuBinding
import es.unex.giiis.asee.totalmergency.util.BACKGROUND
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeMenuFragment : Fragment() {

    private var _binding: FragmentHomeMenuBinding? = null
    private val binding get() = _binding!!

    private var listadoCentrosSalud: List<Localizaciones>? = null

    private lateinit var repository: Repository

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        val caceres = LatLng(39.4743, -6.3710)
        googleMap.addMarker(MarkerOptions().position(caceres).title("Marker in caceres"))

        if(listadoCentrosSalud!!.isNotEmpty()){
            var incr = 1
            for(loc in listadoCentrosSalud!!){
                val posicion = LatLng(loc.latitude, loc.longitude)
                val nombre = loc.name
                Log.i("MAP", "DATA ${incr} IS: ${nombre}, latitud y longitud: ${posicion}")

                googleMap.addMarker(MarkerOptions().position(posicion).title("Marker in ${nombre}"))
                incr++
            }
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(caceres))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // db = TotalEmergencyDatabase.getInstance((activity as HomeActivity).applicationContext)!!
        //repository = Repository.getInstance(db.localizacionesDao(),getNetworkService())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeMenuBinding.inflate(inflater, container, false)
        listadoCentrosSalud = listOf<Localizaciones>()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("OnViewCreated", "Creating view")



        val appContainer = (this.activity?.application as TotalEmergencyApplication).appContainer
        repository = appContainer.repository


        updateMapUI()
        launchDataLoad { repository.tryUpdateRecentLocationCache() }


        Log.i("OnViewCreated", "EL RESULTADO ES: ${listadoCentrosSalud}")
        Log.i("OnViewCreated", "View finished")
    }

    private fun updateMapUI(){
        repository.localizaciones.observe(viewLifecycleOwner) { localizaciones ->
            listadoCentrosSalud = localizaciones

            Log.d("MAP DATA","The list size is: ${localizaciones.size}")
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

            mapFragment?.getMapAsync(callback)
        }
    }

    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return lifecycleScope.launch {
            try{
                block()
            } catch (error: APIError){
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            } finally {

            }
        }
    }

    /*
    private suspend fun fetchUbications(): List<CentrosSalud> {
        return withContext(Dispatchers.IO) {
            var apiUbication = listOf<CentrosSalud>()
            Log.i("OnViewCreated", "Before fetching data on background")
            val result = try{
                getNetworkService().getAllUbications().execute()
            } catch (cause: Throwable){
                throw APIError("Unable to fetch data from API", cause)
            }

            if(result.isSuccessful){
                apiUbication = result.body()!!
            }else{
                throw APIError("API response error ${result.errorBody()}", null)
            }
            apiUbication
        }
    }
    */
}