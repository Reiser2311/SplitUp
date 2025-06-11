package com.example.splitup.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.splitup.Transacciones;
import com.example.splitup.datos.DatosTransacciones;
import com.example.splitup.R;

import java.util.ArrayList;

public class AdaptadorTransacciones extends ArrayAdapter<DatosTransacciones> {

    private ArrayList<DatosTransacciones> datosTransacciones;

    public AdaptadorTransacciones(Context context, ArrayList<DatosTransacciones> datosTransacciones){
        super(context, R.layout.vista_lista_transacciones, datosTransacciones);
        this.datosTransacciones = datosTransacciones;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater mostrado = LayoutInflater.from(getContext());
        View elemento = mostrado.inflate(R.layout.vista_lista_transacciones, parent, false);

        TextView textoDeudorTransaccion = elemento.findViewById(R.id.txtDeudorTransaccion);
        TextView textoTransaccion = elemento.findViewById(R.id.txtTextoTransaccion);
        TextView textoAcreedorTransaccion = elemento.findViewById(R.id.txtAcreedorTransaccion);
        TextView importeTransaccion = elemento.findViewById(R.id.txtImporteTransaccion);
        Button btnEstaPagado = elemento.findViewById(R.id.btnEstaPagado);

        btnEstaPagado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatosTransacciones transaccion = datosTransacciones.get(position);

                if (getContext() instanceof Transacciones) {
                    ((Transacciones) getContext()).aplicarTransaccionPagada(transaccion);
                }
            }
        });

        textoDeudorTransaccion.setText(datosTransacciones.get(position).getTextoDeudorTransaccion());
        textoTransaccion.setText(datosTransacciones.get(position).getTextoTransaccion());
        textoAcreedorTransaccion.setText(datosTransacciones.get(position).getTextoAcreedorTransaccion());
        importeTransaccion.setText(datosTransacciones.get(position).getImporteTransaccion() + "€");

        return elemento;
    }
}
