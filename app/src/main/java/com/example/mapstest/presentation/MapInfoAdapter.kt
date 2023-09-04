package com.example.mapstest.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.mapstest.R
import com.example.mapstest.domain.model.Place
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MapInfoAdapter(
    private val context: Context
) : GoogleMap.InfoWindowAdapter {

    private lateinit var tvScooterName: TextView
    private lateinit var tvCoord: TextView

    override fun getInfoWindow(p0: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View? {
        val place = marker.tag as Place

        val layout = R.layout.marker_info
        val view = LayoutInflater.from(context).inflate(layout, null)

        setViews(view)

        tvScooterName.text = place.name
        tvCoord.text = context.getString(
            R.string.place_lat_lng,
            place.address.latitude,
            place.address.latitude
        )

        return view
    }

    private fun setViews(view: View) {
        tvScooterName = view.findViewById(R.id.tv_info_scooter_name)
        tvCoord = view.findViewById(R.id.tv_info_coord)
    }

}