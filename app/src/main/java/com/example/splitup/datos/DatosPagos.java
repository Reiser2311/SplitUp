package com.example.splitup.datos;

public class DatosPagos {

    private int id;
    private String nombre;
    private int pagadoPor;
    private double importe;

    public DatosPagos(String nombre, int pagadoPor, double importe, int id) {
        this.nombre = nombre;
        this.pagadoPor = pagadoPor;
        this.importe = importe;
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPagadoPor() {
        return pagadoPor;
    }

    public double getImporte() {
        return importe;
    }

    public int getId() {
        return id;
    }
}
