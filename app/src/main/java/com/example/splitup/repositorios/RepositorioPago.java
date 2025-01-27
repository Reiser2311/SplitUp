package com.example.splitup.repositorios;

import com.example.splitup.api.ApiClient;
import com.example.splitup.api.ApiService;
import com.example.splitup.objetos.ObjetoPago;
import com.example.splitup.objetos.ObjetoUsuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class RepositorioPago {
    private ApiService apiService;

    public RepositorioPago() {
        this.apiService = ApiClient.getRetrofitClient().create(ApiService.class);
    }

    public void crearPago(ObjetoPago pagoObjeto, Callback<ObjetoPago> callback) {
        Call<ObjetoPago> call = apiService.crearPago(pagoObjeto);
        call.enqueue(callback);
    }

    public void obtenerPago(int id, Callback<ObjetoPago> callback) {
        Call<ObjetoPago> call = apiService.obtenerPago(id);
        call.enqueue(callback);
    }

    public void obtenerTodosPagos(Callback<List<ObjetoPago>> callback) {
        Call<List<ObjetoPago>> call = apiService.obtenerPagos();
        call.enqueue(callback);
    }

    public void obtenerPagosPorSplit(int id, Callback<List<ObjetoPago>> callback) {
        Call<List<ObjetoPago>> call = apiService.obtenerPagosPorSplit(id);
        call.enqueue(callback);
    }

    public void actualizarPago(int id, ObjetoPago pago, Callback<ObjetoPago> callback) {
        Call<ObjetoPago> call = apiService.actualizarPago(id, pago);
        call.enqueue(callback);
    }

    public void eliminarPago(int id, Callback<Void> callback) {
        Call<Void> call = apiService.eliminarPago(id);
        call.enqueue(callback);
    }

}
