/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.controller;

import com.udec.pojo.Notificacion;
import com.udec.repositorio.RepositorioTablas;
import com.udec.utilitarios.ProcesosCargaBasico;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.Part;
import org.primefaces.PrimeFaces;


/**
 * Clase encargada del manejo de las notificaciones del aplicativo
 * @author Admin
 */
@ManagedBean(name = "vistaNotificaciones")
@ViewScoped
public class VistaNotificaciones implements Serializable {

    private String fecha;
    private String folder;
    private transient List<Notificacion> listadoNotificaciones;
    private transient Notificacion notificacionSeleccionada;
    private transient Part archivoResultados;


    @PostConstruct
    public void iniciar() {
        folder=FacesContext.getCurrentInstance().getExternalContext().getRealPath("/originales_basico/");
        notificacionSeleccionada = new Notificacion();
        cargarNotificacionesPruebas();
    }
    
    public void setNotificacionSeleccionada(Notificacion notificacionSeleccionada) {
        this.notificacionSeleccionada = notificacionSeleccionada;
    }

    /**
     * Carga las notificaiones de las pruebas que presentaron inconvenientes
     */
    public void cargarNotificacionesPruebas() {
        listadoNotificaciones = new ArrayList<>();
        listadoNotificaciones = new RepositorioTablas().obtenerTablasError("tablas_error");
    }
    /**
     * Asignar la notificación seleccionada
     * @param notificacion notificacion seleccionada
     */
    public void seleccion(Notificacion notificacion) {
        notificacionSeleccionada = notificacion;
    }
    /**
     * Metodo usado para cargar los archivos con errores
     * @throws FileNotFoundException 
     */
    public void corregirArchivo() throws FileNotFoundException {
        String[] mensaje=new ProcesosCargaBasico().validarResultados(archivoResultados, notificacionSeleccionada.getTipoResultado());
        try {
            if (mensaje[1].isEmpty()) {
                String nombreArchivo=notificacionSeleccionada.getPrueba()+"_"+notificacionSeleccionada.getNombre();
                String archivo=new ProcesosCargaBasico().guardarArchivo(archivoResultados.getInputStream(),folder,nombreArchivo);
                new RepositorioTablas().modificarDatos(notificacionSeleccionada.getId(), archivo);
                cargarNotificacionesPruebas();
                crearMensaje("Archivo cargado correctamente","Operación exitosa",FacesMessage.SEVERITY_INFO);  
            }else{
                crearMensaje(mensaje[1],mensaje[0],FacesMessage.SEVERITY_WARN);  
            }
        } catch (IOException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al cargar archivos", ex);
        }
    }
    /**
     * Metodo encargado de eliminar proceso de la base de datos
     * @param notificacion notificación seleccionada
     */
    public void eliminarProceso(Notificacion notificacion){
        new RepositorioTablas().eliminarProceso(notificacion.getId());
        cargarNotificacionesPruebas();
        
    }
    /**
     * Crear mensajes
     * @param mensaje mensaje a mostrar
     * @param encabezadoMensaje encabezado del mensaje
     * @param tipoError tipo de error
     */
    private void crearMensaje(String mensaje, String encabezadoMensaje, FacesMessage.Severity tipoError) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage("message-info", new FacesMessage(tipoError,
                encabezadoMensaje, mensaje));
    }
    
    public void cargaArchivo(AjaxBehaviorEvent event) {}
    /**
     * Metodo para cargar dialogo
     */
    public void cargarDialogo(){
          PrimeFaces.current().executeScript("PF('dlg').show();");
    }
     
    public String getFecha() {
        Date ahora = new Date();
        SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
        this.fecha = formateador.format(ahora);
        return fecha;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public Part getArchivoResultados() {
        return archivoResultados;
    }

    public void setArchivoResultados(Part archivoResultados) {
        this.archivoResultados = archivoResultados;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public List<Notificacion> getListadoNotificaciones() {
        return listadoNotificaciones;
    }

    public void setListadoNotificaciones(List<Notificacion> listadoNotificaciones) {
        this.listadoNotificaciones = listadoNotificaciones;
    }

    public Notificacion getNotificacionSeleccionada() {
        return notificacionSeleccionada;
    }
}
