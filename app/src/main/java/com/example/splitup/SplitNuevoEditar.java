package com.example.splitup;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class SplitNuevoEditar extends AppCompatActivity {

    TextView logo;
    Toolbar miToolbar;
    ListView listViewParticipantes;
    Button buttonAñadirParticipante;
    Button buttonCrearSplit;
    EditText edtxtNombreParticipante;
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
            ArrayAdapter<String> adapter = new ArrayAdapter<>(SplitNuevoEditar.this, android.R.layout.simple_list_item_1, participantes);
            listViewParticipantes.setAdapter(adapter);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_nuevo_editar);

        logo = findViewById(R.id.Logo);
        miToolbar= findViewById(R.id.miToolbar);
        listViewParticipantes = findViewById(R.id.listViewParticipantes);
        buttonAñadirParticipante = findViewById(R.id.buttonAñadirParticipante);
        buttonCrearSplit = findViewById(R.id.buttonCrearSplit);
        edtxtNombreParticipante = findViewById(R.id.edtxtNombreParticipante);

        String text = "SplitUp";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4E00CC")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4E00CC")), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        logo.setText(spannable);

        participantes = new ArrayList<>();
        registerForContextMenu(listViewParticipantes);

        setSupportActionBar(miToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        buttonAñadirParticipante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nombreParticipante = edtxtNombreParticipante.getText().toString();

                if (!nombreParticipante.isEmpty()) {
                    listViewParticipantes.setVisibility(View.VISIBLE);
                    participantes.add(nombreParticipante);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(SplitNuevoEditar.this, android.R.layout.simple_list_item_1, participantes);
                    listViewParticipantes.setAdapter(adapter);
                    edtxtNombreParticipante.setText("");
                }
            }
        });

    }
}

