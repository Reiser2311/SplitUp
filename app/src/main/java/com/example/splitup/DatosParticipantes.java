package com.example.splitup;

public class DatosParticipantes {
    int id;
    String nombre;
    String correo;

    public DatosParticipantes(String correo, int id, String nombre) {
        this.correo = correo;
        this.id = id;
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
