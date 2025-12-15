package com.example.notasapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.notasapp.api.RetrofitClient
import com.example.notasapp.model.Nota
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.util.*

/**
 * Activity para adicionar ou editar notas
 */
class AddNoteActivity : AppCompatActivity() {

    private lateinit var etTitulo: EditText
    private lateinit var etConteudo: EditText
    private lateinit var tvLocalizacao: TextView
    private lateinit var btnSalvar: Button
    private lateinit var btnCapturarLocalizacao: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var notaId: Int? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var endereco: String? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        setupViews()
        setupLocationClient()
        loadDataFromIntent()
        requestLocationPermission()
    }

    private fun setupViews() {
        etTitulo = findViewById(R.id.etTitulo)
        etConteudo = findViewById(R.id.etConteudo)
        tvLocalizacao = findViewById(R.id.tvLocalizacao)
        btnSalvar = findViewById(R.id.btnSalvar)
        btnCapturarLocalizacao = findViewById(R.id.btnCapturarLocalizacao)
        progressBar = findViewById(R.id.progressBar)

        btnSalvar.setOnClickListener { saveNota() }
        btnCapturarLocalizacao.setOnClickListener { captureLocation() }

        title = if (notaId == null) "Nova Nota" else "Editar Nota"
    }

    private fun setupLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun loadDataFromIntent() {
        notaId = intent.getIntExtra("nota_id", -1).takeIf { it != -1 }

        if (notaId != null) {
            etTitulo.setText(intent.getStringExtra("nota_titulo"))
            etConteudo.setText(intent.getStringExtra("nota_conteudo"))

            val lat = intent.getDoubleExtra("nota_latitude", 0.0)
            val lng = intent.getDoubleExtra("nota_longitude", 0.0)

            if (lat != 0.0 && lng != 0.0) {
                latitude = lat
                longitude = lng
                endereco = intent.getStringExtra("nota_endereco")
                updateLocationText()
            }
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão concedida!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Permissão negada. Não será possível capturar localização.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun captureLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }

        progressBar.visibility = View.VISIBLE
        btnCapturarLocalizacao.isEnabled = false

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude

                getAddressFromLocation(latitude!!, longitude!!)

                Toast.makeText(
                    this,
                    "Localização capturada!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Não foi possível obter localização",
                    Toast.LENGTH_SHORT
                ).show()
            }

            progressBar.visibility = View.GONE
            btnCapturarLocalizacao.isEnabled = true
        }.addOnFailureListener {
            Toast.makeText(
                this,
                "Erro ao capturar localização: ${it.message}",
                Toast.LENGTH_SHORT
            ).show()
            progressBar.visibility = View.GONE
            btnCapturarLocalizacao.isEnabled = true
        }
    }

    private fun getAddressFromLocation(lat: Double, lng: Double) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                endereco = address.getAddressLine(0) ?: "${address.locality}, ${address.countryName}"
            } else {
                endereco = "Lat: $lat, Lng: $lng"
            }
        } catch (e: Exception) {
            endereco = "Lat: $lat, Lng: $lng"
            e.printStackTrace()
        }

        updateLocationText()
    }

    private fun updateLocationText() {
        if (latitude != null && longitude != null) {
            tvLocalizacao.visibility = View.VISIBLE
            tvLocalizacao.text = "📍 $endereco"
        }
    }

    private fun saveNota() {
        val titulo = etTitulo.text.toString().trim()
        val conteudo = etConteudo.text.toString().trim()

        if (titulo.isEmpty()) {
            etTitulo.error = "Título obrigatório"
            etTitulo.requestFocus()
            return
        }

        if (conteudo.isEmpty()) {
            etConteudo.error = "Conteúdo obrigatório"
            etConteudo.requestFocus()
            return
        }

        val nota = Nota(
            id = notaId,
            titulo = titulo,
            conteudo = conteudo,
            latitude = latitude,
            longitude = longitude,
            endereco = endereco
        )

        progressBar.visibility = View.VISIBLE
        btnSalvar.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = if (notaId == null) {
                    RetrofitClient.apiService.createNota(nota)
                } else {
                    RetrofitClient.apiService.updateNota(notaId!!, nota)
                }

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@AddNoteActivity,
                        if (notaId == null) "Nota criada!" else "Nota atualizada!",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@AddNoteActivity,
                        "Erro ao guardar nota",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@AddNoteActivity,
                    "Erro: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            } finally {
                progressBar.visibility = View.GONE
                btnSalvar.isEnabled = true
            }
        }
    }
}