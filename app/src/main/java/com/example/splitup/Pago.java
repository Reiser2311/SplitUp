package com.example.splitup;

public class Pago {
    private int id;
    private double cantidad;
    private String descripcion;
    private int splitId;

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getSplitId() {
        return splitId;
    }

    public void setSplitId(int splitId) {
        this.splitId = splitId;
    }

    public int getPagadorId() {
        return pagadorId;
    }

    public void setPagadorId(int pagadorId) {
        this.pagadorId = pagadorId;
    }
}
