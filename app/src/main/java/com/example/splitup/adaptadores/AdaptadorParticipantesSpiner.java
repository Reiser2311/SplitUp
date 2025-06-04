package com.example.splitup.adaptadores;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.splitup.R;
import com.example.splitup.datos.DatosParticipantes;

import java.util.ArrayList;

public class AdaptadorParticipantesSpiner extends ArrayAdapter<DatosParticipantes> {

    private ArrayList<DatosParticipantes> datosParticipantes;

    public AdaptadorParticipantesSpiner(Context context, ArrayList<DatosParticipantes> datosParticipantes) {
        super(context, R.layout.vista_lista_participantes, datosParticipantes);
        this.datosParticipantes = datosParticipantes;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater mostrado = LayoutInflater.from(getContext());
        View elemento = mostrado.inflate(R.layout.vista_lista_participantes, parent, false);

        TextView nombre = elemento.findViewById(R.id.texto_item);

        nombre.setText(datosParticipantes.get(position).getNombre());
        nombre.setTextColor(Color.WHITE);

        return elemento;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater mostrado = LayoutInflater.from(getContext());
        View elemento = mostrado.inflate(R.layout.vista_lista_participantes, parent, false);

        TextView nombre = elemento.findViewById(R.id.texto_item);

        nombre.setText(datosParticipantes.get(position).getNombre());
        nombre.setTextColor(Color.BLACK);

        return elemento;
    }
}
