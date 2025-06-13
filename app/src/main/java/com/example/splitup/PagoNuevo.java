package com.example.splitup;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.splitup.adaptadores.AdaptadorDivision;
import com.example.splitup.adaptadores.AdaptadorParticipantesSpiner;
import com.example.splitup.datos.DatosDivision;
import com.example.splitup.datos.DatosParticipantes;
import com.example.splitup.objetos.Pago;
import com.example.splitup.objetos.Participante;
import com.example.splitup.objetos.ParticipantePago;
import com.example.splitup.objetos.Split;
import com.example.splitup.repositorios.RepositorioPago;
import com.example.splitup.repositorios.RepositorioParticipante;
import com.example.splitup.repositorios.RepositorioParticipantePago;
import com.example.splitup.repositorios.RepositorioSplit;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PagoNuevo extends AppCompatActivity {

    private TextView logo;
    private Toolbar miToolbar;
    private Button buttonCrearPago;
    private Button buttonActualizarPago;
    private TextInputEditText edtxtNombrePago;
    private TextInputEditText edtxtImportePago;
    private Spinner pagadoPor;
    private TextView txtNuevoPago;
    private TextView txtEditarPago;
    private ListView listaDivision;
    private ArrayList<DatosDivision> datosDivision;

    private ArrayList<DatosParticipantes> participantes;

    private AdaptadorParticipantesSpiner adaptadorParticipantes;

    private int idPagoActivo;

    private int idSplitActivo;

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
        listaDivision = findViewById(R.id.listaDividir);

        participantes = new ArrayList<>();
        datosDivision = new ArrayList<>();

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
                    LocalDate fechaHoy = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    pago.setFechaCreacion(fechaHoy.format(formatter));

//                    Log.d("Debug", "Pago a enviar: " + new Gson().toJson(pago));

                    RepositorioPago repositorioPago = new RepositorioPago();
                    repositorioPago.crearPago(pago, new Callback<Pago>() {
                        @Override
                        public void onResponse(Call<Pago> call, Response<Pago> response) {
                            if (response.isSuccessful()) {
                                Pago pagoCreado = response.body();
                                Toast.makeText(PagoNuevo.this, "Pago creado con exito: " + pagoCreado.getTitulo(), Toast.LENGTH_SHORT).show();
                                RepositorioParticipantePago repositorioPP = new RepositorioParticipantePago();
                                for (DatosDivision d : datosDivision) {
                                    if (d.isSeleccionado()) {
                                        ParticipantePago pp = new ParticipantePago(d.getId(), pagoCreado.getId());
                                        Log.d("RELACION_DEBUG", "Enviando participante_id=" + d.getId() + " y pago_id=" + pagoCreado.getId());

                                        repositorioPP.crearRelacion(pp, new Callback<ParticipantePago>() {
                                            @Override
                                            public void onResponse(Call<ParticipantePago> call, Response<ParticipantePago> response) {
                                                Log.d("ParticipantePago", "Relacion creada con " + d.getNombre());
                                            }

                                            @Override
                                            public void onFailure(Call<ParticipantePago> call, Throwable t) {
                                                Log.e("ParticipantePago", "Error creando relación: " + t.getMessage());
                                            }
                                        });
                                    }
                                }
                                List<Integer> idsSeleccionados = new ArrayList<>();

                                for (DatosDivision d : datosDivision) {
                                    if (d.isSeleccionado()) {
                                        idsSeleccionados.add(d.getId());
                                    }
                                }

                                SharedPreferences prefs = getSharedPreferences("ParticipantesPorPago", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();

                                Gson gson = new Gson();
                                String json = gson.toJson(idsSeleccionados);

                                editor.putString("pago_" + pagoCreado.getId(), json);
                                editor.apply();
                                finish();
                            } else {
                                Toast.makeText(PagoNuevo.this, "Error al crear el pago", Toast.LENGTH_SHORT).show();
                                Log.e("SplitActivo", "Error al crear el pago: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<Pago> call, Throwable t) {
                            Toast.makeText(PagoNuevo.this, "Error de red", Toast.LENGTH_SHORT).show();
                            Log.e("SplitActivo", "Error de red al crear el pago: " + t.getMessage());
                        }
                    });

                } else {
                    if (edtxtNombrePago.getText().toString().isEmpty()) {
                        edtxtNombrePago.setError("El nombre no puede estar vacio");
                    }

                    if (edtxtImportePago.getText().toString().isEmpty()) {
                        edtxtImportePago.setError("El importe no puede estar vacio");
                    }
                }


            }
        });

        edtxtImportePago.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edtxtImportePago.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtxtNombrePago.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edtxtNombrePago.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

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

                            RepositorioParticipantePago repositorioPP = new RepositorioParticipantePago();
                            for (DatosDivision d : datosDivision) {
                                repositorioPP.eliminarRelacion(d.getId(), idPagoActivo, new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        Log.d("ParticipantePago", "Relacion eliminada");
                                        for (DatosDivision d : datosDivision) {
                                            if (d.isSeleccionado()) {
                                                ParticipantePago pp = new ParticipantePago(d.getId(), idPagoActivo);
                                                repositorioPP.crearRelacion(pp, new Callback<ParticipantePago>() {
                                                    @Override
                                                    public void onResponse(Call<ParticipantePago> call, Response<ParticipantePago> response) {
                                                        Log.d("ParticipantePago", "Relacion nueva creada");
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ParticipantePago> call, Throwable t) {
                                                        Log.e("ParticipantePago", "Error creando nueva relación: " + t.getMessage());
                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Log.e("ParticipantePago", "Error eliminando relación: " + t.getMessage());
                                    }
                                });
                            }



                            Toast.makeText(PagoNuevo.this, "Pago actualizado correctamente", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(PagoNuevo.this, "Error inesperado", Toast.LENGTH_SHORT).show();
                            try {
                                Log.e("Respuesta", "Codigo: " + response.code() + " - Error: " + response.errorBody().string());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(PagoNuevo.this, "Error de red", Toast.LENGTH_SHORT).show();
                        Log.e("SplitActivo", "Error de red al actualizar el pago: " + t.getMessage());
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
                    Toast.makeText(PagoNuevo.this, "Error inesperado", Toast.LENGTH_SHORT).show();
                    Log.e("SplitActivo", "Error en split: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Split> call, Throwable t) {
                Toast.makeText(PagoNuevo.this, "Error de red", Toast.LENGTH_SHORT).show();
                Log.e("SplitActivo", "Error de red al obtener split: " + t.getMessage());
            }
        });

        RepositorioParticipante repositorioParticipante = new RepositorioParticipante();
        repositorioParticipante.obtenerParticipantePorSplit(idSplitActivo, new Callback<List<Participante>>() {
            @Override
            public void onResponse(Call<List<Participante>> call, Response<List<Participante>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    participantes.clear();
                    datosDivision.clear();
                    List<Participante> participantesAPI = response.body();

                    for (Participante participante : participantesAPI) {
                        DatosParticipantes datosParticipante = new DatosParticipantes(participante.getId(), participante.getNombre());
                        participantes.add(datosParticipante);

                        // Por defecto seleccionados
                        DatosDivision division = new DatosDivision(true, participante.getNombre(), participante.getId());
                        datosDivision.add(division);
                    }

                    adaptadorParticipantes = new AdaptadorParticipantesSpiner(PagoNuevo.this, participantes);
                    pagadoPor.setAdapter(adaptadorParticipantes);
                    adaptadorParticipantes.notifyDataSetChanged();

                    AdaptadorDivision adaptadorDivision = new AdaptadorDivision(PagoNuevo.this, datosDivision);
                    listaDivision.setAdapter(adaptadorDivision);

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

                                    for (int i = 0; i < participantes.size(); i++) {
                                        if (participantes.get(i).getId() == pago.getPagadoPor()) {
                                            pagadoPor.setSelection(i);
                                            break;
                                        }
                                    }
                                    RepositorioParticipante repositorioParticipante = new RepositorioParticipante();
                                    repositorioParticipante.obtenerParticipante(pago.getPagadoPor(), new Callback<Participante>() {
                                        @Override
                                        public void onResponse(Call<Participante> call, Response<Participante> response) {
                                            if (response.isSuccessful() && response.body() != null) {
                                                Participante participante = response.body();
                                                pagadoPor.setSelection(((ArrayAdapter<String>) pagadoPor.getAdapter()).getPosition(participante.getNombre()));
                                            } else {
                                                Toast.makeText(PagoNuevo.this, "Error de participante inesperado", Toast.LENGTH_SHORT).show();
                                                Log.e("SplitActivo", "Error en participante: " + response.code());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Participante> call, Throwable t) {
                                            Toast.makeText(PagoNuevo.this, "Error de red", Toast.LENGTH_SHORT).show();
                                            Log.e("SplitActivo", "Error de red al obtener participante: " + t.getMessage());
                                        }
                                    });

                                    repositorioParticipante.obtenerIdsParticipantesPorPago(idPagoActivo, new Callback<List<Integer>>() {
                                        @Override
                                        public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                                            if (response.isSuccessful() && response.body() != null) {
                                                List<Integer> idsParticipantesPago = response.body();

                                                for (DatosDivision division : datosDivision) {
                                                    if (!idsParticipantesPago.contains(division.getId())) {
                                                        division.setSeleccionado(false);
                                                    }
                                                }

                                                AdaptadorDivision nuevoAdaptador = new AdaptadorDivision(PagoNuevo.this, datosDivision);
                                                listaDivision.setAdapter(nuevoAdaptador);
                                                nuevoAdaptador.notifyDataSetChanged();
                                            } else {
                                                Toast.makeText(PagoNuevo.this, "Error al obtener participantes del pago", Toast.LENGTH_SHORT).show();
                                                Log.e("SplitActivo", "Error al obtener participantes del pago: " + response.code());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<List<Integer>> call, Throwable t) {
                                            Toast.makeText(PagoNuevo.this, "Error de red", Toast.LENGTH_SHORT).show();
                                            Log.e("SplitActivo", "Error de red al obtener participantes del pago: " + t.getMessage());
                                        }
                                    });
                                } else {
                                    Toast.makeText(PagoNuevo.this, "Error de pago inesperado", Toast.LENGTH_SHORT).show();
                                    Log.e("SplitActivo", "Error en pago: " + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<Pago> call, Throwable t) {
                                Toast.makeText(PagoNuevo.this, "Error de red", Toast.LENGTH_SHORT).show();
                                Log.e("SplitActivo", "Error de red al obtener pago: " + t.getMessage());
                            }
                        });




                    }
                } else {
                    Toast.makeText(PagoNuevo.this, "Error inesperado", Toast.LENGTH_SHORT).show();
                    Log.e("SplitActivo", "Error en participantes: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Participante>> call, Throwable t) {
                Toast.makeText(PagoNuevo.this, "Error de red", Toast.LENGTH_SHORT).show();
                Log.e("SplitActivo", "Error de red al obtener participantes: " + t.getMessage());
            }
        });




        super.onResume();

    }
}