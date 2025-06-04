package com.example.splitup.repositorios;

import com.example.splitup.api.ApiClient;
import com.example.splitup.api.ApiService;
import com.example.splitup.objetos.Pago;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class RepositorioPago {
    private ApiService apiService;

    public RepositorioPago() {
        this.apiService = ApiClient.getRetrofitClient().create(ApiService.class);
    }

    public void crearPago(Pago pagoObjeto, Callback<Pago> callback) {
        Call<Pago> call = apiService.crearPago(pagoObjeto);
        call.enqueue(callback);
    }

    public void obtenerPago(int id, Callback<Pago> callback) {
        Call<Pago> call = apiService.obtenerPago(id);
        call.enqueue(callback);
    }

    public void obtenerTodosPagos(Callback<List<Pago>> callback) {
        Call<List<Pago>> call = apiService.obtenerPagos();
        call.enqueue(callback);
    }

    public void obtenerPagosPorSplit(int id, Callback<List<Pago>> callback) {
        Call<List<Pago>> call = apiService.obtenerPagosPorSplit(id);
        call.enqueue(callback);
    }

    public void actualizarPago(int id, String titulo, Double importe, int pagadoPor, Callback<Void> callback) {
        Call<Void> call = apiService.actualizarPago(id, titulo, importe, pagadoPor);
        call.enqueue(callback);
    }

    public void eliminarPago(int id, Callback<Void> callback) {
        Call<Void> call = apiService.eliminarPago(id);
        call.enqueue(callback);
    }

}
