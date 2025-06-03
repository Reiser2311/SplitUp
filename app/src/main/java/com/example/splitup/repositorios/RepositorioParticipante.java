package com.example.splitup.repositorios;

import com.example.splitup.api.ApiClient;
import com.example.splitup.api.ApiService;
import com.example.splitup.objetos.Participante;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class RepositorioParticipante {
    private ApiService apiService;

    public RepositorioParticipante() {
        this.apiService = ApiClient.getRetrofitClient().create(ApiService.class);
    }

    public void crearParticipante(Participante participante, Callback<Participante> callback) {
        Call<Participante> call = apiService.crearParticipante(participante);
        call.enqueue(callback);
    }

    public void obtenerParticipante(int id, Callback<Participante> callback) {
        Call<Participante> call = apiService.obtenerParticipante(id);
        call.enqueue(callback);
    }

    public void obtenerParticipantePorSplit(int id, Callback<List<Participante>> callback) {
        Call<List<Participante>> call = apiService.obtenerParticipantePorSplit(id);
        call.enqueue(callback);
    }

    public void obtenerTodosParticipantes(Callback<List<Participante>> callback) {
        Call<List<Participante>> call = apiService.obtenerParticipantes();
        call.enqueue(callback);
    }

    public void actualizarParticipante(int id, String nombre, Callback<Void> callback) {
        Call<Void> call = apiService.actualizarParticipante(id, nombre);
        call.enqueue(callback);
    }

    public void eliminarParticipante(int id, Callback<Void> callback) {
        Call<Void> call = apiService.eliminarParticipante(id);
        call.enqueue(callback);
    }

}
