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

public class AdaptadorParticipantes extends ArrayAdapter<DatosParticipantes> {

    private ArrayList<DatosParticipantes> datosParticipantes;

    public AdaptadorParticipantes(Context context, ArrayList<DatosParticipantes> datosParticipantes) {
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

        return elemento;
    }
}
