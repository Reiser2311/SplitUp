package com.example.splitup;

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

import com.example.splitup.adaptadores.AdaptadorPagos;
import com.example.splitup.adaptadores.AdaptadorSaldos;
import com.example.splitup.datos.DatosPagos;
import com.example.splitup.datos.DatosParticipantes;
import com.example.splitup.datos.DatosSaldos;
import com.example.splitup.objetos.Pago;
import com.example.splitup.objetos.Participante;
import com.example.splitup.repositorios.RepositorioPago;
import com.example.splitup.repositorios.RepositorioParticipante;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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
    Button btnTransacciones;
    LinearLayout layoutSaldos;
    ArrayList<DatosPagos> datosPagos = new ArrayList<>();
    ArrayList<DatosSaldos> datosSaldos = new ArrayList<>();
    AdaptadorPagos adaptadorPagos;
    AdaptadorSaldos adaptadorSaldos;
    ArrayList<DatosParticipantes> participantes = new ArrayList<>();

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
        int id = preferences.getInt("idSplit", 0);
        RepositorioParticipante repositorioParticipante = new RepositorioParticipante();

        repositorioParticipante.obtenerParticipantePorSplit(id, new Callback<List<Participante>>() {
            @Override
            public void onResponse(Call<List<Participante>> call, Response<List<Participante>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Participante> participantes = response.body();
                    for (Participante participante : participantes) {
                        DatosParticipantes datosParticipante = new DatosParticipantes(participante.getId(), participante.getNombre());
                        Pagos.this.participantes.add(datosParticipante);
                    }
                } else {
                    Toast.makeText(Pagos.this, "Error en participantes : " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Participante>> call, Throwable t) {
                Toast.makeText(Pagos.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rehacerLista() {
        SharedPreferences preferences = getSharedPreferences("SplitActivo", MODE_PRIVATE);
        RepositorioPago repositorioPago = new RepositorioPago();
        int id = preferences.getInt("idSplit", 0);
        Log.d("SplitActivo", "idSplit: " + id);
        if (ultimoItem) {
            layoutSiHayPagos.setVisibility(View.GONE);
            layoutNoHayPagos.setVisibility(View.VISIBLE);
        }
        repositorioPago.obtenerPagosPorSplit(id, new Callback<List<Pago>>() {
            @Override
            public void onResponse(Call<List<Pago>> call, Response<List<Pago>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pago> pagos = response.body();
                    if (!pagos.isEmpty()) {
                        actualizarListaPagos(pagos);
                        calcularSaldos(pagos);
                    }
                } else {
                    Toast.makeText(Pagos.this, "Error en pagos: " + response.code(), Toast.LENGTH_SHORT).show();
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
        adaptadorPagos = new AdaptadorPagos(Pagos.this, datosPagos);
        listViewPagos.setAdapter(adaptadorPagos);
        adaptadorPagos.notifyDataSetChanged();

        layoutSiHayPagos.setVisibility(View.VISIBLE);
        layoutNoHayPagos.setVisibility(View.GONE);
    }

    private void calcularSaldos(List<Pago> pagos) {

        HashMap<Integer, Double> pagosRealizados = new HashMap<>();
        double totalGasto = 0.0;

        // Inicializamos con 0 todos los participantes
        for (DatosParticipantes participante : participantes) {
            pagosRealizados.put(participante.getId(), 0.0);
        }

        // Sumamos los pagos por participante (por id)
        for (Pago pago : pagos) {
            int pagadorId = pago.getPagadoPor();
            double cantidad = pago.getImporte();
            pagosRealizados.put(pagadorId, pagosRealizados.getOrDefault(pagadorId, 0.0) + cantidad);
            totalGasto += cantidad;
        }

        double deudaPorPersona = participantes.isEmpty() ? 0.0 : totalGasto / participantes.size();

        datosSaldos.clear();

        for (DatosParticipantes participante : participantes) {
            int id = participante.getId();
            String nombre = participante.getNombre();
            double saldoFinal = pagosRealizados.get(id) - deudaPorPersona;
            saldoFinal = new BigDecimal(saldoFinal).setScale(2, RoundingMode.HALF_UP).doubleValue();
            datosSaldos.add(new DatosSaldos(id, nombre, saldoFinal));
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

            builder.setPositiveButton("Sí", (dialog, which) -> {
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
            });

            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();

        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences = getSharedPreferences("SplitActivo", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("idSplit");
        editor.apply();
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
        btnTransacciones = findViewById(R.id.btnTransacciones);

        String text = "SplitUp";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#601FCD")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#601FCD")), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
//                editor.putStringSet("participantes", new HashSet<>(Pagos.this.participantes));
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

        btnTransacciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String json = gson.toJson(datosSaldos);
                Intent intent = new Intent(Pagos.this, Transacciones.class);
                SharedPreferences preferences = getSharedPreferences("Saldos", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("saldos", json);
                editor.apply();
                startActivity(intent);
            }
        });

    }
}