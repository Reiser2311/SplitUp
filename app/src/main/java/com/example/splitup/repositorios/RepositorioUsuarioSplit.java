package com.example.splitup.repositorios;

import com.example.splitup.api.ApiClient;
import com.example.splitup.api.ApiService;
import com.example.splitup.objetos.UsuarioSplit;

import retrofit2.Call;
import retrofit2.Callback;

public class RepositorioUsuarioSplit {
    private ApiService apiService;

    public RepositorioUsuarioSplit() {
        this.apiService = ApiClient.getRetrofitClient().create(ApiService.class);
    }

    public void crearRelacion(UsuarioSplit usuarioSplit, Callback<UsuarioSplit> callback) {
        Call<UsuarioSplit> call = apiService.crearRelacionUsuarioSplit(usuarioSplit);
        call.enqueue(callback);
    }
}
