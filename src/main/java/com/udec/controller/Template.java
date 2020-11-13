/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.controller;

import com.udec.utilitarios.NotificacionesCorreo;
import com.udec.pojo.Notificacion;
import com.udec.repositorio.RepositorioTablas;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.primefaces.PrimeFaces;

/**
 * Clase encargada de cargar el template del administrador
 * @author Corin Viracacha, Angie Manrique, Alisson Celeita
 */

@ManagedBean(name = "template")
@ViewScoped
public class Template implements Serializable {

  
    private int cantidadNotificaciones;
    private transient List<Notificacion> listadoNotificaciones;
    private transient List<Notificacion> listadoCorreos;
    private int validacion = 0;
    FacesContext context = FacesContext.getCurrentInstance();
    ListadoUsuarios listado= new ListadoUsuarios();
    
    /**
     * Construstor de la clase, aqui se valida la sesión del usuario
     */
    
    public Template(){
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession;
        httpSession = request.getSession();
        try {
            validacion = Integer.parseInt(httpSession.getAttribute("id").toString());
            if (validacion != 1) {
                PrimeFaces.current().executeScript("window.location='login.xhtml'");
            }
        } catch (NullPointerException ex) {
            PrimeFaces.current().executeScript("window.location='login.xhtml'");
        }
    }
    
    @PostConstruct
    public void iniciar() {
        listadoNotificaciones = new ArrayList<>();
        cargarNotificaciones();
        listadoCorreos = new NotificacionesCorreo().consultarCorreos();
    }
    /**
     * Metodo usado para cargar las notificaciones
     */
    private void cargarNotificaciones() {
        listadoNotificaciones = new RepositorioTablas().obtenerTablasError("lista_tablas");
        cantidadNotificaciones = listadoNotificaciones.size();
    }

    public int getCantidadNotificaciones() {
        return cantidadNotificaciones;
    }

    public void setCantidadNotificaciones(int cantidadNotificaciones) {
        this.cantidadNotificaciones = cantidadNotificaciones;
    }

    public List<Notificacion> getListadoNotificaciones() {
        return listadoNotificaciones;
    }

    public void setListadoNotificaciones(List<Notificacion> listadoNotificaciones) {
        this.listadoNotificaciones = listadoNotificaciones;
    }

    public List<Notificacion> getListadoCorreos() {
        return listadoCorreos;
    }

    public void setListadoCorreos(List<Notificacion> listadoCorreos) {
        this.listadoCorreos = listadoCorreos;
    }

    public int getValidacion() {
        return validacion;
    }

    public void setValidacion(int validacion) {
        this.validacion = validacion;
    }
}
