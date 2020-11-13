/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.controller;


import com.udec.pojo.Grafico;
import com.udec.pojo.Indicador;
import com.udec.pojo.Reporte;
import com.udec.pojo.ResultadosPrimaria;
import com.udec.pojo.Tabla;
import com.udec.repositorio.RepositorioResultados;
import com.udec.utilitarios.AnalisisPruebas;
import com.udec.utilitarios.ConfiguracionReporte;
import com.udec.utilitarios.ManejoArchivo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import org.primefaces.PrimeFaces;

import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.ChartDataSet;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.elements.Elements;
import org.primefaces.model.charts.optionconfig.elements.ElementsLine;
import org.primefaces.model.charts.optionconfig.title.Title;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;
import org.primefaces.model.charts.radar.RadarChartDataSet;
import org.primefaces.model.charts.radar.RadarChartOptions;

/**
 * Clase encargada de analizar las pruebas saber noveno
 * @author Corin Viracacha
 */
@ManagedBean(name = "saberNoveno")
@ViewScoped
public class SaberNoveno implements Serializable {
    private static final String SABER = "saber9";  
    private String indicador;
    private String resultadoAno = "";
    private transient List<Integer> anos;
    private int ano;
    private int anoConsulta;
    private int anoSeleccionado;
    private transient List<Tabla> anosCargados;
    private transient List<Indicador> listadoInfoAno;
    private boolean visibilidad;
    private boolean visibilidadContenido;
    private RepositorioResultados repositorio;
    private AnalisisPruebas analisisPruebas;
    private transient Grafico grafico;
    private transient Reporte reporte;
    private transient List<String> colores;
    private int color=0;

    @PostConstruct
    public void init() {
        inicializarVariables();
    }
    /**
     * Metodo para inicializar variables o listas
     */
    private void inicializarVariables() {
        grafico = new Grafico();
        colores = new ManejoArchivo().obtener();
        listadoInfoAno = new ArrayList<>();
        analisisPruebas = new AnalisisPruebas();
        anos = new ArrayList<>();
        anos = analisisPruebas.cargarAnos(SABER);
        repositorio = new RepositorioResultados();
    }

  
  
