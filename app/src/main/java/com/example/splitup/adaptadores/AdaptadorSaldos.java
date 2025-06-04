package com.example.splitup.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.splitup.datos.DatosSaldos;
import com.example.splitup.R;

import java.util.ArrayList;

public class AdaptadorSaldos extends ArrayAdapter<DatosSaldos> {

    private ArrayList<DatosSaldos> datosSaldos;

    public AdaptadorSaldos(Context context, ArrayList<DatosSaldos> datosSaldos) {
        super(context, R.layout.vista_lista_saldos, datosSaldos);
        this.datosSaldos = datosSaldos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater mostrado = LayoutInflater.from(getContext());
        View elemento = mostrado.inflate(R.layout.vista_lista_saldos, parent, false);

        TextView nombre = elemento.findViewById(R.id.txtNombreSaldo);
        TextView saldo = elemento.findViewById(R.id.txtGastoSaldo);

        nombre.setText(datosSaldos.get(position).getNombre());
        saldo.setText(datosSaldos.get(position).getSaldo() + "€");

        if (datosSaldos.get(position).getSaldo() < 0) {
            saldo.setTextColor(getContext().getResources().getColor(R.color.saldo_negativo));
        } else if (datosSaldos.get(position).getSaldo() > 0) {
            saldo.setTextColor(getContext().getResources().getColor(R.color.saldo_positivo));
        } else {
            saldo.setTextColor(getContext().getResources().getColor(R.color.hint));
        }

        return elemento;
    }
}
