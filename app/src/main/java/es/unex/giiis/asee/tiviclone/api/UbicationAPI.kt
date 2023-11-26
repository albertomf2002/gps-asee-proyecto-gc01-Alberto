package es.unex.giiis.asee.tiviclone.api

import es.unex.giiis.asee.tiviclone.data.api.CentrosSalud
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private val service: UbicationAPI by lazy {
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor())
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://datos.vigo.org/data/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    retrofit.create(UbicationAPI::class.java)
}

val call = service.getAllUbications()



fun getNetworkService() = service
interface UbicationAPI{
    @GET("salud/salud-centros.json")
    fun getAllUbications(
    ): Call<List<CentrosSalud>>
}


class APIError(message: String, cause: Throwable?) : Throwable(message, cause)

interface APICallback {
    fun onCompleted(centro: List<CentrosSalud>)
    fun onError(cause: Throwable)
}