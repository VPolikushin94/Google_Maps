package com.example.mapstest.data.network

import com.example.mapstest.data.model.MapsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface MapsApi {

    @GET("/maps/api/directions/json")
    fun getRoute(
        @Query("origin") position: String,
        @Query("destination") destination: String
    ): Call<MapsResponse>
}