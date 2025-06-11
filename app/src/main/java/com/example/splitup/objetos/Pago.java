package com.example.splitup.objetos;

import com.google.gson.annotations.SerializedName;

public class Pago {
    private int id;
    private double importe;
    private String titulo;
    private int pagadoPor;
    private Split split;
    @SerializedName("fechaCreacion")
    private String fechaCreacion;

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getPagadoPor() {
        return pagadoPor;
    }

    public void setPagadoPor(int pagadoPor) {
        this.pagadoPor = pagadoPor;
    }

    public Split getSplit() {
        return split;
    }

    public void setSplit(Split split) {
        this.split = split;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
