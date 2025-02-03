package com.example.splitup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.splitup.objetos.ObjetoPago;
import com.example.splitup.objetos.ObjetoSplit;
import com.example.splitup.repositorios.RepositorioPago;
import com.example.splitup.repositorios.RepositorioSplit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Pagos extends AppCompatActivity {

    TextView logo;
    Toolbar miToolbar;
    ListView miLista;
    ArrayList<String> lista;
    Button nuevoPago;
    RelativeLayout layoutNoHayPagos;

    @Override
    protected void onResume() {
        super.onResume();
        rehacerLista();
    }

    private void rehacerLista() {
        SharedPreferences preferences = getSharedPreferences("SplitActivo", MODE_PRIVATE);
        RepositorioPago repositorioPago = new RepositorioPago();
        int id = preferences.getInt("idSplit", 0);
        repositorioPago.obtenerPagosPorSplit(id, new Callback<List<ObjetoPago>>() {
            @Override
            public void onResponse(Call<List<ObjetoPago>> call, Response<List<ObjetoPago>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ObjetoPago> pagos = response.body();

                    ArrayList<DatosPagos> datosPagos = new ArrayList<>();
                    for (ObjetoPago pago : pagos) {
                        DatosPagos datosPago = new DatosPagos(pago.getTitulo(), pago.getPagadoPor(), pago.getImporte(), pago.getId());
                        datosPagos.add(datosPago);
                    }

                    AdaptadorPagos adaptador = new AdaptadorPagos(Pagos.this, datosPagos);
                    miLista.setAdapter(adaptador);
                    miLista.setVisibility(View.VISIBLE);
                    layoutNoHayPagos.setVisibility(View.GONE);
                } else if (response.body() != null) {
                    Toast.makeText(Pagos.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<ObjetoPago>> call, Throwable t) {
                Toast.makeText(Pagos.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagos);

        logo = findViewById(R.id.Logo);
        miToolbar= findViewById(R.id.miToolbar);
        miLista = findViewById(R.id.listViewPagos);
        lista = new ArrayList<>();
        nuevoPago = findViewById(R.id.botonNuevosPagos);
        layoutNoHayPagos = findViewById(R.id.layoutNoHayPagos);

        String text = "SplitUp";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4E00CC")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4E00CC")), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        logo.setText(spannable);

        setSupportActionBar(miToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        nuevoPago.setOnClickListener(v -> {
            Intent intent = new Intent(Pagos.this, PagoNuevo.class);
            startActivity(intent);
        });

        miLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences preferences = getSharedPreferences("PagoActivo", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
//                editor.putInt("idPago", ((DatosSplits) parent.getItemAtPosition(position)).getId());
                editor.apply();

                Intent intent = new Intent(Pagos.this, PagoNuevo.class);
                startActivity(intent);
            }
        });

    }
}