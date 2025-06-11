package com.example.splitup;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.splitup.objetos.Usuario;
import com.example.splitup.repositorios.RepositorioUsuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Perfil extends AppCompatActivity {

     private TextInputEditText editTextCorreoPerfil;
     private TextInputEditText editTextNombrePerfil;
     private TextInputEditText editTextContrasenyaPerfil;
     private TextInputEditText editTextConfirmarContrasenyaPerfil;
     private Button buttonActualizarUsuario;
     private Button buttonBorrarUsuario;
     private TextView logo;
     private TextInputLayout layoutCorreoPerfil;
     private TextInputLayout layoutNombrePerfil;
     private TextInputLayout layoutContrasenyaPerfil;
     private TextInputLayout layoutConfirmarContrasenyaPerfil;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ImageView imagenPerfil;

    private Uri uriImagenSeleccionada;

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences("InicioSesion", MODE_PRIVATE);
        int id = preferences.getInt("id", 0);

        RepositorioUsuario repositorioUsuario = new RepositorioUsuario();

        repositorioUsuario.obtenerUsuarioPorId(id, new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario usuario = response.body();
                    editTextCorreoPerfil.setText(usuario.getCorreo());
                    editTextNombrePerfil.setText(usuario.getNombre());
                    if (usuario.getFotoPerfil() != null) {
                        Conversor.cargarImagenDesdeBase64(usuario.getFotoPerfil(), imagenPerfil);
                    }
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
                if (!editTextContrasenyaPerfil.getText().toString().isEmpty() &&
                        !editTextNombrePerfil.getText().toString().isEmpty() &&
                        !editTextCorreoPerfil.getText().toString().isEmpty() &&
                        editTextContrasenyaPerfil.getText().toString().equals(editTextConfirmarContrasenyaPerfil.getText().toString())) {

                        Usuario usuario = new Usuario();
                        usuario.setCorreo(editTextCorreoPerfil.getText().toString());
                        usuario.setNombre(editTextNombrePerfil.getText().toString());
                        usuario.setContrasenya(editTextContrasenyaPerfil.getText().toString());
                        usuario.setFotoPerfil(Conversor.imageViewToBase64(imagenPerfil));

                        repositorioUsuario.actualizarUsuario(id, usuario,
                                new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(Perfil.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Perfil.this, Splits.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(Perfil.this, "Error: " +  response.code(), Toast.LENGTH_SHORT).show();
                                    Log.e("Perfil", "Error al actualizar el perfil: " + response.code());

                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(Perfil.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                } else if (!editTextContrasenyaPerfil.getText().toString().equals(editTextConfirmarContrasenyaPerfil.getText().toString())) {
                    layoutConfirmarContrasenyaPerfil.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    editTextConfirmarContrasenyaPerfil.setError("Las contraseñas no coinciden");
                } else if (editTextContrasenyaPerfil.getText().toString().isEmpty()) {
                    layoutContrasenyaPerfil.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    editTextContrasenyaPerfil.setError("La contraseña no puede estar vacía");
                } else if (editTextNombrePerfil.getText().toString().isEmpty()) {
                    editTextNombrePerfil.setError("El nombre no puede estar vacio");
                } else {
                    editTextCorreoPerfil.setError("El correo no puede estar vacio");
                }
            }
        });

        editTextCorreoPerfil.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editTextCorreoPerfil.setError(null);
            }
        });

        editTextNombrePerfil.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editTextNombrePerfil.setError(null);
            }
        });

        editTextContrasenyaPerfil.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editTextContrasenyaPerfil.setError(null);
                layoutContrasenyaPerfil.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }
        });

        editTextConfirmarContrasenyaPerfil.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editTextConfirmarContrasenyaPerfil.setError(null);
                layoutConfirmarContrasenyaPerfil.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }
        });

        buttonBorrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Perfil.this);
                builder.setTitle("Borrar usuario");
                builder.setMessage("¿Estas seguro de que quieres borrar este usuario?");

                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RepositorioUsuario repositorioUsuario = new RepositorioUsuario();
                        repositorioUsuario.eliminarUsuario(id, new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    SharedPreferences sharedPreferences = getSharedPreferences("InicioSesion", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.remove("sesionIniciada");
                                    editor.apply();
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

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        editTextCorreoPerfil = findViewById(R.id.edtxtCorreoPerfil);
        editTextNombrePerfil = findViewById(R.id.editTextNombrePerfil);
        editTextContrasenyaPerfil = findViewById(R.id.editTextContrasenyaPerfil);
        editTextConfirmarContrasenyaPerfil = findViewById(R.id.editTextConfirmarContrasenyaPerfil);
        buttonActualizarUsuario = findViewById(R.id.buttonIniciarSesion);
        logo = findViewById(R.id.Logo);
        buttonBorrarUsuario = findViewById(R.id.buttonBorrarUsuario);
        imagenPerfil = findViewById(R.id.imagenPerfil);
        layoutCorreoPerfil = findViewById(R.id.layoutCorreoPerfil);
        layoutNombrePerfil = findViewById(R.id.layoutNombrePerfil);
        layoutContrasenyaPerfil = findViewById(R.id.layoutContrasenyaPerfil);
        layoutConfirmarContrasenyaPerfil = findViewById(R.id.layoutConfirmarContrasenyaPerfil);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uriImagenSeleccionada = result.getData().getData();
                        Glide.with(this).load(uriImagenSeleccionada).into(imagenPerfil);
                    }
                }
        );

        imagenPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        String text = "SplitUp";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#601FCD")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#601FCD")), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        logo.setText(spannable);



    }
}