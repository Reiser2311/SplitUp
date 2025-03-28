package com.example.splitup;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.splitup.objetos.Pago;
import com.example.splitup.repositorios.RepositorioPago;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Pagos extends AppCompatActivity {

    TextView logo;
    Toolbar miToolbar;
    ListView listaPagos;
    ArrayList<String> lista;
    Button nuevoPago;
    RelativeLayout layoutNoHayPagos;
    Boolean ultimoItem = false;

    @Override
    protected void onResume() {
        super.onResume();
        rehacerLista();
    }

    private void rehacerLista() {
        SharedPreferences preferences = getSharedPreferences("SplitActivo", MODE_PRIVATE);
        RepositorioPago repositorioPago = new RepositorioPago();
        int id = preferences.getInt("idSplit", 0);
        if (ultimoItem) {
            listaPagos.setVisibility(View.GONE);
            layoutNoHayPagos.setVisibility(View.VISIBLE);
        }
        repositorioPago.obtenerPagosPorSplit(id, new Callback<List<Pago>>() {
            @Override
            public void onResponse(Call<List<Pago>> call, Response<List<Pago>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pago> pagos = response.body();

                    ArrayList<DatosPagos> datosPagos = new ArrayList<>();
                    for (Pago pago : pagos) {
                        DatosPagos datosPago = new DatosPagos(pago.getTitulo(), pago.getPagadoPor(), pago.getImporte(), pago.getId());
                        datosPagos.add(datosPago);
                    }

                    AdaptadorPagos adaptador = new AdaptadorPagos(Pagos.this, datosPagos);
                    listaPagos.setAdapter(adaptador);
                    listaPagos.setVisibility(View.VISIBLE);
                    layoutNoHayPagos.setVisibility(View.GONE);
                } else if (response.body() != null) {
                    Toast.makeText(Pagos.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<Pago>> call, Throwable t) {
                Toast.makeText(Pagos.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        menu.setHeaderTitle(((DatosPagos) listaPagos.getAdapter().getItem(info.position)).getNombre());
        inflater.inflate(R.menu.menu_eliminar, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int posicion = info.position;

        AdaptadorPagos adaptadorPagos = (AdaptadorPagos) listaPagos.getAdapter();

        DatosPagos datos = adaptadorPagos.getItem(posicion);

        int id = item.getItemId();

        if (id == R.id.borrar) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Pagos.this);
            builder.setTitle("Borrar pago");
            builder.setMessage("¿Estás seguro de que quiere eliminar este pago?");

            builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    RepositorioPago repositorioPago = new RepositorioPago();
                    repositorioPago.eliminarPago(datos.getId(), new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                if (listaPagos.getAdapter().getCount() == 1) {
                                    ultimoItem = true;
                                }
                                rehacerLista();
                                Toast.makeText(Pagos.this, "Pago eliminado", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Pagos.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(Pagos.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagos);

        logo = findViewById(R.id.Logo);
        miToolbar= findViewById(R.id.miToolbar);
        listaPagos = findViewById(R.id.listViewPagos);
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
        registerForContextMenu(listaPagos);

        nuevoPago.setOnClickListener(v -> {
            Intent intent = new Intent(Pagos.this, PagoNuevo.class);
            startActivity(intent);
        });

        listaPagos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences preferences = getSharedPreferences("PagoActivo", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("idPago", ((DatosPagos) parent.getItemAtPosition(position)).getId());
                editor.apply();

                Intent intent = new Intent(Pagos.this, PagoNuevo.class);
                startActivity(intent);
            }
        });

    }
}