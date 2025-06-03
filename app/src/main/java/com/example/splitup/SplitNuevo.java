package com.example.splitup;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.splitup.objetos.Participante;
import com.example.splitup.objetos.Split;
import com.example.splitup.objetos.Usuario;
import com.example.splitup.objetos.UsuarioSplit;
import com.example.splitup.repositorios.RepositorioParticipante;
import com.example.splitup.repositorios.RepositorioSplit;
import com.example.splitup.repositorios.RepositorioUsuarioSplit;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    Button buttonActualizarSplit;
    TextInputEditText edtxtNombre;
    TextInputEditText edtxtParticipante;
    TextView txtNuevoSplit;
    TextView txtEditarSplit;

    ArrayList<DatosParticipantes> participantes;
    ArrayList<String> participantesSinId;

    AdaptadorParticipantes adapter;
    AdaptadorParticipantesSinId adapterSinId;

    int idSplitActivo;

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
        int posicion = info.position;

        DatosParticipantes datos;
        String nombre;
        if (idSplitActivo != 0) {
            nombre = null;
            AdaptadorParticipantes adaptador = (AdaptadorParticipantes) listViewParticipantes.getAdapter();
            datos = adaptador.getItem(posicion);
        } else {
            datos = null;
            ArrayAdapter arrayAdaptador = (ArrayAdapter) listViewParticipantes.getAdapter();
            nombre = arrayAdaptador.getItem(posicion).toString();
        }

        int id = item.getItemId();

        if (id == R.id.borrarParticipante) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SplitNuevo.this);
            builder.setTitle("Borrar Participante");
            builder.setMessage("¿Estás seguro de que quieres borrar este participante?");

            builder.setPositiveButton("Sí", (dialog, which) -> {
                if (idSplitActivo != 0) {
                    RepositorioParticipante repositorioParticipante = new RepositorioParticipante();
                    repositorioParticipante.eliminarParticipante(datos.getId(), new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                rehacerLista();
                                Log.d("Debug", "Participante eliminado");
                            } else {
                                Log.e("Error", "Error al eliminar participante: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(SplitNuevo.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    participantesSinId.remove(nombre);
                    adapterSinId.notifyDataSetChanged();
                }

            });

            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (id == R.id.editarParticipante) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SplitNuevo.this);
            builder.setTitle("Editar Participante");
            builder.setMessage("Añada el nuevo nombre del participante");

            final EditText input = new EditText(SplitNuevo.this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("Editar", (dialog, which) -> {
                if (idSplitActivo != 0) {
                    RepositorioParticipante repositorioParticipante = new RepositorioParticipante();
                    repositorioParticipante.actualizarParticipante(datos.getId(), input.getText().toString(), new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                rehacerLista();
                                Log.d("Debug", "Participante editado");
                            } else {
                                Log.e("Error", "Error al editar participante: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(SplitNuevo.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    participantesSinId.remove(nombre);
                    participantesSinId.add(input.getText().toString());
                    adapterSinId.notifyDataSetChanged();
                }
            });

            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
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
        txtNuevoSplit = findViewById(R.id.txtNuevoSplit);
        txtEditarSplit = findViewById(R.id.txtEditarSplit);
        buttonActualizarSplit = findViewById(R.id.buttonActualzarSplit);

        participantes = new ArrayList<>();
        participantesSinId = new ArrayList<>();

        String text = "SplitUp";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#601FCD")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#601FCD")), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        logo.setText(spannable);


        registerForContextMenu(listViewParticipantes);

        setSupportActionBar(miToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        buttonAnyadirParticipante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nombreParticipante = edtxtParticipante.getText().toString();

                if (!nombreParticipante.isEmpty()) {

                    if (idSplitActivo != 0) {
                        RepositorioParticipante repositorioParticipante = new RepositorioParticipante();
                        Split split = new Split();
                        split.setId(idSplitActivo);
                        Participante participante = new Participante();
                        participante.setNombre(nombreParticipante);
                        participante.setSplit(split);
                        repositorioParticipante.crearParticipante(participante, new Callback<Participante>() {
                            @Override
                            public void onResponse(Call<Participante> call, Response<Participante> response) {
                                if (response.isSuccessful()) {
                                    Participante participanteCreado = response.body();
//
//                                    listViewParticipantes.setVisibility(View.VISIBLE);
//
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            DatosParticipantes datosParticipantes =
//                                                    new DatosParticipantes(participanteCreado.getId(), participanteCreado.getNombre());
//                                            participantes.add(datosParticipantes);
//                                            adapter.notifyDataSetChanged();
//                                        }
//                                    });

                                    rehacerLista();

                                    edtxtParticipante.setText("");

                                    Log.d("Debug", "Participante creado: " + new Gson().toJson(participanteCreado));
                                    rehacerLista();
                                } else {
                                    Toast.makeText(SplitNuevo.this, "Error al crear el participante", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Participante> call, Throwable t) {
                                Toast.makeText(SplitNuevo.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        participantesSinId.add(nombreParticipante);
                        adapterSinId.notifyDataSetChanged();
                        edtxtParticipante.setText("");
                    }
                }
            }
        });

        buttonCrearSplit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!((participantes.isEmpty() && idSplitActivo != 0) || (participantesSinId.isEmpty() &&
                        idSplitActivo == 0)) && !edtxtNombre.getText().toString().isEmpty()) {
                    Split split = new Split();
                    Usuario usuario = new Usuario();
                    SharedPreferences preferences = getSharedPreferences("InicioSesion", MODE_PRIVATE);
                    int idUsuario = preferences.getInt("id", 0);
                    usuario.setId(idUsuario);
                    split.setTitulo(edtxtNombre.getText().toString());
                    split.setUsuario(usuario);

                    Log.d("Debug", "Split a enviar: " + new Gson().toJson(split));

                    RepositorioSplit repositorioSplit = new RepositorioSplit();
                    repositorioSplit.crearSplit(split, new Callback<Split>() {
                        @Override
                        public void onResponse(Call<Split> call, Response<Split> response) {
                            if (response.isSuccessful()) {
                                Split splitCreado = response.body();
                                UsuarioSplit relacion = new UsuarioSplit(idUsuario, splitCreado.getId());

                                RepositorioUsuarioSplit repositorioUsuarioSplit = new RepositorioUsuarioSplit();
                                repositorioUsuarioSplit.crearRelacion(relacion, new Callback<UsuarioSplit>() {
                                    @Override
                                    public void onResponse(Call<UsuarioSplit> call, Response<UsuarioSplit> response) {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(SplitNuevo.this, "Split creado con éxito: " +
                                                    splitCreado.getTitulo(), Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(SplitNuevo.this, "Error al vincular usuario al split",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<UsuarioSplit> call, Throwable t) {
                                        Toast.makeText(SplitNuevo.this, "Error de red al vincular usuario",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });

                                RepositorioParticipante repositorioParticipante = new RepositorioParticipante();
                                for (String nombreParticipante : participantesSinId) {
                                    Participante nuevoParticipante = new Participante();
                                    nuevoParticipante.setNombre(nombreParticipante);
                                    nuevoParticipante.setSplit(splitCreado);
                                    repositorioParticipante.crearParticipante(nuevoParticipante, new Callback<Participante>() {
                                        @Override
                                        public void onResponse(Call<Participante> call, Response<Participante> response) {
                                            if (!response.isSuccessful()) {
                                                Toast.makeText(SplitNuevo.this, "Error al crear el participante: " + response.code(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Participante> call, Throwable t) {
                                            Toast.makeText(SplitNuevo.this, "Error de red: " + t.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            } else {
                                Toast.makeText(SplitNuevo.this, "Error al crear el split: " + response.code(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Split> call, Throwable t) {
                            Toast.makeText(SplitNuevo.this, "Error de red: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if ((participantes.isEmpty() && idSplitActivo != 0) || (participantesSinId.isEmpty() && idSplitActivo == 0)) {
                    edtxtParticipante.setError("Los participantes no pueden estar vacios");
                } else {
                    edtxtNombre.setError("El nombre no puede estar vacio");
                }


            }
        });

        buttonActualizarSplit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RepositorioSplit repositorioSplit = new RepositorioSplit();

                repositorioSplit.actualizarSplit(idSplitActivo, edtxtNombre.getText().toString(), new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(SplitNuevo.this, "Split actualizado correctamente", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(SplitNuevo.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                            try {
                                Log.e("Respuesta", "Codigo: " + response.code() + " - Error: " + response.errorBody().string());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(SplitNuevo.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        SharedPreferences preferences = getSharedPreferences("SplitActivo", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        idSplitActivo = preferences.getInt("idSplit", 0);
        editor.remove("idSplit");
        editor.apply();
        if (idSplitActivo != 0) {
            adapter = new AdaptadorParticipantes(SplitNuevo.this, participantes);
            listViewParticipantes.setAdapter(adapter);
            buttonCrearSplit.setVisibility(View.GONE);
            buttonActualizarSplit.setVisibility(View.VISIBLE);
            txtNuevoSplit.setVisibility(View.GONE);
            txtEditarSplit.setVisibility(View.VISIBLE);

            RepositorioSplit repositorioSplit = new RepositorioSplit();
            repositorioSplit.obtenerSplit(idSplitActivo, new Callback<Split>() {
                @Override
                public void onResponse(Call<Split> call, Response<Split> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Split split = response.body();
                        edtxtNombre.setText(split.getTitulo());
                        rehacerLista();
                    } else {
                        Toast.makeText(SplitNuevo.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Split> call, Throwable t) {
                    Toast.makeText(SplitNuevo.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            adapterSinId = new AdaptadorParticipantesSinId(SplitNuevo.this, participantesSinId);
            listViewParticipantes.setAdapter(adapterSinId);
        }

        super.onResume();
    }

    private void rehacerLista() {
        RepositorioParticipante repositorioParticipante = new RepositorioParticipante();

        repositorioParticipante.obtenerParticipantePorSplit(idSplitActivo, new Callback<List<Participante>>() {
            @Override
            public void onResponse(Call<List<Participante>> call, Response<List<Participante>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Participante> participantesSplit = response.body();
                    participantes.clear();
                    for (Participante participante : participantesSplit) {
                        DatosParticipantes datosParticipantes = new DatosParticipantes(participante.getId(), participante.getNombre());
                        participantes.add(datosParticipantes);
                    }
                    adapter.notifyDataSetChanged();
                    listViewParticipantes.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(SplitNuevo.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Participante>> call, Throwable t) {
                Toast.makeText(SplitNuevo.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}

