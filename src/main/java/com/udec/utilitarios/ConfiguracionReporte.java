/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.utilitarios;

import com.udec.pojo.DatosPrediccion;
import com.udec.pojo.ModeloReporte;
import com.udec.pojo.Reporte;
import com.udec.pojo.ResultadosPrimaria;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Clase encargada de configurar los reportes
 *
 * @author Corin Jazmin Viracachá
 */
public class ConfiguracionReporte {

    private Reporte reporteInforme;

    public Reporte getReporteInforme() {
        return reporteInforme;
    }

    public void setReporteInforme(Reporte reporteInforme) {
        this.reporteInforme = reporteInforme;
    }

    /**
     * Metodo encargado de cargar los resultados a visualizar en el reporte
     *
     * @param reporte Objeto con los datos del reporte
     * @param path url del reporte
     * @param indicador indicador por el cual se genera el reporte
     */
    public void cargarResultados(Reporte reporte, String path, String indicador) {
        reporteInforme = new Reporte();
        reporteInforme.setTituloInicial(reporte.getTituloInicial());
        if (indicador.equals("Prediccion")) {
            generarReportePrediccion(reporte, path);
        } else {
            for (Map.Entry<String, List<ResultadosPrimaria>> datos : reporte.getListados().entrySet()) {
                if (datos.getKey().contains("pieIndicador")) {
                    cargarPieIndicador(datos.getValue());
                    reporteInforme.setTituloPie(reporte.getTituloPie());
                }
            }
            cargarDatosGraficas(indicador, reporte);
            cargarReporte(reporteInforme, path);
        }
    }

    /**
     * Metodo encargado de generar reporte de prediccion
     *
     * @param reporte objeto reporte
     * @param path url del reporte
     */
    private void generarReportePrediccion(Reporte reporte, String path) {
        for (Map.Entry<String, List<DatosPrediccion>> datos : reporte.getListadoPrediccion().entrySet()) {
            if (!datos.getKey().contains("Indicador")) {
                cargarDatosGenero(datos.getValue(), reporte);
            }
        }
        cargarReporte(reporte, path);

    }

    /**
     * Metodo encargado de obtener los datos a mostrar en la grafica de la torta
     *
     * @param datos Lista de resultados
     */
    private void cargarPieIndicador(List<ResultadosPrimaria> datos) {
        for (ResultadosPrimaria resultados : datos) {
            ModeloReporte reporteDatos = new ModeloReporte(1);
            reporteDatos.setLabel(resultados.getIndicador());
            reporteDatos.getPuntaje()[0] = resultados.getPuntaje();
            reporteInforme.getDatosReporte().add(reporteDatos);
        }

    }

