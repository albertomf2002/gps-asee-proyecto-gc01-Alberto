package es.unex.giiis.asee.totalemergency.data

import es.unex.giiis.asee.totalemergency.data.model.Localizaciones
import es.unex.giiis.asee.totalemergency.data.api.CentrosSalud


fun CentrosSalud.toLoc() = Localizaciones(
    localizationId = ("ID:${id}") ?: "",
    longitude = lon ?: 0.0,
    latitude = lat ?: 0.0,
    name = nombre ?: "",
    telephone = telefono?.toLong() ?: 0,
)