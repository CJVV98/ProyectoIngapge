/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.controller;

import com.udec.pojo.DatosPrediccion;
import com.udec.pojo.Reporte;
import com.udec.repositorio.RepositorioResultados;
import com.udec.utilitarios.ConfiguracionReporte;
import com.udec.utilitarios.ManejoArchivo;
import com.udec.utilitarios.ProcesoAlgoritmoPredictivo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import org.primefaces.PrimeFaces;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.donut.DonutChartDataSet;
import org.primefaces.model.charts.donut.DonutChartModel;
import org.primefaces.model.charts.donut.DonutChartOptions;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.title.Title;

/**
 * Clase encargada de realizar el analisis predictivo de las pruebas basico
 *
 * @author Corin Viracacha
 */
@ManagedBean(name = "analisisPredictivoBasico")
@ViewScoped
public class AnalisisPredictivoBasico implements Serializable {

    private DonutChartModel graficaIndicador;
    private BarChartModel graficaGenero;
    private String[] filtroIndicador;
    private String[] filtroGenero;
    private BarChartModel graficaPredicciones;
    private transient List<String> indicadores;
    private String nucleoTematico;
    private String infoIndicador;
    private boolean mostrarIndicador;
    private boolean mostrarGenero;
    private boolean mostrarPuntaje;
    private boolean mostrarGraficas;
    private int filtroMinimo = 0;
    private int filtroMaximo = 500;
    private String prueba;
    private String[] indicadoresConsulta = {"fami_tieneinternet", "fami_educacionmadre", "fami_educacionpadre", "fami_tienecomputador", "fami_tieneconsolavideojuegos"};
    private transient List<DatosPrediccion> listadoGrafica = new ArrayList<>();
    private transient List<DatosPrediccion> listaConsulta = new ArrayList<>();
    private transient List<DatosPrediccion> listaIndicador = new ArrayList<>();
    private transient List<String> colores;
    private int color = 0;

    @PostConstruct
    public void iniciarVista() {
        colores = new ManejoArchivo().obtener();
        graficaIndicador = new DonutChartModel();
        graficaPredicciones = new BarChartModel();
        graficaGenero = new BarChartModel();
        indicadores = new ArrayList<>();
    }

    /**
     * Metodo encargado de realizar la inicializacion y de ordenar la carga de
     * datos
     *
     * @param e evento
     */
    public void cargarPrueba(ValueChangeEvent e) {
        color = 1;
        try {
            if (prueba == null || prueba == "") {
                prueba = "saber5";
            }
            listaConsulta = new ArrayList<>();
            listadoGrafica = new ArrayList<>();
            RepositorioResultados repository = new RepositorioResultados();
            nucleoTematico = e.getNewValue().toString();
            mostrarGraficas = true;
            new ProcesoAlgoritmoPredictivo().iniciarAnalisis(nucleoTematico, prueba);
            graficaGenero.setData(cargarData(repository));
            indicadores = new ArrayList<>();
            indicadores = Arrays.asList(indicadoresConsulta);
        } catch (Exception ex) {
            System.out.println("Error De AJAX");
        }
    }

    /**
     * Metodo encargado de cargar la gráfica del indicador
     *
     * @param datosPrediccion responde al listado de predicciones
     * @param genero, del estudiante
     * @param indicadoresConsulta, indicador a consulta
     * @return grafica de barras del filtro a consultar
     */
    private BarChartDataSet cargarGraficaIndicador(List<DatosPrediccion> datosPrediccion, String genero, String[] indicadoresConsulta) {
        List<DatosPrediccion> datosIndicador = new ArrayList<>();
        String primerIndicador = "";
        for (int i = 0; i < datosPrediccion.size(); i++) {
            if (!primerIndicador.equals(datosPrediccion.get(i).getIndicador())) {
                primerIndicador = datosPrediccion.get(i).getIndicador();
                datosIndicador.add(datosPrediccion.get(i));
                listadoGrafica.add(datosPrediccion.get(i));
            }
        }
        return cargarDataSet(datosIndicador, genero, indicadoresConsulta, 1);
    }