    /**
     * Metodo para validar si los resultados existen, si no existen los crea.
     */
    public void calcularResultados() {
        reporte=new Reporte();
        grafico.setGraficoDatos(new PieChartModel());
        resultadoAno = "";
        List<ResultadosPrimaria> resultados = repositorio.obtenerResultados("estu_genero", indicador, SABER, ano);
        analizarAnosCargados();
        if (resultados.isEmpty()) {
            crearResultados();
            analisisPruebas.insertarResultadosIndicador(SABER, ano, indicador);
           
        } else {
            actualizarModeloResultados(resultados);
        }
        crearModeloRadar();
        cargarIndicadores(indicador);
        if (indicador.contains("punt")) {
            crearGraficaLinealPuntajes(analisisPruebas.cargarIndicadorAnosPuntaje(indicador, SABER, this.reporte), 0);
            cargarIndicadorPruebasPuntaje(indicador, ano);
        } else {
            cargarIndicadorAnos(indicador);
            cargarIndicadorPruebas(indicador, ano);
        }
        visibilidadContenido = true;
    }
    /**
     * Metodo para crear el analisis de los resultados
     */
    public void crearResultados(){
        List<ResultadosPrimaria> resultados = analisisPruebas.crearResultados(indicador, ano, SABER);
        actualizarModeloResultados(resultados);
       
    }
    /**
     * Metodo para cargar los resultados de indicador vs genero
     * @param resultados listado de resultados consultados
     */
    private void actualizarModeloResultados(List<ResultadosPrimaria> resultados) {
        PieChartDataSet dataSet = new PieChartDataSet();
        List<Number> valores = new ArrayList<>();
        List<String> indicadores = new ArrayList<>();
        this.reporte.getListados().put("pieIndicador", resultados);
    
        for (ResultadosPrimaria resultado : resultados) {
            valores.add(resultado.getPuntaje());
            indicadores.add(resultado.getIndicador());
        }
        dataSet.setData(valores);
        ChartData datos = new ChartData();
        List<String> bgColors = new ArrayList<>();
        bgColors.add("rgb(255, 99, 132)");
        bgColors.add("rgb(54, 162, 235)");
        dataSet.setBackgroundColor(bgColors);
        datos.addChartDataSet(dataSet);
        datos.setLabels(indicadores);
        grafico.getGraficoDatos().setData(datos);
     
    }
    /**
     * Metodo para cargar informacion en el grafico del Radar
     */
    public void crearModeloRadar() {
        color=0;
        ChartData dataSet = new ChartData();
        for (Tabla anoConsultado : anosCargados) {
            if(anoConsultado.getCantidad()==14){
                List<ResultadosPrimaria> listadoPrueba = repositorio.obtenerResultadosGlobales(anoConsultado.getAno(), SABER);
                this.reporte.getListados().put("radarAno_"+String.valueOf(anoConsultado.getAno()),listadoPrueba);
                dataSet.addChartDataSet(crearDataSetRadar(listadoPrueba, anoConsultado.getAno()));
                if (anoConsultado.getAno() == 2014) {
                    dataSet.setLabels(cargarEjes(listadoPrueba));
                }
            }
        }
        RadarChartOptions opciones = new RadarChartOptions();
        Elements elementos = new Elements();
        ElementsLine lineaElementos = new ElementsLine();
        lineaElementos.setTension(0);
        lineaElementos.setBorderWidth(3);
        elementos.setLine(lineaElementos);
        opciones.setElements(elementos);
        grafico.getGraficoPruebas().setOptions(opciones);
        grafico.getGraficoPruebas().setData(dataSet);
    }
    /**
     * Metodo para crear dataset para el grafico del radar
     * @param listadoPrueba resultados consultados
     * @param ano, corresponde al año de presentacion de las pruebas
     * @return Dataset dataset del grafico radar
     */
    private ChartDataSet crearDataSetRadar(List<ResultadosPrimaria> listadoPrueba, int ano) {
        listadoInfoAno = new ArrayList<>();
        RadarChartDataSet dataSet = new RadarChartDataSet();
        dataSet.setLabel(String.valueOf(ano));
        dataSet.setFill(true);
        dataSet.setBackgroundColor("rgba("+colores.get(color)+ ", 0)");
        dataSet.setBorderColor("rgb(" +colores.get(color)+ ")");
        dataSet.setPointBackgroundColor("rgb(" +colores.get(color)+ ")");
        dataSet.setPointBorderColor("#fff");
        dataSet.setPointHoverBackgroundColor("#fff");
        dataSet.setPointHoverBorderColor("rgb(" +colores.get(color)+")");
        color++;
        List<Number> datosNumericos = new ArrayList<>();
        int i = 0;
        Indicador indicadorMostar = new Indicador();
        for (ResultadosPrimaria resultado : listadoPrueba) {
            datosNumericos.add(resultado.getPuntaje());
            indicadorMostar = analisisPruebas.calculoAnual(indicadorMostar, i, resultado.getRespuesta());
            i++;
        }
        indicadorMostar.setAno(String.valueOf(ano));
        listadoInfoAno.add(indicadorMostar);
        dataSet.setData(datosNumericos);
        return dataSet;
    }
    /**
     * Metodo para mostrar informacion del dataset
     * @param event evento cuando selecciona un punto del grafico 
     */
    public void itemSelect(ItemSelectEvent event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Item selected",
                "Item Index: " + event.getItemIndex() + ", DataSet Index:" + event.getDataSetIndex());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    /**
     * Metodo para cargar los ejes del grafico del radar
     * @param listadoPrueba resultados consultados
     * @return un listado de string que corresponde a los ejes
     */
    private List<String> cargarEjes(List<ResultadosPrimaria> listadoPrueba) {
        List<String> indicadorEje = new ArrayList<>();
        for (ResultadosPrimaria resultado : listadoPrueba) {
            indicadorEje.add(extraerIndicador(resultado.getIndicador()));
        }
        return indicadorEje;
    }
    /**
     * Metodo para cargar solo los años que estan completamente analizados
     */
    private void analizarAnosCargados() {
        anosCargados = new ArrayList<>();
        List<Tabla> anosCargadoAux = new RepositorioResultados().cargarAnosAnalizado(SABER);
        for (Tabla anoPresentacion : anosCargadoAux) {
            if (anoPresentacion.getCantidad() == 14) {
                anosCargados.add(anoPresentacion);

            }
        }
    }
    /**
     * Metodo para cargar los indicadores por puntaje
     * @param indicador nombre del indicador
     */
    private void cargarIndicadores(String indicador) {
        List<ResultadosPrimaria> resultados;
        List<ResultadosPrimaria> resultadosHombres = new ArrayList<>();
        List<ResultadosPrimaria> resultadosMujeres = new ArrayList<>();
        resultados = new RepositorioResultados().cargarBrechaIndicadorSE(SABER, indicador);
        for (ResultadosPrimaria resultado : resultados) {
            String[] respuesta = resultado.getIndicador().split(",");
            if (respuesta[0].contains("Hombre")) {
                resultadosHombres.add(new ResultadosPrimaria( resultado.getAno(),resultado.getPuntaje(),respuesta[1]));
                
            } else {
                resultadosMujeres.add(new ResultadosPrimaria( resultado.getAno(),resultado.getPuntaje(),respuesta[1]));
            }
        }
        
        crearGraficaLineal(resultadosHombres, resultadosMujeres);
    }
    /**
     * Metodo para crear el grafico de analisis por años
     * @param resultadosHombres listado de resultados por hombre
     * @param resultadosMujeres listado de resultados por mujer
     */
    private void crearGraficaLineal(List<ResultadosPrimaria> resultadosHombres, List<ResultadosPrimaria> resultadosMujeres) {
        color=1;
        grafico.setLineaAnos(new LineChartModel());
        ChartData data = new ChartData();
        this.reporte.getListados().put("lineAnios_Hombre", resultadosHombres);
        this.reporte.getListados().put("lineAnios_Mujer", resultadosMujeres);
        data.addChartDataSet(cargarDataSetGraficaLineal(resultadosHombres, "Hombre"));
        data.addChartDataSet(cargarDataSetGraficaLineal(resultadosMujeres, "Mujer"));
        List<String> anosConsultados = new ArrayList<>();
        for (ResultadosPrimaria resultados : resultadosHombres) {
            anosConsultados.add(String.valueOf(resultados.getAno()));
        }
        data.setLabels(anosConsultados);
        LineChartOptions options = new LineChartOptions();
        Title titulo = new Title();
        titulo.setDisplay(true);
        options.setTitle(titulo);
        grafico.getLineaAnos().setOptions(options);
        grafico.getLineaAnos().setData(data);
    }
    
