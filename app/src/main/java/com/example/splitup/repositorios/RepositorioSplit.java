package com.example.splitup.repositorios;

import android.util.Log;

import com.example.splitup.api.ApiClient;
import com.example.splitup.api.ApiService;
import com.example.splitup.objetos.ObjetoSplit;
import com.example.splitup.objetos.ObjetoUsuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepositorioSplit {
    private ApiService apiService;

    public RepositorioSplit() {
        this.apiService = ApiClient.getRetrofitClient().create(ApiService.class);
    }

    public void crearSplit(ObjetoSplit objetoSplit, Callback<ObjetoSplit> callback) {
        Call<ObjetoSplit> call = apiService.crearSplit(objetoSplit);
        call.enqueue(callback);
    }

    public void obtenerSplit(int id, Callback<ObjetoSplit> callback) {
        Call<ObjetoSplit> call = apiService.obtenerSplit(id);
        call.enqueue(callback);
    }

    public void obtenerTodosSplits(Callback<List<ObjetoSplit>> callback) {
        Call<List<ObjetoSplit>> call = apiService.obtenerSplits();
        call.enqueue(callback);
    }

    public void obtenerSplitsPorUsuario(String correo, Callback<List<ObjetoSplit>> callback) {
        Call<List<ObjetoSplit>> call = apiService.obtenerSplitsPorUsuario(correo);
        call.enqueue(callback);
    }

    public void actualizarSplit(int id, ObjetoSplit split, Callback<ObjetoSplit> callback) {
        Call<ObjetoSplit> call = apiService.actualizarSplit(id, split);
        call.enqueue(callback);
    }

    public void eliminarSplit(int id, Callback<Void> callback) {
        Call<Void> call = apiService.eliminarSplit(id);
        call.enqueue(callback);
    }

}
