package com.example.splitup;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Estadisticas extends AppCompatActivity {

    private TextView logo;
    private Toolbar miToolbar;
    private BarChart barChart;
    private Spinner spinnerFiltro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        logo = findViewById(R.id.Logo);
        miToolbar= findViewById(R.id.miToolbar);
        barChart = findViewById(R.id.barChart);
        spinnerFiltro = findViewById(R.id.spinnerFiltro);

        String text = "SplitUp";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#601FCD")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#601FCD")), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        logo.setText(spannable);

        setSupportActionBar(miToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filtro = parent.getItemAtPosition(position).toString();
                cargarGrafico(filtro);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void cargarGrafico(String filtro) {
        // Simulamos datos agrupados por fecha. Esto deberías adaptarlo a tus pagos reales.
        HashMap<String, Float> datos = new HashMap<>();
        if (filtro.equals("Dia")) {
            datos.put("11/06", 40f);
            datos.put("12/06", 70f);
            datos.put("13/06", 25f);
        } else if (filtro.equals("Mes")) {
            datos.put("Ene", 120f);
            datos.put("Feb", 90f);
            datos.put("Mar", 60f);
        } else { // Año
            datos.put("2023", 350f);
            datos.put("2024", 500f);
            datos.put("2025", 420f);
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> etiquetas = new ArrayList<>();
        int i = 0;
        for (String clave : datos.keySet()) {
            entries.add(new BarEntry(i, datos.get(clave)));
            etiquetas.add(clave);
            i++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Pagos (€)");
        dataSet.setColor(Color.parseColor("#601FCD"));
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        barChart.setData(barData);
        barChart.setFitBars(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value >= 0 && value < etiquetas.size()) {
                    return etiquetas.get((int) value);
                } else {
                    return "";
                }
            }
        });

        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setTextColor(Color.WHITE);
        yAxisLeft.setDrawGridLines(false);

        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setTextColor(Color.WHITE);
        yAxisRight.setDrawGridLines(false);

        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBorders(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getLegend().setTextColor(Color.WHITE);
        barChart.invalidate(); // ¡Actualiza!
    }
}