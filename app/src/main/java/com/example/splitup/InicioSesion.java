package com.example.splitup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class InicioSesion extends AppCompatActivity {

    TextView logo;
    Toolbar miToolbar;
    Button botonInicioSesion;
    EditText editTextCorreo;
    EditText editTextContrasenya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        logo = findViewById(R.id.Logo);
        miToolbar= findViewById(R.id.miToolbar);
        botonInicioSesion = findViewById(R.id.buttonIniciarSesion);
        editTextCorreo = findViewById(R.id.editTextSesionCorreo);
        editTextContrasenya = findViewById(R.id.editTextSesionContrasenya);

        String text = "SplitUp";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4E00CC")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4E00CC")), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        logo.setText(spannable);

        setSupportActionBar(miToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        botonInicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correo = editTextCorreo.getText().toString();
                String contrasenya = editTextContrasenya.getText().toString();

                String[] dominiosValidos = {
                        "@gmail.com", "@hotmail.com", "@outlook.com", "@yahoo.com", "@live.com", "@icloud.com", "@gmx.com", "@mail.com", "@protonmail.com", "@zoho.com"
                };

                boolean esCorreoValido = false;
                for (String dominio : dominiosValidos) {
                    if (correo.endsWith(dominio)) {
                        esCorreoValido = true;
                        break;
                    }
                }

                if (!correo.isEmpty() && !contrasenya.isEmpty() && esCorreoValido) {
                    SharedPreferences preferences = getSharedPreferences("InicioSesion", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("sesionIniciada", true);
                    editor.apply();

                    Intent intent = new Intent(InicioSesion.this, Splits.class);
                    startActivity(intent);
                } else if (correo.isEmpty()) {
                    editTextCorreo.setError("El correo electrónico no puede estar vacío");
                } else if (contrasenya.isEmpty()) {
                    editTextContrasenya.setError("La contraseña no puede estar vacía");
                } else if (!esCorreoValido) {
                    editTextCorreo.setError("El correo electrónico no es válido");
                }
            }
        });
    }
}