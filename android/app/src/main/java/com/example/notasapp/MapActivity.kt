package com.example.notasapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Activity para exibir a localização da nota no mapa
 */
class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var titulo: String = ""
    private var endereco: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        titulo = intent.getStringExtra("nota_titulo") ?: "Nota"
        latitude = intent.getDoubleExtra("nota_latitude", 0.0)
        longitude = intent.getDoubleExtra("nota_longitude", 0.0)
        endereco = intent.getStringExtra("nota_endereco") ?: ""

        title = "Localização: $titulo"

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true

        val position = LatLng(latitude, longitude)

        googleMap.addMarker(
            MarkerOptions()
                .position(position)
                .title(titulo)
                .snippet(endereco)
        )?.showInfoWindow()

        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(position, 15f)
        )
    }
}