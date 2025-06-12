package com.example.splitup;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.splitup.objetos.Usuario;
import com.example.splitup.objetos.UsuarioDTO;
import com.example.splitup.repositorios.RepositorioUsuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InicioSesion extends AppCompatActivity {

    private TextView logo;
    private Toolbar miToolbar;
    private Button botonInicioSesion;
    private TextInputEditText editTextCorreo;
    private TextInputEditText editTextContrasenya;
    private TextInputLayout layoutCorreo;
    private TextInputLayout layoutContrasenya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        logo = findViewById(R.id.Logo);
        miToolbar= findViewById(R.id.miToolbar);
        botonInicioSesion = findViewById(R.id.buttonIniciarSesion);
        editTextCorreo = findViewById(R.id.editTextSesionCorreo);
        editTextContrasenya = findViewById(R.id.editTextSesionContrasenya);
        layoutCorreo = findViewById(R.id.layoutCorreoInicioSesion);
        layoutContrasenya = findViewById(R.id.layoutContrasenyaInicioSesion);

        String text = "SplitUp";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#601FCD")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#601FCD")), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
                    RepositorioUsuario repositorioUsuario = new RepositorioUsuario();

                    Usuario loginRequest = new Usuario();
                    loginRequest.setCorreo(correo);
                    loginRequest.setContrasenya(contrasenya);

                    repositorioUsuario.login(loginRequest, new Callback<UsuarioDTO>() {
                        @Override
                        public void onResponse(Call<UsuarioDTO> call, Response<UsuarioDTO> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                UsuarioDTO usuario = response.body();

                                Toast.makeText(InicioSesion.this, "Bienvenid@, " + usuario.getNombre(), Toast.LENGTH_SHORT).show();

                                SharedPreferences preferences = getSharedPreferences("InicioSesion", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean("sesionIniciada", true);
                                editor.putInt("id", usuario.getId());
                                editor.apply();

                                finish();
                            } else {
                                Toast.makeText(InicioSesion.this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UsuarioDTO> call, Throwable t) {
                            Toast.makeText(InicioSesion.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("Red", t.getMessage());
                        }
                    });


                } else {
                    if (correo.isEmpty()) {
                        editTextCorreo.setError("El correo electrónico no puede estar vacío");
                    }
                    if (contrasenya.isEmpty()) {
                        layoutContrasenya.setEndIconMode(TextInputLayout.END_ICON_NONE);
                        editTextContrasenya.setError("La contraseña no puede estar vacía");
                    }
                    if (!esCorreoValido) {
                        editTextCorreo.setError("El correo electrónico no es válido");
                    }
                }
            }
        });

        editTextCorreo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editTextCorreo.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextContrasenya.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editTextContrasenya.setError(null);
                layoutContrasenya.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}