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
public class ResultadosPrimaria {
    private String genero;
    private String indicador;
    private Double puntaje;
    private int ano;
    private String respuesta;

    public ResultadosPrimaria() {
    }

    public ResultadosPrimaria(Double puntaje, int ano) {
        this.puntaje = puntaje;
        this.ano = ano;
    }
    
    
    public ResultadosPrimaria(Double puntaje, int ano, String indicador) {
        this.puntaje = puntaje;
        this.ano = ano;
        this.indicador=indicador;
    }
    
    public ResultadosPrimaria( int ano, double puntaje, String respuesta) {
        this.puntaje = puntaje;
        this.ano = ano;
        this.respuesta=respuesta;
    }

    public ResultadosPrimaria(String genero, String indicador) {
        this.genero = genero;
        this.indicador = indicador;
    }

    public ResultadosPrimaria(String indicador, Double puntaje) {
        this.indicador = indicador;
        this.puntaje = puntaje;
    }
    
    public ResultadosPrimaria(String indicador, Double puntaje, String respuesta) {
        this.indicador = indicador;
        this.puntaje = puntaje;
        this.respuesta=respuesta;
    }

    public ResultadosPrimaria(String indicador, int ano) {
        this.indicador = indicador;
        this.ano = ano;
    }

    public ResultadosPrimaria(int ano, String respuesta) {
        this.ano = ano;
        this.respuesta = respuesta;
    }
    
    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
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

    public Double getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(Double puntaje) {
        this.puntaje = puntaje;
    }    
}    

