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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.splitup.objetos.ObjetoUsuario;
import com.example.splitup.repositorios.RepositorioUsuario;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                    ObjetoUsuario usuario = new ObjetoUsuario();
                    usuario.setNombre(editTextNombre.getText().toString());
                    usuario.setCorreo(editTextCorreo.getText().toString());
                    usuario.setContrasenya(editTextContrasenya.getText().toString());

                    RepositorioUsuario repositorioUsuario = new RepositorioUsuario();
                    repositorioUsuario.crearUsuario(usuario, new Callback<ObjetoUsuario>() {
                        @Override
                        public void onResponse(Call<ObjetoUsuario> call, Response<ObjetoUsuario> response) {
                            if (response.isSuccessful()) {
                                ObjetoUsuario usuarioCreado = response.body();
                                Toast.makeText(Registro.this, "Usuario creado: " + usuarioCreado.getNombre(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Registro.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ObjetoUsuario> call, Throwable t) {
                            Toast.makeText(Registro.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });
                    Intent intent = new Intent(Registro.this, Splits.class);
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