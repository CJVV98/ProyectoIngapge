/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.controller;

import com.udec.pojo.Tabla;
import com.udec.repositorio.RepositorioTablas;
import com.udec.utilitarios.ProcesosCargaBasico;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.Part;
import org.primefaces.PrimeFaces;

/**
 * Clase encargada del proceso de carga de archivos
 * @author Corin Jazmin Viracacha
 */
@ManagedBean(name = "cargarResultados")
@ViewScoped
public class CargarResultados implements Serializable{

    private String nombre;
    private int ano;
    private transient List<Integer> anos;
    private boolean visibilidad;
    private boolean visibilidadPanel;
    //archivoResultados, hace referencia a los puntajes
    private transient Part archivoResultados;
    //archivoResultadosEconomicos, hace referencia a los datos socioeconomicos
    private transient Part archivoResultadosEconomicos;
    private String folder;
    private String valor;
    private String vistaPaneles;
    private boolean ocultarPrimaria = false, ocultarSuperior = false;

    /**
     * Cargar valores iniciales
     */
    @PostConstruct
    public void iniciar() {
        folder = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/originales_basico/");
        valor = "Seleccionar año ";
    }

    /**
     * Metodo encargado de cambiar la visibilidad de los paneles
     * @param event evento generado
     */
    public void visibilidadPaneles(ValueChangeEvent event) {
        vistaPaneles = event.getNewValue().toString();
        if ("primaria".equals(vistaPaneles)) {
            ocultarPrimaria = true;
            ocultarSuperior = false;
        } else if ("superior".equals(vistaPaneles)) {
            ocultarPrimaria = false;
            ocultarSuperior = true;
        }
    }

    /**
     * Visibilidad del panel de cargar archivo socio-economico
     */
    public void cargarArchivos() {
        if (ano >= 2017 || "saber3".equals(nombre)) {
            visibilidad = false;
        } else {
            visibilidad = true;
        }
        visibilidadPanel = true;

    }

    /**
     * Insertar y guardar archivos cargados por el usuario
     *
     * @throws FileNotFoundException
     */
    public void insertarResultados() throws FileNotFoundException {
        String[] mensajeResSoc = new String[2];
        String[] mensaje = new String[2];
        boolean validar = false;
        boolean estado = true;
        String tipo;
        if (ano < 2017 && !"saber3".equals(nombre)) {
            validar = true;
            tipo = "puntajes";
            mensajeResSoc = new ProcesosCargaBasico().validarResultados(archivoResultadosEconomicos, "economicos");
        } else {
            tipo = "puntajes-economico";
        }
        mensaje = new ProcesosCargaBasico().validarResultados(archivoResultados, tipo);
        try {
            if (mensaje[1] == "") {
                if (!validar) {
                    cargarDatos(archivoResultados.getInputStream(), tipo, 1);
                }
            } else {
                estado = false;
                crearMensaje(mensaje[1], mensaje[0], FacesMessage.SEVERITY_WARN);
            }
            if (validar) {
                if (mensajeResSoc[1] == "") {
                    if (estado) {
                        cargarDatos(archivoResultados.getInputStream(), tipo, 1);
                        cargarDatos(archivoResultadosEconomicos.getInputStream(), "socioeco", 2);
                        crearMensaje("Archivos cargado correctamente", "Archivos puntajes y socioeconomico", FacesMessage.SEVERITY_INFO);
                    }
                } else {
                    crearMensaje(mensajeResSoc[1], mensajeResSoc[0], FacesMessage.SEVERITY_WARN);
                }
            }
            consultarAnios(this.nombre);
            visibilidadPanel = false;
        } catch (IOException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al cargar archivos", ex);
        }
    }

    /**
     * Metodo encargado de insertar datos a la base de datos
     *
     * @param archivoCargado, archivo cargado
     * @param tipo, de pueba
     * @param estado indica si solo se cargo un archivo o carios
     */
    private void cargarDatos(InputStream archivoCargado, String tipo, int estado) {
        String archivo = "";
        String nombreArchivo = "";
        if (estado == 1) {
            nombreArchivo = nombre + "_" + "resultados" + ano;
            archivo = new ProcesosCargaBasico().guardarArchivo(archivoCargado, folder, nombreArchivo);
            insertarInformacion("resultados" + ano, nombre, archivo, ano, tipo);
            if (tipo.contains("economico")) {
                crearMensaje("Archivo cargado correctamente", "Archivo puntajes - socioeconomico", FacesMessage.SEVERITY_INFO);
            }
        } else {
            nombreArchivo = nombre + "_" + "resultadosse" + ano;
            archivo = new ProcesosCargaBasico().guardarArchivo(archivoCargado, folder, nombreArchivo);
            insertarInformacion("resultadosse" + ano, nombre, archivo, ano, "economico");

        }
    }

