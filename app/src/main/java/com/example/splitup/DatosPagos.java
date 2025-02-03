package com.example.splitup;

public class DatosPagos {

    private int id;
    private String nombre;
    private String pagadoPor;
    private double importe;

    public DatosPagos(String nombre, String pagadoPor, double importe, int id) {
        this.nombre = nombre;
        this.pagadoPor = pagadoPor;
        this.importe = importe;
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPagadoPor() {
        return pagadoPor;
    }

    public double getImporte() {
        return importe;
    }

    public int getId() {
        return id;
    }
}
