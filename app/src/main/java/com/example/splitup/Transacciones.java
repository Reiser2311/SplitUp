package com.example.splitup;

import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.splitup.adaptadores.AdaptadorTransacciones;
import com.example.splitup.datos.DatosSaldos;
import com.example.splitup.datos.DatosTransacciones;
import com.example.splitup.objetos.Pago;
import com.example.splitup.objetos.ParticipantePago;
import com.example.splitup.objetos.Split;
import com.example.splitup.repositorios.RepositorioPago;
import com.example.splitup.repositorios.RepositorioParticipantePago;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Transacciones extends AppCompatActivity {

    private Toolbar miToolbar;
    private TextView logo;
    private ListView listViewTransacciones;
    private ArrayList<DatosTransacciones> datosTransacciones;
    private AdaptadorTransacciones adaptadorTransacciones;
    private ArrayList<DatosSaldos> datosSaldos;

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences("Saldos", MODE_PRIVATE);

        String json = preferences.getString("saldos", null);

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<DatosSaldos>>() {}.getType();

        datosSaldos = gson.fromJson(json, type);

        if (datosSaldos != null) {
            balancearSaldos(datosSaldos);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        inflater.inflate(R.menu.menu_mandar_correo, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int posicion = info.position;

        AdaptadorTransacciones adaptadorTransacciones = (AdaptadorTransacciones) listViewTransacciones.getAdapter();

        DatosTransacciones datos = adaptadorTransacciones.getItem(posicion);

        int id = item.getItemId();

        if (id == R.id.mandarPorCorreo) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Transacciones.this);
            builder.setTitle("Enviar por correo");
            builder.setMessage("Ingrese el correo electrónico del destinatario:");

            final EditText input = new EditText(Transacciones.this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("Enviar", (dialog, which) -> {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{input.getText().toString()});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Transacción de SplitUp");
                intent.putExtra(Intent.EXTRA_TEXT, "Hola " + datos.getTextoDeudorTransaccion() +
                        ", \nEste es un recordatorio de SplitUp sobre una transaccion pendiente\n" +
                        "Según nuestros registros, tienes un pago pendiente de " + datos.getImporteTransaccion() + "€ a " + datos.getTextoAcreedorTransaccion() + " aun por completar.\n" +
                        "Si ya realizaste el pago, puedes ignorar este mensaje. De lo contrario, te recomendamos gestionarlo lo antes posible.\n\n" +
                        "Este mensaje ha sido generado automáticamente. Por favor, no respondas a este correo.");

                try {
                    startActivity(Intent.createChooser(intent, "Enviar correo..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this, "No hay aplicaciones de correo instaladas", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_transacciones);

        miToolbar= findViewById(R.id.miToolbar);
        logo = findViewById(R.id.Logo);
        listViewTransacciones = findViewById(R.id.listViewTransacciones);
        datosTransacciones = new ArrayList<>();
        adaptadorTransacciones = new AdaptadorTransacciones(Transacciones.this, datosTransacciones);
        listViewTransacciones.setAdapter(adaptadorTransacciones);

        String text = "SplitUp";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#601FCD")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#601FCD")), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        logo.setText(spannable);

        setSupportActionBar(miToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        registerForContextMenu(listViewTransacciones);

    }

    public void balancearSaldos(ArrayList<DatosSaldos> datos) {
        double totalSaldo = 0;
        int numPersonas = datos.size();

        for (DatosSaldos dato : datos) {
            totalSaldo += dato.getSaldo();
        }

        double saldoMedio = totalSaldo / numPersonas;

        List<DatosSaldos> deudores = new ArrayList<>();
        List<DatosSaldos> acreedores = new ArrayList<>();

        for (DatosSaldos dato : datos) {
            double diferencia = dato.getSaldo() - saldoMedio;
            // Redondeo de diferencia para evitar errores de precisión flotante
            diferencia = new BigDecimal(diferencia).setScale(2, RoundingMode.HALF_UP).doubleValue();

            if (diferencia < 0) {
                deudores.add(new DatosSaldos(dato.getId(), dato.getNombre(), dato.getSaldo()));
            } else if (diferencia > 0) {
                acreedores.add(new DatosSaldos(dato.getId(), dato.getNombre(), dato.getSaldo()));
            }
        }

        datosTransacciones.clear();
        int i = 0, j = 0;

        while (i < deudores.size() && j < acreedores.size()) {
            double deuda = new BigDecimal(saldoMedio - deudores.get(i).getSaldo())
                    .setScale(2, RoundingMode.HALF_UP).doubleValue();
            double credito = new BigDecimal(acreedores.get(j).getSaldo() - saldoMedio)
                    .setScale(2, RoundingMode.HALF_UP).doubleValue();

            double transaccion = Math.min(deuda, credito);
            transaccion = new BigDecimal(transaccion).setScale(2, RoundingMode.HALF_UP).doubleValue();

            datosTransacciones.add(new DatosTransacciones(
                    transaccion,
                    acreedores.get(j).getNombre(),
                    deudores.get(i).getNombre(),
                    " paga a ",
                    deudores.get(i).getId(),
                    acreedores.get(j).getId()
            ));

            deudores.get(i).setSaldo(
                    new BigDecimal(deudores.get(i).getSaldo() + transaccion)
                            .setScale(2, RoundingMode.HALF_UP).doubleValue());

            acreedores.get(j).setSaldo(
                    new BigDecimal(acreedores.get(j).getSaldo() - transaccion)
                            .setScale(2, RoundingMode.HALF_UP).doubleValue());

            if (Math.abs(deudores.get(i).getSaldo() - saldoMedio) < 0.01) {
                i++;
            }

            if (Math.abs(acreedores.get(j).getSaldo() - saldoMedio) < 0.01) {
                j++;
            }
        }

        for (DatosSaldos saldo : datosSaldos) {
            if (Math.abs(saldo.getSaldo()) < 0.01) {
                saldo.setSaldo(0.0);
            }
        }

        adaptadorTransacciones.notifyDataSetChanged();
    }

    public void aplicarTransaccionPagada(DatosTransacciones transaccion) {
        RepositorioPago repositorioPago = new RepositorioPago();
        RepositorioParticipantePago repositorioPP = new RepositorioParticipantePago();

        SharedPreferences preferences = getSharedPreferences("SplitActivo", MODE_PRIVATE);
        int idSplit = preferences.getInt("idSplit", 0);

        Pago pago = new Pago();
        pago.setImporte(transaccion.getImporteTransaccion() * -1);
        pago.setPagadoPor(transaccion.getIdAcreedor()); // El acreedor paga
        pago.setTitulo("Reembolso de " + transaccion.getTextoDeudorTransaccion());

        Split split = new Split();
        split.setId(idSplit);
        pago.setSplit(split);

        repositorioPago.crearPago(pago, new Callback<Pago>() {
            @Override
            public void onResponse(Call<Pago> call, Response<Pago> response) {
                if (response.isSuccessful()) {
                    Pago pagoCreado = response.body();

                    // Ahora se asocia el DEUDOR al pago creado
                    ParticipantePago pp = new ParticipantePago(transaccion.getIdDeudor(), pagoCreado.getId());
                    RepositorioParticipantePago repositorioPP = new RepositorioParticipantePago();

                    repositorioPP.crearRelacion(pp, new Callback<ParticipantePago>() {
                        @Override
                        public void onResponse(Call<ParticipantePago> call, Response<ParticipantePago> response) {
                            if (response.isSuccessful()) {
                                Log.d("Transacciones", "Pago OK, se asignó el deudor");
                                datosTransacciones.remove(transaccion);
                                adaptadorTransacciones.notifyDataSetChanged();
                            } else {
                                Toast.makeText(Transacciones.this, "Error inesperado", Toast.LENGTH_SHORT).show();
                                Log.e("Transacciones", "Error al asignar el deudor: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<ParticipantePago> call, Throwable t) {
                            Toast.makeText(Transacciones.this, "Error de red", Toast.LENGTH_SHORT).show();
                            Log.e("Transacciones", "Error de red al asignar el deudor: " + t.getMessage());
                        }
                    });

                } else {
                    Toast.makeText(Transacciones.this, "Error inesperado", Toast.LENGTH_SHORT).show();
                    Log.e("Transacciones", "Error al crear el pago: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Pago> call, Throwable t) {
                Toast.makeText(Transacciones.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }








}