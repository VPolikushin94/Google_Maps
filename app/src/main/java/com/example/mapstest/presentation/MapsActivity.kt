package com.example.mapstest.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mapstest.R
import com.example.mapstest.databinding.ActivityMapsBinding
import com.example.mapstest.domain.model.Place
import com.example.mapstest.util.BitmapConverter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.lang.RuntimeException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var viewModel: MapViewModel

    private lateinit var mMap: GoogleMap

    private lateinit var bsInfo: LinearLayout
    private lateinit var bsBehavior: BottomSheetBehavior<LinearLayout>

    private lateinit var tvScooterName: TextView
    private lateinit var ivCharge: ImageView
    private lateinit var tvChargePercents: TextView
    private lateinit var buttonMinutes: RadioButton
    private lateinit var buttonBook: Button

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var userPosition: String
    private lateinit var destination: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MapViewModel::class.java]

        setViews()

        bsBehavior = BottomSheetBehavior.from(bsInfo)
        bsBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        buttonMinutes.isChecked = true

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        buttonBook.setOnClickListener {
            viewModel.buildRoute(userPosition, destination)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setViews() {
        bsInfo = findViewById(R.id.bs_marker_info)
        tvScooterName = findViewById(R.id.tv_bs_scooter_name)
        ivCharge = findViewById(R.id.iv_bs_charge)
        tvChargePercents = findViewById(R.id.tv_bs_charge_percents)
        buttonMinutes = findViewById(R.id.bs_button_minutes)
        buttonBook = findViewById(R.id.button_book)
    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map

        val placeList = viewModel.placeList

        mMap.uiSettings.isZoomControlsEnabled = true

        setMapAdapter(mMap)
        addMarkers(mMap, placeList)
        setMarkerClickListener(mMap)

        setMapClickListener(mMap)

        enableMyLocation(mMap)
        moveCameraToCurrentLocation(mMap)
    }

    private fun setMapAdapter(map: GoogleMap) {
        val mapInfoAdapter = MapInfoAdapter(this)
        map.setInfoWindowAdapter(mapInfoAdapter)
    }

    private fun setMapClickListener(map: GoogleMap) {
        map.setOnMapClickListener {
            bsBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun addMarkers(map: GoogleMap, places: List<Place>) {
        places.forEach { place ->
            val chargeLvl = viewModel.getChargeLvl(place.charge)
            val chargeIconId = when (chargeLvl) {
                DISCHARGED -> R.drawable.ic_discharged
                CHARGED_HALF -> R.drawable.ic_charged_half
                CHARGED -> R.drawable.ic_charged
                else -> throw RuntimeException("Unknown charge level")
            }
            val chargeBitmapIcon = BitmapConverter.vectorToBitmap(this, chargeIconId)
            val marker = map.addMarker(
                MarkerOptions()
                    .title(place.name)
                    .position(place.address)
                    .icon(chargeBitmapIcon)
            )
            marker?.tag = place
        }
    }

    private fun setMarkerClickListener(map: GoogleMap) {
        map.setOnMarkerClickListener { marker ->
            bsBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            val place = marker.tag as Place

            tvScooterName.text = place.name
            tvChargePercents.text = getString(R.string.charge_percents, place.charge)

            val chargeLvl = viewModel.getChargeLvl(place.charge)
            val chargeIconId = when (chargeLvl) {
                DISCHARGED -> R.drawable.ic_discharged_bs
                CHARGED_HALF -> R.drawable.ic_charged_half_bs
                CHARGED -> R.drawable.ic_charged_bs
                else -> throw RuntimeException("Unknown charge level")
            }
            ivCharge.setImageResource(chargeIconId)

            marker.showInfoWindow()

            destination = "${place.address.latitude},${place.address.longitude}"
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(place.address, MARKER_CAMERA_ZOOM),
                CAMERA_ZOOM_DURATION_MS,
                null
            )
            true
        }
    }

    private fun isPermissionGranted() : Boolean {
        return (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation(map: GoogleMap) {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled = true
            moveCameraToCurrentLocation(map)
        }
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun moveCameraToCurrentLocation(map: GoogleMap) {
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                userPosition = "${currentLatLng.latitude},${currentLatLng.longitude}"
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation(mMap)
            }
        }
    }

    companion object {
        const val DISCHARGED = 0
        const val CHARGED_HALF = 1
        const val CHARGED = 2

        private const val CAMERA_ZOOM_DURATION_MS = 800
        private const val MARKER_CAMERA_ZOOM = 18f

        private const val REQUEST_LOCATION_PERMISSION = 1
    }

}