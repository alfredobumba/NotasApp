package com.example.notasapp.api

import com.example.notasapp.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface que define os endpoints da API REST
 */
interface ApiService {

    /**
     * Criar uma nova nota
     */
    @POST("api/notas")
    suspend fun createNota(@Body nota: Nota): Response<CreateNoteResponse>

    /**
     * Listar todas as notas
     */
    @GET("api/notas")
    suspend fun getNotas(): Response<ListNotasResponse>

    /**
     * Obter uma nota específica por ID
     */
    @GET("api/notas/{id}")
    suspend fun getNota(@Path("id") id: Int): Response<Nota>

    /**
     * Atualizar uma nota existente
     */
    @PUT("api/notas/{id}")
    suspend fun updateNota(
        @Path("id") id: Int,
        @Body nota: Nota
    ): Response<ApiResponse>

    /**
     * Eliminar uma nota
     */
    @DELETE("api/notas/{id}")
    suspend fun deleteNota(@Path("id") id: Int): Response<ApiResponse>

    /**
     * Buscar notas por localização
     */
    @GET("api/notas/localizacao/{endereco}")
    suspend fun getNotasByLocalizacao(
        @Path("endereco") endereco: String
    ): Response<ListNotasResponse>
}