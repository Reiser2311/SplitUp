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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.splitup.objetos.Pago;
import com.example.splitup.objetos.Split;
import com.example.splitup.repositorios.RepositorioPago;
import com.example.splitup.repositorios.RepositorioSplit;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PagoNuevo extends AppCompatActivity {

    TextView logo;
    Toolbar miToolbar;
    Button buttonCrearPago;
    Button buttonActualizarPago;
    EditText edtxtNombrePago;
    EditText edtxtImportePago;
    Spinner pagadoPor;
    TextView txtNuevoPago;
    TextView txtEditarPago;
    String[] participantes;
    int id;

    @Override
    protected void onResume() {
        SharedPreferences preferences = getSharedPreferences("PagoActivo", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int id = preferences.getInt("idPago", 0);
        editor.remove("idPago");
        editor.apply();
        if (id != 0) {
            buttonCrearPago.setVisibility(View.GONE);
            buttonActualizarPago.setVisibility(View.VISIBLE);
            txtNuevoPago.setVisibility(View.GONE);
            txtEditarPago.setVisibility(View.VISIBLE);

            RepositorioPago repositorioPago = new RepositorioPago();
            repositorioPago.obtenerPago(id, new Callback<Pago>() {
                @Override
                public void onResponse(Call<Pago> call, Response<Pago> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Pago pago = response.body();
                        edtxtNombrePago.setText(pago.getTitulo());
                        edtxtImportePago.setText(pago.getImporte() + "");
                    } else {
                        Toast.makeText(PagoNuevo.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
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

        String text = "SplitUp";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4E00CC")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4E00CC")), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        logo.setText(spannable);

        setSupportActionBar(miToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        SharedPreferences preferences = getSharedPreferences("SplitActivo", MODE_PRIVATE);
        id = preferences.getInt("idSplit", 0);

        RepositorioSplit repositorioSplit = new RepositorioSplit();
        repositorioSplit.obtenerSplit(id, new Callback<Split>() {
            @Override
            public void onResponse(Call<Split> call, Response<Split> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Split split = response.body();
                    participantes = split.getParticipantes();
                } else {
                    Toast.makeText(PagoNuevo.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Split> call, Throwable t) {
                Toast.makeText(PagoNuevo.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        ArrayAdapter<String> adapater = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, participantes);
        pagadoPor.setAdapter(adapater);

//        pagadoPor.setOnItemClickListener(new AdapterView.OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        buttonCrearPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RepositorioPago repositorioPago = new RepositorioPago();
                Pago pago = new Pago();
                Split split = new Split();
                split.setId(id);
                pago.setImporte(Double.parseDouble(edtxtImportePago.getText().toString()));
                pago.setTitulo(edtxtNombrePago.getText().toString());
                pago.setPagadoPor("pepe");
                pago.setSplit(split);
                repositorioPago.crearPago(pago, new Callback<Pago>() {
                    @Override
                    public void onResponse(Call<Pago> call, Response<Pago> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(PagoNuevo.this, "Pago creado", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PagoNuevo.this, Pagos.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(PagoNuevo.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Pago> call, Throwable t) {
                        Toast.makeText(PagoNuevo.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }
}