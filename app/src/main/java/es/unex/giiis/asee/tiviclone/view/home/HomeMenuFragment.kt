package es.unex.giiis.asee.tiviclone.view.home

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


import es.unex.giiis.asee.tiviclone.R
import es.unex.giiis.asee.tiviclone.api.APICallback
import es.unex.giiis.asee.tiviclone.api.APIError
import es.unex.giiis.asee.tiviclone.data.api.CentrosSalud
import es.unex.giiis.asee.tiviclone.api.getNetworkService
import es.unex.giiis.asee.tiviclone.databinding.FragmentHomeMenuBinding
import es.unex.giiis.asee.tiviclone.util.BACKGROUND

private const val health_care_url = "https://opendata.caceres.es/sparql/?default-graph-uri=&query=SELECT+%3Furi+%3Fgeo_long++%3Fgeo_lat+%3Fcategoria+%3Frdfs_label+%3FtieneEnlaceSIG+%3Fschema_url++WHERE+%7B+%0D%0A%3Furi+a+cts%3Acentros.+%0D%0AOPTIONAL++%7B%3Furi+geo%3Along+%3Fgeo_long.+%7D%0D%0AOPTIONAL++%7B%3Furi+geo%3Alat+%3Fgeo_lat.+%7D%0D%0AOPTIONAL++%7B%3Furi+cts%3Acategoria+%3Fcategoria.+%7D%0D%0AOPTIONAL++%7B%3Furi+cts%3Anombre+%3Frdfs_label.+%7D%0D%0AOPTIONAL++%7B%3Furi+cts%3AurlSig+%3FtieneEnlaceSIG.+%7D%0D%0AOPTIONAL++%7B%3Furi+cts%3AurlWeb+%3Fschema_url.+%7D.%0D%0A%7D&format=application%2Fsparql-results%2Bjson&timeout=0&debug=on"

class HomeMenuFragment : Fragment() {



    private var _binding: FragmentHomeMenuBinding? = null
    private val binding get() = _binding!!

    private var resultado: List<CentrosSalud>? = null



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

        if(resultado != null){

            for(i in 1..10){
                val posicion = LatLng(resultado!![i].lat!!.toDouble(), resultado!![i].lon!!.toDouble())
                val nombre = resultado!![i].nombre
                Log.i("MAP", "DATA ${i} IS: ${nombre}, latitud y longitud: ${posicion}")

                googleMap.addMarker(MarkerOptions().position(posicion).title("Marker in ${nombre}"))
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

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        mapFragment?.getMapAsync(callback)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("OnViewCreated", "Creating view")


        fetchUbications(object : APICallback {
            override fun onCompleted(centrosSalud: List<CentrosSalud>) {

                Log.d("HomeMenuFragment", "ApiCallBack completed")


                if (centrosSalud != null) {
                    Log.d(
                        "RESULTS",
                        "The results are filled with bindings: ${centrosSalud.size}"
                    )
                    resultado = centrosSalud

                    activity?.runOnUiThread {
                        Log.d("HomeMenuFragment", "ApiCallBack completed")
                        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

                        mapFragment?.getMapAsync(callback)
                    }
                } else {
                    Log.d("RESULTS", "The results are not filled")
                }


            }

            override fun onError(cause: Throwable) {
                Log.d("HomeMenuFragment", "Error fetching data")
            }
        })



        Log.i("OnViewCreated", "EL RESULTADO ES: ${resultado}")
        Log.i("OnViewCreated", "View finished")
    }


    private fun fetchUbications(apiCallback: APICallback){
        Log.i("OnViewCreated", "Before fetching data on background")
        BACKGROUND.submit{
            try {
                Log.i("OnViewCreated", "Before catastrophe data on background")
                val result = getNetworkService().getAllUbications().execute()
                Log.i("apiCallBack", "Retrieving data from API")
                if (result.isSuccessful) {
                    apiCallback.onCompleted(result.body()!!)
                } else {
                    apiCallback.onError(APIError("API Response error ${result.errorBody()}", null))
                }
            } catch(cause: Throwable){

                activity?.runOnUiThread {
                    Toast.makeText(context, "Connection errror", Toast.LENGTH_SHORT).show()
                    Log.e("API ERROR", "${cause.message}")
                }


                throw APIError("Unable to fetch data from API", cause)
            }
        }
    }
}