package com.example.splitup.datos;

public class DatosTransacciones {
    private String textoDeudorTransaccion;
    private String textoAcreedorTransaccion;
    private String textoTransaccion;
    private double importeTransaccion;

    public DatosTransacciones(double importeTransaccion, String textoAcreedorTransaccion, String textoDeudorTransaccion, String textoTransaccion) {
        this.importeTransaccion = importeTransaccion;
        this.textoAcreedorTransaccion = textoAcreedorTransaccion;
        this.textoDeudorTransaccion = textoDeudorTransaccion;
        this.textoTransaccion = textoTransaccion;
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
}
