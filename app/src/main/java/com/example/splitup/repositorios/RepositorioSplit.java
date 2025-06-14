package com.example.splitup.repositorios;

import com.example.splitup.api.ApiClient;
import com.example.splitup.api.ApiService;
import com.example.splitup.objetos.Split;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class RepositorioSplit {
    private ApiService apiService;

    public RepositorioSplit() {
        this.apiService = ApiClient.getRetrofitClient().create(ApiService.class);
    }

    public void crearSplit(Split split, Callback<Split> callback) {
        Call<Split> call = apiService.crearSplit(split);
        call.enqueue(callback);
    }

    public void obtenerSplit(int id, Callback<Split> callback) {
        Call<Split> call = apiService.obtenerSplit(id);
        call.enqueue(callback);
    }

    public void obtenerTodosSplits(Callback<List<Split>> callback) {
        Call<List<Split>> call = apiService.obtenerSplits();
        call.enqueue(callback);
    }

    public void obtenerSplitsPorUsuario(int id, Callback<List<Split>> callback) {
        Call<List<Split>> call = apiService.obtenerSplitsPorUsuario(id);
        call.enqueue(callback);
    }

    public void actualizarSplit(int id, String titulo, Callback<Void> callback) {
        Call<Void> call = apiService.actualizarSplit(id, titulo);
        call.enqueue(callback);
    }

    public void eliminarSplit(int id, Callback<Void> callback) {
        Call<Void> call = apiService.eliminarSplit(id);
        call.enqueue(callback);
    }

}
