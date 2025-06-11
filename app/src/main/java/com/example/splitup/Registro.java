package com.example.splitup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
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
    private TextView logo;
    private Toolbar miToolbar;
    private TextInputEditText editTextNombre;
    private TextInputEditText editTextCorreo;
    private TextInputEditText editTextContrasenya;
    private TextInputEditText editTextConfirmar;
    private Button botonRegistro;
    private TextInputLayout layoutContrasenya;
    private TextInputLayout layoutConfirmarContrasenya;
    private TextInputLayout layoutCorreo;
    private TextInputLayout layoutNombre;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ImageView imagenPerfil;

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
                        !confirmacion.isEmpty() && contrasenya.equals(confirmacion) &&
                        esContrasenyaSegura(contrasenya)) {
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
                            if (response.code() == 200 && response.body() != null) {
                                Toast.makeText(Registro.this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 404) {
                                repositorioUsuario.crearUsuario(usuario, new Callback<Usuario>() {
                                    @Override
                                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                                        if (response.isSuccessful()) {
                                            Usuario usuarioCreado = response.body();
                                            Toast.makeText(Registro.this, "Usuario creado: " +
                                                    usuarioCreado.getNombre(), Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(Registro.this, "Error: " +
                                                    response.code(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Usuario> call, Throwable t) {
                                        Toast.makeText(Registro.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(Registro.this, "Error inesperado: " + response.code(), Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<Usuario> call, Throwable t) {
                            Toast.makeText(Registro.this, "Error de red: " +
                                    t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    if (confirmacion.isEmpty()) {
                        layoutConfirmarContrasenya.setEndIconMode(TextInputLayout.END_ICON_NONE);
                        editTextConfirmar.setError("La confirmación de contraseña no puede estar vacía");
                    }
                    if (!esContrasenyaSegura(contrasenya)) {
                        layoutContrasenya.setEndIconMode(TextInputLayout.END_ICON_NONE);
                        editTextContrasenya.setError("La contraseña debe contener minimo 8 caracteres, una mayuscula, una minuscula, un numero y un caracter especial");
                    }
                    if (!contrasenya.equals(confirmacion)) {
                        layoutConfirmarContrasenya.setEndIconMode(TextInputLayout.END_ICON_NONE);
                        editTextConfirmar.setError("Las contraseñas no coinciden");
                    }
                    if (!esCorreoValido){
                        layoutCorreo.setEndIconMode(TextInputLayout.END_ICON_NONE);
                        editTextCorreo.setError("El correo electrónico no es válido");
                    }
                    if (editTextNombre.getText().toString().isEmpty()){
                        editTextNombre.setError("El nombre no puede estar vacío");
                    }
                }
            }
        });

        editTextNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editTextNombre.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

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

        editTextConfirmar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editTextConfirmar.setError(null);
                layoutConfirmarContrasenya.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public boolean esContrasenyaSegura(String contrasenya) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return contrasenya.matches(regex);
    }
}