package com.example.splitup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.splitup.objetos.Usuario;
import com.example.splitup.objetos.UsuarioDTO;
import com.example.splitup.objetos.UsuarioSplit;
import com.example.splitup.repositorios.RepositorioPago;
import com.example.splitup.repositorios.RepositorioParticipante;
import com.example.splitup.repositorios.RepositorioParticipantePago;
import com.example.splitup.repositorios.RepositorioUsuario;
import com.example.splitup.repositorios.RepositorioUsuarioSplit;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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

    private TextView logo;
    private Toolbar miToolbar;
    private ListView listViewPagos;
    private Button nuevoPago;
    private RelativeLayout layoutNoHayPagos;
    private boolean ultimoItem = false;
    private ListView listViewSaldos;
    private LinearLayout layoutSiHayPagos;
    private Button btnPagos;
    private Button btnSaldos;
    private Button btnTransacciones;
    private LinearLayout layoutSaldos;
    private ArrayList<DatosPagos> datosPagos = new ArrayList<>();
    private ArrayList<DatosSaldos> datosSaldos = new ArrayList<>();
    private AdaptadorPagos adaptadorPagos;
    private AdaptadorSaldos adaptadorSaldos;
    private ArrayList<DatosParticipantes> participantes = new ArrayList<>();

    private int idSplitActivo;

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
                    Toast.makeText(Pagos.this, "Error en participantes inesperado", Toast.LENGTH_SHORT).show();
                    Log.e("SplitActivo", "Error en participantes: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Participante>> call, Throwable t) {
                Toast.makeText(Pagos.this, "Error de red", Toast.LENGTH_SHORT).show();
                Log.e("SplitActivo", "Error de red al obtener participantes: " + t.getMessage());
            }
        });
    }

    private void rehacerLista() {
        SharedPreferences preferences = getSharedPreferences("SplitActivo", MODE_PRIVATE);
        RepositorioPago repositorioPago = new RepositorioPago();
        idSplitActivo = preferences.getInt("idSplit", 0);
        Log.d("SplitActivo", "idSplit: " + idSplitActivo);
        if (ultimoItem) {
            layoutSiHayPagos.setVisibility(View.GONE);
            layoutNoHayPagos.setVisibility(View.VISIBLE);
        }
        repositorioPago.obtenerPagosPorSplit(idSplitActivo, new Callback<List<Pago>>() {
            @Override
            public void onResponse(Call<List<Pago>> call, Response<List<Pago>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pago> pagos = response.body();
                    if (!pagos.isEmpty()) {
                        actualizarListaPagos(pagos);
                        calcularSaldos(pagos);
                    }
                } else {
                    Toast.makeText(Pagos.this, "Error en pagos inesperado", Toast.LENGTH_SHORT).show();
                    Log.e("SplitActivo", "Error en pagos: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Pago>> call, Throwable t) {
                Toast.makeText(Pagos.this, "Error de red", Toast.LENGTH_SHORT).show();
                Log.e("SplitActivo", "Error de red al obtener pagos: " + t.getMessage());
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

        // Inicializar todos los saldos en 0
        for (DatosParticipantes participante : participantes) {
            pagosRealizados.put(participante.getId(), 0.0);
        }

        RepositorioParticipante repositorio = new RepositorioParticipante();
        int totalPagos = pagos.size();
        final int[] pagosProcesados = {0};

        for (Pago pago : pagos) {
            int pagoId = pago.getId();
            double importe = pago.getImporte();
            int pagadorId = pago.getPagadoPor();

            repositorio.obtenerIdsParticipantesPorPago(pagoId, new Callback<List<Integer>>() {
                @Override
                public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Integer> idsAfectados = response.body();

                        if (idsAfectados.isEmpty()) {
                            pagosProcesados[0]++;
                            if (pagosProcesados[0] == totalPagos) {
                                recalcularSaldosFinales(pagosRealizados);
                            }
                            return;
                        }

                        double importePorPersona = new BigDecimal(importe / idsAfectados.size())
                                .setScale(2, RoundingMode.HALF_UP).doubleValue();

                        for (int id : idsAfectados) {
                            pagosRealizados.put(id, pagosRealizados.getOrDefault(id, 0.0) - importePorPersona);
                        }

                        pagosRealizados.put(pagadorId, pagosRealizados.getOrDefault(pagadorId, 0.0) + importe);
                    }

                    pagosProcesados[0]++;
                    if (pagosProcesados[0] == totalPagos) {
                        recalcularSaldosFinales(pagosRealizados);
                    }
                }

                @Override
                public void onFailure(Call<List<Integer>> call, Throwable t) {
                    Log.e("SALDOS", "Error al obtener participantes del pago " + pagoId + ": " + t.getMessage());
                    pagosProcesados[0]++;
                    if (pagosProcesados[0] == totalPagos) {
                        recalcularSaldosFinales(pagosRealizados);
                    }
                }
            });
        }
    }

    private void recalcularSaldosFinales(HashMap<Integer, Double> pagosRealizados) {
        datosSaldos.clear();

        for (DatosParticipantes participante : participantes) {
            double saldo = pagosRealizados.getOrDefault(participante.getId(), 0.0);

            // Si el saldo está entre -0.10 y 0.10, lo consideramos 0
            if (Math.abs(saldo) < 0.10) {
                saldo = 0.0;
            }

            datosSaldos.add(new DatosSaldos(participante.getId(), participante.getNombre(), saldo));
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
                            Toast.makeText(Pagos.this, "Error inesperado", Toast.LENGTH_SHORT).show();
                            Log.e("SplitActivo", "Error al eliminar el pago: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(Pagos.this, "Error de red", Toast.LENGTH_SHORT).show();
                        Log.e("SplitActivo", "Error de red al eliminar el pago: " + t.getMessage());
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

    private void cambiarColorOverflow() {
        Drawable overflowIcon = miToolbar.getOverflowIcon();
        if (overflowIcon != null) {
            overflowIcon.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.compartir) {
//            Toast.makeText(this, "Funcion aun en desarrollo", Toast.LENGTH_SHORT).show();

            AlertDialog.Builder builder = new AlertDialog.Builder(Pagos.this);
            builder.setTitle("¿Con quién quieres compartir este split?");
            builder.setMessage("Introduce el correo de la persona que quieras invitar");

            final EditText input = new EditText(Pagos.this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("Enviar", null); // Lo asignamos luego
            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();

            input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    input.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            dialog.setOnShowListener(dlg -> {
                Button botonEnviar = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button botonCancelar = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                botonEnviar.setOnClickListener(v -> {
                    String correo = input.getText().toString().trim();

                    if (correo.isEmpty()) {
                        input.setError("El correo no puede estar vacío");
                        return;
                    }

                    String[] dominiosValidos = {
                            "@gmail.com", "@hotmail.com", "@outlook.com", "@yahoo.com", "@live.com",
                            "@icloud.com", "@gmx.com", "@mail.com", "@protonmail.com", "@zoho.com"
                    };

                    boolean esCorreoValido = false;
                    for (String dominio : dominiosValidos) {
                        if (correo.endsWith(dominio)) {
                            esCorreoValido = true;
                            break;
                        }
                    }

                    if (!esCorreoValido) {
                        input.setError("El correo no es válido");
                        return;
                    }

                    RepositorioUsuario repositorioUsuario = new RepositorioUsuario();
                    repositorioUsuario.obtenerUsuarioPorCorreo(correo, new Callback<UsuarioDTO>() {
                        @Override
                        public void onResponse(Call<UsuarioDTO> call, Response<UsuarioDTO> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                UsuarioDTO usuario = response.body();
                                RepositorioUsuarioSplit repositorioUsuarioSplit = new RepositorioUsuarioSplit();
                                UsuarioSplit usuarioSplit = new UsuarioSplit(usuario.getId(), idSplitActivo);

                                repositorioUsuarioSplit.crearRelacion(usuarioSplit, new Callback<UsuarioSplit>() {
                                    @Override
                                    public void onResponse(Call<UsuarioSplit> call, Response<UsuarioSplit> response) {
                                        if (response.isSuccessful()) {
                                            Intent intent = new Intent(Intent.ACTION_SEND);
                                            intent.setType("message/rfc822");
                                            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{correo});
                                            intent.putExtra(Intent.EXTRA_SUBJECT, "Invitación a participar en Split");
                                            intent.putExtra(Intent.EXTRA_TEXT, "Hola " + usuario.getNombre() + ".\nSe te ha añadido en un split como usuario. " +
                                                    "Cuando entres en la app podrás ver el nuevo split en tu listado para " +
                                                    "que puedas interactuar con él");

                                            try {
                                                startActivity(Intent.createChooser(intent, "Enviar correo..."));
                                                dialog.dismiss();
                                            } catch (android.content.ActivityNotFoundException ex) {
                                                Toast.makeText(Pagos.this, "No hay aplicaciones de correo instaladas", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(Pagos.this, "Error inesparado", Toast.LENGTH_SHORT).show();
                                            Log.e("SplitActivo", "Error al crear la relacion: " + response.code());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<UsuarioSplit> call, Throwable t) {
                                        Toast.makeText(Pagos.this, "Error de red", Toast.LENGTH_SHORT).show();
                                        Log.e("SplitActivo", "Error de red al crear la relacion: " + t.getMessage());
                                    }
                                });
                            } else {
                                input.setError("El usuario no está registrado");
                            }
                        }

                        @Override
                        public void onFailure(Call<UsuarioDTO> call, Throwable t) {
                            Toast.makeText(Pagos.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            });

            dialog.show();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_compartir, menu);
        return true;
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
        cambiarColorOverflow();
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

        btnTransacciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("Saldos", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                String json = new Gson().toJson(datosSaldos);

                editor.putString("saldos", json);
                editor.apply();

                Intent intent = new Intent(Pagos.this, Transacciones.class);
                startActivity(intent);
            }
        });

    }
}