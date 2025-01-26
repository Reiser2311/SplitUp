package com.example.splitup.repositorios;

import com.example.splitup.api.ApiClient;
import com.example.splitup.api.ApiService;
import com.example.splitup.objetos.ObjetoUsuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class RepositorioUsuario {
    private ApiService apiService;

    public RepositorioUsuario() {
        this.apiService = ApiClient.getRetrofitClient().create(ApiService.class);
    }

    public void crearUsuario(ObjetoUsuario usuarioObjeto, Callback<ObjetoUsuario> callback) {
        Call<ObjetoUsuario> call = apiService.crearUsuario(usuarioObjeto);
        call.enqueue(callback);
    }

    public void obtenerUsuario(int id, Callback<ObjetoUsuario> callback) {
        Call<ObjetoUsuario> call = apiService.obtenerUsuario(id);
        call.enqueue(callback);
    }

    public void obtenerTodosUsuarios(Callback<List<ObjetoUsuario>> callback) {
        Call<List<ObjetoUsuario>> call = apiService.obtenerUsuarios();
        call.enqueue(callback);
    }

    public void actualizarUsuario(int id, ObjetoUsuario usuario, Callback<ObjetoUsuario> callback) {
        Call<ObjetoUsuario> call = apiService.actualizarUsuario(id, usuario);
        call.enqueue(callback);
    }

    public void eliminarUsuario(int id, Callback<Void> callback) {
        Call<Void> call = apiService.eliminarUsuario(id);
        call.enqueue(callback);
    }

}
