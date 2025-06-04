package com.example.splitup.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.splitup.datos.DatosPagos;
import com.example.splitup.R;
import com.example.splitup.objetos.Participante;
import com.example.splitup.repositorios.RepositorioParticipante;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdaptadorPagos extends ArrayAdapter<DatosPagos> {

    private ArrayList<DatosPagos> datoPagos;

    public AdaptadorPagos(Context context, ArrayList<DatosPagos> datoPagos){
        super(context, R.layout.vista_lista_pagos, datoPagos);
        this.datoPagos = datoPagos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater mostrado = LayoutInflater.from(getContext());
        View elemento = mostrado.inflate(R.layout.vista_lista_pagos, parent, false);

        TextView nombre = elemento.findViewById(R.id.txtNombre);
        TextView pagadoPor = elemento.findViewById(R.id.txtPagadoPor);
        TextView gasto = elemento.findViewById(R.id.txtGasto);

        RepositorioParticipante repositorioParticipante = new RepositorioParticipante();
        repositorioParticipante.obtenerParticipante(datoPagos.get(position).getPagadoPor(), new Callback<Participante>() {
            @Override
            public void onResponse(Call<Participante> call, Response<Participante> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Participante participante = response.body();
                    pagadoPor.setText("Pagado por: " + participante.getNombre());
                } else {
                    Toast.makeText(getContext(), "Error en participante: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Participante> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        nombre.setText(datoPagos.get(position).getNombre());
        gasto.setText(datoPagos.get(position).getImporte() + "€");

        return elemento;
    }
}
