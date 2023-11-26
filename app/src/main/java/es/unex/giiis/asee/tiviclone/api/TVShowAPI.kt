package es.unex.giiis.asee.tiviclone.api

import es.unex.giiis.asee.tiviclone.data.api.TvShow
import es.unex.giiis.asee.tiviclone.data.api.TvShowDetail
import es.unex.giiis.asee.tiviclone.data.api.TvShowPage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


/*
private val service: TVShowAPI by lazy {
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor())
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://opendata.caceres.es/sparql/?default-graph-uri=&query=SELECT+%3Furi+%3Fgeo_long++%3Fgeo_lat+%3Fcategoria+%3Frdfs_label+%3FtieneEnlaceSIG+%3Fschema_url++WHERE+%7B+%0D%0A%3Furi+a+cts%3Acentros.+%0D%0AOPTIONAL++%7B%3Furi+geo%3Along+%3Fgeo_long.+%7D%0D%0AOPTIONAL++%7B%3Furi+geo%3Alat+%3Fgeo_lat.+%7D%0D%0AOPTIONAL++%7B%3Furi+cts%3Acategoria+%3Fcategoria.+%7D%0D%0AOPTIONAL++%7B%3Furi+cts%3Anombre+%3Frdfs_label.+%7D%0D%0AOPTIONAL++%7B%3Furi+cts%3AurlSig+%3FtieneEnlaceSIG.+%7D%0D%0AOPTIONAL++%7B%3Furi+cts%3AurlWeb+%3Fschema_url.+%7D.%0D%0A%7D&format=application%2Fsparql-results%2Bjson&timeout=0&debug=on")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //retrofit.create(TVShowAPI::class.java)
}
*/


//fun getNetworkService() = service

interface TVShowAPI {

    @GET("most-popular")
    fun getShows(
        @Query("page") page: Int
    ): Call<TvShowPage>

    @GET("show-details")
    fun getShowDetail(
        @Query("q") id: Int
    ): Call<TvShowDetail>
}

//class APIError(message: String, cause: Throwable?) : Throwable(message, cause)

//interface APICallback {
//    fun onCompleted(tvShows:List<TvShow?>)
//    fun onError(cause: Throwable)
//}