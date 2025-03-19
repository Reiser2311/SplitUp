package com.example.splitup.repositorios;

import com.example.splitup.api.ApiClient;
import com.example.splitup.api.ApiService;
import com.example.splitup.objetos.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class RepositorioUsuario {
    private ApiService apiService;

    public RepositorioUsuario() {
        this.apiService = ApiClient.getRetrofitClient().create(ApiService.class);
    }

    public void crearUsuario(Usuario usuarioObjeto, Callback<Usuario> callback) {
        Call<Usuario> call = apiService.crearUsuario(usuarioObjeto);
        call.enqueue(callback);
    }

    public void obtenerUsuario(String correo, Callback<Usuario> callback) {
        Call<Usuario> call = apiService.obtenerUsuario(correo);
        call.enqueue(callback);
    }

    public void obtenerTodosUsuarios(Callback<List<Usuario>> callback) {
        Call<List<Usuario>> call = apiService.obtenerUsuarios();
        call.enqueue(callback);
    }

    public void actualizarUsuario(String correo, String nombre, String contrasenya, Callback<Void> callback) {
        Call<Void> call = apiService.actualizarUsuario(correo, nombre, contrasenya);
        call.enqueue(callback);
    }

    public void eliminarUsuario(String correo, Callback<Void> callback) {
        Call<Void> call = apiService.eliminarUsuario(correo);
        call.enqueue(callback);
    }

}
