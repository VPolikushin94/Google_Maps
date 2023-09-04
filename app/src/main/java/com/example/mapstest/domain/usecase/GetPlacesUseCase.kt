package com.example.mapstest.domain.usecase

import com.example.mapstest.domain.model.Place
import com.example.mapstest.domain.repository.MapRepository

class GetPlacesUseCase(private val mapRepository: MapRepository) {

    operator fun invoke(): List<Place> {
        return mapRepository.getPlaces()
    }

}