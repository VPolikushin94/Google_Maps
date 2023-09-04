package com.example.mapstest.domain.usecase

import com.example.mapstest.data.model.MapsResponse
import com.example.mapstest.domain.repository.MapRepository
import retrofit2.Call

class GetPointsUseCase(private val repository: MapRepository) {

    operator fun invoke(position: String, destination: String): Call<MapsResponse> {
        return repository.getPoints(position, destination)
    }
}