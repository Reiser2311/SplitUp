package com.example.splitup;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
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

import com.example.splitup.adaptadores.AdaptadorParticipantes;
import com.example.splitup.adaptadores.AdaptadorParticipantesSinId;
import com.example.splitup.datos.DatosParticipantes;
import com.example.splitup.objetos.Participante;
import com.example.splitup.objetos.Split;
import com.example.splitup.objetos.Usuario;
import com.example.splitup.objetos.UsuarioDTO;
import com.example.splitup.objetos.UsuarioParticipante;
import com.example.splitup.objetos.UsuarioSplit;
import com.example.splitup.repositorios.RepositorioParticipante;
import com.example.splitup.repositorios.RepositorioSplit;
import com.example.splitup.repositorios.RepositorioUsuario;
import com.example.splitup.repositorios.RepositorioUsuarioParticipante;
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

    private TextView logo;
    private Toolbar miToolbar;
    private ListView listViewParticipantes;
    private Button buttonAnyadirParticipante;
    private Button buttonCrearSplit;
    private Button buttonActualizarSplit;
    private TextInputEditText edtxtNombre;
    private TextInputEditText edtxtParticipante;
    private TextView txtNuevoSplit;
    private TextView txtEditarSplit;

    private ArrayList<DatosParticipantes> participantes;
    private ArrayList<String> participantesSinId;

    private AdaptadorParticipantes adapter;
    private AdaptadorParticipantesSinId adapterSinId;

    private int idSplitActivo;
    private int idUsuarioCreador;
    private String nombreUsuarioCreador = "";

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(listViewParticipantes.getAdapter().getItem(info.position).toString());
        if (idSplitActivo == 0) {
            inflater.inflate(R.menu.menu_lista_participantes, menu);
        } else {
            inflater.inflate(R.menu.menu_lista_participantes_existentes, menu);
        }
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
        } else if (id == R.id.soyYo) {
            RepositorioParticipante repositorioParticipante = new RepositorioParticipante();
            repositorioParticipante.obtenerParticipantePorSplit(idSplitActivo, new Callback<List<Participante>>() {
                @Override
                public void onResponse(Call<List<Participante>> call, Response<List<Participante>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        comprobarYReasignarRelacion(response.body(), idUsuarioCreador, datos.getId(), datos.getNombre());
                    }
                }

                @Override
                public void onFailure(Call<List<Participante>> call, Throwable t) {
                    Toast.makeText(SplitNuevo.this, "Error al obtener participantes: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }

        return super.onContextItemSelected(item);
    }

    private void comprobarYReasignarRelacion(List<Participante> participantes, int idUsuario, int idNuevoParticipante, String nuevoNombre) {
        comprobarRelacionSecuencial(participantes, idUsuario, 0, (idExistenteRelacion) -> {
            if (idExistenteRelacion != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SplitNuevo.this);
                builder.setTitle("Ya estás vinculado");
                builder.setMessage("Ya estás relacionado con otro participante. ¿Quieres cambiar la relación a \"" + nuevoNombre + "\"?");

                builder.setPositiveButton("Sí", (dialog, which) -> {
                    RepositorioUsuarioParticipante repoUP = new RepositorioUsuarioParticipante();
                    repoUP.eliminarRelacion(idExistenteRelacion, idUsuario, new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                crearRelacionUsuarioParticipante(idUsuario, idNuevoParticipante, nuevoNombre);
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(SplitNuevo.this, "Error al eliminar relación: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });

                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                builder.show();
            } else {
                crearRelacionUsuarioParticipante(idUsuario, idNuevoParticipante, nuevoNombre);
            }
        });
    }


    private void comprobarRelacionSecuencial(List<Participante> participantes, int idUsuario, int indice, ParticipanteRelacionadoCallback callback) {
        if (indice >= participantes.size()) {
            callback.onResultado(null); // No hay relación
            return;
        }

        Participante p = participantes.get(indice);
        RepositorioUsuarioParticipante repo = new RepositorioUsuarioParticipante();
        repo.existeRelacion(p.getId(), idUsuario, new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && Boolean.TRUE.equals(response.body())) {
                    callback.onResultado(p.getId()); // Ya está relacionado con este participante
                } else {
                    comprobarRelacionSecuencial(participantes, idUsuario, indice + 1, callback); // Sigue con el siguiente
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("Red", "Error en relación: " + t.getMessage());
                comprobarRelacionSecuencial(participantes, idUsuario, indice + 1, callback); // Intenta el siguiente igual
            }
        });
    }


    private void crearRelacionUsuarioParticipante(int idUsuario, int idParticipante, String nombreParticipante) {
        RepositorioUsuarioParticipante repo = new RepositorioUsuarioParticipante();
        UsuarioParticipante nuevaRelacion = new UsuarioParticipante(idParticipante, idUsuario);

        repo.crearRelacion(nuevaRelacion, new Callback<UsuarioParticipante>() {
            @Override
            public void onResponse(Call<UsuarioParticipante> call, Response<UsuarioParticipante> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SplitNuevo.this, "Ahora eres " + nombreParticipante, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SplitNuevo.this, "Ya estás vinculado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UsuarioParticipante> call, Throwable t) {
                Toast.makeText(SplitNuevo.this, "Error de red", Toast.LENGTH_SHORT).show();
                Log.e("Red", "Error al crear relación: " + t.getMessage());
            }
        });
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

                    boolean encontrado = false;
                    if (idSplitActivo != 0 ) {
                        for (DatosParticipantes participante : participantes) {
                            if (participante.getNombre().equals(nombreParticipante)) {
                                encontrado = true;
                                break;
                            }
                        }
                        if (!encontrado) {
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
                                        rehacerLista();
                                        Log.d("Debug", "Participante creado: " + new Gson().toJson(participanteCreado));
                                    } else {
                                        Toast.makeText(SplitNuevo.this, "Error al crear el participante", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Participante> call, Throwable t) {
                                    Toast.makeText(SplitNuevo.this, "Error de red", Toast.LENGTH_SHORT).show();
                                    Log.e("Error", "Error de red: " + t.getMessage());
                                }
                            });
                        } else {
                            Toast.makeText(SplitNuevo.this, "El participante ya existe", Toast.LENGTH_SHORT).show();
                        }
                        edtxtParticipante.setText("");
                    } else {
                        for (String participanteNombre : participantesSinId) {
                            if (participanteNombre.equals(nombreParticipante)) {
                                encontrado = true;
                                break;
                            }
                        }
                        if (!encontrado) {
                            participantesSinId.add(nombreParticipante);
                        } else {
                            Toast.makeText(SplitNuevo.this, "El participante ya existe", Toast.LENGTH_SHORT).show();
                        }
                        adapterSinId.notifyDataSetChanged();
                        edtxtParticipante.setText("");
                    }
                    encontrado = false;
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
                    usuario.setId(idUsuarioCreador);
                    split.setTitulo(edtxtNombre.getText().toString());
                    split.setUsuario(usuario);

                    Log.d("Debug", "Split a enviar: " + new Gson().toJson(split));

                    RepositorioSplit repositorioSplit = new RepositorioSplit();
                    repositorioSplit.crearSplit(split, new Callback<Split>() {
                        @Override
                        public void onResponse(Call<Split> call, Response<Split> response) {
                            if (response.isSuccessful()) {
                                Split splitCreado = response.body();
                                UsuarioSplit relacion = new UsuarioSplit(idUsuarioCreador, splitCreado.getId());

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

                                RepositorioUsuario repositorioUsuario = new RepositorioUsuario();
                                repositorioUsuario.obtenerUsuarioPorId(idUsuarioCreador, new Callback<UsuarioDTO>() {
                                    @Override
                                    public void onResponse(Call<UsuarioDTO> call, Response<UsuarioDTO> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            UsuarioDTO usuario = response.body();
                                            nombreUsuarioCreador = usuario.getNombre();

                                            RepositorioParticipante repositorioParticipante = new RepositorioParticipante();
                                            for (String nombreParticipante : participantesSinId) {
                                                Participante nuevoParticipante = new Participante();
                                                nuevoParticipante.setNombre(nombreParticipante);
                                                nuevoParticipante.setSplit(splitCreado);
                                                repositorioParticipante.crearParticipante(nuevoParticipante, new Callback<Participante>() {
                                                    @Override
                                                    public void onResponse(Call<Participante> call, Response<Participante> response) {
                                                        if (response.isSuccessful() && response.body() != null) {
                                                            Log.d("Debug", "Participante creado: " + new Gson().toJson(response.body()));

                                                            if (nombreParticipante.equals(nombreUsuarioCreador)) {
                                                                RepositorioUsuarioParticipante repositorioUsuarioParticipante = new RepositorioUsuarioParticipante();
                                                                repositorioUsuarioParticipante.crearRelacion(new UsuarioParticipante(response.body().getId(), idUsuarioCreador), new Callback<UsuarioParticipante>() {
                                                                    @Override
                                                                    public void onResponse(Call<UsuarioParticipante> call, Response<UsuarioParticipante> response) {
                                                                        if (response.isSuccessful() && response.body() != null) {
                                                                            Log.d("Debug", "Relacion creada: " + new Gson().toJson(response.body()));
                                                                        } else {
                                                                            Log.e("Error", "Error al crear relacion: " + response.code());
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onFailure(Call<UsuarioParticipante> call, Throwable t) {
                                                                        Log.e("Error", "Error de red: " + t.getMessage());
                                                                    }
                                                                });
                                                            }
                                                        } else {
                                                            Log.e("Error", "Error al crear participante: " + response.code());
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Participante> call, Throwable t) {
                                                        Log.e("Error", "Error de red: " + t.getMessage());
                                                    }
                                                });
                                            }
                                        } else {
                                            Log.e("Error", "No se pudo obtener el nombre del usuario");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<UsuarioDTO> call, Throwable t) {
                                        Log.e("Error", "Error de red al obtener usuario: " + t.getMessage());
                                    }
                                });

                            } else {
                                Toast.makeText(SplitNuevo.this, "Error al crear el split",
                                        Toast.LENGTH_SHORT).show();
                                Log.e("Error", "Error al crear el split: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<Split> call, Throwable t) {
                            Log.e("Error", "Error de red: " + t.getMessage());
                            Toast.makeText(SplitNuevo.this, "Error de red", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    if ((participantes.isEmpty() && idSplitActivo != 0) || (participantesSinId.isEmpty() && idSplitActivo == 0)) {
                        edtxtParticipante.setError("Los participantes no pueden estar vacios");
                    }
                    if (edtxtNombre.getText().toString().isEmpty()){
                        edtxtNombre.setError("El nombre no puede estar vacio");
                    }
                }


            }
        });

        edtxtParticipante.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edtxtParticipante.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtxtNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edtxtNombre.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

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
                            Toast.makeText(SplitNuevo.this, "Error inesperado", Toast.LENGTH_SHORT).show();
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
        SharedPreferences preferencias = getSharedPreferences("InicioSesion", MODE_PRIVATE);
        idUsuarioCreador = preferencias.getInt("id", 0);

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
                        Toast.makeText(SplitNuevo.this, "Error inesperado", Toast.LENGTH_SHORT).show();
                        Log.e("Error", "Error al obtener el split: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Split> call, Throwable t) {
                    Toast.makeText(SplitNuevo.this, "Error de red", Toast.LENGTH_SHORT).show();
                    Log.e("Error", "Error de red: " + t.getMessage());
                }
            });
        } else {

            participantesSinId.clear();
            adapterSinId = new AdaptadorParticipantesSinId(SplitNuevo.this, participantesSinId);
            listViewParticipantes.setAdapter(adapterSinId);

            RepositorioUsuario repositorioUsuario = new RepositorioUsuario();
            repositorioUsuario.obtenerUsuarioPorId(idUsuarioCreador, new Callback<UsuarioDTO>() {
                @Override
                public void onResponse(Call<UsuarioDTO> call, Response<UsuarioDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        UsuarioDTO usuario = response.body();
                        participantesSinId.add(usuario.getNombre());

                        Log.d("Debug", "Usuario encontrado: " + new Gson().toJson(usuario));

                        adapterSinId.notifyDataSetChanged();
                    } else {
                        Log.e("Error", "Error al obtener usuario " + idUsuarioCreador + ": " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<UsuarioDTO> call, Throwable t) {
                    Log.e("Error", "Error: " + t.getMessage());
                }
            });
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
                    Toast.makeText(SplitNuevo.this, "Error inesperado", Toast.LENGTH_SHORT).show();
                    Log.e("Error", "Error al obtener participantes: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Participante>> call, Throwable t) {
                Toast.makeText(SplitNuevo.this, "Error de red", Toast.LENGTH_SHORT).show();
                Log.e("Error", "Error de red: " + t.getMessage());
            }
        });


    }
}

