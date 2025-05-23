package com.example.splitup.repositorios;

import com.example.splitup.api.ApiClient;
import com.example.splitup.api.ApiService;
import com.example.splitup.objetos.UsuarioParticipante;

import retrofit2.Call;
import retrofit2.Callback;

public class RepositorioUsuarioParticipante {
    private ApiService apiService;

    public RepositorioUsuarioParticipante(ApiService apiService) {
        this.apiService = ApiClient.getRetrofitClient().create(ApiService.class);
    }

    public void crearRelacion(UsuarioParticipante relacion, Callback<UsuarioParticipante> callback) {
        Call<UsuarioParticipante> call = apiService.crearRelacionUsuarioParticipante(relacion);
        call.enqueue(callback);
    }

}
