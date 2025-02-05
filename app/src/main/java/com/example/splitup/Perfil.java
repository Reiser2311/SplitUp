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

import com.example.splitup.objetos.Usuario;
import com.example.splitup.repositorios.RepositorioUsuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Perfil extends AppCompatActivity {

    TextView txtCorreoPerfil;
    EditText editTextNombrePerfil;
    EditText editTextContrasenyaPerfil;
    EditText editTextConfirmarContrasenyaPerfil;
    Button buttonActualizarUsuario;
    Button buttonBorrarUsuario;
    TextView logo;

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences("InicioSesion", MODE_PRIVATE);
        String correo = preferences.getString("correo", "");

        RepositorioUsuario repositorioUsuario = new RepositorioUsuario();

        repositorioUsuario.obtenerUsuario(correo, new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario usuario = response.body();
                    txtCorreoPerfil.setText(usuario.getCorreo());
                    editTextNombrePerfil.setText(usuario.getNombre());
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(Perfil.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        buttonActualizarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextContrasenyaPerfil.getText().toString().isEmpty()) {
                    if (editTextContrasenyaPerfil.getText().toString().equals(editTextConfirmarContrasenyaPerfil.getText().toString())) {
                        Usuario usuario = new Usuario();
                        usuario.setNombre(editTextNombrePerfil.getText().toString());
                        usuario.setContrasenya(editTextContrasenyaPerfil.getText().toString());
                        usuario.setCorreo(correo);

                        repositorioUsuario.actualizarUsuario(correo, usuario, new Callback<Usuario>() {
                            @Override
                            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Usuario usuarioCreado = response.body();
                                    Toast.makeText(Perfil.this, "Perfil actualizado correctamente " + usuarioCreado.getNombre(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Perfil.this, Splits.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(Perfil.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onFailure(Call<Usuario> call, Throwable t) {
                                Toast.makeText(Perfil.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        editTextConfirmarContrasenyaPerfil.setError("Las contraseñas no coinciden");
                    }
                } else {
                    editTextContrasenyaPerfil.setError("La contraseña no puede estar vacía");
                }
            }
        });

        buttonBorrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RepositorioUsuario repositorioUsuario = new RepositorioUsuario();
                repositorioUsuario.eliminarUsuario(txtCorreoPerfil.getText().toString(), new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(Perfil.this, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Perfil.this, Splits.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Perfil.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(Perfil.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        txtCorreoPerfil = findViewById(R.id.txtCorreoPerfil);
        editTextNombrePerfil = findViewById(R.id.editTextNombrePerfil);
        editTextContrasenyaPerfil = findViewById(R.id.editTextContrasenyaPerfil);
        editTextConfirmarContrasenyaPerfil = findViewById(R.id.editTextConfirmarContrasenyaPerfil);
        buttonActualizarUsuario = findViewById(R.id.buttonIniciarSesion);
        logo = findViewById(R.id.Logo);
        buttonBorrarUsuario = findViewById(R.id.buttonBorrarUsuario);

        String text = "SplitUp";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4E00CC")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4E00CC")), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        logo.setText(spannable);

    }
}