    /**
     * Crear DataSet para graficos lineales
     * @param resultadosGenero listado de los resultados
     * @param genero genero a crear data set
     * @return DataSet
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
        dataSet.setBorderColor("rgb(" +colores.get(color)+")");
        color++;
        dataSet.setLineTension(0.1);
        return dataSet;

    }
    /**
     * Metodo para cargar resultados de indicador por año
     * @param indicador nombre del indicador
     */
    private void cargarIndicadorAnos(String indicador) {
        List<ResultadosPrimaria> resultados;
        List<String> anosLista = new ArrayList<>();
        for (Tabla anoPresentacion : anosCargados) {
            anosLista.add(String.valueOf(anoPresentacion.getAno()));
        }
        grafico.setLineaAnosIndicador(new LineChartModel());
        ChartData data = new ChartData();
        resultados = new RepositorioResultados().cargarIndicadorAnos(SABER, indicador);
        data = analisisPruebas.cargarGraficaLinealAnos(resultados, data, this.reporte, colores);
        data.setLabels(anosLista);
        LineChartOptions options = new LineChartOptions();
        Title titulo = new Title();
        titulo.setDisplay(true);
        options.setTitle(titulo);
        grafico.getLineaAnosIndicador().setOptions(options);
        grafico.getLineaAnosIndicador().setData(data);
    }
    /**
     * Metodo para cargar indicadores por tipo de prueba
     * @param indicador nombre del indicador
     * @param ano año a consultar
     */
    private void cargarIndicadorPruebas(String indicador, int ano) {
        grafico.setLineaSaber(new LineChartModel());
        ChartData data = new ChartData();
        List<String> pruebasSaber = new RepositorioResultados().cargarNombrePruebas(ano, indicador);
        List<ResultadosPrimaria> resultados = new RepositorioResultados().cargarIndicadorPrueba(ano, indicador);
        data = analisisPruebas.cargarGraficaLinealPrueba(resultados, data, this.reporte, colores);
        data.setLabels(pruebasSaber);
        LineChartOptions opciones = new LineChartOptions();
        Title title = new Title();
        title.setDisplay(true);
        opciones.setTitle(title);
        grafico.getLineaSaber().setOptions(opciones);
        grafico.getLineaSaber().setData(data);
    }
    /**
     * Metodo encargado de crear grafica lineal ya sea por año o prueba
     * @param resultadosMujeres listado de resultados
     * @param estado indica el estado para crear la grafica
     */
    private void crearGraficaLinealPuntajes(List<ResultadosPrimaria> resultadosMujeres, int estado) {
        ChartData data = new ChartData();
        color=0;
        data.addChartDataSet(cargarDataSetGraficaLineal(resultadosMujeres, "Mujer"));
        List<String> labels = new ArrayList<>();
        for (ResultadosPrimaria resultados : resultadosMujeres) {
            if (estado == 0) 
                labels.add(String.valueOf(resultados.getAno()));
            else 
                labels.add(resultados.getIndicador());
        }
        data.setLabels(labels);
        LineChartOptions opciones = new LineChartOptions();
        Title titulo = new Title();
        titulo.setDisplay(true);
        opciones.setTitle(titulo);
        if (estado == 0) {
            grafico.setLineaAnosIndicador(new LineChartModel());
            grafico.getLineaAnosIndicador().setOptions(opciones);
            grafico.getLineaAnosIndicador().setData(data);
        } else {
            grafico.setLineaSaber(new LineChartModel());
            grafico.getLineaSaber().setOptions(opciones);
            grafico.getLineaSaber().setData(data);
        }
    }
    /**
     * Metodo para crear grafica de indicadores por puntaje
     * @param indicador nombre del indicador
     * @param ano año a consultar
     */
    private void cargarIndicadorPruebasPuntaje(String indicador, int ano) {
        List<ResultadosPrimaria> resultadosPruebas = new ArrayList<>();
        List<ResultadosPrimaria> resultados = new RepositorioResultados().cargarBrechaIndicadorPuntaje(indicador, ano);
        for (ResultadosPrimaria resultado : resultados) {
            Double puntaje = 0.0;
            String[] respuesta = resultado.getIndicador().split(",");
            puntaje = (double) Math.round(Double.valueOf(respuesta[1]) * 1000d) / 1000d;
            resultadosPruebas.add(new ResultadosPrimaria(resultado.getGenero(), puntaje));
        }
        this.reporte.getListados().put("lineSaber_punt", resultadosPruebas);
        crearGraficaLinealPuntajes(resultadosPruebas, 1);
    }
    /**
     * Cargar el listado de informacion de acuerdo al año e indicador
     * @param e evento al seleccionar un año
     */
    public void cargarDatosAnoPrueba(ValueChangeEvent e) {
        Integer anoConsultar = (Integer) e.getNewValue();
        if(anoConsultar==null)
            anoConsultar=ano;
        resultadoAno = new RepositorioResultados().cargarInformacionAno(indicador, anoConsultar, SABER);
    }
    /**
     * Cargar el listado de informacion de acuerdo al año
     * @param e evento al seleccionar un año del radar
     */
    public void cargarDatosAno(ValueChangeEvent e) {
        int i = 0;
        Integer anoConsultar = (Integer) e.getNewValue();
        if(anoConsultar==null)
            anoConsultar=ano;
        if (anoConsultar != null) {
            listadoInfoAno = new ArrayList<>();
            List<ResultadosPrimaria> listadoPrueba = repositorio.obtenerResultadosGlobales(anoConsultar, SABER);
            Indicador indicadorSeleccionado = new Indicador();
            for (ResultadosPrimaria resultado : listadoPrueba) {
                indicadorSeleccionado = analisisPruebas.calculoAnual(indicadorSeleccionado, i, resultado.getRespuesta());
                i++;
            }
            indicadorSeleccionado.setAno(String.valueOf(anoConsultar));
            listadoInfoAno.add(indicadorSeleccionado);
        }
    }
    
