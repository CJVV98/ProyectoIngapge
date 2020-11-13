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
public class DatosPrediccion {
     private String genero; 
     private String indicador;
     private String descripcion;
     private int maxPuntaje;
     private int minPuntaje;
     private double probabilidad;
     private String prueba;
     private String nucleo;
     
     private int puntaje;
     private String datoIndicador;

    public String getPrueba() {
        return prueba;
    }

    public void setPrueba(String prueba) {
        this.prueba = prueba;
    }

    public String getNucleo() {
        return nucleo;
    }

    public void setNucleo(String nucleo) {
        this.nucleo = nucleo;
    }
     
    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    public String getDatoIndicador() {
        return datoIndicador;
    }

    public void setDatoIndicador(String datoIndicador) {
        this.datoIndicador = datoIndicador;
    }
     
    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getIndicador() {
        return indicador;
    }

    public void setIndicador(String indicador) {
        this.indicador = indicador;
    }

    public int getMaxPuntaje() {
        return maxPuntaje;
    }

    public void setMaxPuntaje(int maxPuntaje) {
        this.maxPuntaje = maxPuntaje;
    }

    public int getMinPuntaje() {
        return minPuntaje;
    }

    public void setMinPuntaje(int minPuntaje) {
        this.minPuntaje = minPuntaje;
    }

    public double getProbabilidad() {
        return probabilidad;
    }

    public void setProbabilidad(double probabilidad) {
        this.probabilidad = probabilidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    
    public DatosPrediccion(int puntaje, String datoIndicador) {
        this.puntaje = puntaje;
        this.datoIndicador = datoIndicador;
    }

    public DatosPrediccion(String genero, String indicador, String descripcion, int maxPuntaje, int minPuntaje, double probabilidad, String prueba, String nucleo) {
        this.genero = genero;
        this.indicador = indicador;
        this.descripcion = descripcion;
        this.maxPuntaje = maxPuntaje;
        this.minPuntaje = minPuntaje;
        this.probabilidad = probabilidad;
        this.prueba = prueba;
        this.nucleo = nucleo;
    }

    public DatosPrediccion() {
    }

    
    
     
     
     
}
