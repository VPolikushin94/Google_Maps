package com.example.mapstest.domain.model

import com.google.android.gms.maps.model.LatLng

data class Place(
    val id: Int,
    val name: String,
    val charge: Int,
    val address: LatLng
)
