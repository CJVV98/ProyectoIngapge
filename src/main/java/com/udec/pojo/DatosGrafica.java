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
public class DatosGrafica {
    String prueba;
    String anio;
    String label;
    String indicador;
    String centroide;
    String cluster;
    String atributos;

    public DatosGrafica(String prueba, String anio, String label, String indicador, String centroide, String cluster, String atributos) {
        this.prueba = prueba;
        this.anio = anio;
        this.label = label;
        this.indicador = indicador;
        this.centroide = centroide;
        this.cluster = cluster;
        this.atributos = atributos;
    }

    public DatosGrafica(String centroide, String cluster, String atributos) {
        this.centroide = centroide;
        this.cluster = cluster;
        this.atributos = atributos;
    }

    public String getPrueba() {
        return prueba;
    }

    public void setPrueba(String prueba) {
        this.prueba = prueba;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIndicador() {
        return indicador;
    }

    public void setIndicador(String indicador) {
        this.indicador = indicador;
    }

    public String getCentroide() {
        return centroide;
    }

    public void setCentroide(String centroide) {
        this.centroide = centroide;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getAtributos() {
        return atributos;
    }

    public void setAtributos(String atributos) {
        this.atributos = atributos;
    }

    
}
