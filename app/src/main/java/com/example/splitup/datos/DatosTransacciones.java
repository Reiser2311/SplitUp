package com.example.splitup.datos;

import androidx.annotation.Nullable;

import java.util.Objects;

public class DatosTransacciones {
    private String textoDeudorTransaccion;
    private String textoAcreedorTransaccion;
    private String textoTransaccion;
    private double importeTransaccion;
    private int idDeudor;
    private int idAcreedor;

    public DatosTransacciones(double importeTransaccion, String textoAcreedorTransaccion,
                              String textoDeudorTransaccion, String textoTransaccion,
                              int idDeudor, int idAcreedor) {
        this.importeTransaccion = importeTransaccion;
        this.textoAcreedorTransaccion = textoAcreedorTransaccion;
        this.textoDeudorTransaccion = textoDeudorTransaccion;
        this.textoTransaccion = textoTransaccion;
        this.idDeudor = idDeudor;
        this.idAcreedor = idAcreedor;

    }

    public double getImporteTransaccion() {
        return importeTransaccion;
    }

    public void setImporteTransaccion(double importeTransaccion) {
        this.importeTransaccion = importeTransaccion;
    }

    public String getTextoAcreedorTransaccion() {
        return textoAcreedorTransaccion;
    }

    public void setTextoAcreedorTransaccion(String textoAcreedorTransaccion) {
        this.textoAcreedorTransaccion = textoAcreedorTransaccion;
    }

    public String getTextoDeudorTransaccion() {
        return textoDeudorTransaccion;
    }

    public void setTextoDeudorTransaccion(String textoDeudorTransaccion) {
        this.textoDeudorTransaccion = textoDeudorTransaccion;
    }

    public String getTextoTransaccion() {
        return textoTransaccion;
    }

    public void setTextoTransaccion(String textoTransaccion) {
        this.textoTransaccion = textoTransaccion;
    }

    public int getIdAcreedor() {
        return idAcreedor;
    }

    public void setIdAcreedor(int idAcreedor) {
        this.idAcreedor = idAcreedor;
    }

    public int getIdDeudor() {
        return idDeudor;
    }

    public void setIdDeudor(int idDeudor) {
        this.idDeudor = idDeudor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(importeTransaccion, idAcreedor, idDeudor, textoAcreedorTransaccion, textoDeudorTransaccion, textoTransaccion);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        DatosTransacciones that = (DatosTransacciones) obj;
        return Double.compare(that.importeTransaccion, importeTransaccion) == 0 &&
                idDeudor == that.idDeudor &&
                idAcreedor == that.idAcreedor &&
                textoAcreedorTransaccion.equals(that.textoAcreedorTransaccion) &&
                textoDeudorTransaccion.equals(that.textoDeudorTransaccion) &&
                textoTransaccion.equals(that.textoTransaccion);
    }
}
