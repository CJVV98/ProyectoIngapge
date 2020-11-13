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
@ManagedBean(name = "resultadosSaber11")
@ViewScoped

public class ResultadosSaber11 implements Serializable {

    String anioPr, tipoI, indicadorNombre, atributos, nomInd, prueba = "sb11_", definicion;
    boolean ocultarSoc = false, ocultarEco = true, ocultarAca = false, ocultarGlobal = false, ocultarCantidad = true;
    private transient Instances resultados, resultadosIngles11, resultadosLec, resultadosMate, resultadosCiencias, resultadosSociales;
    private static final SaberSuperiorBD REPOSITORIO = new SaberSuperiorBD();
    private BarChartModel graphGenero, graphIngles, graphLectura, graphCiencias, graphMatematicas, graphSociales;
    int numClusters = 0;
    private transient List<String> clusterSB11, centroides11, titulo, anios;
    private transient List<Number> resultadosMujer, resultadosHombre, resultados01, resultados02, resultados03, resultados04, resultados05;
    BarChartDataSet serieH, serieM, serie0a20, serie20a40, serie40a60, serie60a80, serie80a100;
    ChartData datosGraph;
    private LineChartModel cantEstudiantes;
    private transient List<DatosGrafica> datos = new ArrayList();

    /*
     Método que inicializa gráficas
     */
    @PostConstruct
    public void init() {
        tipoI = "economico";
        graphGenero = new BarChartModel();
        graphCiencias = new BarChartModel();
        graphMatematicas = new BarChartModel();
        graphIngles = new BarChartModel();
        graphLectura = new BarChartModel();
        graphSociales = new BarChartModel();
        validarAnios();
        graficarCantidad();
        mostrarCantidad();
    }

