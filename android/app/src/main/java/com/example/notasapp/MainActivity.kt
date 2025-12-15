package com.example.notasapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notasapp.adapter.NotasAdapter
import com.example.notasapp.api.RetrofitClient
import com.example.notasapp.model.Nota
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotasAdapter
    private lateinit var fab: FloatingActionButton
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViews()
        setupRecyclerView()
        setupFab()
    }

    override fun onResume() {
        super.onResume()
        loadNotas()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerView)
        fab = findViewById(R.id.fab)
        progressBar = findViewById(R.id.progressBar)
        tvEmpty = findViewById(R.id.tvEmpty)
    }

    private fun setupRecyclerView() {
        adapter = NotasAdapter(
            notas = emptyList(),
            onNotaClick = { nota -> editNota(nota) },
            onMapClick = { nota -> showMap(nota) },
            onDeleteClick = { nota -> confirmDelete(nota) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupFab() {
        fab.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadNotas() {
        progressBar.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getNotas()

                if (response.isSuccessful) {
                    val notasResponse = response.body()
                    val notas = notasResponse?.notas ?: emptyList()

                    if (notas.isEmpty()) {
                        tvEmpty.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        tvEmpty.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        adapter.updateNotas(notas)
                    }
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Erro ao carregar notas",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Erro: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun editNota(nota: Nota) {
        val intent = Intent(this, AddNoteActivity::class.java).apply {
            putExtra("nota_id", nota.id)
            putExtra("nota_titulo", nota.titulo)
            putExtra("nota_conteudo", nota.conteudo)
            putExtra("nota_latitude", nota.latitude ?: 0.0)
            putExtra("nota_longitude", nota.longitude ?: 0.0)
            putExtra("nota_endereco", nota.endereco)
        }
        startActivity(intent)
    }

    private fun showMap(nota: Nota) {
        if (nota.latitude != null && nota.longitude != null) {
            val intent = Intent(this, MapActivity::class.java).apply {
                putExtra("nota_titulo", nota.titulo)
                putExtra("nota_latitude", nota.latitude)
                putExtra("nota_longitude", nota.longitude)
                putExtra("nota_endereco", nota.endereco)
            }
            startActivity(intent)
        }
    }

    private fun confirmDelete(nota: Nota) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Nota")
            .setMessage("Tem certeza que deseja eliminar esta nota?")
            .setPositiveButton("Sim") { _, _ -> deleteNota(nota) }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun deleteNota(nota: Nota) {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.deleteNota(nota.id!!)

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@MainActivity,
                        "Nota eliminada!",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadNotas()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Erro ao eliminar nota",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Erro: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
}