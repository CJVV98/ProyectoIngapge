/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.controller;

import com.udec.pojo.DatosGrafica;
import com.udec.repositorio.SaberSuperiorBD;
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
@ManagedBean(name = "resultadosSaberTyT")
@ViewScoped
public class ResultadosSaberTyT implements Serializable {

    /**
     * Creates a new instance of ResultadosSaberTyT
     */
    String anioPrueba, tipoIndicador, indicador, atributos, nombreInd, prueba = "sabertyt_", definicion;
    boolean ocultarS = false, ocultarE = true, ocultarA = false, ocultarG = false, ocultarC = true;
    private transient Instances resultadosPrueba, resultadosIngles, resultadosLectura, resultadosRazona, resultadosComuni, resultadosCompe;
    private BarChartModel graficaGenderInd, graficaIngles, graficaLectura, graficaRazona, graficaComuni, graficaCompe;
    SimpleKMeans kmeans;
    private transient List<String> clusterSBTyT, centroides, labels, anios;
    private transient List<Number> resultM, resultH, result1, result2, result3, result4, result5;
    BarChartDataSet serieHombres, serieMujeres, serie0a30, serie30a60, serie60a90, serie90a120, serie120a150;
    private static final SaberSuperiorBD SABERBD = new SaberSuperiorBD();
    ChartData data;
    private LineChartModel cantPersonas;
    private transient List<DatosGrafica> datos = new ArrayList();

    @PostConstruct
    public void init() {
        tipoIndicador = "economico";
        graficaGenderInd = new BarChartModel();
        graficaCompe = new BarChartModel();
        graficaComuni = new BarChartModel();
        graficaIngles = new BarChartModel();
        graficaLectura = new BarChartModel();
        graficaRazona = new BarChartModel();
        validarAnios();
        crearGraficaCantidad();
        mostrarCantidadEstudiantes();
    }

    /*
     Método que valida la existencia de los indicadores en cada año
     */
    public void validarExistenciaTyT() {
        int cantidad = SABERBD.consultarExistencia(anioPrueba, "sabertyt", indicador);
        if (cantidad > 0) {
            graficarAnalisisTyT();
        } else {
            generarAnalisis();
        }
    }

    /*
     Método que grafica los resultados del análisis alojados en BD
     */
    public void graficarAnalisisTyT() {
        ocultarC = false;
        ocultarG = true;
        String nPrueba = "sabertyt";
        actualizarNombresIndicadores();
        datos = SABERBD.listarAnalisis(anioPrueba, indicador, nPrueba, "Genero");
        llenarListasPuntajes();
        generarGrafica(centroides, clusterSBTyT);
        mostrarGraficaTyT(graficaGenderInd);

        datos = SABERBD.listarAnalisis(anioPrueba, indicador, nPrueba, "Ingles");
        llenarListasPuntajes();
        generarGraficaPunt(clusterSBTyT, centroides);
        mostrarGraficaTyT(graficaIngles);

        datos = SABERBD.listarAnalisis(anioPrueba, indicador, nPrueba, "Lectura");
        llenarListasPuntajes();
        generarGraficaPunt(clusterSBTyT, centroides);
        mostrarGraficaTyT(graficaLectura);

        datos = SABERBD.listarAnalisis(anioPrueba, indicador, nPrueba, "Razonamiento");
        llenarListasPuntajes();
        generarGraficaPunt(clusterSBTyT, centroides);
        mostrarGraficaTyT(graficaRazona);

        datos = SABERBD.listarAnalisis(anioPrueba, indicador, nPrueba, "Comunicacion");
        llenarListasPuntajes();
        generarGraficaPunt(clusterSBTyT, centroides);
        mostrarGraficaTyT(graficaComuni);

        datos = SABERBD.listarAnalisis(anioPrueba, indicador, nPrueba, "Competencias");
        llenarListasPuntajes();
        generarGraficaPunt(clusterSBTyT, centroides);
        mostrarGraficaTyT(graficaCompe);
    }

