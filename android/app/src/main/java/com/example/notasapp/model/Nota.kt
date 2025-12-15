package com.example.notasapp.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de dados para uma Nota
 */
data class Nota(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("titulo")
    val titulo: String,

    @SerializedName("conteudo")
    val conteudo: String,

    @SerializedName("latitude")
    val latitude: Double? = null,

    @SerializedName("longitude")
    val longitude: Double? = null,

    @SerializedName("endereco")
    val endereco: String? = null,

    @SerializedName("data_criacao")
    val dataCriacao: String? = null
)

/**
 * Resposta da API ao criar nota
 */
data class CreateNoteResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("id")
    val id: Int
)

/**
 * Resposta da API ao listar notas
 */
data class ListNotasResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("total")
    val total: Int,

    @SerializedName("notas")
    val notas: List<Nota>
)

/**
 * Resposta genérica da API
 */
data class ApiResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("error")
    val error: String? = null
)