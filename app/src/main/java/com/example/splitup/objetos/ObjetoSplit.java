package com.example.splitup.objetos;

import java.util.ArrayList;

public class ObjetoSplit {
    private int id;
    private String nombre;
    private String creadorCorreo;
    private ArrayList<String> participantes;

    // Getters y Setters
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

    public String getCreadorCorreo() {
        return creadorCorreo;
    }

    public void setCreadoCorreo(String creadorCorreo) {
        this.creadorCorreo = creadorCorreo;
    }

    public ArrayList<String> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(ArrayList<String> participantes) {
        this.participantes = participantes;
    }
}
