/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.controller;

import com.udec.pojo.Indicador;
import com.udec.pojo.Reporte;
import com.udec.pojo.ResultadosPrimaria;
import com.udec.pojo.Tabla;
import com.udec.repositorio.RepositorioResultados;
import com.udec.repositorio.RepositorioTablas;
import com.udec.utilitarios.AnalisisPruebas;
import com.udec.utilitarios.ConfiguracionReporte;
import com.udec.utilitarios.ManejoArchivo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.PrimeFaces;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.title.Title;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;
import weka.core.Instances;

/**
 * Clase encargada del analisis de las pruebas saber tercero
 * @author Corin Viracacha
 */
@ManagedBean(name = "saberTercero")
@ViewScoped
public class SaberTercero implements Serializable {

    private static final String SABER = "saber3";
    private String indicador;
    private boolean contenido;
    private boolean contenidoGrafico;
    private transient Reporte reporte;
    private transient List<Integer> anos;
    private int ano;
    private transient List<Indicador> listadoInfoAno;
    private RepositorioResultados repositorio;
    private PieChartModel graficoDatos;
    private LineChartModel lineaAnos;
    private Instances datos;
    private transient List<String> colores;
    private int color = 0;
    private AnalisisPruebas analisisPruebas;

    @PostConstruct
    public void init() {
        reporte = new Reporte();
        listadoInfoAno = new ArrayList<>();
        repositorio = new RepositorioResultados();
        analisisPruebas = new AnalisisPruebas();
        cargarAnos();
        crearGrafica();
    }

    /**
     * Metodo si existen resultados cargados para ese indicador, de lo contrario
     * crea los resultados
     */
    public void calcularResultados() {
        color = 0;
        graficoDatos = new PieChartModel();
        List<ResultadosPrimaria> resultados = new ArrayList<>();
        resultados = repositorio.obtenerResultados("estu_genero", indicador, SABER, ano);
        if (resultados.isEmpty()) {
            crearResultados();
        } else {
            actualizarModelo(resultados);
        }
        analizarAnosCargados(ano, indicador);

    }

    /**
     * Metodo para crear los resultados
     */
    public void crearResultados() {
        List<ResultadosPrimaria> resultados = null;
        resultados = analisisPruebas.crearResultados(indicador, ano, SABER);
        actualizarModelo(resultados);
    }

    /**
     * Metodo para inicializar graficas
     */
    private void crearGrafica() {
        colores = new ManejoArchivo().obtener();
        graficoDatos = new PieChartModel();
    }

    /**
     * Metodo para actualizar el grafico de la torta
     * @param resultados listado de resultados consultados
     */
    private void actualizarModelo(List<ResultadosPrimaria> resultados) {
        contenido = true;
        PieChartDataSet dataSet = new PieChartDataSet();
        List<Number> valores = new ArrayList<>();
        List<String> indicadores = new ArrayList<>();
        this.reporte.getListados().put("pieIndicador", resultados);
        for (ResultadosPrimaria resultado : resultados) {
            valores.add(resultado.getPuntaje());
            indicadores.add(resultado.getIndicador());
        }
        dataSet.setData(valores);
        ChartData datosTorta = new ChartData();
        List<String> bgColors = new ArrayList<>();
        bgColors.add("rgb(255, 99, 132)");
        bgColors.add("rgb(54, 162, 235)");
        dataSet.setBackgroundColor(bgColors);
        datosTorta.addChartDataSet(dataSet);
        datosTorta.setLabels(indicadores);
        graficoDatos.setData(datosTorta);
    }

    /**
     * Metodo encargado de cargar los años, sobre los cuales se puede realizar
     * el analisis
     */
    private void cargarAnos() {
        anos = new ArrayList<>();
        List<Tabla> anosCargados = new RepositorioTablas().cargarAnosPresentacion(SABER);
        for (Tabla anoPresentacion : anosCargados) {
            if (anoPresentacion.getCantidad() == 1) {
                anos.add(anoPresentacion.getAno());
            }
        }
    }

    /**
     * Metodo usado para analizar los años cargados
     * @param ano, corresponde al año seleccionado por el usuario
     * @param indicador, corresponde al indicador seleccionado por el usuario
     */
    private void analizarAnosCargados(int ano, String indicador) {
        List<Tabla> anosCargados = new RepositorioResultados().cargarAnosAnalizado("saber3");
        for (Tabla anoPresentacion : anosCargados) {
            if (ano == anoPresentacion.getAno()) {
                cargarIndicadores(indicador);
                contenidoGrafico = true;
                break;
            }
        }
    }

