package com.example.splitup.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.splitup.datos.DatosSplits;
import com.example.splitup.R;

import java.util.ArrayList;

public class AdaptadorSplits extends ArrayAdapter<DatosSplits> {

    private ArrayList<DatosSplits> datosSplits;

    public AdaptadorSplits(Context context, ArrayList<DatosSplits> datosSplits) {
        super(context, R.layout.vista_lista_splits, datosSplits);
        this.datosSplits = datosSplits;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View vista = inflater.inflate(R.layout.vista_lista_splits, parent, false);

        TextView txtNombre = vista.findViewById(R.id.nombreSplits);
        txtNombre.setText(datosSplits.get(position).getNombre());

        return vista;
    }
}
