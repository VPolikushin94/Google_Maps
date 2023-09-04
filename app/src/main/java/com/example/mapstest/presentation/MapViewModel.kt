package com.example.mapstest.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.mapstest.R
import com.example.mapstest.data.model.MapsResponse
import com.example.mapstest.data.repository.MapRepositoryImpl
import com.example.mapstest.domain.model.Place
import com.example.mapstest.domain.usecase.GetPlacesUseCase
import com.example.mapstest.domain.usecase.GetPointsUseCase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException

class MapViewModel : ViewModel() {
    private val repository = MapRepositoryImpl()

    private val getPlacesUseCase = GetPlacesUseCase(repository)
    private val getPointsUseCase = GetPointsUseCase(repository)

    val placeList = getPlacesUseCase()

    fun getChargeLvl(charge: Int): Int {
        return when(charge) {
            in 0..30 -> MapsActivity.DISCHARGED
            in 31..75 -> MapsActivity.CHARGED_HALF
            in 76..100 -> MapsActivity.CHARGED
            else -> throw RuntimeException("Charge level out of bounds")
        }
    }

    fun buildRoute(position: String, destination: String) {
        getPointsUseCase(position, destination).enqueue(object : Callback<MapsResponse>{
            override fun onResponse(call: Call<MapsResponse>, response: Response<MapsResponse>) {

            }

            override fun onFailure(call: Call<MapsResponse>, t: Throwable) {
                Log.d("WAY", t.toString())
            }

        })
    }
}