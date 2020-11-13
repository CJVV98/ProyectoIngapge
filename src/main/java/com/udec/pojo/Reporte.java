/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Admin
 */
public class Reporte {
    private Map<String, List<ResultadosPrimaria>> listados;
    private Map<String, List<DatosPrediccion>> listadoPrediccion;
    private Map<String, Object> listadoPrediccionIndicador;
    private List<ModeloReporte> datosReporte;
    private List<ModeloReporte> datoslineGenero;
    private List<ModeloReporte> datoslineIndicadorAno;
    private List<ModeloReporte> datoslineIndicadorSaber;
    private List<ModeloReporte> radarIndicadores;
    private List<ResultadosPrimaria> resultadosPie;
    private String tituloInicial;
    private String tituloPie;

    public Map<String, Object> getListadoPrediccionIndicador() {
        return listadoPrediccionIndicador;
    }

    public void setListadoPrediccionIndicador(Map<String, Object> listadoPrediccionIndicador) {
        this.listadoPrediccionIndicador = listadoPrediccionIndicador;
    }

    
    public Map<String, List<DatosPrediccion>> getListadoPrediccion() {
        return listadoPrediccion;
    }

    public void setListadoPrediccion(Map<String, List<DatosPrediccion>> listadoPrediccion) {
        this.listadoPrediccion = listadoPrediccion;
    }
    
    public List<ModeloReporte> getDatoslineGenero() {
        return datoslineGenero;
    }

    public void setDatoslineGenero(List<ModeloReporte> datoslineGenero) {
        this.datoslineGenero = datoslineGenero;
    }

    public List<ModeloReporte> getDatoslineIndicadorAno() {
        return datoslineIndicadorAno;
    }

    public void setDatoslineIndicadorAno(List<ModeloReporte> datoslineIndicadorAno) {
        this.datoslineIndicadorAno = datoslineIndicadorAno;
    }

    public List<ModeloReporte> getDatoslineIndicadorSaber() {
        return datoslineIndicadorSaber;
    }

    public void setDatoslineIndicadorSaber(List<ModeloReporte> datoslineIndicadorSaber) {
        this.datoslineIndicadorSaber = datoslineIndicadorSaber;
    }

    public List<ModeloReporte> getRadarIndicadores() {
        return radarIndicadores;
    }

    public void setRadarIndicadores(List<ModeloReporte> radarIndicadores) {
        this.radarIndicadores = radarIndicadores;
    }
    
    public String getTituloPie() {
        return tituloPie;
    }

    public void setTituloPie(String tituloPie) {
        this.tituloPie = tituloPie;
    }
    

    public String getTituloInicial() {
        return tituloInicial;
    }

    public void setTituloInicial(String tituloInicial) {
        this.tituloInicial = tituloInicial;
    }

    public Map<String, List<ResultadosPrimaria>> getListados() {
        return listados;
    }

    public void setListados(Map<String, List<ResultadosPrimaria>> listados) {
        this.listados = listados;
    }

    public List<ModeloReporte> getDatosReporte() {
        return datosReporte;
    }

    public void setDatosReporte(List<ModeloReporte> datosReporte) {
        this.datosReporte = datosReporte;
    }

    public List<ResultadosPrimaria> getResultadosPie() {
        return resultadosPie;
    }

    public void setResultadosPie(List<ResultadosPrimaria> resultadosPie) {
        this.resultadosPie = resultadosPie;
    }

    public Reporte() {
        listados=new HashMap<String, List<ResultadosPrimaria>>();
        datosReporte=new ArrayList<>();
        datoslineGenero=new ArrayList<>();
        datoslineIndicadorAno=new ArrayList<>();
        datoslineIndicadorSaber=new ArrayList<>();
        radarIndicadores=new ArrayList<>();
    }
    
    public Reporte(String dato){
        listadoPrediccion=new HashMap<String, List<DatosPrediccion>>();
        datoslineGenero=new ArrayList<>();
        listadoPrediccionIndicador=new HashMap<String, Object>();
    }
    
 
}
