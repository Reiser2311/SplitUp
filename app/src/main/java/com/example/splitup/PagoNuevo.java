package com.example.splitup;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.splitup.adaptadores.AdaptadorParticipantesSpiner;
import com.example.splitup.datos.DatosParticipantes;
import com.example.splitup.objetos.Pago;
import com.example.splitup.objetos.Participante;
import com.example.splitup.objetos.Split;
import com.example.splitup.repositorios.RepositorioPago;
import com.example.splitup.repositorios.RepositorioParticipante;
import com.example.splitup.repositorios.RepositorioSplit;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PagoNuevo extends AppCompatActivity {

    TextView logo;
    Toolbar miToolbar;
    Button buttonCrearPago;
    Button buttonActualizarPago;
    TextInputEditText edtxtNombrePago;
    TextInputEditText edtxtImportePago;
    Spinner pagadoPor;
    TextView txtNuevoPago;
    TextView txtEditarPago;

    ArrayList<DatosParticipantes> participantes;

    AdaptadorParticipantesSpiner adaptadorParticipantes;

    int idPagoActivo;

    int idSplitActivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago_nuevo);

        logo = findViewById(R.id.Logo);
        miToolbar= findViewById(R.id.miToolbar);
        buttonCrearPago = findViewById(R.id.buttonCrearPago);
        edtxtNombrePago = findViewById(R.id.editTextNombrePago);
        buttonActualizarPago = findViewById(R.id.buttonActualizarPago);
        edtxtImportePago = findViewById(R.id.edtxtImportePago);
        pagadoPor = findViewById(R.id.spinnerPagadoPor);
        txtNuevoPago = findViewById(R.id.txtNuevoPago);
        txtEditarPago = findViewById(R.id.txtEditarPago);

        participantes = new ArrayList<>();


        String text = "SplitUp";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#601FCD")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#601FCD")), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        logo.setText(spannable);

        setSupportActionBar(miToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        adaptadorParticipantes = new AdaptadorParticipantesSpiner(PagoNuevo.this, participantes);
        pagadoPor.setAdapter(adaptadorParticipantes);

        buttonCrearPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!edtxtNombrePago.getText().toString().isEmpty() && !edtxtImportePago.getText().toString().isEmpty()) {
                    Pago pago = new Pago();
                    Split split = new Split();
                    SharedPreferences preferences = getSharedPreferences("SplitActivo", MODE_PRIVATE);
                    int id = preferences.getInt("idSplit", 0);
                    split.setId(id);
                    pago.setImporte(Double.parseDouble(edtxtImportePago.getText().toString()));
                    pago.setSplit(split);
                    pago.setPagadoPor(((DatosParticipantes) pagadoPor.getSelectedItem()).getId());
                    pago.setTitulo(edtxtNombrePago.getText().toString());

//                    Log.d("Debug", "Pago a enviar: " + new Gson().toJson(pago));

                    RepositorioPago repositorioPago = new RepositorioPago();
                    repositorioPago.crearPago(pago, new Callback<Pago>() {
                        @Override
                        public void onResponse(Call<Pago> call, Response<Pago> response) {
                            if (response.isSuccessful()) {
                                Pago pagoCreado = response.body();
                                Toast.makeText(PagoNuevo.this, "Pago creado con exito: " + pagoCreado.getTitulo(), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(PagoNuevo.this, "Error al crear el pago: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Pago> call, Throwable t) {
                            Toast.makeText(PagoNuevo.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (edtxtNombrePago.getText().toString().isEmpty()) {
                    edtxtNombrePago.setError("El nombre no puede estar vacio");
                } else {
                    edtxtImportePago.setError("El importe no puede estar vacio");
                }


            }
        });

        buttonActualizarPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RepositorioPago repositorioPago = new RepositorioPago();

                repositorioPago.actualizarPago(idPagoActivo, edtxtNombrePago.getText().toString(), Double.parseDouble(edtxtImportePago.getText().toString()), ((DatosParticipantes) pagadoPor.getSelectedItem()).getId(), new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(PagoNuevo.this, "Pago actualizado correctamente", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(PagoNuevo.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                            try {
                                Log.e("Respuesta", "Codigo: " + response.code() + " - Error: " + response.errorBody().string());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(PagoNuevo.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        SharedPreferences preferences = getSharedPreferences("PagoActivo", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        idPagoActivo = preferences.getInt("idPago", 0);

        editor.remove("idPago");
        editor.apply();
        preferences = getSharedPreferences("SplitActivo", MODE_PRIVATE);
        idSplitActivo = preferences.getInt("idSplit", 0);

        RepositorioSplit repositorioSplit = new RepositorioSplit();
        repositorioSplit.obtenerSplit(idSplitActivo, new Callback<Split>() {
            @Override
            public void onResponse(Call<Split> call, Response<Split> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Split split = response.body();
                } else {
                    Toast.makeText(PagoNuevo.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Split> call, Throwable t) {
                Toast.makeText(PagoNuevo.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RepositorioParticipante repositorioParticipante = new RepositorioParticipante();
        repositorioParticipante.obtenerParticipantePorSplit(idSplitActivo, new Callback<List<Participante>>() {
            @Override
            public void onResponse(Call<List<Participante>> call, Response<List<Participante>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    participantes.clear();
                    List<Participante> participantes = response.body();
                    for (Participante participante : participantes) {
                        DatosParticipantes datosParticipante = new DatosParticipantes(participante.getId(), participante.getNombre());
                        PagoNuevo.this.participantes.add(datosParticipante);
                    }
                    adaptadorParticipantes = new AdaptadorParticipantesSpiner(PagoNuevo.this, PagoNuevo.this.participantes);
                    pagadoPor.setAdapter(adaptadorParticipantes);
                    adaptadorParticipantes.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Participante>> call, Throwable t) {

            }
        });

        if (idPagoActivo != 0) {
            buttonCrearPago.setVisibility(View.GONE);
            buttonActualizarPago.setVisibility(View.VISIBLE);
            txtNuevoPago.setVisibility(View.GONE);
            txtEditarPago.setVisibility(View.VISIBLE);

            RepositorioPago repositorioPago = new RepositorioPago();
            repositorioPago.obtenerPago(idPagoActivo, new Callback<Pago>() {
                @Override
                public void onResponse(Call<Pago> call, Response<Pago> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Pago pago = response.body();
                        edtxtNombrePago.setText(pago.getTitulo());
                        edtxtImportePago.setText(Double.toString(pago.getImporte()));
                        RepositorioParticipante repositorioParticipante = new RepositorioParticipante();
                        repositorioParticipante.obtenerParticipante(pago.getPagadoPor(), new Callback<Participante>() {
                            @Override
                            public void onResponse(Call<Participante> call, Response<Participante> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Participante participante = response.body();
                                    pagadoPor.setSelection(((ArrayAdapter<String>) pagadoPor.getAdapter()).getPosition(participante.getNombre()));
                                } else {
                                    Toast.makeText(PagoNuevo.this, "Error de participante: " + response.code(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Participante> call, Throwable t) {
                                Toast.makeText(PagoNuevo.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(PagoNuevo.this, "Error de pago: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Pago> call, Throwable t) {
                    Toast.makeText(PagoNuevo.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

        super.onResume();

    }
}