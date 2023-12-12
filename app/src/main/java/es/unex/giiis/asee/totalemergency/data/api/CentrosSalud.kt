package es.unex.giiis.asee.totalmergency.data.api


import com.google.gson.annotations.SerializedName


data class CentrosSalud (
/*
    @SerializedName("Código"    )      var Código    :    String? = null,
    @SerializedName("Nombre"    )      var Nombre    :    String? = null,
    @SerializedName("Dirección" )      var Dirección :    String? = null,
    @SerializedName("C.P."      )      var cp        :    String? = null,
    @SerializedName("Municipio" )      var Municipio :    String? = null,
    @SerializedName("Pedanía"   )      var Pedanía   :    String? = null,
    @SerializedName("Teléfono"  )      var Teléfono  :    String? = null,
    @SerializedName("Fax"       )      var Fax       :    String? = null,
    @SerializedName("Email"     )      var Email     :    String? = null,
    @SerializedName("URL Real"  )      var URLReal   :    String? = null,
    @SerializedName("URL Corta" )      var URLCorta  :    String? = null,
    @SerializedName("Latitud"   )      var Latitud   :    String? = null,
    @SerializedName("Longitud"  )      var Longitud  :    String? = null,
    @SerializedName("Foto 1"    )      var Foto1     :    String? = null
 */
    @SerializedName("barrio"        ) var barrio       : String? = null,
    @SerializedName("codigo_postal" ) var codigoPostal : String? = null,
    @SerializedName("numero"        ) var numero       : Int?    = null,
    @SerializedName("web"           ) var web          : String? = null,
    @SerializedName("calle"         ) var calle        : String? = null,
    @SerializedName("parroquia"     ) var parroquia    : String? = null,
    @SerializedName("lon"           ) var lon          : Double? = null,
    @SerializedName("id"            ) var id           : Int?    = null,
    @SerializedName("telefono"      ) var telefono     : String? = null,
    @SerializedName("nombre"        ) var nombre       : String? = null,
    @SerializedName("lat"           ) var lat          : Double? = null

)