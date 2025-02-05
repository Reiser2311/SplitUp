package com.example.splitup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.splitup.objetos.Split;
import com.example.splitup.objetos.Usuario;
import com.example.splitup.repositorios.RepositorioSplit;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplitNuevo extends AppCompatActivity {

    TextView logo;
    Toolbar miToolbar;
    ListView listViewParticipantes;
    Button buttonAnyadirParticipante;
    Button buttonCrearSplit;
    EditText edtxtNombre;
    EditText edtxtParticipante;

    ArrayList<String> participantes;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(listViewParticipantes.getAdapter().getItem(info.position).toString());
        inflater.inflate(R.menu.menu_lista_participantes, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        participantes.remove(info.position);
        if (participantes.isEmpty()) {
            listViewParticipantes.setVisibility(View.GONE);
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(SplitNuevo.this, android.R.layout.simple_list_item_1, participantes);
            listViewParticipantes.setAdapter(adapter);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_nuevo);

        logo = findViewById(R.id.Logo);
        miToolbar= findViewById(R.id.miToolbar);
        listViewParticipantes = findViewById(R.id.listViewParticipantes);
        buttonAnyadirParticipante = findViewById(R.id.buttonAnyadirParticipante);
        buttonCrearSplit = findViewById(R.id.buttonCrearSplit);
        edtxtNombre = findViewById(R.id.editTextNombre);
        edtxtParticipante = findViewById(R.id.edtxtNombreParticipante);

        String text = "SplitUp";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4E00CC")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4E00CC")), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        logo.setText(spannable);

        participantes = new ArrayList<>();
        registerForContextMenu(listViewParticipantes);

        setSupportActionBar(miToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        buttonAnyadirParticipante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nombreParticipante = edtxtParticipante.getText().toString();

                if (!nombreParticipante.isEmpty()) {
                    listViewParticipantes.setVisibility(View.VISIBLE);
                    participantes.add(nombreParticipante);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(SplitNuevo.this, android.R.layout.simple_list_item_1, participantes);
                    listViewParticipantes.setAdapter(adapter);
                    edtxtParticipante.setText("");
                }
            }
        });

        buttonCrearSplit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Split split = new Split();
                Usuario usuario = new Usuario();
                SharedPreferences preferences = getSharedPreferences("InicioSesion", MODE_PRIVATE);
                String correo = preferences.getString("correo", "");
                usuario.setCorreo(correo);
                split.setTitulo(edtxtNombre.getText().toString());
                split.setParticipantes(participantes.toArray(new String[0]));
                split.setUsuario(usuario);

                Log.d("Debug", "Split a enviar: " + new Gson().toJson(split));

                RepositorioSplit repositorioSplit = new RepositorioSplit();
                repositorioSplit.crearSplit(split, new Callback<Split>() {
                    @Override
                    public void onResponse(Call<Split> call, Response<Split> response) {
                        if (response.isSuccessful()) {
                            Split splitCreado = response.body();
                            Toast.makeText(SplitNuevo.this, "Split creado con éxito: " + splitCreado.getTitulo(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SplitNuevo.this, Splits.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SplitNuevo.this, "Error al crear el split " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Split> call, Throwable t) {
                        Toast.makeText(SplitNuevo.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}

