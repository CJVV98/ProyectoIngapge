/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.controller;

import com.udec.pojo.DatosGrafica;
import com.udec.repositorio.SaberSuperiorBD;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

/**
 *
 * @author Angie Paola Manrique
 * @author Alisson Catalina Celeita
 */
@ManagedBean(name = "resultadosSaberPro")
@ViewScoped
public class ResultadosSaberPro implements Serializable {
   
    String anioPrueba, tipoInd, indic, atributos, nombreIndicador, nomPrueba = "saberpro_", definicion;
    boolean ocultarSocial = false, ocultarEconomi = true, ocultarAcademi = false, ocultar = false, ocultarCant = true;
    private transient Instances resultadosPrueba, resultadosIngles, resultadosLectura, resultadosRazona, resultadosComuni, resultadosCompe;
    private BarChartModel graficaGenderIndicador, graficaInglesPro, graficaLecturaPro, graficaRazonaPro, graficaComuniPro, graficaCompePro;
    SimpleKMeans algoritmoKM;
    int numClusters = 0;
    private transient List<String> clusterSBPro, centroidesPro, labelsPro, aniosPro;
    private transient List<Number> resultadosM, resultadosH, resultados1, resultados2, resultados3, resultados4, resultados5, resultados6, resultados7, resultados8, resultados9, resultados0;
    BarChartDataSet serieHombres, serieMujeres, serie0a30, serie30a60, serie60a90, serie90a120, serie120a150, serie150a180, serie180a210, serie210a240, serie240a270, serie270a300;
    ChartData data;
    private static final SaberSuperiorBD REPOSITORIO = new SaberSuperiorBD();
    private LineChartModel cantidadPersonas;
    private transient List<DatosGrafica> datos = new ArrayList();

    @PostConstruct
    public void inicio() {
        tipoInd = "economico";
        graficaGenderIndicador = new BarChartModel();
        graficaCompePro = new BarChartModel();
        graficaComuniPro = new BarChartModel();
        graficaInglesPro = new BarChartModel();
        graficaLecturaPro = new BarChartModel();
        graficaRazonaPro = new BarChartModel();
        validarAniosPrueba();
        graficarCantidades();
        mostrarCantidad();
    }

    /*
     Permite el cambio según la selección de tipo en el radio button
     */
    public void cargarIndicadores(ValueChangeEvent event) {
        tipoInd = event.getNewValue().toString();
        if (null != tipoInd) {
            switch (tipoInd) {
                case "social":
                    ocultarEconomi = false;
                    ocultarSocial = true;
                    ocultarAcademi = false;
                    break;
                case "academico":
                    ocultarAcademi = true;
                    ocultarEconomi = false;
                    ocultarSocial = false;
                    break;
                default:
                    ocultarEconomi = true;
                    ocultarAcademi = false;
                    ocultarSocial = false;
                    break;
            }
        }
    }

    /*
     Valida años de las pruebas
     */
    public void validarAniosPrueba() {
        aniosPro = new ArrayList();
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);

