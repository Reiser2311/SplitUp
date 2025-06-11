package com.example.splitup.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.splitup.R;
import com.example.splitup.datos.DatosDivision;

import java.util.ArrayList;

public class AdaptadorDivision extends ArrayAdapter<DatosDivision> {
    private ArrayList<DatosDivision> datosDivision;

    public AdaptadorDivision(Context context, ArrayList<DatosDivision> datosDivision) {
        super(context, R.layout.vista_lista_division, datosDivision);
        this.datosDivision = datosDivision;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater mostrado = LayoutInflater.from(getContext());
        View elemento = mostrado.inflate(R.layout.vista_lista_division, parent, false);

        TextView nombre = elemento.findViewById(R.id.nombreDivision);
        CheckBox seleccionado = elemento.findViewById(R.id.seleccionado);

        DatosDivision division = getItem(position);

        if (division != null) {
            nombre.setText(division.getNombre());

            seleccionado.setOnCheckedChangeListener(null);

            seleccionado.setChecked(division.isSeleccionado());

            seleccionado.setOnCheckedChangeListener((buttonView, isChecked) -> {
                division.setSeleccionado(isChecked);
            });
        }


        return elemento;
    }
}
