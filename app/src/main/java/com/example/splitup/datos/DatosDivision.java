package com.example.splitup.datos;

public class DatosDivision {
    int id;
    boolean seleccionado;
    String nombre;

    public DatosDivision(boolean seleccionado, String nombre, int id) {
        this.seleccionado = seleccionado;
        this.nombre = nombre;
        this.id = id;

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
