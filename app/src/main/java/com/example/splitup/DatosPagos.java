package com.example.splitup;

public class DatosPagos {

    private String nombre;
    private String pagadoPor;
    private String gasto;

    public DatosPagos(String nombre, String pagadoPor, String gasto) {
        this.nombre = nombre;
        this.pagadoPor = pagadoPor;
        this.gasto = gasto;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPagadoPor() {
        return pagadoPor;
    }

    public String getGasto() {
        return gasto;
    }
}
