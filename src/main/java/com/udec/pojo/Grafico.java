/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.pojo;

import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.pie.PieChartModel;
import org.primefaces.model.charts.radar.RadarChartModel;

/**
 *
 * @author Admin
 */
public class Grafico {
    private PieChartModel graficoDatos;
    private RadarChartModel graficoPruebas;
    private LineChartModel lineaAnos;
    private LineChartModel lineaSaber;
    private LineChartModel lineaAnosIndicador;

    public Grafico() {
        graficoPruebas = new RadarChartModel();
        lineaAnos = new LineChartModel();
        lineaAnosIndicador = new LineChartModel();
        lineaSaber=  new LineChartModel();
    }

    public PieChartModel getGraficoDatos() {
        return graficoDatos;
    }

    public void setGraficoDatos(PieChartModel graficoDatos) {
        this.graficoDatos = graficoDatos;
    }

    public RadarChartModel getGraficoPruebas() {
        return graficoPruebas;
    }

    public void setGraficoPruebas(RadarChartModel graficoPruebas) {
        this.graficoPruebas = graficoPruebas;
    }

    public LineChartModel getLineaAnos() {
        return lineaAnos;
    }

    public void setLineaAnos(LineChartModel lineaAnos) {
        this.lineaAnos = lineaAnos;
    }

    public LineChartModel getLineaSaber() {
        return lineaSaber;
    }

    public void setLineaSaber(LineChartModel lineaSaber) {
        this.lineaSaber = lineaSaber;
    }

    public LineChartModel getLineaAnosIndicador() {
        return lineaAnosIndicador;
    }

    public void setLineaAnosIndicador(LineChartModel lineaAnosIndicador) {
        this.lineaAnosIndicador = lineaAnosIndicador;
    }
}
