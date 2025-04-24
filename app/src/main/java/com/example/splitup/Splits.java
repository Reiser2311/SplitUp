package com.example.splitup;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.splitup.objetos.Split;
import com.example.splitup.repositorios.RepositorioSplit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;


public class Splits extends AppCompatActivity {

    TextView logo;
    Toolbar miToolbar;
    ListView listaSplits;
    ArrayList<DatosSplits> lista;
    Button nuevoSplit;
    Boolean sesionIniciada;
    RelativeLayout layoutNoHaySplits;
    Boolean ultimoItem = false;
    ArrayList<DatosSplits> datosSplits = new ArrayList<>();
    AdaptadorSplits adaptador;

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
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int posicion = info.position;

        AdaptadorSplits adaptador = (AdaptadorSplits) listaSplits.getAdapter();

        DatosSplits datos = adaptador.getItem(posicion);

        int id = item.getItemId();

        if (id == R.id.borrar) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Splits.this);
            builder.setTitle("Borrar split");
            builder.setMessage("¿Estás seguro de que quiere borrar este split?");

            builder.setPositiveButton("Sí", (dialog, which) -> {
                RepositorioSplit repositorioSplit = new RepositorioSplit();
                repositorioSplit.eliminarSplit(datos.getId(), new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            if (listaSplits.getAdapter().getCount() == 1) {
                                ultimoItem = true;
                            }
                            rehacerLista();
                            Toast.makeText(Splits.this, "Split eliminado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Splits.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(Splits.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });

            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();

        } else if (id == R.id.editar){
            SharedPreferences preferences = getSharedPreferences("SplitActivo", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("idSplit", datos.getId());
            editor.apply();

            Intent intent = new Intent(Splits.this, SplitNuevo.class);
            startActivity(intent);
        }

        return super.onContextItemSelected(item);
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
            invalidateOptionsMenu();
            lista.clear();
            AdaptadorSplits adaptador = new AdaptadorSplits(this, lista);
            listaSplits.setAdapter(adaptador);
            listaSplits.setVisibility(View.GONE);
            layoutNoHaySplits.setVisibility(View.VISIBLE);
        } else if (id == R.id.Perfil) {
            Intent intent = new Intent(this, Perfil.class);
            startActivity(intent);
        }

        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        menu.setHeaderTitle(((DatosSplits) listaSplits.getAdapter().getItem(info.position)).getNombre());
        inflater.inflate(R.menu.menu_eliminar_editar, menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences("InicioSesion", MODE_PRIVATE);
        sesionIniciada = preferences.getBoolean("sesionIniciada", false);
        invalidateOptionsMenu();

        if (sesionIniciada) {
            rehacerLista();
        }
    }

    private void rehacerLista() {
        SharedPreferences preferences = getSharedPreferences("InicioSesion", MODE_PRIVATE);
        RepositorioSplit repositorioSplit = new RepositorioSplit();
        String correo = preferences.getString("correo", "");
        if (ultimoItem) {
            listaSplits.setVisibility(View.GONE);
            layoutNoHaySplits.setVisibility(View.VISIBLE);
        }
        repositorioSplit.obtenerSplitsPorUsuario(correo , new Callback<List<Split>>() {
            @Override
            public void onResponse(Call<List<Split>> call, Response<List<Split>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Split> splits = response.body();
                    datosSplits.clear();
                    for (Split split : splits) {
                        DatosSplits datosSplit = new DatosSplits(split.getTitulo(), split.getId());
                        datosSplits.add(datosSplit);
                    }
                    adaptador.notifyDataSetChanged();
                    listaSplits.setVisibility(View.VISIBLE);
                    layoutNoHaySplits.setVisibility(View.GONE);
                } else if (response.body() != null){
                    Toast.makeText(Splits.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Split>> call, Throwable t) {
                Toast.makeText(Splits.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cambiarColorOverflow() {
        Drawable overflowIcon = miToolbar.getOverflowIcon();
        if (overflowIcon != null) {
            overflowIcon.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splits);

        logo = findViewById(R.id.Logo);
        miToolbar= findViewById(R.id.miToolbar);
        listaSplits = findViewById(R.id.listViewSplits);
        lista = new ArrayList<>();
        nuevoSplit = findViewById(R.id.botonNuevosSplit);
        sesionIniciada = false;
        layoutNoHaySplits = findViewById(R.id.layoutNoHaySplits);
        adaptador = new AdaptadorSplits(Splits.this, datosSplits);
        listaSplits.setAdapter(adaptador);

        String text = "SplitUp";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#601FCD")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#601FCD")), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        logo.setText(spannable);

        setSupportActionBar(miToolbar);
        cambiarColorOverflow();
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        registerForContextMenu(listaSplits);

        nuevoSplit.setOnClickListener(v -> {
            if (sesionIniciada) {
                Intent intent = new Intent(Splits.this, SplitNuevo.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Inicia sesion primero antes de crear un Split", Toast.LENGTH_SHORT).show();
            }
        });

        listaSplits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences preferences = getSharedPreferences("SplitActivo", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("idSplit", ((DatosSplits) parent.getItemAtPosition(position)).getId());
                editor.apply();

                Intent intent = new Intent(Splits.this, Pagos.class);
                intent.putExtra("origen", "Splits");
                startActivity(intent);
            }
        });
    }
}