    /*
     Permite el cambio según la selección de tipo en el radio button
     */
    public void cambiarIndicadores(ValueChangeEvent ev) {
        tipoI = ev.getNewValue().toString();
        if ("social".equals(tipoI)) {
            ocultarSoc = true;
            ocultarEco = false;
            ocultarAca = false;
        } else if ("academico".equals(tipoI)) {
            ocultarAca = true;
            ocultarEco = false;
            ocultarSoc = false;
        } else if ("economico".equals(tipoI)) {
            ocultarEco = true;
            ocultarAca = false;
            ocultarSoc = false;
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
     Método que valida la existencia de los indicadores en cada año
     */
    public void validarExistencia() {
        int cantidad = REPOSITORIO.consultarExistencia(anioPr, "sb11", indicadorNombre);
        if (cantidad > 0) {
            graficarAnalisis();
        } else {
            generarAnalisis();
        }
    }

    /*
     Genera el análisis para los diferentes núcleos
     */
    public void generarAnalisis() {
        try {
            ocultarCantidad = false;
            resultados = REPOSITORIO.listarIndicador(indicadorNombre, anioPr, prueba);
            ocultarGlobal = true;
            if (!resultados.isEmpty()) {
                clusterData(resultados, 50, true, "Genero");
                mostrarGrafica(graphGenero);
                resultadosIngles11 = REPOSITORIO.listarNucleo(indicadorNombre, anioPr, prueba, "punt_ingles");
                clusterData(resultadosIngles11, 200, false, "Ingles");
                mostrarGrafica(graphIngles);
                resultadosLec = REPOSITORIO.listarNucleo(indicadorNombre, anioPr, prueba, "punt_lectura_critica");
                clusterData(resultadosLec, 200, false, "Lectura");
                mostrarGrafica(graphLectura);
                resultadosMate = REPOSITORIO.listarNucleo(indicadorNombre, anioPr, prueba, "punt_matematicas");
                clusterData(resultadosMate, 200, false, "Matematicas");
                mostrarGrafica(graphMatematicas);
                resultadosCiencias = REPOSITORIO.listarNucleo(indicadorNombre, anioPr, prueba, "punt_c_naturales");
                clusterData(resultadosCiencias, 200, false, "Ciencias");
                mostrarGrafica(graphCiencias);
                resultadosSociales = REPOSITORIO.listarNucleo(indicadorNombre, anioPr, prueba, "punt_sociales_ciudadanas");
                clusterData(resultadosSociales, 200, false, "Sociales");
                mostrarGrafica(graphSociales);
            } else {
                ocultarGlobal = false;
                ocultarCantidad = true;
                generarMensaje("Este indicador no está en este año", "Indicador no disponible", FacesMessage.SEVERITY_INFO);
            }
        } catch (Exception ex) {
            generarMensaje("Este indicador no se encuentra en este año", "Indicador no disponible", FacesMessage.SEVERITY_INFO);
            ocultarGlobal = false;
            ocultarCantidad = true;
        }
    }

    /*
     Método que grafica los resultados del análisis alojados en BD
     */
    public void graficarAnalisis() {
        ocultarCantidad = false;
        ocultarGlobal = true;
        cambiarNombres();
        datos = REPOSITORIO.listarAnalisis(anioPr, indicadorNombre, "sb11", "Genero");
        llenarListasPuntajes();
        generarGrafica(clusterSB11, centroides11);
        mostrarGrafica(graphGenero);

        datos = REPOSITORIO.listarAnalisis(anioPr, indicadorNombre, "sb11", "Ingles");
        llenarListasPuntajes();
        generarGraficaPuntajes(clusterSB11, centroides11);
        mostrarGrafica(graphIngles);

        datos = REPOSITORIO.listarAnalisis(anioPr, indicadorNombre, "sb11", "Lectura");
        llenarListasPuntajes();
        generarGraficaPuntajes(clusterSB11, centroides11);
        mostrarGrafica(graphLectura);

        datos = REPOSITORIO.listarAnalisis(anioPr, indicadorNombre, "sb11", "Matematicas");
        llenarListasPuntajes();
        generarGraficaPuntajes(clusterSB11, centroides11);
        mostrarGrafica(graphMatematicas);

        datos = REPOSITORIO.listarAnalisis(anioPr, indicadorNombre, "sb11", "Ciencias");
        llenarListasPuntajes();
        generarGraficaPuntajes(clusterSB11, centroides11);
        mostrarGrafica(graphCiencias);

        datos = REPOSITORIO.listarAnalisis(anioPr, indicadorNombre, "sb11", "Sociales");
        llenarListasPuntajes();
        generarGraficaPuntajes(clusterSB11, centroides11);
        mostrarGrafica(graphSociales);
    }

    /*
     Método para actualizar las listas de puntajes de la prueba
     */
    public void llenarListasPuntajes() {
        clusterSB11 = new ArrayList();
        centroides11 = new ArrayList();

        for (DatosGrafica dato : datos) {
            clusterSB11.add(dato.getCluster());
            centroides11.add(dato.getCentroide());
            if (dato.getAtributos().length() != 0) {
                atributos = dato.getAtributos();
            }

        }
    }

    /*
     Procesamiento de los datos por medio del algoritmo  kmeans
     */
    public void clusterData(Instances resultadosPrueba, int numCluster, boolean b, String titulo) {
        SimpleKMeans kmedias = new SimpleKMeans();
        kmedias.setSeed(10);
        numClusters = numCluster;
        try {
            kmedias.setPreserveInstancesOrder(true);
            kmedias.setNumClusters(numClusters);
            kmedias.setMaxIterations(500);
            kmedias.setCanopyMaxNumCanopiesToHoldInMemory(100);
            kmedias.setCanopyMinimumCanopyDensity(2.0);
            kmedias.setCanopyPeriodicPruningRate(10000);
            kmedias.setCanopyT1(-1.25);
            kmedias.setCanopyT2(-1.0);
            kmedias.buildClusterer(resultadosPrueba);
            Instances centroids = kmedias.getClusterCentroids();
            clusterSB11 = new ArrayList();
            centroides11 = new ArrayList();
            atributos = (centroids.attribute(1)).toString().replace("'", "");

            for (int i = 0; i < centroids.size(); i++) {
                int tamanio = (int) kmedias.getClusterSizes()[i];
                String cluster = "Cluster:" + i + " Tamanio:" + tamanio;
                clusterSB11.add(cluster);
                centroides11.add(centroids.get(i).toString().replace("'", ""));
                REPOSITORIO.insertarAnalisis(new DatosGrafica("sb11", anioPr, titulo, indicadorNombre, centroids.get(i).toString().replace("'", ""), cluster, atributos));
            }

        } catch (Exception e) {
            Logger.getLogger(ResultadosSaber11.class.getName()).log(Level.SEVERE, "Error", e);
        }
        if (b) {
            this.generarGrafica(clusterSB11, centroides11);
        } else {
            this.generarGraficaPuntajes(clusterSB11, centroides11);
        }

    }

    /*
     Llena las listas que se van a graficar con la infromación del análisis
     */
    public void generarGrafica(List<String> result, List<String> centroides) {
        int contador;
        graphGenero = new BarChartModel();
        resultadosHombre = new ArrayList<>();
        resultadosMujer = new ArrayList<>();
        datosGraph = new ChartData();
        String separador = Pattern.quote(",");
        String[] nombres = atributos.split(separador);
        titulo = new ArrayList();
        for (int i = 0; i < nombres.length; i++) {
            if (i == 0) {
                String separador1 = Pattern.quote("{");
                String[] aux = nombres[i].split(separador1);
                titulo.add(aux[1]);
            } else {
                if (i == (nombres.length - 1)) {
                    String separador2 = Pattern.quote("}");
                    String[] aux = nombres[i].split(separador2);
                    titulo.add(aux[0]);
                } else {
                    titulo.add(nombres[i].replace("'", ""));
                }
            }
        }
        serieH = new BarChartDataSet();
        serieM = new BarChartDataSet();
        estiloGraficas(serieH, "Hombres", "rgb(102, 178, 255)", "rgb(128, 128, 128)", 1);
        estiloGraficas(serieM, "Mujeres", "rgb(178, 9, 178)", "rgb(255, 159, 64)", 1);

        datosGraph.setLabels(titulo);
        for (String aux1 : titulo) {
            contador = 0;
            int sumamu = 0;
            int sumahom = 0;
            for (String aux : centroides) {
                if (aux.contains(aux1)) {
                    if (aux.contains("F,")) {
                        String separador1 = Pattern.quote("Tamanio:");
                        String[] auxcen = result.get(contador).split(separador1);
                        sumamu = sumamu + Integer.parseInt(auxcen[1]);
                    } else if (aux.contains("M,")) {
                        String separador1 = Pattern.quote("Tamanio:");
                        String[] auxcen = result.get(contador).split(separador1);
                        sumahom = sumahom + Integer.parseInt(auxcen[1]);
                    }
                }
                contador++;
            }
            resultadosMujer.add(sumamu);
            resultadosHombre.add(sumahom);
        }

        serieM.setData(resultadosMujer);
        serieH.setData(resultadosHombre);
        datosGraph.addChartDataSet(serieH);
        datosGraph.addChartDataSet(serieM);
    }

    /*
     Determina los valores de colores y estilo de las gráficas
     */
    public void estiloGraficas(BarChartDataSet serie, String titulo, String colores, String coloresBorde, int borde) {
        cambiarNombres();
        serie.setLabel(titulo);
        serie.setBackgroundColor(colores);
        serie.setBorderColor(coloresBorde);
        serie.setBorderWidth(borde);
    }

    /*
     Guarda la infomación a graficar de puntajes
     */
    public void generarGraficaPuntajes(List<String> resultados, List<String> centroides) {
        int cont;
        resultados01 = new ArrayList<>();
        resultados02 = new ArrayList<>();
        resultados03 = new ArrayList<>();
        resultados04 = new ArrayList<>();
        resultados05 = new ArrayList<>();
        datosGraph = new ChartData();
        serie0a20 = new BarChartDataSet();
        serie20a40 = new BarChartDataSet();
        serie40a60 = new BarChartDataSet();
        serie60a80 = new BarChartDataSet();
        serie80a100 = new BarChartDataSet();

        estiloGraficas(serie0a20, "De 0 a 20", "rgb(147, 112, 219)", "rgb(200, 128, 128)", 1);
        estiloGraficas(serie20a40, "De 20 a 40", "rgb(135, 206, 250)", "rgb(128, 200, 128)", 1);
        estiloGraficas(serie40a60, "De 40 a 60", "rgb(0, 153, 153)", "rgb(128, 128, 200)", 1);
        estiloGraficas(serie60a80, "De 60 a 80", "rgb(123,104,238)", "rgb(255, 128, 128)", 1);
        estiloGraficas(serie80a100, "De 80 a 100", "rgb(178, 102, 255)", "rgb(128, 255, 128)", 1);

        datosGraph.setLabels(titulo);

        for (String auxiliar : titulo) {
            cont = 0;
            int total1 = 0, total2 = 0, total3 = 0, total4 = 0, total5 = 0;
            for (String aux : centroides) {
                String separador2 = Pattern.quote(",");
                String[] auxsep = aux.split(separador2);
                int number = Integer.parseInt(auxsep[0]);
                String separador1 = Pattern.quote("Tamanio:");
                String[] auxcen = resultados.get(cont).split(separador1);
                if (aux.contains(auxiliar)) {
                    if (number >= 0 && number < 20) {
                        total1 = total1 + Integer.parseInt(auxcen[1]);
                    } else if (number >= 20 && number < 40) {
                        total2 = total2 + Integer.parseInt(auxcen[1]);
                    } else if (number >= 40 && number < 60) {
                        total3 = total3 + Integer.parseInt(auxcen[1]);
                    } else if (number >= 60 && number < 100) {
                        total4 = total4 + Integer.parseInt(auxcen[1]);
                    } else {
                        total5 = total5 + Integer.parseInt(auxcen[1]);
                    }
                }
                cont++;
            }
            resultados01.add(total1);
            resultados02.add(total2);
            resultados03.add(total3);
            resultados04.add(total4);
            resultados05.add(total5);
        }
        serie0a20.setData(resultados01);
        serie20a40.setData(resultados02);
        serie40a60.setData(resultados03);
        serie60a80.setData(resultados04);
        serie80a100.setData(resultados05);

        datosGraph.addChartDataSet(serie0a20);
        datosGraph.addChartDataSet(serie20a40);
        datosGraph.addChartDataSet(serie40a60);
        datosGraph.addChartDataSet(serie60a80);
        datosGraph.addChartDataSet(serie80a100);

    }

    /*
     Guarda los datos a graficar de cantidad de estudiantes
     */
    public void graficarCantidad() {
        List<Object> cantMujeres = new ArrayList();
        List<Object> cantHombres = new ArrayList();
        Calendar calendario = Calendar.getInstance();
        int anioActual = calendario.get(Calendar.YEAR);

        for (int i = 2016; i < anioActual; i++) {
            cantMujeres.add(REPOSITORIO.listarMujer(String.valueOf(i), prueba));
            cantHombres.add(REPOSITORIO.listarHombre(String.valueOf(i), prueba));
        }

        cantEstudiantes = new LineChartModel();
        datosGraph = new ChartData();

        LineChartDataSet datosM = new LineChartDataSet();
        datosM.setLineTension(0.1);
        datosM.setLabel("Mujeres");
        datosM.setBorderColor("rgb(178, 9, 178)");
        datosM.setData(cantMujeres);
        datosGraph.addChartDataSet(datosM);

        LineChartDataSet datosH = new LineChartDataSet();
        datosH.setLineTension(0.1);
        datosH.setData(cantHombres);
        datosH.setBorderColor("rgb(102, 178, 255)");
        datosH.setLabel("Hombres");
        datosGraph.addChartDataSet(datosH);

        datosGraph.setLabels(anios);
        cantEstudiantes.setData(datosGraph);
    }

    public void mostrarCantidad() {
        LineChartOptions graficaOpt = new LineChartOptions();
        CartesianScales escalasC = new CartesianScales();
        CartesianLinearAxes ejes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        ejes.setTicks(ticks);
        ejes.setScaleLabel(null);
        escalasC.addYAxesData(ejes);
        graficaOpt.setScales(escalasC);
        cantEstudiantes.setOptions(graficaOpt);
    }

    /*
     Genera gráficas puntajes 
     */
    public BarChartOptions graficarPuntajes() {
        BarChartOptions graficaOpt = new BarChartOptions();
        CartesianScales escalasC = new CartesianScales();
        CartesianLinearAxes ejes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        ejes.setTicks(ticks);
        ejes.setScaleLabel(null);
        escalasC.addYAxesData(ejes);
        graficaOpt.setScales(escalasC);
        return graficaOpt;
    }

    public void mostrarGrafica(BarChartModel grafica) {
        grafica.setData(datosGraph);
        BarChartOptions options = graficarPuntajes();
        grafica.setOptions(options);
    }

    /*
     Cambia los nombres de los indicadores y su definición
     */
    public void cambiarNombres() {
        switch (indicadorNombre) {
            case "fami_educacionmadre":
                nomInd = "Educación madre";
                definicion = "Educación madre: Este indicador muestra con qué niveles educativos cuentan las madres de los estudiantes, estando clasificados en los siguientes atributos: ninguno, no sabe, no aplica, primaria incompleta, primaria completa, secundaria incompleta, secundaria completa, técnica y tecnológica incompleta, técnica y tecnológica completa, educación profesional incompleta, educación profesional completa y postgrado. ";
                break;
            case "fami_educacionpadre":
                nomInd = "Educación padre";
                definicion = "Educación padre: Este indicador muestra con qué niveles educativos cuentan los padres de los estudiantes, estando clasificados en los siguientes atributos: ninguno, no sabe, no aplica, primaria incompleta, primaria completa, secundaria incompleta, secundaria completa, técnica y tecnológica incompleta, técnica y tecnológica completa, educación profesional incompleta, educación profesional completa y postgrado. ";
                break;
            case "fami_estratovivienda":
                nomInd = "Estrato de vivienda";
                definicion = "Estrato de vivienda: Indica la clasificación socioeconómica jerarquizada del país, sus categorías comprenden desde el estrato 1 hasta el estrato 6.";
                break;
            case "fami_tienecomputador":
                nomInd = "Tiene computador";
                definicion = "Tiene computador: Este indicador nos señala qué estudiantes cuentan con computador en sus hogares y quiénes no.";
                break;
            case "fami_tieneserviciotv":
                nomInd = "Servicio TV";
                definicion = "Tiene servicio TV: Este indicador nos señala qué estudiantes cuentan con el servicio de televisión en sus hogares y quiénes no.";
                break;
            case "cole_area_ubicacion":
                nomInd = "Área ubicación colegio";
                definicion = "Área de ubicación de colegio: Se refiere a la zona donde queda ubicado el colegio donde el estudiante pertenecía al momento de presentación de la prueba. Sus atributos se dividen en Rural y Urbano.";
                break;
            case "cole_jornada":
                nomInd = "Jornada del colegio";
                definicion = "Jornada del colegio: Este indicador refleja dentro de sus categorías los diferentes horarios de asistencia a clase o jornadas que tienen los colegios en el país.";
                break;
            default:
                nomInd = "Género del colegio";
                definicion = "Género del colegio: Este indicador hace referencia al género por el cual se encuentra clasificado el colegio al que pertenecen los estudiantes que presentan la prueba. Puede ser Femenino, Masculino o Mixto.";
                break;
        }
    }

    /*
     Método para generar mensajes
     */
    private void generarMensaje(String mensaje, String headerMensaje, FacesMessage.Severity tipoError) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage("mensaje-info", new FacesMessage(tipoError, headerMensaje, mensaje));
    }

    public String getAnioPr() {
        return anioPr;
    }

    public void setAnioPr(String anioPr) {
        this.anioPr = anioPr;
    }

    public String getIndicadorNombre() {
        return indicadorNombre;
    }

    public void setIndicadorNombre(String indicadorNombre) {
        this.indicadorNombre = indicadorNombre;
    }

    public String getTipoI() {
        return tipoI;
    }

    public void setTipoI(String tipoI) {
        this.tipoI = tipoI;
    }

    public String getAtributos() {
        return atributos;
    }

    public void setAtributos(String atributos) {
        this.atributos = atributos;
    }

    public boolean isOcultarSoc() {
        return ocultarSoc;
    }

    public void setOcultarSoc(boolean ocultarSoc) {
        this.ocultarSoc = ocultarSoc;
    }

    public boolean isOcultarEco() {
        return ocultarEco;
    }

    public void setOcultarEco(boolean ocultarEco) {
        this.ocultarEco = ocultarEco;
    }

    public boolean isOcultarAca() {
        return ocultarAca;
    }

    public void setOcultarAca(boolean ocultarAca) {
        this.ocultarAca = ocultarAca;
    }

    public boolean isOcultarGlobal() {
        return ocultarGlobal;
    }

    public void setOcultarGlobal(boolean ocultarGlobal) {
        this.ocultarGlobal = ocultarGlobal;
    }

    public boolean isOcultarCantidad() {
        return ocultarCantidad;
    }

    public void setOcultarCantidad(boolean ocultarCantidad) {
        this.ocultarCantidad = ocultarCantidad;
    }

    public Instances getResultadosMate() {
        return resultadosMate;
    }

    public void setResultadosMate(Instances resultadosMate) {
        this.resultadosMate = resultadosMate;
    }

    public Instances getResultadosCiencias() {
        return resultadosCiencias;
    }

    public void setResultadosCiencias(Instances resultadosCiencias) {
        this.resultadosCiencias = resultadosCiencias;
    }

    public Instances getResultadosSociales() {
        return resultadosSociales;
    }

    public void setResultadosSociales(Instances resultadosSociales) {
        this.resultadosSociales = resultadosSociales;
    }

    public Instances getResultados() {
        return resultados;
    }

    public void setResultados(Instances resultados) {
        this.resultados = resultados;
    }

    public Instances getResultadosIngles11() {
        return resultadosIngles11;
    }

    public void setResultadosIngles11(Instances resultadosIngles11) {
        this.resultadosIngles11 = resultadosIngles11;
    }

    public Instances getResultadosLec() {
        return resultadosLec;
    }

    public void setResultadosLec(Instances resultadosLec) {
        this.resultadosLec = resultadosLec;
    }

    public BarChartModel getGraphGenero() {
        return graphGenero;
    }

    public void setGraphGenero(BarChartModel graphGenero) {
        this.graphGenero = graphGenero;
    }

    public BarChartModel getGraphIngles() {
        return graphIngles;
    }

    public void setGraphIngles(BarChartModel graphIngles) {
        this.graphIngles = graphIngles;
    }

    public BarChartModel getGraphLectura() {
        return graphLectura;
    }

    public void setGraphLectura(BarChartModel graphLectura) {
        this.graphLectura = graphLectura;
    }

    public BarChartModel getGraphCiencias() {
        return graphCiencias;
    }

    public void setGraphCiencias(BarChartModel graphCiencias) {
        this.graphCiencias = graphCiencias;
    }

    public BarChartModel getGraphMatematicas() {
        return graphMatematicas;
    }

    public void setGraphMatematicas(BarChartModel graphMatematicas) {
        this.graphMatematicas = graphMatematicas;
    }

    public BarChartModel getGraphSociales() {
        return graphSociales;
    }

    public void setGraphSociales(BarChartModel graphSociales) {
        this.graphSociales = graphSociales;
    }

    public LineChartModel getCantEstudiantes() {
        return cantEstudiantes;
    }

    public void setCantEstudiantes(LineChartModel cantEstudiantes) {
        this.cantEstudiantes = cantEstudiantes;
    }

    public List<String> getAnios() {
        return anios;
    }

    public void setAnios(List<String> anios) {
        this.anios = anios;
    }

    public String getNomInd() {
        return nomInd;
    }

    public void setNomInd(String nomInd) {
        this.nomInd = nomInd;
    }

    public String getDefinicion() {
        return definicion;
    }

    public void setDefinicion(String definicion) {
        this.definicion = definicion;
    }

}