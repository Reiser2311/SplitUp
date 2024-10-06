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

public class AdaptadorSplit extends ArrayAdapter<Datos> {

    private ArrayList<Datos> datos;

    public AdaptadorSplit(Context context, ArrayList<Datos> datos){
        super(context, R.layout.vista_lista_splits, datos);
        this.datos = datos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater mostrado = LayoutInflater.from(getContext());
        View elemento = mostrado.inflate(R.layout.vista_lista_splits, parent, false);

        TextView nombre = elemento.findViewById(R.id.txtNombre);
        TextView pagadoPor = elemento.findViewById(R.id.txtPagadoPor);
        TextView gasto = elemento.findViewById(R.id.txtGasto);

        nombre.setText(datos.get(position).getNombre());
        pagadoPor.setText(datos.get(position).getPagadoPor());
        gasto.setText(datos.get(position).getGasto());

        return elemento;
    }
}
