package com.example.splitup;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.splitup.objetos.Pago;
import com.example.splitup.objetos.Split;
import com.example.splitup.repositorios.RepositorioPago;
import com.example.splitup.repositorios.RepositorioSplit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Pagos extends AppCompatActivity {

    TextView logo;
    Toolbar miToolbar;
    ListView listViewPagos;
    Button nuevoPago;
    RelativeLayout layoutNoHayPagos;
    boolean ultimoItem = false;
    ListView listViewSaldos;
    LinearLayout layoutSiHayPagos;
    Button btnPagos;
    Button btnSaldos;
    LinearLayout layoutSaldos;
    ArrayList<DatosPagos> datosPagos = new ArrayList<>();
    ArrayList<DatosSaldos> datosSaldos = new ArrayList<>();
    AdaptadorPagos adaptadorPagos;
    AdaptadorSaldos adaptadorSaldos;
    List<String> participantes = new ArrayList<>();
    int idSplitActivo;

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String origen = intent.getStringExtra("origen");

        if ("Splits".equals(origen)) {
            obtenerParticipantes();
            intent.removeExtra("origen");
        }

        rehacerLista();
    }

    private void obtenerParticipantes() {
        SharedPreferences preferences = getSharedPreferences("SplitActivo", MODE_PRIVATE);
        RepositorioSplit repositorioSplit = new RepositorioSplit();
        int id = preferences.getInt("idSplit", 0);

        repositorioSplit.obtenerSplit(id, new Callback<Split>() {
            @Override
            public void onResponse(Call<Split> call, Response<Split> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Split split = response.body();
                    participantes.clear();
                    participantes.addAll(split.getParticipantes());
                } else {
                    Toast.makeText(Pagos.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Split> call, Throwable t) {
                Toast.makeText(Pagos.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rehacerLista() {
        SharedPreferences preferences = getSharedPreferences("SplitActivo", MODE_PRIVATE);
        RepositorioPago repositorioPago = new RepositorioPago();
        int id = preferences.getInt("idSplit", 0);
        if (ultimoItem) {
            layoutSiHayPagos.setVisibility(View.GONE);
            layoutNoHayPagos.setVisibility(View.VISIBLE);
        }
        repositorioPago.obtenerPagosPorSplit(id, new Callback<List<Pago>>() {
            @Override
            public void onResponse(Call<List<Pago>> call, Response<List<Pago>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pago> pagos = response.body();
                    actualizarListaPagos(pagos);
                    calcularSaldos(pagos);
                } else {
                    Toast.makeText(Pagos.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Pago>> call, Throwable t) {
                Toast.makeText(Pagos.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarListaPagos(List<Pago> pagos) {
        datosPagos.clear();
        for (Pago pago : pagos) {
            DatosPagos datosPago = new DatosPagos(pago.getTitulo(), pago.getPagadoPor(), pago.getImporte(), pago.getId());
            datosPagos.add(datosPago);
        }
        adaptadorPagos.notifyDataSetChanged();
        layoutSiHayPagos.setVisibility(View.VISIBLE);
        layoutNoHayPagos.setVisibility(View.GONE);
    }

    private void calcularSaldos(List<Pago> pagos) {

        HashMap<String, Double> pagosRealizados = new HashMap<>();
        double totalGasto = 0.0;

        for (String participante : participantes) {
            pagosRealizados.put(participante, 0.0);
        }

        for (Pago pago : pagos) {
            String pagador = pago.getPagadoPor();
            double cantidad = pago.getImporte();
            pagosRealizados.put(pagador, pagosRealizados.getOrDefault(pagador, 0.0) + cantidad);
            totalGasto += cantidad;
        }

        double deudaPorPersona = participantes.isEmpty() ? 0.00 : totalGasto / participantes.size();

        datosSaldos.clear();
        for (String participante : participantes) {
            double saldoFinal = pagosRealizados.get(participante) - deudaPorPersona;
            saldoFinal = new BigDecimal(saldoFinal).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            datosSaldos.add(new DatosSaldos(participante, saldoFinal));
        }

        if (datosSaldos.isEmpty()) {
            Log.e("ERROR_SALDOS", "¡Error! La lista de saldos está vacía después de calcular.");
        }

        adaptadorSaldos.notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        menu.setHeaderTitle(((DatosPagos) listViewPagos.getAdapter().getItem(info.position)).getNombre());
        inflater.inflate(R.menu.menu_eliminar, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int posicion = info.position;

        AdaptadorPagos adaptadorPagos = (AdaptadorPagos) listViewPagos.getAdapter();

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
                                if (listViewPagos.getAdapter().getCount() == 1) {
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
        listViewPagos = findViewById(R.id.listViewPagos);
        nuevoPago = findViewById(R.id.botonNuevosPagos);
        layoutNoHayPagos = findViewById(R.id.layoutNoHayPagos);
        listViewSaldos = findViewById(R.id.listViewSaldos);
        layoutSiHayPagos = findViewById(R.id.layoutSiHayPagos);
        btnPagos = findViewById(R.id.btnPagos);
        btnSaldos = findViewById(R.id.btnSaldos);
        layoutSaldos = findViewById(R.id.layoutSaldos);
        adaptadorPagos = new AdaptadorPagos(Pagos.this, datosPagos);
        listViewPagos.setAdapter(adaptadorPagos);
        adaptadorSaldos = new AdaptadorSaldos(Pagos.this, datosSaldos);
        listViewSaldos.setAdapter(adaptadorSaldos);

        String text = "SplitUp";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4E00CC")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4E00CC")), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        logo.setText(spannable);

        setSupportActionBar(miToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        registerForContextMenu(listViewPagos);

        nuevoPago.setOnClickListener(v -> {
            Intent intent = new Intent(Pagos.this, PagoNuevo.class);
            startActivity(intent);
        });

        listViewPagos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        btnPagos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutSaldos.setVisibility(View.GONE);
                listViewPagos.setVisibility(View.VISIBLE);
                btnPagos.setBackgroundTintList(ContextCompat.getColorStateList(Pagos.this, R.color.hint));
                btnSaldos.setBackgroundTintList(ContextCompat.getColorStateList(Pagos.this, R.color.boton));
            }
        });

        btnSaldos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listViewPagos.setVisibility(View.GONE);
                layoutSaldos.setVisibility(View.VISIBLE);
                btnSaldos.setBackgroundTintList(ContextCompat.getColorStateList(Pagos.this, R.color.hint));
                btnPagos.setBackgroundTintList(ContextCompat.getColorStateList(Pagos.this, R.color.boton));
            }
        });

    }
}