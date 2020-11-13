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
public class ModeloReporte {
     private String label;
     private double [] puntaje;
     private String [] indicador;
     private String [] respuesta;
     private String [] series;


     
    public ModeloReporte(int tamano) {
        if(tamano>3)
            indicador=new String[tamano];
        respuesta=new String[tamano];
        series=new String[tamano];
        puntaje=new double[tamano];
    }
    
    public ModeloReporte() {
    }
     
    public String[] getSeries() {
        return series;
    }

    public void setSeries(String[] series) {
        this.series = series;
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double[] getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(double[] puntaje) {
        this.puntaje = puntaje;
    }

    public String[] getIndicador() {
        return indicador;
    }

    public void setIndicador(String[] indicador) {
        this.indicador = indicador;
    }

    public String[] getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String[] respuesta) {
        this.respuesta = respuesta;
    } 
}
