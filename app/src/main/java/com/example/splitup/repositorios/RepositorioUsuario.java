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

    public void obtenerUsuarioPorCorreo(String correo, Callback<Usuario> callback) {
        Call<Usuario> call = apiService.obtenerUsuarioPorCorreo(correo);
        call.enqueue(callback);
    }

    public void obtenerUsuarioPorId(int id, Callback<Usuario> callback) {
        Call<Usuario> call = apiService.obtenerUsuarioPorId(id);
        call.enqueue(callback);
    }

    public void obtenerTodosUsuarios(Callback<List<Usuario>> callback) {
        Call<List<Usuario>> call = apiService.obtenerUsuarios();
        call.enqueue(callback);
    }

    public void actualizarUsuario(int id, Usuario usuarioObjeto, Callback<Void> callback) {
        Call<Void> call = apiService.actualizarUsuario(id, usuarioObjeto);
        call.enqueue(callback);
    }

    public void eliminarUsuario(int id, Callback<Void> callback) {
        Call<Void> call = apiService.eliminarUsuario(id);
        call.enqueue(callback);
    }

}