    /**
     * Metodo encargado de la conexion con el reporte
     *
     * @param reporte Objeto reporte
     * @param path url del reporte
     */
    public void cargarReporte(Reporte reporte, String path) {
        try {
            JasperPrint jasper = null;
            String fondo = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/images/fondo.jpg");
            List<Reporte> reporteLista = new ArrayList<>();
            String titulo = reporte.getTituloPie();
            reporte.setTituloPie(tituloReporte(titulo));
            reporteLista.add(reporte);
            JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(reporteLista);
            if (reporte.getTituloInicial().contains("PREDICTIVO")) {
                reporte.getListadoPrediccionIndicador().put("imagen", fondo);
                jasper = JasperFillManager.fillReport(path, reporte.getListadoPrediccionIndicador(), beanCollection);
            } else {
                HashMap<String, Object> parametros = new HashMap<>();
                parametros.put("imagen", fondo);
                jasper = JasperFillManager.fillReport(path, parametros, beanCollection);
            }
            HttpServletResponse httpServlet = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            httpServlet.addHeader("Content-disposition", "attachment; filename=reporte" + ".pdf");
            ServletOutputStream servletOutputStream = httpServlet.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasper, servletOutputStream);
            FacesContext.getCurrentInstance().responseComplete();
        } catch (JRException ex) {
            Logger.getLogger(ConfiguracionReporte.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConfiguracionReporte.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Metodo encargado de establecer el nombre del reporte
     *
     * @param tituloP, contiene el nombre del indicador
     * @return Nombre del indicador
     */
    private String tituloReporte(String tituloP) {
        switch (tituloP) {
            case "fami_educacionpadre":
                return "Educacion del padre";
            case "fami_educacionmadre":
                return "Educacion de la madre";
            case "fami_tienecomputador":
                return "Acceso a computador";
            case "fami_tieneinternet":
                return "Acceso a internet";
            case "fami_tieneconsolavideojuegos":
                return "Acceso a consola de videojuegos";
            case "punt_lenguaje":
                return "Puntaje en lenguaje";
            case "punt_matematicas":
                return "Puntaje en matematicas";
            default:
                return "";
        }
    }

    /**
     * Metodo para crear un lisatdo con la informacion
     *
     * @param datosReporte Informacion del reporte
     * @param tipo tipo de grafica a generar
     */
    private void anadirListado(ModeloReporte[] datosReporte, int tipo) {
        for (int i = 0; i < datosReporte.length; i++) {
            switch (tipo) {
                case 1:
                    this.reporteInforme.getDatoslineGenero().add(datosReporte[i]);
                    break;
                case 2:
                    this.reporteInforme.getDatoslineIndicadorAno().add(datosReporte[i]);
                    break;
                case 3:
                    this.reporteInforme.getDatoslineIndicadorSaber().add(datosReporte[i]);
                    break;
                case 4:
                    this.reporteInforme.getRadarIndicadores().add(datosReporte[i]);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Metodo para validar que datos se tendran en cuenta en la grafica
     *
     * @param indicador indicador seleccionado
     * @param reporte Objeto reporte
     */
    private void cargarDatosGraficas(String indicador, Reporte reporte) {
        int graficaLineIndicador = 0;
        if (indicador.contains("fami_edu")) {
            graficaLineIndicador = 5;
        }
        if (indicador.contains("fami_ti")) {
            graficaLineIndicador = 2;
        }
        if (indicador.contains("punt")) {
            graficaLineIndicador = 1;
        }
        if (!reporte.getTituloInicial().contains("3")) {
            anadirListado(crearDataSetGrafica(graficaLineIndicador, "lineAnosIndi", reporte.getListados()), 2);
            anadirListado(crearDataSetGrafica(graficaLineIndicador, "lineSaber", reporte.getListados()), 3);
            anadirListado(cargarRadarIndicadores(reporte.getListados()), 4);
        }
        anadirListado(crearDataSetGrafica(2, "lineAnios", reporte.getListados()), 1);

    }

    /**
     * Metodo para generar dataSet de las graficas
     *
     * @param graficaLineIndicador, hace referencia a la grafica del indicador
     * @param tipoChart tipo de grafica
     * @param listados listado de resultados
     * @return modelo reporte
     */
    private ModeloReporte[] crearDataSetGrafica(int graficaLineIndicador, String tipoChart, Map<String, List<ResultadosPrimaria>> listados) {
        int cantidad = 0;
        int contador = 0;
        int i = 0;
        for (Map.Entry<String, List<ResultadosPrimaria>> datos : listados.entrySet()) {
            if (datos.getKey().contains(tipoChart)) {
                cantidad = datos.getValue().size();
                break;
            }
        }
        ModeloReporte[] datosReporte = new ModeloReporte[cantidad];
        try {
            for (Map.Entry<String, List<ResultadosPrimaria>> datosCarga : listados.entrySet()) {
                if (datosCarga.getKey().contains(tipoChart)) {
                    for (ResultadosPrimaria res : datosCarga.getValue()) {
                        if (i == 0) {
                            datosReporte[contador] = new ModeloReporte(graficaLineIndicador);
                            if (tipoChart.contains("lineAn")) {
                                datosReporte[contador].setLabel(String.valueOf(res.getAno()));
                            } else {
                                datosReporte[contador].setLabel(res.getIndicador());
                            }
                        }
                        datosReporte[contador].getPuntaje()[i] = res.getPuntaje();
                        datosReporte[contador].getRespuesta()[i] = res.getRespuesta();
                        contador++;
                    }
                    contador = 0;
                    i++;
                }
            }
            return datosReporte;
        } catch (Exception ex) {
            System.out.println("Error " + ex + " " + i + "cantoda " + cantidad);
            return datosReporte;
        }
    }

    /**
     * Crear grafica radar de indicadores
     *
     * @param listados listado de los resultados
     * @return Modelo reporte con los datos de las graficas
     */
    private ModeloReporte[] cargarRadarIndicadores(Map<String, List<ResultadosPrimaria>> listados) {
        int cantidad = 0;
        int contador = 0;
        int i = 0;
        boolean primeraVez = true;
        for (Map.Entry<String, List<ResultadosPrimaria>> datos : listados.entrySet()) {
            if (datos.getKey().contains("radarAno")) {
                cantidad++;
            }
        }
        ModeloReporte[] datosReporte = new ModeloReporte[cantidad];
        for (Map.Entry<String, List<ResultadosPrimaria>> datosCarga : listados.entrySet()) {
            if (datosCarga.getKey().contains("radarAno")) {
                for (ResultadosPrimaria res : datosCarga.getValue()) {
                    if (primeraVez) {
                        String[] ano = datosCarga.getKey().split("_");
                        datosReporte[contador] = new ModeloReporte(7);
                        datosReporte[contador].setLabel(ano[1]);
                    }
                    datosReporte[contador].getPuntaje()[i] = res.getPuntaje();
                    String[] respuesta = res.getRespuesta().split(",");
                    datosReporte[contador].getRespuesta()[i] = respuesta[1];
                    i++;
                    primeraVez = false;
                }
                i = 0;
                contador++;
                primeraVez = true;
            }
        }
        return datosReporte;
    }

    /**
     * Metodo encargado de generar el modelo para el reporte
     *
     * @param listado listado de las predicciones
     * @param reporte Objeto reporte
     */
    private void cargarDatosGenero(List<DatosPrediccion> listado, Reporte reporte) {
        String primero = listado.get(0).getGenero();
        int posicion = 1;
        int i = 0;
        double probabilidad = 0;
        if (primero.equals("Mujer")) {
            posicion = 0;
        }
        for (DatosPrediccion datos : listado) {
            probabilidad = (double) Math.round(datos.getProbabilidad() * 100d) / 100d;
            if (posicion == 0) {
                ModeloReporte modeloGenero = new ModeloReporte(4);
                modeloGenero.getRespuesta()[posicion] = datos.getDescripcion();
                modeloGenero.getPuntaje()[posicion] = probabilidad;
                modeloGenero.getIndicador()[posicion] = tituloReporte(datos.getIndicador());
                reporte.getDatoslineGenero().add(modeloGenero);
            } else {
                reporte.getDatoslineGenero().get(i).getRespuesta()[posicion] = datos.getDescripcion();
                reporte.getDatoslineGenero().get(i).getPuntaje()[posicion] = probabilidad;
                i++;
            }
        }
    }
}
