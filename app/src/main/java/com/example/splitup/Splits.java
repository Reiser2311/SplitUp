package com.example.splitup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Objects;

public class Splits extends AppCompatActivity {

    TextView logo;
    Toolbar miToolbar;
    ListView miLista;
    ArrayList<String> lista;
    Button nuevoSplit;
    Boolean sesionIniciada;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!sesionIniciada) {
            getMenuInflater().inflate(R.menu.menu_inicio_sesion, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_cerrar_sesion, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.InicioSesion) {
            Intent intent = new Intent(this, InicioSesion.class);
            startActivity(intent);
        } else if (id == R.id.Registro) {
            Intent intent = new Intent(this, Registro.class);
            startActivity(intent);
        } else if (id == R.id.CerrarSesion) {
            sesionIniciada = false;
            SharedPreferences sharedPreferences = getSharedPreferences("InicioSesion", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("sesionIniciada");
            editor.apply();
            recreate();
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences("InicioSesion", MODE_PRIVATE);
        sesionIniciada = preferences.getBoolean("sesionIniciada", false);
        invalidateOptionsMenu();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splits);

        logo = findViewById(R.id.Logo);
        miToolbar= findViewById(R.id.miToolbar);
        miLista = findViewById(R.id.listViewSplits);
        lista = new ArrayList<>();
        nuevoSplit = findViewById(R.id.botonNuevosSplit);
        sesionIniciada = false;

        String text = "SplitUp";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4E00CC")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4E00CC")), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        logo.setText(spannable);

        setSupportActionBar(miToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        lista.add("Split 1");
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);
        miLista.setAdapter(adaptador);

        nuevoSplit.setOnClickListener(v -> {
            Intent intent = new Intent(Splits.this, SplitNuevo.class);
            startActivity(intent);
        });

        miLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Splits.this, Pagos.class);
                startActivity(intent);
            }
        });
    }
}