package com.example.splitup;

import android.content.Intent;
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

public class Registro extends AppCompatActivity {
    TextView logo;
    Toolbar miToolbar;
    EditText editTextNombre;
    EditText editTextCorreo;
    EditText editTextContrasenya;
    EditText editTextConfirmar;
    Button botonRegistro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        logo = findViewById(R.id.Logo);
        miToolbar= findViewById(R.id.miToolbar);
        editTextNombre = findViewById(R.id.edtxtNombreRegistro);
        editTextCorreo = findViewById(R.id.edtxtCorreoRegistro);
        editTextContrasenya = findViewById(R.id.edtxtContrasenyaRegistro);
        editTextConfirmar = findViewById(R.id.edtxtConfirmarContrasenyaRegistro);
        botonRegistro = findViewById(R.id.buttonRegistro);

        String text = "SplitUp";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4E00CC")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4E00CC")), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        logo.setText(spannable);

        setSupportActionBar(miToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        botonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextCorreo.getText().toString().isEmpty() && !editTextContrasenya.getText().toString().isEmpty() && editTextCorreo.getText().toString().endsWith("@gmail.com")) {
                    Intent intent = new Intent(Registro.this, Splits.class);
                    startActivity(intent);
                } else if (editTextCorreo.getText().toString().isEmpty()) {
                    editTextCorreo.setError("El correo electrónico no puede estar vacío");
                } else if (editTextContrasenya.getText().toString().isEmpty()) {
                    editTextContrasenya.setError("La contraseña no puede estar vacía");
                } else if (!editTextCorreo.getText().toString().endsWith("@gmail.com")) {
                    editTextCorreo.setError("El correo electrónico no es válido");
                }
            }
        });
    }
}