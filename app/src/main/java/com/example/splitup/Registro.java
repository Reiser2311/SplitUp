package com.example.splitup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.splitup.objetos.Usuario;
import com.example.splitup.repositorios.RepositorioUsuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registro extends AppCompatActivity {
    TextView logo;
    Toolbar miToolbar;
    TextInputEditText editTextNombre;
    TextInputEditText editTextCorreo;
    TextInputEditText editTextContrasenya;
    TextInputEditText editTextConfirmar;
    Button botonRegistro;
    TextInputLayout layoutContrasenya;
    TextInputLayout layoutConfirmarContrasenya;
    TextInputLayout layoutCorreo;
    TextInputLayout layoutNombre;

    ActivityResultLauncher<Intent> imagePickerLauncher;
    ImageView imagenPerfil;

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
        imagenPerfil = findViewById(R.id.imagenPerfilRegistro);
        layoutContrasenya = findViewById(R.id.layoutContrasenyaRegistro);
        layoutConfirmarContrasenya = findViewById(R.id.layoutConfirmarContrasenyaRegistro);
        layoutCorreo = findViewById(R.id.layoutCorreoRegistro);
        layoutNombre = findViewById(R.id.layoutNombreRegistro);

        String text = "SplitUp";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#601FCD")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#601FCD")), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        logo.setText(spannable);

        setSupportActionBar(miToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uriImagenSeleccionada = result.getData().getData();
                        imagenPerfil.setImageURI(uriImagenSeleccionada);
                    }
                }
        );

        imagenPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        botonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correo = editTextCorreo.getText().toString();
                String contrasenya = editTextContrasenya.getText().toString();
                String confirmacion = editTextConfirmar.getText().toString();

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

                if (!correo.isEmpty() && !contrasenya.isEmpty() && esCorreoValido &&
                        !confirmacion.isEmpty() && contrasenya.equals(confirmacion)) {
                    Usuario usuario = new Usuario();
                    usuario.setNombre(editTextNombre.getText().toString());
                    usuario.setCorreo(editTextCorreo.getText().toString());
                    usuario.setContrasenya(editTextContrasenya.getText().toString());
                    usuario.setFotoPerfil(Conversor.imageViewToBase64(imagenPerfil));

                    RepositorioUsuario repositorioUsuario = new RepositorioUsuario();

                    //Comprobacion de si el usario existe
                    repositorioUsuario.obtenerUsuarioPorCorreo(correo, new Callback<Usuario>() {
                        @Override
                        public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                            if (response.isSuccessful()) {
                                //en caso de que existe avisa al usuario
                                Toast.makeText(Registro.this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
                            } else {
                                //en caso de que no exista lo crea
                                repositorioUsuario.crearUsuario(usuario, new Callback<Usuario>() {
                                    @Override
                                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                                        if (response.isSuccessful()) {
                                            Usuario usuarioCreado = response.body();
                                            Toast.makeText(Registro.this, "Usuario creado: " +
                                                    usuarioCreado.getNombre(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(Registro.this, "Error: " +
                                                    response.code(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Usuario> call, Throwable t) {
                                        Toast.makeText(Registro.this, "Error de red: " +
                                                t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                });
                            }

                        }

                        @Override
                        public void onFailure(Call<Usuario> call, Throwable t) {
                            Toast.makeText(Registro.this, "Error de red: " +
                                    t.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });


                    Intent intent = new Intent(Registro.this, Splits.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else if (correo.isEmpty()) {
                    editTextCorreo.setError("El correo electrónico no puede estar vacío");
                } else if (contrasenya.isEmpty()) {
                    layoutContrasenya.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    editTextContrasenya.setError("La contraseña no puede estar vacía");
                } else if (confirmacion.isEmpty()) {
                    layoutConfirmarContrasenya.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    editTextConfirmar.setError("La confirmación de contraseña no puede estar vacía");
                } else if (!contrasenya.equals(confirmacion)) {
                    layoutConfirmarContrasenya.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    editTextConfirmar.setError("Las contraseñas no coinciden");
                } else {
                    layoutCorreo.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    editTextCorreo.setError("El correo electrónico no es válido");
                }
            }
        });

        editTextNombre.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editTextNombre.setError(null);
            }
        });

        editTextCorreo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editTextCorreo.setError(null);
            }
        });

        editTextContrasenya.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editTextContrasenya.setError(null);
                layoutContrasenya.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }
        });

        editTextConfirmar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editTextConfirmar.setError(null);
                layoutConfirmarContrasenya.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }
        });
    }
}