    /**
     * Metodo para extraer la descripción del indicador
     * @param datosIndicador, nombre del indicador
     * @return descripcion del indicador
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
     *Metodo para cargar dialogo de auto-ayuda 
     */
    public void cargarDialogo(){
          PrimeFaces.current().executeScript("PF('dlg').show();");
    }
    /**
     * Metodo para generar reportes
     */
    public void generarReporte(){
        String path="";
        if(indicador.contains("fami_edu"))
            path=FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reportes/ReporteResultadosEdu.jasper");
        if(indicador.contains("fami_ti"))
            path=FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reportes/ReporteResultadosTecnologia.jasper");
        if(indicador.contains("punt"))
            path=FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reportes/ReporteResultadosPuntaje.jasper");
        reporte.setTituloInicial("NOVENO");
        reporte.setTituloPie(indicador);
        new ConfiguracionReporte().cargarResultados(reporte, path,indicador);
        
    }
    
    /**
     * Metodo para identificar el año seleccionado
     * @param e evento del menu desplegable del año
     */
    public void seleccionar(ValueChangeEvent e) {
        ano = Integer.parseInt(e.getNewValue().toString());
        visibilidad = (ano >= 2017) ? true : false;
    }
    
    public Reporte getReporte() {
        return reporte;
    }

