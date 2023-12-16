package es.unex.giiis.asee.totalmergency.view.home

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import es.unex.giiis.asee.totalemergency.data.model.Localizaciones
import es.unex.giiis.asee.totalemergency.view.home.HomeMenuViewModel
import es.unex.giiis.asee.totalemergency.view.home.HomeViewModel


import es.unex.giiis.asee.totalmergency.R
import es.unex.giiis.asee.totalmergency.databinding.FragmentHomeMenuBinding

class HomeMenuFragment : Fragment() {

    private var _binding: FragmentHomeMenuBinding? = null
    private val binding get() = _binding!!

    private var listadoCentrosSalud: List<Localizaciones>? = listOf()

    private val viewModel : HomeMenuViewModel by viewModels{ HomeMenuViewModel.Factory }
    private val homeViewModel: HomeViewModel by activityViewModels()

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
        Log.i("Map", "Callback started")
        val caceres = LatLng(39.4743, -6.3710)
        googleMap.addMarker(MarkerOptions().position(caceres).title("Marker in caceres"))

        if(listadoCentrosSalud != null) {
            if (listadoCentrosSalud!!.isNotEmpty()) {
                var incr = 1
                for (loc in listadoCentrosSalud!!) {
                    val posicion = LatLng(loc.latitude, loc.longitude)
                    val nombre = loc.name
                    Log.i("MAP", "DATA ${incr} IS: ${nombre}, latitud y longitud: ${posicion}")

                    googleMap.addMarker(
                        MarkerOptions().position(posicion).title("Marker in ${nombre}")
                    )
                    incr++
                }
            }
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(caceres))
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

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        homeViewModel.user.observe(viewLifecycleOwner) { us ->
            viewModel.user = us
        }

        viewModel.toast.observe(viewLifecycleOwner) { text ->
            text?.let {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                viewModel.onToastShown()
            }
        }
        updateMapUI()

        Log.i("OnViewCreated", "EL RESULTADO ES: ${listadoCentrosSalud}")
        Log.i("OnViewCreated", "View finished")
    }

    private fun updateMapUI(){
        viewModel.localizaciones.observe(viewLifecycleOwner) { localizaciones ->
            listadoCentrosSalud = localizaciones

            Log.d("MAP DATA","The list size is: ${localizaciones.size}")
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

            mapFragment?.getMapAsync(callback)
        }
    }
}