    /**
     * Insertar informacion de la prueba en base de datos
     *
     * @param nombre de la prueba
     * @param esquema esta variable representa el tipo de prueba
     * @param url representa la ubicacion del archivo
     * @param ano indica el ano de presentacion de la prueba
     * @param tipo indica si el archivo corresponde a puntajes o datos
     * socioeconomicos
     */
    public void insertarInformacion(String nombre, String esquema, String url, int ano, String tipo) {
        RepositorioTablas repositorio = new RepositorioTablas();
        url = url.replaceFirst("originales_basico", "procesados");
        url = url.replaceFirst(".zip", ".csv");
        repositorio.insertarTabla(ano, nombre, esquema, url, tipo);
    }

    /**
     * Metodo para cargar años de acuerdo al tipo de prueba
     * @param e
     */
    public void cargarAnos(ValueChangeEvent e) {
        this.nombre = (String) e.getNewValue();
        consultarAnios(this.nombre);
    }

    /**
     * Metodo para consultar años cargados
     */
    private void consultarAnios(String esquema) {
        RepositorioTablas repositorio = new RepositorioTablas();
        List<Tabla> resultados = repositorio.consultaPruebasRegistradas(esquema);
        anos = new ArrayList<>();
        if ("saber359".equals(esquema)) {
            for (int i = 2017; i < 2021; i++) {
                if (validarResultadosCargados(i, resultados)) {
                    anos.add(i);
                }
            }
        } else {
            for (int i = 2013; i < 2017; i++) {
                if (validarResultadosCargados(i, resultados)) {
                    anos.add(i);
                }
            }
        }
        if (anos.isEmpty()) {
            valor = "No hay años a cargar";
        } else {
            valor = "Seleccionar año ";
        }
    }

    /**
     * Metodo cuando el usuario cancela el proceso de cargue de archivos
     */
    public void cancelarProceso() {
        visibilidadPanel = false;
    }

    /**
     * Metodo para crear el mensaje en la vista
     * @param mensaje mensaje segun las validaciones
     * @param tipo este corresponde al tipo de gravedad del mensaje
     */
    private void crearMensaje(String mensaje, String encabezadoMensaje, FacesMessage.Severity tipoError) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage("message-info", new FacesMessage(tipoError,
                encabezadoMensaje, mensaje));
    }

    /**
     * Validar que años ya fueron cargados
     * @param ano de presentacion de la prueba
     * @param resultados corresponde al listado de años consultados
     * @return si el año fue cargado
     */
    private boolean validarResultadosCargados(int ano, List<Tabla> resultados) {
        for (int i = 0; i < resultados.size(); i++) {
            if (ano == resultados.get(i).getAno()) {
                return false;
            }
        }
        return true;
    }
    
    
    /**
     * Metodo encargado de cargar el dialogo de auto-ayuda
     */
    public void cargarDialogo(){
          PrimeFaces.current().executeScript("PF('dlg').show();");
    }

    public String getVistaPaneles() {
        return vistaPaneles;
    }

    public void setVistaPaneles(String vistaPaneles) {
        this.vistaPaneles = vistaPaneles;
    }

    public boolean isOcultarPrimaria() {
        return ocultarPrimaria;
    }

    public void setOcultarPrimaria(boolean ocultarPrimaria) {
        this.ocultarPrimaria = ocultarPrimaria;
    }

    public boolean isOcultarSuperior() {
        return ocultarSuperior;
    }

    public void setOcultarSuperior(boolean ocultarSuperior) {
        this.ocultarSuperior = ocultarSuperior;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public List<Integer> getAnos() {
        return anos;
    }

    public void setAnos(List<Integer> anos) {
        this.anos = anos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public Part getArchivoResultados() {
        return archivoResultados;
    }

    public void setArchivoResultados(Part archivoResultados) {
        this.archivoResultados = archivoResultados;
    }

    public Part getArchivoResultadosEconomicos() {
        return archivoResultadosEconomicos;
    }

    public void setArchivoResultadosEconomicos(Part archivoResultadosEconomicos) {
        this.archivoResultadosEconomicos = archivoResultadosEconomicos;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public boolean isVisibilidad() {
        return visibilidad;
    }

    public void setVisibilidad(boolean visibilidad) {
        this.visibilidad = visibilidad;
    }

    public boolean isVisibilidadPanel() {
        return visibilidadPanel;
    }

    public void setVisibilidadPanel(boolean visibilidadPanel) {
        this.visibilidadPanel = visibilidadPanel;
    }

}
