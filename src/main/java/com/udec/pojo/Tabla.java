/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.pojo;

/**
 *
 * @author Admin
 */
public class Tabla {
    private String esquema;
    private String nombreTabla;
    private int ano;
    private int cantidad;
    private String prueba;

    public Tabla(String esquema, String tabla) {
        this.esquema = esquema;
        this.nombreTabla = tabla;
    }

    public Tabla(String esquema, int ano) {
        this.esquema = esquema;
        this.ano = ano;
    }

    public Tabla(int ano, int cantidad) {
        this.ano = ano;
        this.cantidad = cantidad;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    
    public String getEsquema() {
        return esquema;
    }

    public void setEsquema(String esquema) {
        this.esquema = esquema;
    }

    public String getNombreTabla() {
        return nombreTabla;
    }

    public void setNombreTabla(String nombreTabla) {
        this.nombreTabla = nombreTabla;
    }

    public String getPrueba() {
        return prueba;
    }

    public void setPrueba(String prueba) {
        this.prueba = prueba;
    }

    
}
