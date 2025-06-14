package com.example.splitup.repositorios;

import com.example.splitup.api.ApiClient;
import com.example.splitup.api.ApiService;
import com.example.splitup.objetos.Usuario;
import com.example.splitup.objetos.UsuarioDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class RepositorioUsuario {
    private ApiService apiService;

    public RepositorioUsuario() {
        this.apiService = ApiClient.getRetrofitClient().create(ApiService.class);
    }

    public void crearUsuario(Usuario usuarioObjeto, Callback<UsuarioDTO> callback) {
        Call<UsuarioDTO> call = apiService.crearUsuario(usuarioObjeto);
        call.enqueue(callback);
    }

    public void login(Usuario loginRequest, Callback<UsuarioDTO> callback) {
        Call<UsuarioDTO> call = apiService.login(loginRequest);
        call.enqueue(callback);
    }

    public void obtenerUsuarioPorCorreo(String correo, Callback<UsuarioDTO> callback) {
        Call<UsuarioDTO> call = apiService.obtenerUsuarioPorCorreo(correo);
        call.enqueue(callback);
    }

    public void obtenerUsuarioPorId(int id, Callback<UsuarioDTO> callback) {
        Call<UsuarioDTO> call = apiService.obtenerUsuarioPorId(id);
        call.enqueue(callback);
    }

    public void obtenerUsuariosPorParticipante (int id, Callback<List<Usuario>> callback) {
        Call<List<Usuario>> call = apiService.obtenerUsuarioPorParticipante(id);
        call.enqueue(callback);
    }

    public void obtenerTodosUsuarios(Callback<List<UsuarioDTO>> callback) {
        Call<List<UsuarioDTO>> call = apiService.obtenerUsuarios();
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
