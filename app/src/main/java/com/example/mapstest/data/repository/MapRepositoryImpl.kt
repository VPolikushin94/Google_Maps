package com.example.mapstest.data.repository

import com.example.mapstest.data.model.MapsResponse
import com.example.mapstest.data.network.RetrofitInstance
import com.example.mapstest.domain.model.Place
import com.example.mapstest.domain.repository.MapRepository
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call

class MapRepositoryImpl: MapRepository {

    private val mockPlaceList = listOf(
        Place(
            0,
            "0",
            20,
            LatLng(55.080650, 38.801548)
        ),
        Place(
            1,
            "1",
            40,
            LatLng(55.080848, 38.804435)
        ),
        Place(
            2,
            "2",
            60,
            LatLng(55.079087, 38.805096)
        ),
        Place(
            3,
            "3",
            90,
            LatLng(55.078629, 38.801563)
        )
    )

    override fun getPlaces(): List<Place> {
        return mockPlaceList
    }

    override fun getPoints(position: String, destination: String): Call<MapsResponse> {
        return RetrofitInstance.api.getRoute(position, destination)
    }


}