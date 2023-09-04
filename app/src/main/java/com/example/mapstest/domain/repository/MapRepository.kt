package com.example.mapstest.domain.repository

import com.example.mapstest.data.model.MapsResponse
import com.example.mapstest.domain.model.Place
import retrofit2.Call

interface MapRepository {
    fun getPlaces(): List<Place>

    fun getPoints(position: String, destination: String): Call<MapsResponse>
}