package com.example.splitup;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Objects;

public class Split extends AppCompatActivity {

    TextView logo;
    Toolbar miToolbar;
    ListView miLista;
    ArrayList<String> lista;
    Button nuevoPago;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split);

        logo = findViewById(R.id.Logo);
        miToolbar= findViewById(R.id.miToolbar);
        miLista = findViewById(R.id.listViewPagos);
        lista = new ArrayList<>();
        nuevoPago = findViewById(R.id.botonNuevosPagos);

        String text = "SplitUp";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4E00CC")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4E00CC")), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        logo.setText(spannable);

        setSupportActionBar(miToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        nuevoPago.setOnClickListener(v -> {
            Intent intent = new Intent(Split.this, SplitNuevo.class);
            startActivity(intent);
        });
    }
}