    /**
     * Metodo encargado de generar data set y de asignar propiedades de estilo
     *
     * @param datosIndicador listado de los indicadores existentes
     * @param titulo titulo de la grafica
     * @param indicadorConsulta Se refiere al indicador filtro
     * @param tipoConsulta Se refiere al tipo de consulta a realizar
     * @return grafica del indicador y sus probabilidades
     */
    private BarChartDataSet cargarDataSet(List<DatosPrediccion> datosIndicador, String titulo, String[] indicadorConsulta, int tipoConsulta) {
        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel(titulo);
        barDataSet.setBackgroundColor("rgba(" + colores.get(color) + ", 0.6");
        barDataSet.setBorderColor("rgb(" + colores.get(color) + ")");
        barDataSet.setHoverBackgroundColor("rgba(" + colores.get(color) + ", 0.8");
        color++;
        barDataSet.setBorderWidth(1);
        barDataSet.setData(obtenerValores(indicadorConsulta, datosIndicador, tipoConsulta));
        return barDataSet;
    }

    /**
     * Metodo encargado de asignar las propiedades del chartData
     *
     * @param repository clase encargada de conexion a base de datos
     * @return la Data del chart
     */
    private ChartData cargarData(RepositorioResultados repository) {
        List<DatosPrediccion> datosPredPrimerGen = new ArrayList<>();
        List<DatosPrediccion> datosPredSegundoGen = new ArrayList<>();
        ChartData data = new ChartData();
        datosPredPrimerGen = repository.consultarResultadosPred(nucleoTematico, prueba, "Mujer");
        datosPredSegundoGen = repository.consultarResultadosPred(nucleoTematico, prueba, "Hombre");
        data.addChartDataSet(cargarGraficaIndicador(datosPredPrimerGen, "Mujer", indicadoresConsulta));
        data.addChartDataSet(cargarGraficaIndicador(datosPredSegundoGen, "Hombre", indicadoresConsulta));
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < indicadoresConsulta.length; i++) {
            labels.add(extraerIndicador(indicadoresConsulta[i]));
        }
        data.setLabels(labels);
        return data;
    }

    /**
     * Metodo encargada de filtrar datos
     */
    public void aplicarFiltros() {
        color = 1;
        listaConsulta.clear();
        listadoGrafica.clear();
        graficaPredicciones = new BarChartModel();
        ChartData data = new ChartData();
        listadoGrafica.clear();
        List<String> labels = new ArrayList<>();
        if (filtroGenero.length == 0) {
            labels.add("Hombre");
            labels.add("Mujer");
        } else {
            labels = Arrays.asList(filtroGenero);
        }
        if (filtroIndicador.length == 0) {
            data = obtenerDataSet(indicadoresConsulta, labels, data);
        } else {
            data = obtenerDataSet(filtroIndicador, labels, data);
        }
        data.setLabels(labels);
        graficaPredicciones.setOptions(cargarOpcionesBarIndicador());
        graficaPredicciones.setData(data);
        indicadores = new ArrayList<>();
        indicadores = Arrays.asList(filtroIndicador);
    }

    /**
     * Metodo para obtener el data set, a partir de los filtros dados
     *
     * @param filtroIndicadores, Indicadores filtrados
     * @param filtroGeneros, los generos que fueron filtrados
     * @param data, representa el Data del grafico
     * @return la Data del grafico
     */
    private ChartData obtenerDataSet(String[] filtroIndicadores, List<String> filtroGeneros, ChartData data) {
        RepositorioResultados repository = new RepositorioResultados();
        List<DatosPrediccion> predicciones = new ArrayList<>();
        for (String indicador : filtroIndicadores) {
            predicciones.clear();
            for (String genero : filtroGeneros) {
                DatosPrediccion datos = repository.consultarFiltrosPred(indicador, genero, filtroMinimo, filtroMaximo, nucleoTematico, prueba);
                if (datos == null) {
                    datos = new DatosPrediccion(genero, indicador, "", 0, 0, 0, prueba, nucleoTematico);
                }
                predicciones.add(datos);
                listadoGrafica.add(datos);
            }
            data.addChartDataSet(cargarDataSet(predicciones, indicador, null, 2));
        }
        return data;
    }

    private List<Number> obtenerValores(String[] indicadorConsulta, List<DatosPrediccion> datosIndicador, int tipoConsulta) {
        List<Number> values = new ArrayList<>();
        boolean encontrado = false;
        if (tipoConsulta == 1) {
            for (int i = 0; i < indicadorConsulta.length; i++) {
                encontrado = false;
                for (DatosPrediccion dato : datosIndicador) {
                    if (dato.getIndicador().equals(indicadorConsulta[i])) {
                        values.add((double) Math.round(dato.getProbabilidad() * 100d) / 100d);
                        encontrado = true;
                    }
                }
                if (!encontrado) {
                    values.add(0);
                }
            }
        } else {
            for (DatosPrediccion dato : datosIndicador) {
                values.add((double) Math.round(dato.getProbabilidad() * 100d) / 100d);
            }
        }
        return values;
    }

    /**
     * Metodo usado para extraer indicadores
     *
     * @param datosIndicador se refiere al string del indicador
     * @return retorna a que corresponde el indicador
     */
    public String extraerIndicador(String datosIndicador) {
        switch (datosIndicador) {
            case "fami_educacionpadre":
                return "Educación del padre";
            case "fami_educacionmadre":
                return "Educación de la madre";
            case "fami_tienecomputador":
                return "Acceso a computador";
            case "fami_tieneinternet":
                return "Acceso a internet";
            case "fami_tieneconsolavideojuegos":
                return "Acceso a consola de videojuegos";
            case "punt_lenguaje":
                return "Puntaje en lenguaje";
            case "punt_matematicas":
                return "Puntaje en matemáticas";
            default:
                return "";
        }
    }

    /**
     * Consultar informacion del indicador seleccionado
     *
     * @param e Consultar datos por indicador
     */
    public void consultarInfoIndicador(ValueChangeEvent e) {
        try {
            infoIndicador = e.getNewValue().toString();
            listaConsulta = new ArrayList<>();
            for (DatosPrediccion datos : listadoGrafica) {
                if (datos.getIndicador().contains(infoIndicador) && (!(datos.getMaxPuntaje() == 0 && datos.getMinPuntaje() == 0))) {
                    datos.setProbabilidad((double) Math.round(datos.getProbabilidad() * 100d) / 100d);
                    listaConsulta.add(datos);
                }
            }
        } catch (Exception ex) {
            System.out.println("Error De AJAX");
        }
    }

    /**
     * Consultar datos sobre el indicador seleccionado
     *
     * @param datosIndicador datos del indicador seleccionado
     */
    public void consultarIndicador(DatosPrediccion datosIndicador) {
        ChartData data = new ChartData();
        DonutChartDataSet dataSet = new DonutChartDataSet();
        graficaIndicador = new DonutChartModel();
        List<Number> valores = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> coloresPa = new ArrayList<>();
        listaIndicador = new RepositorioResultados().consultarIndicadorPrediccion(datosIndicador);
        color = 0;
        double prob = 0;
        for (int i = 0; i < listaIndicador.size(); i++) {
            prob = (double) Math.round(listaIndicador.get(i).getProbabilidad() * 10d) / 10d;
            listaIndicador.get(i).setProbabilidad(prob);
            valores.add(prob);
            coloresPa.add("rgb(" + colores.get(color) + ")");
            listaIndicador.get(i).setDatoIndicador("rgb(" + colores.get(color) + ")");
            labels.add(prob + "% " + listaIndicador.get(i).getDescripcion());
            color++;
        }
        dataSet.setData(valores);
        dataSet.setBackgroundColor(coloresPa);
        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        graficaIndicador.setOptions(cargarOpcionesGraficoDonut(datosIndicador.getIndicador(), datosIndicador.getNucleo()));
        graficaIndicador.setData(data);
        PrimeFaces.current().executeScript("PF('dlg2').show();");

    }

    /**
     * Metodo encargado de asignar las propiedades y estilos al grafico donut
     *
     * @param indicadorConsultado, indicador seleccionado
     * @param nucleoConsultado, nucleo tematico seleccionado
     * @return Propiedades del grafico Donut
     */
    private DonutChartOptions cargarOpcionesGraficoDonut(String indicadorConsultado, String nucleoConsultado) {
        DonutChartOptions opciones = new DonutChartOptions();
        Title titulo = new Title();
        titulo.setDisplay(true);
        titulo.setText("Analisis " + extraerIndicador(indicadorConsultado) + " y " + extraerIndicador(nucleoConsultado));
        opciones.setTitle(titulo);
        Legend legend = new Legend();
        legend.setDisplay(false);
        opciones.setLegend(legend);
        return opciones;
    }

    /**
     * Cargar propiedades y aspectos del grafico de barras
     *
     * @return retorna el grafico con sus propiedades
     */
    private BarChartOptions cargarOpcionesBarIndicador() {
        BarChartOptions opciones = new BarChartOptions();
        CartesianScales escala = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        escala.addYAxesData(linearAxes);
        opciones.setScales(escala);
        return opciones;
    }

    /**
     * Metodo encargado de cargar el dialogo de auto-ayuda
     */
    public void cargarDialogo() {
        PrimeFaces.current().executeScript("PF('dlg').show();");
    }

    /**
     * Metodo encargado de generar reporte
     */
    public void generarReporte() {
        RepositorioResultados repository = new RepositorioResultados();
        String[] generos = {"Mujer", "Hombre"};
        Reporte reporte = new Reporte("Prediccion");
        reporte.setTituloPie(nucleoTematico);
        if (prueba.contains("5")) {
            reporte.setTituloInicial("ANÁLISIS PREDICTIVO EN PRUEBAS SABER QUINTO");
        } else {
            reporte.setTituloInicial("ANÁLISIS PREDICTIVO EN PRUEBAS SABER NOVENO");
        }
        List<String> filtroInd = Arrays.asList(indicadoresConsulta);
        for (int i = 0; i < 2; i++) {
            List<DatosPrediccion> listadoPred = new ArrayList<>();
            for (String indicador : filtroInd) {
                DatosPrediccion datos = repository.consultarFiltrosPred(indicador, generos[i], 0, 500, nucleoTematico, prueba);
                reporte.getListadoPrediccionIndicador().put(indicador + "_" + generos[i], repository.consultarIndicadorPrediccion(new DatosPrediccion(generos[i], indicador, "", 0, 0, 0, prueba, nucleoTematico)));
                if (datos == null) {
                    listadoPred.add(new DatosPrediccion(generos[i], indicador, "No afecta", 0, 0, 0, prueba, nucleoTematico));
                } else {
                    listadoPred.add(datos);
                }
            }
            reporte.getListadoPrediccion().put("Genero_" + generos[i], listadoPred);
        }
        String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reportes/ReporteResultadosPredictivo.jasper");
        new ConfiguracionReporte().cargarResultados(reporte, path, "Prediccion");
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

    public DonutChartModel getGraficaIndicador() {
        return graficaIndicador;
    }

    public void setGraficaIndicador(DonutChartModel graficaIndicador) {
        this.graficaIndicador = graficaIndicador;
    }

    public List<DatosPrediccion> getListaIndicador() {
        return listaIndicador;
    }

    public void setListaIndicador(List<DatosPrediccion> listaIndicador) {
        this.listaIndicador = listaIndicador;
    }

    public String getInfoIndicador() {
        return infoIndicador;
    }

    public void setInfoIndicador(String infoIndicador) {
        this.infoIndicador = infoIndicador;
    }

    public String[] getIndicadoresConsulta() {
        return indicadoresConsulta;
    }

    public void setIndicadoresConsulta(String[] indicadoresConsulta) {
        this.indicadoresConsulta = indicadoresConsulta;
    }

    public List<DatosPrediccion> getListadoGrafica() {
        return listadoGrafica;
    }

    public void setListadoGrafica(List<DatosPrediccion> listadoGrafica) {
        if (listadoGrafica == null) {
            listadoGrafica = new ArrayList<>();
        }
        this.listadoGrafica = listadoGrafica;
    }

    public List<DatosPrediccion> getListaConsulta() {
        return listaConsulta;
    }

    public void setListaConsulta(List<DatosPrediccion> listaConsulta) {
        if (listaConsulta == null) {
            listaConsulta = new ArrayList<>();
        }
        this.listaConsulta = listaConsulta;
    }

    public String[] getFiltroIndicador() {
        return filtroIndicador;
    }

    public void setFiltroIndicador(String[] filtroIndicador) {
        this.filtroIndicador = filtroIndicador;
    }

    public String[] getFiltroGenero() {
        return filtroGenero;
    }

    public void setFiltroGenero(String[] filtroGenero) {
        this.filtroGenero = filtroGenero;
    }

    public boolean isMostrarGraficas() {
        return mostrarGraficas;
    }

    public void setMostrarGraficas(boolean mostrarGraficas) {
        this.mostrarGraficas = mostrarGraficas;
    }

    public boolean isMostrarIndicador() {
        return mostrarIndicador;
    }

    public void setMostrarIndicador(boolean mostrarIndicador) {
        this.mostrarIndicador = mostrarIndicador;
    }

    public void ocultarPanelIndicador() {
        mostrarIndicador = !mostrarIndicador;
    }

    public void ocultarPanelGenero() {
        mostrarGenero = !mostrarGenero;
    }

    public void ocultarPanelPuntaje() {
        mostrarPuntaje = !mostrarPuntaje;
    }

    public List<String> getIndicadores() {
        return indicadores;
    }

    public void setIndicadores(List<String> indicadores) {
        this.indicadores = indicadores;
    }

    public BarChartModel getGraficaPredicciones() {
        return graficaPredicciones;
    }

    public void setGraficaPredicciones(BarChartModel graficaPredicciones) {
        this.graficaPredicciones = graficaPredicciones;
    }

    public String getPrueba() {
        return prueba;
    }

    public void setPrueba(String prueba) {
        this.prueba = prueba;
    }

    public String getNucleoTematico() {
        return nucleoTematico;
    }

    public void setNucleoTematico(String nucleoTematico) {
        this.nucleoTematico = nucleoTematico;
    }

    public BarChartModel getGraficaGenero() {
        return graficaGenero;
    }

    public void setGraficaGenero(BarChartModel graficaGenero) {
        this.graficaGenero = graficaGenero;
    }

    public boolean isMostrarGenero() {
        return mostrarGenero;
    }

    public void setMostrarGenero(boolean mostrarGenero) {
        this.mostrarGenero = mostrarGenero;
    }

    public boolean isMostrarPuntaje() {
        return mostrarPuntaje;
    }

    public void setMostrarPuntaje(boolean mostrarPuntaje) {
        this.mostrarPuntaje = mostrarPuntaje;
    }

    public void seleccionar(ValueChangeEvent e) {
        prueba = e.getNewValue().toString();
    }

    public int getFiltroMinimo() {
        return filtroMinimo;
    }

    public void setFiltroMinimo(int filtroMinimo) {
        this.filtroMinimo = filtroMinimo;
    }

    public int getFiltroMaximo() {
        return filtroMaximo;
    }

    public void setFiltroMaximo(int filtroMaximo) {
        this.filtroMaximo = filtroMaximo;
    }
}