    /**
     * Metodo que se encarga de consultar indicadores de acuerdo a los puntajes
     * @param ano corresponde al año a analizar
     * @param indicador corresponde al indicador a analizar
     */
    private void cargarIndicadores(String indicador) {
        List<ResultadosPrimaria> resultadosHombres = new ArrayList<>();
        List<ResultadosPrimaria> resultadosMujeres = new ArrayList<>();
        List<ResultadosPrimaria> resultados = new RepositorioResultados().cargarBrechaIndicador("saber3", indicador);
        for (ResultadosPrimaria resultado : resultados) {
            Double puntaje = 0.0;
            String[] respuesta = resultado.getRespuesta().split(",");
            if (resultado.getAno() > 2016) {
                puntaje = (double) Math.round((Double.valueOf(respuesta[1]) / 100) * 1000d) / 1000d;
            } else {
                puntaje = (double) Math.round(Double.valueOf(respuesta[1]) * 1000d) / 1000d;
            }
            if (respuesta[0].contains("Hombre")) {
                resultadosHombres.add(new ResultadosPrimaria(puntaje, resultado.getAno()));
            } else {
                resultadosMujeres.add(new ResultadosPrimaria(puntaje, resultado.getAno()));
            }
        }
        this.reporte.getListados().put("lineAnios_Hombre", resultadosHombres);
        this.reporte.getListados().put("lineAnios_Mujer", resultadosMujeres);
        crearGraficaLineal(resultadosHombres, resultadosMujeres);
    }

    /**
     * Metodo para crear Grafica Lineal
     * @param resultadosHombres listado de resultados por hombre
     * @param resultadosMujeres listado de resultados por mujer
     */
    private void crearGraficaLineal(List<ResultadosPrimaria> resultadosHombres, List<ResultadosPrimaria> resultadosMujeres) {
        lineaAnos = new LineChartModel();
        ChartData data = new ChartData();
        data.addChartDataSet(cargarDataSetGraficaLineal(resultadosHombres, "Hombre"));
        data.addChartDataSet(cargarDataSetGraficaLineal(resultadosMujeres, "Mujer"));
        List<String> anosCargados = new ArrayList<>();
        for (ResultadosPrimaria resultados : resultadosHombres) {
            anosCargados.add(String.valueOf(resultados.getAno()));
        }
        data.setLabels(anosCargados);
        LineChartOptions opciones = new LineChartOptions();
        Title titulo = new Title();
        titulo.setDisplay(true);
        titulo.setText("Análisis de puntajes");
        opciones.setTitle(titulo);
        lineaAnos.setOptions(opciones);
        lineaAnos.setData(data);
    }

    /**
     * Crear dataSet para la grafica de analisis por años
     * @param resultadosGenero listado de resultados consultados
     * @param genero genero a analzar
     * @return dataSet
     */
    private LineChartDataSet cargarDataSetGraficaLineal(List<ResultadosPrimaria> resultadosGenero, String genero) {
        LineChartDataSet dataSet = new LineChartDataSet();
        List<Object> valores = new ArrayList<>();
        for (ResultadosPrimaria resultados : resultadosGenero) {
            valores.add(resultados.getPuntaje());
        }
        dataSet.setData(valores);
        dataSet.setFill(false);
        dataSet.setLabel(genero);
        dataSet.setBorderColor("rgb(" + colores.get(color) + ")");
        color++;
        dataSet.setLineTension(0.1);
        return dataSet;
    }
    /**
     * Metodo para cargar dialogo
     */
    public void cargarDialogo() {
        PrimeFaces.current().executeScript("PF('dlg').show();");
    }
    /**
     * Metodo usado para generar reportes
     */
    public void generarReporte() {
        String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reportes/ReporteResultadosSaberTercero.jasper");
        reporte.setTituloInicial("TERCERO");
        reporte.setTituloPie(indicador);
        new ConfiguracionReporte().cargarResultados(reporte, path, indicador);

    }
    /**
     * Metodo para obtener la descripcion del indicador
     * @param datosIndicador, nombre del indicador
     * @return descripción del indicador
     */
    public String extraerIndicador(String datosIndicador) {
        switch (datosIndicador) {
            case "punt_lenguaje":
                return "Puntaje en Lenguaje";
            case "punt_matematicas":
                return "Puntaje en Matemáticas";
            default:
                return "";
        }
    }

    public List<String> getColores() {
        return colores;
    }

    public void setColores(List<String> colores) {
        this.colores = colores;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public LineChartModel getLineaAnos() {
        return lineaAnos;
    }

    public void setLineaAnos(LineChartModel lineaAnos) {
        this.lineaAnos = lineaAnos;
    }

    public boolean isContenidoGrafico() {
        return contenidoGrafico;
    }

    public void setContenidoGrafico(boolean contenidoGrafico) {
        this.contenidoGrafico = contenidoGrafico;
    }

    public boolean isContenido() {
        return contenido;
    }

    public void setContenido(boolean contenido) {
        this.contenido = contenido;
    }

    public List<Integer> getAnos() {
        return anos;
    }

    public void setAnos(List<Integer> anos) {
        this.anos = anos;
    }

    public List<Indicador> getListadoInfoAno() {
        return listadoInfoAno;
    }

    public void setListadoInfoAno(List<Indicador> listadoInfoAno) {
        this.listadoInfoAno = listadoInfoAno;
    }

    public RepositorioResultados getRepositorio() {
        return repositorio;
    }

    public void setRepositorio(RepositorioResultados repositorio) {
        this.repositorio = repositorio;
    }

    public Instances getDatos() {
        return datos;
    }

    public void setDatos(Instances datos) {
        this.datos = datos;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getIndicador() {
        return indicador;
    }

    public void setIndicador(String indicador) {
        this.indicador = indicador;
    }

    public PieChartModel getGraficoDatos() {
        return graficoDatos;
    }

    public void setGraficoDatos(PieChartModel graficoDatos) {
        this.graficoDatos = graficoDatos;
    }
}
