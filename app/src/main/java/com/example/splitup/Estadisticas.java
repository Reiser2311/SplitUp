package com.example.splitup;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.splitup.objetos.Pago;
import com.example.splitup.objetos.Participante;
import com.example.splitup.repositorios.RepositorioPago;
import com.example.splitup.repositorios.RepositorioParticipante;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    @Override
    protected void onResume() {
        super.onResume();




    }

    private void cargarGrafico(String filtro) {
        SharedPreferences prefs = getSharedPreferences("InicioSesion", MODE_PRIVATE);
        int usuarioId = prefs.getInt("id", 0);

        RepositorioParticipante repoParticipante = new RepositorioParticipante();
        repoParticipante.obtenerParticipantesPorUsuario(usuarioId, new Callback<List<Participante>>() {
            @Override
            public void onResponse(Call<List<Participante>> call, Response<List<Participante>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Participante> participantes = response.body();
                    Set<Integer> idsParticipantes = new HashSet<>();
                    for (Participante p : participantes) {
                        idsParticipantes.add(p.getId());
                    }

                    // Metodo real
//                    RepositorioPago repoPago = new RepositorioPago();
//                    repoPago.obtenerTodosPagos(new Callback<List<Pago>>() {
//                        @Override
//                        public void onResponse(Call<List<Pago>> call, Response<List<Pago>> response) {
//                            if (response.isSuccessful() && response.body() != null) {
//                                HashMap<String, Float> datosAgrupados = new HashMap<>();
//
//                                for (Pago pago : response.body()) {
//                                    if (idsParticipantes.contains(pago.getPagadoPor())) {
//                                        String fecha = pago.getFechaCreacion();
//
//                                        String clave;
//                                        switch (filtro) {
//                                            case "Mes":
//                                                // yyyy-MM-dd → obtenemos "MM/yyyy"
//                                                clave = fecha.substring(5, 7) + "/" + fecha.substring(0, 4); // MM/yyyy
//                                                break;
//                                            case "Año":
//                                                // yyyy-MM-dd → obtenemos "yyyy"
//                                                clave = fecha.substring(0, 4); // yyyy
//                                                break;
//                                            default:
//                                                // yyyy-MM-dd → obtenemos "dd/MM"
//                                                clave = fecha.substring(8, 10) + "/" + fecha.substring(5, 7); // dd/MM
//                                                break;
//                                        }
//
//
//                                        float importe = (float) pago.getImporte();
//                                        datosAgrupados.put(clave, datosAgrupados.getOrDefault(clave, 0f) + importe);
//                                    }
//                                }
//
//                                mostrarGrafico(datosAgrupados);
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<List<Pago>> call, Throwable t) {
//                            Log.e("Grafico", "Error al obtener pagos: " + t.getMessage());
//                        }
//                    });

                    //Metodo simulado
                    List<Pago> pagosSimulados = new ArrayList<>();

                    pagosSimulados.add(crearPagoFicticio(25.0, "2025-06-13"));
                    pagosSimulados.add(crearPagoFicticio(40.0, "2025-06-11"));
                    pagosSimulados.add(crearPagoFicticio(70.0, "2025-06-12"));
                    pagosSimulados.add(crearPagoFicticio(90.0, "2025-05-10"));
                    pagosSimulados.add(crearPagoFicticio(110.0, "2024-12-25"));
                    pagosSimulados.add(crearPagoFicticio(60.0, "2023-06-13"));
                    pagosSimulados.add(crearPagoFicticio(35.0, "2025-06-13")); // misma fecha, suma

                    procesarPagosParaGrafico(pagosSimulados, filtro);


                } else {
                    Toast.makeText(Estadisticas.this, "Error inesperado", Toast.LENGTH_SHORT).show();
                    Log.e("Grafico", "No se pudieron obtener los participantes del usuario.");
                }
            }

            @Override
            public void onFailure(Call<List<Participante>> call, Throwable t) {
                Toast.makeText(Estadisticas.this, "Error de red", Toast.LENGTH_SHORT).show();
                Log.e("Grafico", "Error de red: " + t.getMessage());
            }
        });
    }

    private void mostrarGrafico(HashMap<String, Float> datos) {
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
        barChart.invalidate();
    }

    private Pago crearPagoFicticio(double importe, String fecha) {
        Pago pago = new Pago();
        pago.setImporte(importe);
        pago.setFechaCreacion(fecha);
        return pago;
    }

    private void procesarPagosParaGrafico(List<Pago> pagos, String filtro) {
        HashMap<String, Float> datosAgrupados = new HashMap<>();

        for (Pago pago : pagos) {
            String fecha = pago.getFechaCreacion();
            String clave;
            switch (filtro) {
                case "Mes":
                    clave = fecha.substring(5, 7) + "/" + fecha.substring(0, 4);
                    break;
                case "Año":
                    clave = fecha.substring(0, 4);
                    break;
                default:
                    clave = fecha.substring(8, 10) + "/" + fecha.substring(5, 7);
                    break;
            }

            float importeActual = datosAgrupados.getOrDefault(clave, 0f);
            datosAgrupados.put(clave, importeActual + (float) pago.getImporte());
        }

        // Construye y actualiza el gráfico
        List<BarEntry> entries = new ArrayList<>();
        List<String> etiquetas = new ArrayList<>();
        int i = 0;
        for (String clave : datosAgrupados.keySet()) {
            entries.add(new BarEntry(i, datosAgrupados.get(clave)));
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

        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setTextColor(Color.WHITE);
        yAxisRight.setDrawGridLines(false);

        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBorders(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getLegend().setTextColor(Color.WHITE);
        barChart.invalidate();
    }


}