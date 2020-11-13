/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.utilitarios;

import com.udec.controller.Login;
import com.udec.pojo.Indicador;
import com.udec.pojo.Reporte;
import com.udec.pojo.ResultadosPrimaria;
import com.udec.pojo.Tabla;
import com.udec.repositorio.RepositorioPrueba;
import com.udec.repositorio.RepositorioResultados;
import com.udec.repositorio.RepositorioTablas;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.line.LineChartDataSet;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.SelectedTag;

/**
 * Clase encargada de realizar análisis predictivo
 *
 * @author Corin Viracacha
 */
public class AnalisisPruebas implements Serializable {

    private RepositorioResultados repositorio;
    private transient List<String> colores;
    private int color = 0;

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

    public RepositorioResultados getRepositorio() {
        if (repositorio == null) {
            repositorio = new RepositorioResultados();
        }
        return repositorio;
    }

    public void setRepositorio(RepositorioResultados repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Generar análisis con algoritmo J48
     *
     * @param indicador indicador a analizar
     * @param ano año a analizar
     * @param prueba prueba a analizar
     * @return listado de resultados
     */
    public List<ResultadosPrimaria> crearResultados(String indicador, int ano, String prueba) {
        List<ResultadosPrimaria> resultados = new ArrayList<>();
        try {
            repositorio = new RepositorioResultados();
            SimpleKMeans kmeans = new SimpleKMeans();
            kmeans.setNumClusters(2);
            kmeans.setMaxIterations(10000);
            kmeans.setDisplayStdDevs(false);
            kmeans.setInitializationMethod(new SelectedTag(SimpleKMeans.KMEANS_PLUS_PLUS, SimpleKMeans.TAGS_SELECTION));
            kmeans.setSeed(30);
            kmeans.setPreserveInstancesOrder(true);
            Instances datos = new RepositorioPrueba().seleccionar(indicador, ano, prueba);
            kmeans.buildClusterer(datos);
            Instances centroids = kmeans.getClusterCentroids();
            Double totalRegistros = calculoTotal(kmeans);

            for (int i = 0; i < kmeans.getNumClusters(); i++) {
                Double respuesta = (double) Math.round(kmeans.getClusterSizes()[i] / totalRegistros * 100d) / 100d;
                resultados.add(new ResultadosPrimaria(centroids.instance(i).toString(), respuesta));
                repositorio.insertarResultados("estu_genero", indicador, centroids.instance(i).toString(), respuesta, prueba, ano);
            }
        } catch (Exception ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error con implementaciÃ³n del algoritmo KMeans", ex);
        }
        return resultados;
    }

    /**
     * Calculo de cluster asignados
     *
     * @param kmeans algoritmo
     * @return resultado
     */
    private Double calculoTotal(SimpleKMeans kmeans) {
        Double resultado = 0.0;
        for (int i = 0; i < kmeans.getNumClusters(); i++) {
            resultado = resultado + kmeans.getClusterSizes()[i];
        }
        return resultado;
    }

    /**
     * Metodo usado para asignar la respueta
     *
     * @param indicador indicador seleccionado
     * @param posicion posicion de consulta
     * @param respuesta respuesta del indicador
     * @return indicador
     */
    public Indicador calculoAnual(Indicador indicador, int posicion, String respuesta) {
        String[] resultadoIndicador = new String[2];
        if (!respuesta.isEmpty()) {
            resultadoIndicador = respuesta.split(",");
        }
        switch (posicion) {
            case 0:
                indicador.setEducacionMadre(resultadoIndicador[1]);
                break;

            case 1:
                indicador.setEducacionPadre(resultadoIndicador[1]);
                break;

            case 2:
                indicador.setComputador(resultadoIndicador[1]);
                break;

            case 3:
                indicador.setConsola(resultadoIndicador[1]);
                break;

            case 4:
                indicador.setAccesoI(resultadoIndicador[1]);
                break;

            case 5:
                indicador.setLenguaje(resultadoIndicador[1]);
                break;

            case 6:
                indicador.setMatematica(resultadoIndicador[1]);
                break;
            default:
                break;
        }
        return indicador;
    }

    /**
     * Metodo para cargar los años existentes
     *
     * @param prueba nombre de la prueba
     * @return listado de años
     */
    public List<Integer> cargarAnos(String prueba) {
        List<Integer> anos = new ArrayList<>();
        List<Tabla> anosCargados = new RepositorioTablas().cargarAnosPresentacion(prueba);
        for (Tabla anoPresentacion : anosCargados) {
            if (anoPresentacion.getCantidad() == 1 && anoPresentacion.getAno() > 2016) {
                anos.add(anoPresentacion.getAno());
            } else if (anoPresentacion.getCantidad() == 2 && anoPresentacion.getAno() < 2017) {
                anos.add(anoPresentacion.getAno());
            }
        }
        return anos;
    }

    /**
     * Metodo encargado de insertar resultados de indicador
     *
     * @param prueba prueba consultada
     * @param ano año consultado
     * @param indicador indicador consultado
     */
    public void insertarResultadosIndicador(String prueba, int ano, String indicador) {
        SimpleKMeans kmeans = new SimpleKMeans();
        int cantidadCluster = calculoCantidadCluster(indicador);
        if (cantidadCluster > 0) {
            try {
                kmeans.setNumClusters(cantidadCluster);
                kmeans.setMaxIterations(10000);
                kmeans.setDisplayStdDevs(false);
                kmeans.setInitializationMethod(new SelectedTag(SimpleKMeans.KMEANS_PLUS_PLUS, SimpleKMeans.TAGS_SELECTION));
                kmeans.setSeed(30);
                kmeans.setPreserveInstancesOrder(false);
                Instances datos = new RepositorioPrueba().seleccionarIndicador(indicador, ano, prueba);
                kmeans.buildClusterer(datos);
                Instances centroids = kmeans.getClusterCentroids();
                Double totalRegistros = calculoTotal(kmeans);
                repositorio = new RepositorioResultados();
                for (int i = 0; i < kmeans.getNumClusters(); i++) {
                    Double respuesta = (double) Math.round(kmeans.getClusterSizes()[i] / totalRegistros * 100d) / 100d;
                    repositorio.insertarResultadosIndicador(indicador, centroids.instance(i).toString(), respuesta, prueba, ano);
                }
            } catch (Exception ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error con implementaciÃ³n del algoritmo KMeans", ex);
            }
        }
    }

    /**
     * Metodo para determinar la cantidad de centroides
     *
     * @param indicador
     * @return cantidad de centroides
     */
    private int calculoCantidadCluster(String indicador) {
        if (indicador.contains("fami_educacionpadre") || indicador.contains("fami_educacionmadre")) {
            return 5;
        } else if (indicador.contains("punt_lenguaje") || indicador.contains("punt_matematicas")) {
            return 0;
        } else {
            return 2;
        }
    }

    /**
     * Cargar grafica lineal de años
     *
     * @param resultados listado de resultados
     * @param data Objeto Char
     * @param reporte objeto reporte
     * @param colores lista de colores
     * @return Data del char
     */
    public ChartData cargarGraficaLinealAnos(List<ResultadosPrimaria> resultados, ChartData data, Reporte reporte, List<String> colores) {
        int contador = 1;
        color = 0;
        String primerIndicador = "";
        String[] resp = null;
        primerIndicador = resultados.get(0).getIndicador();
        List<ResultadosPrimaria> resultadosAnos = new ArrayList<>();
        for (ResultadosPrimaria resultadoAno : resultados) {
            if (primerIndicador.contains(resultadoAno.getIndicador())) {
                resultadosAnos.add(resultadoAno);
                resp = resultadoAno.getIndicador().split(",");
            } else {
                primerIndicador = resultadoAno.getIndicador();
                LineChartDataSet dataSet = new LineChartDataSet();
                dataSet = cargarDataSetIndicadorAnos(resultadosAnos, dataSet, resp[1], colores);
                reporte.getListados().put("lineAnosIndi_" + resp[1], resultadosAnos);
                data.addChartDataSet(dataSet);
                resultadosAnos = new ArrayList<>();
                resultadosAnos.add(resultadoAno);
            }
            if (contador == resultados.size()) {
                LineChartDataSet dataSet = new LineChartDataSet();
                dataSet = cargarDataSetIndicadorAnos(resultadosAnos, dataSet, resp[1], colores);
                reporte.getListados().put("lineAnosIndi_" + resp[1], resultadosAnos);
                data.addChartDataSet(dataSet);
            }
            contador++;
        }
        return data;
    }

    /**
     * Cargar grafica lineal de pruebas
     *
     * @param resultados listado de resultados
     * @param data Grafico Char
     * @param reporte reporte
     * @param colores listado de colores
     * @return Grafico Char
     */
    public ChartData cargarGraficaLinealPrueba(List<ResultadosPrimaria> resultados, ChartData data, Reporte reporte, List<String> colores) {
        int contador = 1;
        String primerIndicador = "";
        color = 0;
        String[] resp = null;
        primerIndicador = resultados.get(0).getRespuesta();
        List<ResultadosPrimaria> resultadosPrueba = new ArrayList<>();
        if ((resultados.size() == 2 || resultados.size() == 5)) {
            for (ResultadosPrimaria resultadoAno : resultados) {
                resultadosPrueba = new ArrayList<>();
                resp = resultadoAno.getRespuesta().split(",");
                resultadosPrueba.add(resultadoAno);
                LineChartDataSet dataSet = new LineChartDataSet(); 
                dataSet = cargarDataSetIndicadorAnos(resultadosPrueba, dataSet, resp[1], colores);
                reporte.getListados().put("lineSaber" + resp[1], resultadosPrueba);
                data.addChartDataSet(dataSet);
            
            }
        } else {
            resultadosPrueba = new ArrayList<>();
            for (ResultadosPrimaria resultadoAno : resultados) {
                if (primerIndicador.contains(resultadoAno.getRespuesta())) {
                    resultadosPrueba.add(resultadoAno);
                    resp = resultadoAno.getRespuesta().split(",");
                } else {
                    primerIndicador = resultadoAno.getRespuesta();
                    LineChartDataSet dataSet = new LineChartDataSet();
                    dataSet = cargarDataSetIndicadorAnos(resultadosPrueba, dataSet, resp[1], colores);
                    reporte.getListados().put("lineSaber" + resp[1], resultadosPrueba);
                    data.addChartDataSet(dataSet);
                    resultadosPrueba = new ArrayList<>();
                    resultadosPrueba.add(resultadoAno);
                }
                if (contador == resultados.size()) {
                    LineChartDataSet dataSet = new LineChartDataSet();
                    dataSet = cargarDataSetIndicadorAnos(resultadosPrueba, dataSet, resp[1], colores);
                    reporte.getListados().put("lineSaber" + resp[1], resultadosPrueba);
                    data.addChartDataSet(dataSet);
                }
                contador++;
            }
        }
        return data;
    }

    /**
     * Cargar data set indicadores por años
     *
     * @param resultadosGenero lisatdo de resultados por genero
     * @param dataSet Date set grafico char
     * @param titulo titulod de la grafica
     * @param colores listado de colores
     * @return DataSet Grafica lineal
     */
    private LineChartDataSet cargarDataSetIndicadorAnos(List<ResultadosPrimaria> resultadosGenero, LineChartDataSet dataSet, String titulo, List<String> colores) {
        List<Object> valores = new ArrayList<>();
        for (ResultadosPrimaria resultados : resultadosGenero) {
            valores.add(resultados.getPuntaje());
        }
        dataSet.setData(valores);
        dataSet.setFill(false);
        dataSet.setLabel(titulo);
        dataSet.setBorderColor("rgb(" + colores.get(color) + ")");
        color++;
        dataSet.setLineTension(0.1);
        return dataSet;
    }

    /**
     * CArgar indicadores de año y puntaje
     *
     * @param indicador nombre del indicador
     * @param prueba nombre de la prueba
     * @param reporte datos del reporte
     * @return listado de resultados
     */
    public List<ResultadosPrimaria> cargarIndicadorAnosPuntaje(String indicador, String prueba, Reporte reporte) {
        List<ResultadosPrimaria> resultados = new ArrayList<>();
        List<ResultadosPrimaria> resultadosMujeres = new ArrayList<>();
        resultados = new RepositorioResultados().cargarBrechaIndicador(prueba, indicador);
        for (ResultadosPrimaria resultado : resultados) {
            Double puntaje = 0.0;
            String[] respuesta = resultado.getRespuesta().split(",");
            if (resultado.getAno() > 2016) {
                puntaje = (double) Math.round((Double.valueOf(respuesta[1]) / 100) * 1000d) / 1000d;
            } else {
                puntaje = (double) Math.round(Double.valueOf(respuesta[1]) * 1000d) / 1000d;
            }
            if (respuesta[0].contains("Mujer")) {
                resultadosMujeres.add(new ResultadosPrimaria(puntaje, resultado.getAno()));
            }

        }
        reporte.getListados().put("lineAnosIndi_ano", resultadosMujeres);
        return resultadosMujeres;
    }

}
