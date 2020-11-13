/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.controller;

import com.udec.pojo.Tabla;
import com.udec.repositorio.RepositorioTablas;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.optionconfig.title.Title;

/**
 * Clase encargada del manejo de la pagina principal del usuario
 * @author Corin Viracacha
 */
@ManagedBean(name = "principalUsuario")
@ViewScoped
public class PrincipalUsuario implements Serializable {
    
    private String fecha;
    private transient List<Integer> anos;
    private int anio;
    private String prueba;
    private String tipo;
    private transient List<Tabla> datosPruebas;
    private transient List<Tabla> datos;
    private BarChartModel graficaMujeres;
    private RepositorioTablas repositorio;

    
    @PostConstruct
    public void iniciar() {
        try {
            tipo = "ano";
            anio = 2017;
            repositorio = new RepositorioTablas();
            datos = repositorio.obtenerTablasPrimaria();
            datos = repositorio.obtenerTablasSuperior(datos);
            validarAnos();
        } catch (Exception ex) {
            Logger.getLogger(PrincipalUsuario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Constructor de la clase
     */
    public PrincipalUsuario() {
        repositorio = new RepositorioTablas();
        Date ahora = new Date();
        SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
        fecha = formateador.format(ahora);
    }

    /**
     * Metodo encargado de seleccionar el año a consultar
     * @param e evento
     */
    public void actualizarTablaAno(ValueChangeEvent e) {
        anio = Integer.parseInt(e.getNewValue().toString());
        validarAnos();
    }
    
    /**
     * Metodo encargado de actualizar la prueba a consultar
     * @param e evento
     */
    public void actualizarTablaPrueba(ValueChangeEvent e) {
        prueba = e.getNewValue().toString();
        validarAnos();
    }

    
    /**
     * Asignar propiedades de las graficas
     * @param tabla, contiene el listado de tablas existentes en el aplicativo
     * @param tipo Hace referencia al tipo de dato
     */
    public void graficarCantidadMujeres(List<Tabla> tabla, String tipo) {
        graficaMujeres = new BarChartModel();
        ChartData data = new ChartData();
        BarChartDataSet dataSet = new BarChartDataSet();
        dataSet.setLabel("Cantidad de Mujeres");
        List<Number> valores = new ArrayList<>();
        List<String> titulosBarra = new ArrayList<>();
        for (Tabla datosConsulta : tabla) {
            valores.add(datosConsulta.getCantidad());
            if (tipo.contains("ano")) {
                titulosBarra.add(datosConsulta.getPrueba());
            } else {
                titulosBarra.add(String.valueOf(datosConsulta.getAno()));
            }
        }
        dataSet.setData(valores);
        List<String> colores = new ArrayList<>();
        colores = cargarColores(titulosBarra, colores);
        dataSet.setBackgroundColor(colores);
        data.addChartDataSet(dataSet);
        data.setLabels(titulosBarra);
        graficaMujeres.setData(data);
        graficaMujeres.setOptions(cargarOpciones());
    }
    /**
     * En este metodo se valida los años existentes por prueba
     */
    private void validarAnos() {
        int menor = 2050;
        int mayor = 0;
        datosPruebas = new ArrayList<>();
        for (Tabla pruebaCargada : datos) {
            int posicion = pruebaCargada.getNombreTabla().indexOf("2");
            if (posicion > 0) {
                pruebaCargada.setAno(Integer.parseInt(pruebaCargada.getNombreTabla().substring(posicion)));
                if (pruebaCargada.getAno() < menor) {
                    menor = pruebaCargada.getAno();
                }
                if (pruebaCargada.getAno() > mayor) {
                    mayor = pruebaCargada.getAno();
                }
                if (!pruebaCargada.getNombreTabla().contains("resultadosse") && validarDatos(pruebaCargada)) {
                    if (pruebaCargada.getNombreTabla().contains("resultados")) {
                        String[] nombre = pruebaCargada.getEsquema().split("saber");
                        pruebaCargada.setPrueba("Saber " + nombre[1]);
                        pruebaCargada = llenarCantidad( pruebaCargada, 1);
                    } else {
                        try {
                            pruebaCargada = verificarSuperior(pruebaCargada);
                            pruebaCargada = llenarCantidad( pruebaCargada, 2);
                        } catch (Exception ex) {
                            Logger.getLogger(PrincipalUsuario.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    datosPruebas.add(pruebaCargada);
                }
            }
        }
        cargarAnios(mayor, menor);
        graficarCantidadMujeres(datosPruebas, tipo);
    }
    /**
     * Este metodo se encarga de validar si el registro se encuentra en BD, de lo contrario
     * lo crea.
     * @param pruebaCargada, contiene información de la prueba cargada
     * @param estado, Indica si la prueba corresponde a superior o basico
     * @return informacion de la tabla pero con la cantidad de registros
     */
    private Tabla llenarCantidad( Tabla pruebaCargada, int estado) {
        int cantidad = 0;
        cantidad = repositorio.consultarMujeres(pruebaCargada.getPrueba(), pruebaCargada.getAno());
        if (cantidad == 0) {
            if (estado == 1) {
                pruebaCargada.setCantidad(repositorio.consultarMujeresBasico(pruebaCargada.getEsquema() + "." + pruebaCargada.getNombreTabla()));
            } else {
                pruebaCargada.setCantidad(repositorio.consultarMujeresSuperior("pruebas." + pruebaCargada.getNombreTabla()));
            }
            repositorio.registrarCantidadMujeres(pruebaCargada);
        } else {
            pruebaCargada.setCantidad(cantidad);
        }
        return pruebaCargada;
    }
    /**
     * Metodo encargadode verificar el nombre de le prueba
     * @param pruebaSuperior, informacion sobre la tabla(prueba) registrada
     * @return Objeto tabla con la informacion de la prueba
     */
    private Tabla verificarSuperior(Tabla pruebaSuperior) {
        String[] nombreTabla = pruebaSuperior.getNombreTabla().split("_");
        switch (nombreTabla[0]) {
            case "sb11":
                pruebaSuperior.setPrueba("Saber 11");
                break;

            case "saberpro":
                pruebaSuperior.setPrueba("Saber Pro");
                break;

            case "sabertyt":
                pruebaSuperior.setPrueba("Saber TyT");
                break;

            default:
                break;
        }
        return pruebaSuperior;
    }
    /**
     * Informacion que ordena los años cargados
     * @param mayor, el año mayor
     * @param menor, el año menor
     */
    private void cargarAnios(int mayor, int menor) {
        anos = new ArrayList<>();
        for (int i = menor; i <= mayor; i++) {
            anos.add(i);
        }
    }
    /**
     * Metodo encargado de validar la informacion de la tabla
     * @param pruebaAnalizar, corresponde a la tabla(prueba) a analizar
     * @return un dato logico
     */
    private boolean validarDatos(Tabla pruebaAnalizar) {
        if (tipo.contains("ano") && pruebaAnalizar.getAno() == anio) {
            return true;
        }
        if (tipo.contains("prueba") && (pruebaAnalizar.getEsquema().contains(prueba) || pruebaAnalizar.getNombreTabla().contains(prueba))) {
            return true;
        }
        return false;
    }
    /**
     * Carga de colores de la grafica
     * @param titulosBarra corresponde a los titulos de la barra
     * @param colores, listado de colores
     * @return el listado de colores
     */
    private List<String> cargarColores(List<String> titulosBarra, List<String> colores) {
        String color = "rgb(5, 41, 99,";
        for (int i = 0; i < titulosBarra.size(); i++) {
            colores.add(color + String.valueOf((i + 0.8) * 0.2) + ")");
        }
        return colores;
    }
    /**
     * Cargar opciones y propiedades del grafico de barras
     * @return propiedades del grafico de barras
     */
    private BarChartOptions cargarOpciones() {
        BarChartOptions opciones = new BarChartOptions();
        CartesianScales escalas = new CartesianScales();
        CartesianLinearAxes lineasAxes = new CartesianLinearAxes();
        CartesianLinearTicks instantes = new CartesianLinearTicks();
        instantes.setBeginAtZero(true);
        lineasAxes.setTicks(instantes);
        escalas.addYAxesData(lineasAxes);
        opciones.setScales(escalas);
        Title titulo = new Title();
        titulo.setDisplay(true);
        titulo.setText("CANTIDAD DE MUJERES");
        opciones.setTitle(titulo);

        return opciones;
    }
    
    
    public void seleccionar(ValueChangeEvent e) {
        tipo = e.getNewValue().toString();
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public BarChartModel getGraficaMujeres() {
        return graficaMujeres;
    }

    public void setGraficaMujeres(BarChartModel graficaMujeres) {
        this.graficaMujeres = graficaMujeres;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public List<Integer> getAnos() {
        return anos;
    }

    public void setAnos(List<Integer> anos) {
        this.anos = anos;
    }

    public List<Tabla> getDatosPruebas() {
        return datosPruebas;
    }

    public void setDatosPruebas(List<Tabla> datosPruebas) {
        this.datosPruebas = datosPruebas;
    }

    public String getPrueba() {
        return prueba;
    }

    public void setPrueba(String prueba) {
        this.prueba = prueba;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

}
