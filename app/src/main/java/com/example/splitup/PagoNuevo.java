package com.example.splitup;

import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.splitup.objetos.ObjetoPago;
import com.example.splitup.repositorios.RepositorioPago;

import java.io.IOException;
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
    EditText edtxtNombrePago;
    EditText edtxtImportePago;
    Spinner pagadoPor;

    @Override
    protected void onResume() {
        SharedPreferences preferences = getSharedPreferences("PagoActivo", MODE_PRIVATE);
        int id = preferences.getInt("idPago", 0);
        if (id != 0) {
            buttonCrearPago.setVisibility(View.GONE);
            buttonActualizarPago.setVisibility(View.VISIBLE);

            RepositorioPago repositorioPago = new RepositorioPago();
            repositorioPago.obtenerPago(id, new Callback<ObjetoPago>() {
                @Override
                public void onResponse(Call<ObjetoPago> call, Response<ObjetoPago> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ObjetoPago pago = response.body();
                        edtxtNombrePago.setText(pago.getTitulo());
                        edtxtImportePago.setText(pago.getImporte() + "");
                    } else {
                        Toast.makeText(PagoNuevo.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ObjetoPago> call, Throwable t) {
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

        String text = "SplitUp";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4E00CC")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4E00CC")), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        logo.setText(spannable);

        setSupportActionBar(miToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        SharedPreferences preferences = getSharedPreferences("SplitActivo", MODE_PRIVATE);
        int id = preferences.getInt("idSplit", 0);

        buttonCrearPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RepositorioPago repositorioPago = new RepositorioPago();
                ObjetoPago pago = new ObjetoPago();
                pago.setImporte(2.2);
                pago.setTitulo("prueba");
                pago.setPagadoPor("pepe");
                pago.setSplitId(1);
                repositorioPago.crearPago(pago, new Callback<ObjetoPago>() {
                    @Override
                    public void onResponse(Call<ObjetoPago> call, Response<ObjetoPago> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(PagoNuevo.this, "Pago creado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PagoNuevo.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();

                            try {
                                String errorResponse = response.errorBody().string();

                                Log.e("Error", errorResponse);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ObjetoPago> call, Throwable t) {
                        Toast.makeText(PagoNuevo.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                Intent intent = new Intent(PagoNuevo.this, Pagos.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}