    public void setReporte(Reporte reporte) {
        this.reporte = reporte;
    }

    public Grafico getGrafico() {
        return grafico;
    }

    public void setGrafico(Grafico grafico) {
        this.grafico = grafico;
    }

    public String getResultadoAno() {
        return resultadoAno;
    }

    public void setResultadoAno(String resultadoAno) {
        this.resultadoAno = resultadoAno;
    }

    public int getAnoSeleccionado() {
        return anoSeleccionado;
    }

    public void setAnoSeleccionado(int anoSeleccionado) {
        this.anoSeleccionado = anoSeleccionado;
    }

    public boolean isVisibilidadContenido() {
        return visibilidadContenido;
    }

    public void setVisibilidadContenido(boolean visibilidadContenido) {
        this.visibilidadContenido = visibilidadContenido;
    }

    public int getAnoConsulta() {
        return anoConsulta;
    }

    public void setAnoConsulta(int anoConsulta) {
        this.anoConsulta = anoConsulta;
    }

    public List<Tabla> getAnosCargados() {
        return anosCargados;
    }

    public void setAnosCargados(List<Tabla> anosCargados) {
        this.anosCargados = anosCargados;
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

    public boolean isVisibilidad() {
        return visibilidad;
    }

    public void setVisibilidad(boolean visibilidad) {
        this.visibilidad = visibilidad;
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
}
