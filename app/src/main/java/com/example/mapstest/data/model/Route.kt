package com.example.mapstest.data.model

import com.google.gson.annotations.SerializedName

data class Route(

    @SerializedName("overview_polyline")
    val polyline: OverviewPolyline
)
