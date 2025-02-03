package com.example.splitup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

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

        nombre.setText(datoPagos.get(position).getNombre());
        pagadoPor.setText("Pagado por: " + datoPagos.get(position).getPagadoPor());
        gasto.setText(datoPagos.get(position).getImporte() + "€");

        return elemento;
    }
}