        for (int i = 2016; i < year; i++) {
            aniosPro.add(String.valueOf(i));
        }
    }

    /*
     Método que valida la existencia de los indicadores en cada año
     */
    public void validarExistencia() {
        actualizarNombres();
        int cantidad = REPOSITORIO.consultarExistencia(anioPrueba, "saberpro", indic);
        if (cantidad > 0) {
            graficarAnalisisPro();
        } else {
            generarAnalisisCluster();
        }
        cantidad = 0;
    }

    /*
     Método que grafica los resultados del análisis alojados en BD
     */
    public void graficarAnalisisPro() {
        ocultarCant = false;
        ocultar = true;
        actualizarNombres();
        datos = REPOSITORIO.listarAnalisis(anioPrueba, indic, "saberpro", "Genero");
        llenarListasPuntajes();
        crearGrafica(clusterSBPro, centroidesPro);
        mostrarGraficaPro(graficaGenderIndicador);

        datos = REPOSITORIO.listarAnalisis(anioPrueba, indic, "saberpro", "Ingles");
        llenarListasPuntajes();
        crearGraficaPuntajes(clusterSBPro, centroidesPro);
        mostrarGraficaPro(graficaInglesPro);

        datos = REPOSITORIO.listarAnalisis(anioPrueba, indic, "saberpro", "Lectura");
        llenarListasPuntajes();
        crearGraficaPuntajes(clusterSBPro, centroidesPro);
        mostrarGraficaPro(graficaLecturaPro);

        datos = REPOSITORIO.listarAnalisis(anioPrueba, indic, "saberpro", "Razonamiento");
        llenarListasPuntajes();
        crearGraficaPuntajes(clusterSBPro, centroidesPro);
        mostrarGraficaPro(graficaRazonaPro);

        datos = REPOSITORIO.listarAnalisis(anioPrueba, indic, "saberpro", "Comunicacion");
        llenarListasPuntajes();
        crearGraficaPuntajes(clusterSBPro, centroidesPro);
        mostrarGraficaPro(graficaComuniPro);

        datos = REPOSITORIO.listarAnalisis(anioPrueba, indic, "saberpro", "Competencias");
        llenarListasPuntajes();
        crearGraficaPuntajes(clusterSBPro, centroidesPro);
        mostrarGraficaPro(graficaCompePro);
    }

    /*
     Método para actualizar las listas de puntajes de la prueba
     */
    public void llenarListasPuntajes() {
        clusterSBPro = new ArrayList();
        centroidesPro = new ArrayList();

        for (DatosGrafica dato : datos) {
            clusterSBPro.add(dato.getCluster());
            centroidesPro.add(dato.getCentroide());
            if (dato.getAtributos().length() != 0) {
                atributos = dato.getAtributos();
            }

        }
    }

    /*
     Genera el análisis para los diferentes núcleos
     */
    public void generarAnalisisCluster() {
        ocultar = true;
        ocultarCant = false;
        try {
            resultadosPrueba = REPOSITORIO.listarIndicador(indic, anioPrueba, nomPrueba);
            if (!resultadosPrueba.isEmpty()) {
                clusterDataPro(resultadosPrueba, 50, true, "Genero");
                mostrarGraficaPro(graficaGenderIndicador);
                resultadosIngles = REPOSITORIO.listarNucleo(indic, anioPrueba, nomPrueba, "mod_ingles_punt");
                clusterDataPro(resultadosIngles, 300, false, "Ingles");
                mostrarGraficaPro(graficaInglesPro);
                resultadosLectura = REPOSITORIO.listarNucleo(indic, anioPrueba, nomPrueba, "mod_lectura_critica_punt");
                clusterDataPro(resultadosLectura, 300, false, "Lectura");
                mostrarGraficaPro(graficaLecturaPro);
                resultadosRazona = REPOSITORIO.listarNucleo(indic, anioPrueba, nomPrueba, "mod_razona_cuantitat_punt");
                clusterDataPro(resultadosRazona, 300, false, "Razonamiento");
                mostrarGraficaPro(graficaRazonaPro);
                resultadosComuni = REPOSITORIO.listarNucleo(indic, anioPrueba, nomPrueba, "mod_comuni_escrita_punt");
                clusterDataPro(resultadosComuni, 300, false, "Comunicacion");
                mostrarGraficaPro(graficaComuniPro);
                resultadosCompe = REPOSITORIO.listarNucleo(indic, anioPrueba, nomPrueba, "mod_competen_ciudada_punt");
                clusterDataPro(resultadosCompe, 300, false, "Competencias");
                mostrarGraficaPro(graficaCompePro);
            } else {
                ocultar = false;
                ocultarCant = true;
                crearMensaje();
            }
        } catch (Exception ex) {
            crearMensaje();
            ocultar = false;
            ocultarCant = true;
        }
    }

    /*
     Llena las listas que se van a graficar con la infromación del análisis
     */
    public void crearGrafica(List<String> resultados, List<String> centroides) {
        int cont;
        String color = "rgb(128, 128, 128)";
        graficaGenderIndicador = new BarChartModel();
        resultadosH = new ArrayList<>();
        resultadosM = new ArrayList<>();
        data = new ChartData();
        labelsPro = new ArrayList();
        String separador = Pattern.quote(",");
        String[] nombres = atributos.split(separador);
        for (int i = 0; i < nombres.length; i++) {
            if (!nombres[i].contains("Diez") && !nombres[i].contains("Ocho") && !nombres[i].contains("Nueve") && !nombres[i].contains("12 o más") && !nombres[i].contains("Once") && !nombres[i].contains("Doce o más")) {
                if (i == 0) {
                    String separador1 = Pattern.quote("{");
                    String[] aux = nombres[i].split(separador1);
                    labelsPro.add(aux[1].replace("'", ""));
                } else {
                    if (i == (nombres.length - 1)) {
                        String separador2 = Pattern.quote("}");
                        String[] aux = nombres[i].split(separador2);
                        labelsPro.add(aux[0].replace("'", ""));
                    } else {
                        labelsPro.add(nombres[i].replace("'", ""));
                    }
                }
            }
        }
        serieHombres = new BarChartDataSet();
        serieMujeres = new BarChartDataSet();

        estiloGraficas(serieHombres, "Hombres", "rgb(102, 178, 255)", color, 1);
        estiloGraficas(serieMujeres, "Mujeres", "rgb(178, 9, 178)", "rgb(255, 159, 64)", 1);

        data.setLabels(labelsPro);

        for (String aux1 : labelsPro) {
            cont = 0;
            int sumamu = 0;
            int sumahom = 0;
            for (String aux : centroides) {
                if (aux.contains(aux1)) {
                    if (aux.contains("F,")) {
                        String separador1 = Pattern.quote("Tamanio:");
                        String[] auxcen = resultados.get(cont).split(separador1);
                        sumamu = sumamu + Integer.parseInt(auxcen[1]);
                    } else if (aux.contains("M,")) {
                        String separador1 = Pattern.quote("Tamanio:");
                        String[] auxcen = resultados.get(cont).split(separador1);
                        sumahom = sumahom + Integer.parseInt(auxcen[1]);
                    }
                }
                cont++;
            }
            resultadosM.add(sumamu);
            resultadosH.add(sumahom);
        }
        serieHombres.setData(resultadosH);
        serieMujeres.setData(resultadosM);
        data.addChartDataSet(serieHombres);
        data.addChartDataSet(serieMujeres);
    }

    public void mostrarGraficaPro(BarChartModel grafica) {
        grafica.setData(data);
        BarChartOptions options = crearPuntajesPruebas();
        grafica.setOptions(options);
    }

    /*
     Procesamiento de los datos por medio del algoritmo  kmeans
     */
    public void clusterDataPro(Instances resultadosPrueba, int numCluster, boolean b, String titulo) {
        algoritmoKM = new SimpleKMeans();
        algoritmoKM.setSeed(10);
        numClusters = numCluster;
        try {
            algoritmoKM.setPreserveInstancesOrder(true);
            algoritmoKM.setNumClusters(numClusters);
            algoritmoKM.setMaxIterations(500);
            algoritmoKM.setCanopyMinimumCanopyDensity(2.0);
            algoritmoKM.setCanopyPeriodicPruningRate(10000);
            algoritmoKM.setCanopyT1(-1.25);
            algoritmoKM.setCanopyT2(-1.0);
            algoritmoKM.buildClusterer(resultadosPrueba);
            Instances centroids = algoritmoKM.getClusterCentroids();
            clusterSBPro = new ArrayList();
            centroidesPro = new ArrayList();
            atributos = (centroids.attribute(1)).toString().replace("'", "");

            for (int i = 0; i < centroids.size(); i++) {
                int tamanio = (int) algoritmoKM.getClusterSizes()[i];
                String cluster = "Cluster:" + i + " Tamanio:" + tamanio;
                clusterSBPro.add(cluster);
                centroidesPro.add(centroids.get(i).toString().replace("'", ""));
                REPOSITORIO.insertarAnalisis(new DatosGrafica("saberpro", anioPrueba, titulo, indic, centroids.get(i).toString().replace("'", ""), cluster, atributos));
            }
        } catch (Exception e) {
            Logger.getLogger(ResultadosSaberPro.class.getName()).log(Level.SEVERE, "Error", e);
        }
        if (b) {
            this.crearGrafica(clusterSBPro, centroidesPro);
        } else {
            this.crearGraficaPuntajes(clusterSBPro, centroidesPro);
        }
    }

    /*
     Determina los valores de colores y estilo de las gráficas
     */
    public void estiloGraficas(BarChartDataSet serie, String label, String colores, String coloresBorde, int borde) {
        serie.setLabel(label);
        serie.setBackgroundColor(colores);
        serie.setBorderColor(coloresBorde);
        serie.setBorderWidth(borde);
        actualizarNombres();
    }

    /*
     Guarda la infomación a graficar de puntajes
     */
    public BarChartOptions crearPuntajesPruebas() {
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();

        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();

        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);

        linearAxes.setScaleLabel(null);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);
        return options;
    }

    /*
     Guarda los datos a graficar de cantidad de estudiantes
     */
    public void graficarCantidades() {
        List<Object> cantMujeres = new ArrayList();
        List<Object> cantHombres = new ArrayList();

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);

        for (int i = 2016; i < year; i++) {
            cantMujeres.add(REPOSITORIO.listarMujer(String.valueOf(i), nomPrueba));
            cantHombres.add(REPOSITORIO.listarHombre(String.valueOf(i), nomPrueba));
        }

        cantidadPersonas = new LineChartModel();
        data = new ChartData();

        LineChartDataSet dataHombres = new LineChartDataSet();
        dataHombres.setData(cantHombres);
        dataHombres.setLabel("Hombres");
        dataHombres.setBorderColor("rgb(102, 178, 255)");
        dataHombres.setLineTension(0.1);
        data.addChartDataSet(dataHombres);
        LineChartDataSet dataMujeres = new LineChartDataSet();
        dataMujeres.setData(cantMujeres);
        dataMujeres.setLabel("Mujeres");
        dataMujeres.setBorderColor("rgb(178, 9, 178)");
        dataMujeres.setLineTension(0.1);
        data.addChartDataSet(dataMujeres);

        data.setLabels(aniosPro);
        cantidadPersonas.setData(data);
    }

    /*
     Genera gráficas puntajes 
     */
    public void crearGraficaPuntajes(List<String> resultados, List<String> centroides) {
        int cont;
        resultados1 = new ArrayList<>();
        resultados2 = new ArrayList<>();
        resultados3 = new ArrayList<>();
        resultados4 = new ArrayList<>();
        resultados5 = new ArrayList<>();
        resultados6 = new ArrayList<>();
        resultados7 = new ArrayList<>();
        resultados8 = new ArrayList<>();
        resultados9 = new ArrayList<>();
        resultados0 = new ArrayList<>();
        data = new ChartData();
        serie0a30 = new BarChartDataSet();
        serie30a60 = new BarChartDataSet();
        serie60a90 = new BarChartDataSet();
        serie90a120 = new BarChartDataSet();
        serie120a150 = new BarChartDataSet();
        serie150a180 = new BarChartDataSet();
        serie180a210 = new BarChartDataSet();
        serie210a240 = new BarChartDataSet();
        serie240a270 = new BarChartDataSet();
        serie270a300 = new BarChartDataSet();
        String color = "rgb(128, 128, 128)";
        data.setLabels(labelsPro);

        estiloGraficas(serie0a30, "De 0 a 30", "rgb(147, 112, 219)", color, 1);
        estiloGraficas(serie30a60, "De 30 a 60", "rgb(135, 206, 250)", color, 1);
        estiloGraficas(serie60a90, "De 60 a 90", "rgb(123,104,238)", color, 1);
        estiloGraficas(serie90a120, "De 90 a 120", "rgb(0, 153, 153)", color, 1);
        estiloGraficas(serie120a150, "De 120 a 150", "rgb(178, 102, 255)", color, 1);
        estiloGraficas(serie150a180, "De 150 a 180", "rgb(0, 128, 255)", color, 1);
        estiloGraficas(serie180a210, "De 180 a 210", "rgb(204, 153, 255)", color, 1);
        estiloGraficas(serie210a240, "De 210 a 240", "rgb(0, 76, 153)", color, 1);
        estiloGraficas(serie240a270, "De 240 a 270", "rgb(127, 0, 255)", color, 1);
        estiloGraficas(serie270a300, "De 270 a 300", "rgb(153, 204, 255)", color, 1);

        for (String aux1 : labelsPro) {
            cont = 0;
            int sum1 = 0, sum2 = 0, sum3 = 0, sum4 = 0, sum5 = 0, sum6 = 0, sum7 = 0, sum8 = 0, sum9 = 0, sum0 = 0;
            for (String aux : centroides) {
                String separador2 = Pattern.quote(",");
                String[] auxsep = aux.split(separador2);
                int num = Integer.parseInt(auxsep[0]);
                String separador1 = Pattern.quote("Tamanio:");
                String[] auxcen = resultados.get(cont).split(separador1);
                if (aux.contains(aux1)) {
                    if (num >= 0 && num < 30) {
                        sum1 = sum1 + Integer.parseInt(auxcen[1]);
                    } else if (num >= 30 && num < 60) {
                        sum2 = sum2 + Integer.parseInt(auxcen[1]);
                    } else if (num >= 60 && num < 90) {
                        sum3 = sum3 + Integer.parseInt(auxcen[1]);
                    } else if (num >= 90 && num < 120) {
                        sum4 = sum4 + Integer.parseInt(auxcen[1]);
                    } else if (num >= 120 && num < 150) {
                        sum5 = sum5 + Integer.parseInt(auxcen[1]);
                    } else if (num >= 150 && num < 180) {
                        sum6 = sum6 + Integer.parseInt(auxcen[1]);
                    } else if (num >= 180 && num < 210) {
                        sum7 = sum7 + Integer.parseInt(auxcen[1]);
                    } else if (num >= 210 && num < 240) {
                        sum8 = sum8 + Integer.parseInt(auxcen[1]);
                    } else if (num >= 240 && num < 270) {
                        sum9 = sum9 + Integer.parseInt(auxcen[1]);
                    } else {
                        sum0 = sum0 + Integer.parseInt(auxcen[1]);
                    }
                }
                cont++;
            }
            resultados1.add(sum1);
            resultados2.add(sum2);
            resultados3.add(sum3);
            resultados4.add(sum4);
            resultados5.add(sum5);
            resultados6.add(sum6);
            resultados7.add(sum7);
            resultados8.add(sum8);
            resultados9.add(sum9);
            resultados0.add(sum0);
        }
        serie0a30.setData(resultados1);
        serie30a60.setData(resultados2);
        serie60a90.setData(resultados3);
        serie90a120.setData(resultados4);
        serie120a150.setData(resultados5);
        serie150a180.setData(resultados6);
        serie180a210.setData(resultados7);
        serie210a240.setData(resultados8);
        serie240a270.setData(resultados9);
        serie270a300.setData(resultados0);

        data.addChartDataSet(serie0a30);
        data.addChartDataSet(serie30a60);
        data.addChartDataSet(serie60a90);
        data.addChartDataSet(serie90a120);
        data.addChartDataSet(serie120a150);
        data.addChartDataSet(serie150a180);
        data.addChartDataSet(serie180a210);
        data.addChartDataSet(serie210a240);
        data.addChartDataSet(serie240a270);
        data.addChartDataSet(serie270a300);
    }

    public void mostrarCantidad() {
        LineChartOptions options = new LineChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        linearAxes.setScaleLabel(null);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);
        cantidadPersonas.setOptions(options);
    }

    /*
     Método para generar mensajes
     */
    private void crearMensaje() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage("message-info", new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Indicador no disponible", "Este indicador no se encuentra en este año"));
    }

    /*
     Actualiza los nombres de los indicadores y su definición
     */
    public void actualizarNombres() {
        switch (indic) {
            case "fami_educacionmadre":
                nombreIndicador = "Educación madre";
                definicion = "Educación madre: Este indicador muestra con qué niveles educativos cuentan las madres de los estudiantes, estando clasificados en los siguientes atributos: ninguno, no sabe, no aplica, primaria incompleta, primaria completa, secundaria incompleta, secundaria completa, técnica y tecnológica incompleta, técnica y tecnológica completa, educación profesional incompleta, educación profesional completa y postgrado. ";
                break;
            case "fami_educacionpadre":
                nombreIndicador = "Educación padre";
                definicion = "Educación padre: Este indicador muestra con qué niveles educativos cuentan los padres de los estudiantes, estando clasificados en los siguientes atributos: ninguno, no sabe, no aplica, primaria incompleta, primaria completa, secundaria incompleta, secundaria completa, técnica y tecnológica incompleta, técnica y tecnológica completa, educación profesional incompleta, educación profesional completa y postgrado. ";
                break;
            case "fami_estratovivienda":
                nombreIndicador = "Estrato de vivienda";
                definicion = "Estrato de vivienda: Indica la clasificación socioeconómica jerarquizada del país, sus categorías comprenden desde el estrato 0 hasta el estrato 6, o si vive en una zona sin estratificación.";
                break;
            case "fami_tieneinternet":
                nombreIndicador = "Tiene internet";
                definicion = "Tiene servicio internet: Este indicador nos señala qué estudiantes cuentan con el servicio de internet en sus hogares y quiénes no.";
                break;
            case "estu_areareside":
                nombreIndicador = "Área de residencia";
                definicion = "Área de residencia: Se refiere a la zona donde el estudiante vivía al momento de presentación de la prueba. Sus atributos se dividen en Rural y Urbano.";
                break;
            case "estu_pagomatriculacredito":
                nombreIndicador = "Pago matrícula a crédito";
                definicion = "Pago de matrícula a crédito: En esta prueba se tienen varias opciones de respuesta de tipos de pago matricula entre los cuales se encuentra si es por medio de crédito, si fue pago propio o si el estudiante se encuentra becado. Entre estos métodos, el pago por crédito fue aquel indicador que arrojo una afectación significativa. Los atributos con los que cuenta el indicador son sí y no.";
                break;
            case "estu_estadocivil":
                nombreIndicador = "Estado civil";
                definicion = "Estado civil: Representa la condición al momento de la realización de la prueba de los estudiantes determinada por sus relaciones de pareja, se dividen entre 4 opciones: si está casado, soltero, unión libre y separado y/o viudo.";
                break;
            case "fami_cabezafamilia":
                nombreIndicador = "Cabeza de familia";
                definicion = "Cabeza de familia: Determina si la persona es quién tiene a cargo, o no, toda la responsabilidad económica del hogar.  ";
                break;
            case "fami_numpersonasacargo":
                nombreIndicador = "Número personas a cargo";
                definicion = "Número de personas a cargo: Este indicador está directamente relacionado con el de cabeza de familia, y pretende hacer la misma medición, pero desde un punto de vista cuantitativo. Estando en diferentes opciones comprendidas por un rango entre ninguna y seis personas.";
                break;
            case "estu_dedicacionlecturadiaria":
                nombreIndicador = "Dedicación lectura diaria";
                definicion = "Dedicación lectura diaria: Muestra el tiempo diario que le dedican los estudiantes al ejercicio de la lectura, siendo clasificada por rangos de tiempo de la siguiente manera: 30 minutos o menos, no leo por entretenimiento, entre 30 y 60 minutos, más de 2 horas, y entre 1 y 2 horas.";
                break;
            case "gruporeferencia":
                nombreIndicador = "Área de la carrera";
                definicion = "Área de la carrera: Este indicador en las pruebas se conoce como grupo de referencia, y especifica en que área se encuentra clasificada la carrera que se está cursando. Existen múltiples categorías, dentro de las que se encuentran: Administración y afines, Contaduría y afines, psicología, ingeniería, entre otras.";
                break;
            case "estu_valormatriculauniversidad":
                nombreIndicador = "Valor matrícula universidad";
                definicion = "Valor matricula universidad: Este indicador representa el rango de valores pagados por los estudiantes en la matricula del programa que están cursando en la universidad cada semestre. Las diferentes categorías comprenden un rango entre menos de 500 mil y 7 millones de pesos colombianos, o si no realizó el pago.";
                break;
            default:
                nombreIndicador = "Método programa";
                definicion = "Método del programa: Hace referencia al modo de acceso que tienen los estudiantes al programa educativo, los tipos de clasificación son presencial, distancia, virtual y semipresencial.";
                break;
        }
    }

    public BarChartModel getGraficaGenderIndicador() {
        return graficaGenderIndicador;
    }

    public void setGraficaGenderIndicador(BarChartModel graficaGenderIndicador) {
        this.graficaGenderIndicador = graficaGenderIndicador;
    }

    public BarChartModel getGraficaInglesPro() {
        return graficaInglesPro;
    }

    public void setGraficaInglesPro(BarChartModel graficaInglesPro) {
        this.graficaInglesPro = graficaInglesPro;
    }

    public BarChartModel getGraficaLecturaPro() {
        return graficaLecturaPro;
    }

    public void setGraficaLecturaPro(BarChartModel graficaLecturaPro) {
        this.graficaLecturaPro = graficaLecturaPro;
    }

    public BarChartModel getGraficaRazonaPro() {
        return graficaRazonaPro;
    }

    public void setGraficaRazonaPro(BarChartModel graficaRazonaPro) {
        this.graficaRazonaPro = graficaRazonaPro;
    }

    public BarChartModel getGraficaComuniPro() {
        return graficaComuniPro;
    }

    public void setGraficaComuniPro(BarChartModel graficaComuniPro) {
        this.graficaComuniPro = graficaComuniPro;
    }

    public BarChartModel getGraficaCompePro() {
        return graficaCompePro;
    }

    public void setGraficaCompePro(BarChartModel graficaCompePro) {
        this.graficaCompePro = graficaCompePro;
    }

    public String getAnioPrueba() {
        return anioPrueba;
    }

    public void setAnioPrueba(String anioPrueba) {
        this.anioPrueba = anioPrueba;
    }

    public String getTipoInd() {
        return tipoInd;
    }

    public void setTipoInd(String tipoInd) {
        this.tipoInd = tipoInd;
    }

    public String getIndic() {
        return indic;
    }

    public void setIndic(String indic) {
        this.indic = indic;
    }

    public String getAtributos() {
        return atributos;
    }

    public void setAtributos(String atributos) {
        this.atributos = atributos;
    }

    public boolean isOcultarSocial() {
        return ocultarSocial;
    }

    public void setOcultarSocial(boolean ocultarSocial) {
        this.ocultarSocial = ocultarSocial;
    }

    public boolean isOcultarEconomi() {
        return ocultarEconomi;
    }

    public void setOcultarEconomi(boolean ocultarEconomi) {
        this.ocultarEconomi = ocultarEconomi;
    }

    public boolean isOcultarAcademi() {
        return ocultarAcademi;
    }

    public void setOcultarAcademi(boolean ocultarAcademi) {
        this.ocultarAcademi = ocultarAcademi;
    }

    public Instances getResultadosPrueba() {
        return resultadosPrueba;
    }

    public void setResultadosPrueba(Instances resultadosPrueba) {
        this.resultadosPrueba = resultadosPrueba;
    }

    public Instances getResultadosIngles() {
        return resultadosIngles;
    }

    public void setResultadosIngles(Instances resultadosIngles) {
        this.resultadosIngles = resultadosIngles;
    }

    public Instances getResultadosLectura() {
        return resultadosLectura;
    }

    public void setResultadosLectura(Instances resultadosLectura) {
        this.resultadosLectura = resultadosLectura;
    }

    public Instances getResultadosRazona() {
        return resultadosRazona;
    }

    public void setResultadosRazona(Instances resultadosRazona) {
        this.resultadosRazona = resultadosRazona;
    }

    public Instances getResultadosComuni() {
        return resultadosComuni;
    }

    public void setResultadosComuni(Instances resultadosComuni) {
        this.resultadosComuni = resultadosComuni;
    }

    public Instances getResultadosCompe() {
        return resultadosCompe;
    }

    public void setResultadosCompe(Instances resultadosCompe) {
        this.resultadosCompe = resultadosCompe;
    }

    public int getNumClusters() {
        return numClusters;
    }

    public void setNumClusters(int numClusters) {
        this.numClusters = numClusters;
    }

    public List<String> getClusterSBPro() {
        return clusterSBPro;
    }

    public void setClusterSBPro(List<String> clusterSBPro) {
        this.clusterSBPro = clusterSBPro;
    }

    public boolean isOcultar() {
        return ocultar;
    }

    public void setOcultar(boolean ocultar) {
        this.ocultar = ocultar;
    }

    public boolean isOcultarCant() {
        return ocultarCant;
    }

    public void setOcultarCant(boolean ocultarCant) {
        this.ocultarCant = ocultarCant;
    }

    public List<String> getCentroidesPro() {
        return centroidesPro;
    }

    public void setCentroidesPro(List<String> centroidesPro) {
        this.centroidesPro = centroidesPro;
    }

    public LineChartModel getCantidadPersonas() {
        return cantidadPersonas;
    }

    public void setCantidadPersonas(LineChartModel cantidadPersonas) {
        this.cantidadPersonas = cantidadPersonas;
    }

    public List<String> getAniosPro() {
        return aniosPro;
    }

    public void setAniosPro(List<String> aniosPro) {
        this.aniosPro = aniosPro;
    }

    public String getNombreIndicador() {
        return nombreIndicador;
    }

    public void setNombreIndicador(String nombreIndicador) {
        this.nombreIndicador = nombreIndicador;
    }

    public String getDefinicion() {
        return definicion;
    }

    public void setDefinicion(String definicion) {
        this.definicion = definicion;
    }

}