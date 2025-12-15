package com.example.notasapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notasapp.R
import com.example.notasapp.model.Nota
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter para exibir lista de notas no RecyclerView
 */
class NotasAdapter(
    private var notas: List<Nota>,
    private val onNotaClick: (Nota) -> Unit,
    private val onMapClick: (Nota) -> Unit,
    private val onDeleteClick: (Nota) -> Unit
) : RecyclerView.Adapter<NotasAdapter.NotaViewHolder>() {

    class NotaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitulo: TextView = view.findViewById(R.id.tvTitulo)
        val tvConteudo: TextView = view.findViewById(R.id.tvConteudo)
        val tvEndereco: TextView = view.findViewById(R.id.tvEndereco)
        val tvData: TextView = view.findViewById(R.id.tvData)
        val btnMap: ImageButton = view.findViewById(R.id.btnMap)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nota, parent, false)
        return NotaViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotaViewHolder, position: Int) {
        val nota = notas[position]

        holder.tvTitulo.text = nota.titulo
        holder.tvConteudo.text = nota.conteudo

        // Exibir endereço se existir
        if (!nota.endereco.isNullOrEmpty()) {
            holder.tvEndereco.visibility = View.VISIBLE
            holder.tvEndereco.text = "📍 ${nota.endereco}"
            holder.btnMap.visibility = View.VISIBLE
        } else {
            holder.tvEndereco.visibility = View.GONE
            holder.btnMap.visibility = View.GONE
        }

        // Formatar data
        nota.dataCriacao?.let {
            holder.tvData.text = formatDate(it)
        }

        // Click handlers
        holder.itemView.setOnClickListener {
            onNotaClick(nota)
        }

        holder.btnMap.setOnClickListener {
            onMapClick(nota)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(nota)
        }
    }

    override fun getItemCount() = notas.size

    fun updateNotas(newNotas: List<Nota>) {
        notas = newNotas
        notifyDataSetChanged()
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }
}