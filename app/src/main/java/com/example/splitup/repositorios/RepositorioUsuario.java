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

    public void obtenerUsuario(String correo, Callback<ObjetoUsuario> callback) {
        Call<ObjetoUsuario> call = apiService.obtenerUsuario(correo);
        call.enqueue(callback);
    }

    public void obtenerTodosUsuarios(Callback<List<ObjetoUsuario>> callback) {
        Call<List<ObjetoUsuario>> call = apiService.obtenerUsuarios();
        call.enqueue(callback);
    }

    public void actualizarUsuario(String correo, ObjetoUsuario usuario, Callback<ObjetoUsuario> callback) {
        Call<ObjetoUsuario> call = apiService.actualizarUsuario(correo, usuario);
        call.enqueue(callback);
    }

    public void eliminarUsuario(String correo, Callback<Void> callback) {
        Call<Void> call = apiService.eliminarUsuario(correo);
        call.enqueue(callback);
    }

}
