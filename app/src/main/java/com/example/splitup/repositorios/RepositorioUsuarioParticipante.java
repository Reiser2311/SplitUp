package com.example.splitup.repositorios;

import com.example.splitup.api.ApiClient;
import com.example.splitup.api.ApiService;
import com.example.splitup.objetos.UsuarioParticipante;

import retrofit2.Call;
import retrofit2.Callback;

public class RepositorioUsuarioParticipante {
    private ApiService apiService;

    public RepositorioUsuarioParticipante() {
        this.apiService = ApiClient.getRetrofitClient().create(ApiService.class);
    }

    public void crearRelacion(UsuarioParticipante relacion, Callback<UsuarioParticipante> callback) {
        Call<UsuarioParticipante> call = apiService.crearRelacionUsuarioParticipante(relacion);
        call.enqueue(callback);
    }

    public void existeRelacion(int participanteId, int usuarioId, Callback<Boolean> callback) {
        Call<Boolean> call = apiService.existeRelacionUsuarioParticipante(participanteId, usuarioId);
        call.enqueue(callback);
    }

    public void eliminarRelacion(int participanteId, int usuarioId, Callback<Void> callback) {
        Call<Void> call = apiService.eliminarRelacionUsuarioParticipante(participanteId, usuarioId);
        call.enqueue(callback);
    }

}
