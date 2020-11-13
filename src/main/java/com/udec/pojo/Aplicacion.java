/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.pojo;

/**
 *
 * @author Angie Paola Manrique
 * @author Alisson Catalina Celeita
 */
public class Aplicacion {

    private String nucleo;
    private int registro;
    private int puntaje;
    private float porcentaje;

    public Aplicacion() {
    }

    public Aplicacion(String nucleo, int puntaje, float porcentaje) {
        this.nucleo = nucleo;
        this.puntaje = puntaje;
        this.porcentaje = porcentaje;
    }

    public void setPorcentaje(float porcentaje) {
        this.porcentaje = porcentaje;
    }

    public float getPorcentaje() {
        return porcentaje;
    }

    public String getNucleo() {
        return nucleo;
    }

    public void setNucleo(String nucleo) {
        this.nucleo = nucleo;
    }

    public int getRegistro() {
        return registro;
    }

    public void setRegistro(int registro) {
        this.registro = registro;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }
}
