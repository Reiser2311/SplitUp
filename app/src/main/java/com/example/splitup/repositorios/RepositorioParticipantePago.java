package com.example.splitup.repositorios;

import com.example.splitup.api.ApiClient;
import com.example.splitup.api.ApiService;
import com.example.splitup.objetos.ParticipantePago;

import retrofit2.Call;
import retrofit2.Callback;

public class RepositorioParticipantePago {
    private ApiService apiService;

    public RepositorioParticipantePago() {
        this.apiService = ApiClient.getRetrofitClient().create(ApiService.class);
    }

    public void crearRelacion(ParticipantePago relacion, Callback<ParticipantePago> callback) {
        Call<ParticipantePago> call = apiService.crearRelacionParticipantePago(relacion);
        call.enqueue(callback);
    }

    public void eliminarRelacion(int participanteId, int pagoId, Callback<Void> callback) {
        Call<Void> call = apiService.eliminarRelacionParticipantePago(participanteId, pagoId);
        call.enqueue(callback);
    }

}