    /*
     Método para actualizar las listas de puntajes de la prueba
     */
    public void llenarListasPuntajes() {
        clusterSBTyT = new ArrayList();
        centroides = new ArrayList();

        for (DatosGrafica dato : datos) {
            clusterSBTyT.add(dato.getCluster());
            centroides.add(dato.getCentroide());
            if (dato.getAtributos().length() != 0) {
                atributos = dato.getAtributos();
            }

        }
    }

    /*
     Permite el cambio según la selección de tipo en el radio button
     */
    public void cargarIndicadores(ValueChangeEvent event) {
        tipoIndicador = event.getNewValue().toString();
        if ("social".equals(tipoIndicador)) {
            ocultarS = true;
            ocultarE = false;
            ocultarA = false;
        } else if ("academico".equals(tipoIndicador)) {
            ocultarA = true;
            ocultarE = false;
            ocultarS = false;
        } else if ("economico".equals(tipoIndicador)) {
            ocultarE = true;
            ocultarA = false;
            ocultarS = false;
        }
    }

    /*
     Valida años de las pruebas
     */
    public void validarAnios() {
        anios = new ArrayList();
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);

        for (int i = 2016; i < year; i++) {
            anios.add(String.valueOf(i));
        }
    }

    /*
     Método que grafica los resultados del análisis alojados en BD
     */
    public void generarAnalisis() {
        ocultarG = true;
        ocultarC = false;
        try {
            resultadosPrueba = SABERBD.listarIndicador(indicador, anioPrueba, prueba);
            if (!resultadosPrueba.isEmpty()) {
                clusterDataTyT(true, resultadosPrueba, 50, "Genero");
                mostrarGraficaTyT(graficaGenderInd);
                resultadosIngles = SABERBD.listarNucleo(indicador, anioPrueba, prueba, "mod_ingles_punt");
                clusterDataTyT(false, resultadosIngles, 300, "Ingles");
                mostrarGraficaTyT(graficaIngles);
                resultadosLectura = SABERBD.listarNucleo(indicador, anioPrueba, prueba, "mod_lectura_critica_punt");
                clusterDataTyT(false, resultadosLectura, 300, "Lectura");
                mostrarGraficaTyT(graficaLectura);
                resultadosRazona = SABERBD.listarNucleo(indicador, anioPrueba, prueba, "mod_razona_cuantitat_punt");
                clusterDataTyT(false, resultadosRazona, 300, "Razonamiento");
                mostrarGraficaTyT(graficaRazona);
                resultadosComuni = SABERBD.listarNucleo(indicador, anioPrueba, prueba, "mod_comuni_escrita_punt");
                clusterDataTyT(false, resultadosComuni, 300, "Comunicacion");
                mostrarGraficaTyT(graficaComuni);
                resultadosCompe = SABERBD.listarNucleo(indicador, anioPrueba, prueba, "mod_competen_ciudada_punt");
                clusterDataTyT(false, resultadosCompe, 300, "Competencias");
                mostrarGraficaTyT(graficaCompe);
            } else {
                ocultarG = false;
                ocultarC = true;
                FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage("message-info", new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Indicador no disponible", "Este indicador no se encuentra en este año"));
            }
        } catch (Exception ex) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("message-info", new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Indicador no disponible", "Este indicador no se encuentra en este año"));
            ocultarG = false;
            ocultarC = true;
        }
    }

    /*
     Procesamiento de los datos por medio del algoritmo  kmeans
     */
    public void clusterDataTyT(boolean b, Instances resultadosPrueba, int numCluster, String titulo) {
        kmeans = new SimpleKMeans();
        kmeans.setSeed(10);
        int numeroClusters = numCluster;
        try {
            kmeans.setPreserveInstancesOrder(true);
            kmeans.setNumClusters(numeroClusters);
            kmeans.setMaxIterations(500);
            kmeans.setCanopyMinimumCanopyDensity(2.0);
            kmeans.setCanopyPeriodicPruningRate(10000);
            kmeans.setCanopyT1(-1.25);
            kmeans.setCanopyT2(-1.0);
            kmeans.buildClusterer(resultadosPrueba);
            Instances centroids = kmeans.getClusterCentroids();
            clusterSBTyT = new ArrayList();
            centroides = new ArrayList();
            atributos = (centroids.attribute(1)).toString().replace("'", "");

            for (int i = 0; i < centroids.size(); i++) {
                int tamanio = (int) kmeans.getClusterSizes()[i];
                String cluster = "Cluster:" + i + " Tamanio:" + tamanio;
                clusterSBTyT.add(cluster);
                centroides.add(centroids.get(i).toString().replace("'", ""));
                SABERBD.insertarAnalisis(new DatosGrafica("sabertyt", anioPrueba, titulo, indicador, centroids.get(i).toString().replace("'", ""), cluster, atributos));

            }
        } catch (Exception e) {
            Logger.getLogger(ResultadosSaberTyT.class.getName()).log(Level.SEVERE, "Error", e);
        }
        if (b) {
            this.generarGrafica(centroides, clusterSBTyT);
        } else {
            this.generarGraficaPunt(clusterSBTyT, centroides);
        }
    }

    /*
     Llena las listas que se van a graficar con la infromación del análisis
     */
    public void generarGrafica(List<String> centroides, List<String> resultados) {
        int cont;
        graficaGenderInd = new BarChartModel();
        resultH = new ArrayList<>();
        resultM = new ArrayList<>();
        data = new ChartData();
        String separador = Pattern.quote(",");
        String[] nombres = atributos.split(separador);
        labels = new ArrayList();
        for (int i = 0; i < nombres.length; i++) {
            if (!nombres[i].contains("EDUCACIÓN") && (!nombres[i].contains("Diez") && !nombres[i].contains("Ocho") && !nombres[i].contains("Nueve") && !nombres[i].contains("Doce o más") && !nombres[i].contains("12 o más") && !nombres[i].contains("Once"))) {
                if (i == 0) {
                    String separador1 = Pattern.quote("{");
                    String[] aux = nombres[i].split(separador1);
                    labels.add(aux[1]);
                } else {
                    if (i == (nombres.length - 1)) {
                        String separador2 = Pattern.quote("}");
                        String[] aux = nombres[i].split(separador2);
                        labels.add(aux[0]);
                    } else {
                        labels.add(nombres[i].replace("'", ""));
                    }
                }
            }
        }
        serieHombres = new BarChartDataSet();
        serieMujeres = new BarChartDataSet();

        estiloGraphics("Hombres", "rgb(102, 178, 255)", "rgb(128, 128, 128)", serieHombres, 1);
        estiloGraphics("Mujeres", "rgb(178, 9, 178)", "rgb(255, 159, 64)", serieMujeres, 1);

        data.setLabels(labels);

        for (String aux1 : labels) {
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
            resultM.add(sumamu);
            resultH.add(sumahom);
        }
        serieHombres.setData(resultH);
        serieMujeres.setData(resultM);
        data.addChartDataSet(serieHombres);
        data.addChartDataSet(serieMujeres);
    }

    /*
     Guarda la infomación a graficar de puntajes
     */
    public void generarGraficaPunt(List<String> resultados, List<String> centroides) {
        int cont;
        result1 = new ArrayList<>();
        result2 = new ArrayList<>();
        result3 = new ArrayList<>();
        result4 = new ArrayList<>();
        result5 = new ArrayList<>();
        serie0a30 = new BarChartDataSet();
        serie30a60 = new BarChartDataSet();
        serie60a90 = new BarChartDataSet();
        serie90a120 = new BarChartDataSet();
        serie120a150 = new BarChartDataSet();
        data = new ChartData();
        data.setLabels(labels);
        String color = "rgb(128, 128, 128)";
        estiloGraphics("De 0 a 30", "rgb(147, 112, 219)", color, serie0a30, 1);
        estiloGraphics("De 30 a 60", "rgb(135, 206, 250)", color, serie30a60, 1);
        estiloGraphics("De 60 a 90", "rgb(123,104,238)", color, serie60a90, 1);
        estiloGraphics("De 90 a 120", "rgb(0, 153, 153)", color, serie90a120, 1);
        estiloGraphics("De 120 a 150", "rgb(178, 102, 255)", color, serie120a150, 1);

        for (String aux1 : labels) {
            cont = 0;
            int sum1 = 0, sum2 = 0, sum3 = 0, sum4 = 0, sum5 = 0;
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
                    } else {
                        sum5 = sum5 + Integer.parseInt(auxcen[1]);
                    }
                }
                cont++;
            }
            result1.add(sum1);
            result2.add(sum2);
            result3.add(sum3);
            result4.add(sum4);
            result5.add(sum5);
        }
        serie0a30.setData(result1);
        serie30a60.setData(result2);
        serie60a90.setData(result3);
        serie90a120.setData(result4);
        serie120a150.setData(result5);

        data.addChartDataSet(serie0a30);
        data.addChartDataSet(serie30a60);
        data.addChartDataSet(serie60a90);
        data.addChartDataSet(serie90a120);
        data.addChartDataSet(serie120a150);
    }

    /*
     Determina los valores de colores y estilo de las gráficas
     */
    public void estiloGraphics(String label, String colores, String coloresBorde, BarChartDataSet serie, int borde) {
        serie.setLabel(label);
        serie.setBackgroundColor(colores);
        serie.setBorderColor(coloresBorde);
        serie.setBorderWidth(borde);
        actualizarNombresIndicadores();
    }

    /*
     Genera gráficas puntajes 
     */
    public BarChartOptions crearGraficaPuntajes() {
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

    public void mostrarGraficaTyT(BarChartModel grafica) {
        grafica.setData(data);
        BarChartOptions opciones = crearGraficaPuntajes();
        grafica.setOptions(opciones);
    }

    /*
     Guarda los datos a graficar de cantidad de estudiantes
     */
    public void crearGraficaCantidad() {
        List<Object> cantMujeres = new ArrayList();
        List<Object> cantHombres = new ArrayList();

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);

        for (int i = 2016; i < year; i++) {
            cantMujeres.add(SABERBD.listarMujer(String.valueOf(i), prueba));
            cantHombres.add(SABERBD.listarHombre(String.valueOf(i), prueba));
        }

        cantPersonas = new LineChartModel();
        data = new ChartData();

        LineChartDataSet dataMujeres = new LineChartDataSet();
        dataMujeres.setData(cantMujeres);
        dataMujeres.setLabel("Mujeres");
        dataMujeres.setBorderColor("rgb(178, 9, 178)");
        dataMujeres.setLineTension(0.1);
        data.addChartDataSet(dataMujeres);

        LineChartDataSet dataHombres = new LineChartDataSet();
        dataHombres.setData(cantHombres);
        dataHombres.setLabel("Hombres");
        dataHombres.setBorderColor("rgb(102, 178, 255)");
        dataHombres.setLineTension(0.1);
        data.addChartDataSet(dataHombres);
        data.setLabels(anios);
        cantPersonas.setData(data);
    }

    public void mostrarCantidadEstudiantes() {
        LineChartOptions options = new LineChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        linearAxes.setScaleLabel(null);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);
        cantPersonas.setOptions(options);
    }

    /*
     Actualiza los nombres de los indicadores y su definición
     */
    public void actualizarNombresIndicadores() {
        switch (indicador) {
            case "fami_educacionmadre":
                nombreInd = "Educación madre";
                definicion = "Educación madre: Este indicador muestra con qué niveles educativos cuentan las madres de los estudiantes, estando clasificados en los siguientes atributos: ninguno, no sabe, no aplica, primaria incompleta, primaria completa, secundaria incompleta, secundaria completa, técnica y tecnológica incompleta, técnica y tecnológica completa, educación profesional incompleta, educación profesional completa y postgrado. ";
                break;
            case "fami_educacionpadre":
                nombreInd = "Educación padre";
                definicion = "Educación padre: Este indicador muestra con qué niveles educativos cuentan los padres de los estudiantes, estando clasificados en los siguientes atributos: ninguno, no sabe, no aplica, primaria incompleta, primaria completa, secundaria incompleta, secundaria completa, técnica y tecnológica incompleta, técnica y tecnológica completa, educación profesional incompleta, educación profesional completa y postgrado. ";
                break;
            case "fami_estratovivienda":
                nombreInd = "Estrato de vivienda";
                definicion = "Estrato de vivienda: Indica la clasificación socioeconómica jerarquizada del país, sus categorías comprenden desde el estrato 0 hasta el estrato 6, o si vive en una zona sin estratificación.";
                break;
            case "fami_tieneinternet":
                nombreInd = "Tiene internet";
                definicion = "Tiene servicio internet: Este indicador nos señala qué estudiantes cuentan con el servicio de internet en sus hogares y quiénes no.";
                break;
            case "estu_areareside":
                nombreInd = "Área de residencia";
                definicion = "Área de residencia: Se refiere a la zona donde el estudiante vivía al momento de presentación de la prueba. Sus atributos se dividen en Rural y Urbano.";
                break;
            case "estu_pagomatriculacredito":
                nombreInd = "Pago matrícula a crédito";
                definicion = "Pago de matrícula a crédito: En esta prueba se tienen varias opciones de respuesta de tipos de pago matricula entre los cuales se encuentra si es por medio de crédito, si fue pago propio o si el estudiante se encuentra becado. Entre estos métodos, el pago por crédito fue aquel indicador que arrojo una afectación significativa. Los atributos con los que cuenta el indicador son sí y no.";
                break;
            case "estu_estadocivil":
                nombreInd = "Estado civil";
                definicion = "Estado civil: Representa la condición al momento de la realización de la prueba de los estudiantes determinada por sus relaciones de pareja, se dividen entre 4 opciones: si está casado, soltero, unión libre y separado y/o viudo.";
                break;
            case "fami_cabezafamilia":
                nombreInd = "Cabeza de familia";
                definicion = "Cabeza de familia: Determina si la persona es quién tiene a cargo, o no, toda la responsabilidad económica del hogar.  ";
                break;
            case "fami_numpersonasacargo":
                nombreInd = "Número personas a cargo";
                definicion = "Número de personas a cargo: Este indicador está directamente relacionado con el de cabeza de familia, y pretende hacer la misma medición, pero desde un punto de vista cuantitativo. Estando en diferentes opciones comprendidas por un rango entre ninguna y seis personas.";
                break;
            case "estu_dedicacionlecturadiaria":
                nombreInd = "Dedicación lectura diaria";
                definicion = "Dedicación lectura diaria: Muestra el tiempo diario que le dedican los estudiantes al ejercicio de la lectura, siendo clasificada por rangos de tiempo de la siguiente manera: 30 minutos o menos, no leo por entretenimiento, entre 30 y 60 minutos, más de 2 horas, y entre 1 y 2 horas.";
                break;
            default:
                nombreInd = "Área de la carrera";
                definicion = "Área de la carrera: Este indicador en las pruebas se conoce como grupo de referencia, y especifica en que área se encuentra clasificada la carrera que se está cursando. Existen múltiples categorías, dentro de las que se encuentran: tecnológico en administración y turismo, tecnológico en ingeniería, industria y minas, técnico en administración y turismo, técnico en ingeniería, industria y minas, entre otras.";
                break;
        }
    }

    public BarChartModel getGraficaGenderInd() {
        return graficaGenderInd;
    }

    public void setGraficaGenderInd(BarChartModel graficaGenderInd) {
        this.graficaGenderInd = graficaGenderInd;
    }

    public String getAnioPrueba() {
        return anioPrueba;
    }

    public void setAnioPrueba(String anioPrueba) {
        this.anioPrueba = anioPrueba;
    }

    public String getTipoIndicador() {
        return tipoIndicador;
    }

    public void setTipoIndicador(String tipoIndicador) {
        this.tipoIndicador = tipoIndicador;
    }

    public String getIndicador() {
        return indicador;
    }

    public void setIndicador(String indicador) {
        this.indicador = indicador;
    }

    public String getAtributos() {
        return atributos;
    }

    public void setAtributos(String atributos) {
        this.atributos = atributos;
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

    public BarChartModel getGraficaIngles() {
        return graficaIngles;
    }

    public void setGraficaIngles(BarChartModel graficaIngles) {
        this.graficaIngles = graficaIngles;
    }

    public BarChartModel getGraficaLectura() {
        return graficaLectura;
    }

    public void setGraficaLectura(BarChartModel graficaLectura) {
        this.graficaLectura = graficaLectura;
    }

    public BarChartModel getGraficaRazona() {
        return graficaRazona;
    }

    public void setGraficaRazona(BarChartModel graficaRazona) {
        this.graficaRazona = graficaRazona;
    }

    public BarChartModel getGraficaComuni() {
        return graficaComuni;
    }

    public void setGraficaComuni(BarChartModel graficaComuni) {
        this.graficaComuni = graficaComuni;
    }

    public BarChartModel getGraficaCompe() {
        return graficaCompe;
    }

    public void setGraficaCompe(BarChartModel graficaCompe) {
        this.graficaCompe = graficaCompe;
    }

    public ChartData getData() {
        return data;
    }

    public void setData(ChartData data) {
        this.data = data;
    }

    public SimpleKMeans getKmeans() {
        return kmeans;
    }

    public void setKmeans(SimpleKMeans kmeans) {
        this.kmeans = kmeans;
    }

    public List<String> getClusterSBTyT() {
        return clusterSBTyT;
    }

    public void setClusterSBTyT(List<String> clusterSBTyT) {
        this.clusterSBTyT = clusterSBTyT;
    }

    public List<String> getCentroides() {
        return centroides;
    }

    public void setCentroides(List<String> centroides) {
        this.centroides = centroides;
    }

    public LineChartModel getCantPersonas() {
        return cantPersonas;
    }

    public void setCantPersonos(LineChartModel cantPersonas) {
        this.cantPersonas = cantPersonas;
    }

    public List<String> getAnios() {
        return anios;
    }

    public void setAnios(List<String> anios) {
        this.anios = anios;
    }

    public String getNombreInd() {
        return nombreInd;
    }

    public void setNombreInd(String nombreInd) {
        this.nombreInd = nombreInd;
    }

    public boolean isOcultarS() {
        return ocultarS;
    }

    public void setOcultarS(boolean ocultarS) {
        this.ocultarS = ocultarS;
    }

    public boolean isOcultarE() {
        return ocultarE;
    }

    public void setOcultarE(boolean ocultarE) {
        this.ocultarE = ocultarE;
    }

    public boolean isOcultarA() {
        return ocultarA;
    }

    public void setOcultarA(boolean ocultarA) {
        this.ocultarA = ocultarA;
    }

    public boolean isOcultarG() {
        return ocultarG;
    }

    public void setOcultarG(boolean ocultarG) {
        this.ocultarG = ocultarG;
    }

    public boolean isOcultarC() {
        return ocultarC;
    }

    public void setOcultarC(boolean ocultarC) {
        this.ocultarC = ocultarC;
    }

    public String getDefinicion() {
        return definicion;
    }

    public void setDefinicion(String definicion) {
        this.definicion = definicion;
    }

}