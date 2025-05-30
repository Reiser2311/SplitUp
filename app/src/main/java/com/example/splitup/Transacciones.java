package com.example.splitup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Transacciones extends AppCompatActivity {

    Toolbar miToolbar;
    TextView logo;
    ListView listViewTransacciones;
    ArrayList<DatosTransacciones> datosTransacciones;
    AdaptadorTransacciones adaptadorTransacciones;
    ArrayList<DatosSaldos> datosSaldos;

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences("Saldos", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

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
            if (diferencia < 0) {
                deudores.add(dato);
            } else if (diferencia > 0) {
                acreedores.add(dato);
            }
        }

        datosTransacciones.clear();
        int i = 0, j = 0;
        while (i < deudores.size() && j < acreedores.size()) {
            double deuda = - (deudores.get(i).getSaldo() - saldoMedio);
            double credito = acreedores.get(j).getSaldo() - saldoMedio;

            double transaccion = Math.min(deuda, credito);

            datosTransacciones.add(new DatosTransacciones(transaccion, acreedores.get(j).getNombre(), deudores.get(i).getNombre(), " paga a "));

            deudores.get(i).setSaldo(deudores.get(i).getSaldo() + transaccion);
            acreedores.get(j).setSaldo(acreedores.get(j).getSaldo() - transaccion);

            if (deudores.get(i).getSaldo() == saldoMedio) {
                i++;
            }

            if (acreedores.get(j).getSaldo() == saldoMedio) {
                j++;
            }
        }

        adaptadorTransacciones.notifyDataSetChanged();
    }




}