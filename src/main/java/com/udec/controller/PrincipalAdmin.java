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

/**
 *
 * @author Angie Paola Manrique
 * @author Alisson Catalina Celeita
 */
@ManagedBean(name = "principalAdmin")
@ViewScoped
public class PrincipalAdmin implements Serializable {
    private String fecha;
    private transient List<Integer> anios;
    private int anio;
    private String prueba;
    private transient List<Tabla> datos, datosPruebas;
    private RepositorioTablas repositorio;

    /**
     * Creates a new instance of PrincipalAdmin
     */
    @PostConstruct
    public void init() {
        try {
            repositorio = new RepositorioTablas();
            datos = repositorio.obtenerTablasPrimaria();
            datos = repositorio.obtenerTablasSuperior(datos);
            anio = 2014;
            Date fechaActual = new Date();
            SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
            fecha = formateador.format(fechaActual);
            validarAnios();
        } catch (Exception ex) {
            Logger.getLogger(PrincipalAdmin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void validarAnios() {
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
                if ((!pruebaCargada.getNombreTabla().contains("resultadosse")) && (pruebaCargada.getAno() == anio)) {
                    if (pruebaCargada.getNombreTabla().contains("resultados")) {
                        String[] nombre = pruebaCargada.getEsquema().split("saber");
                        pruebaCargada.setPrueba("Saber " + nombre[1]);
                        pruebaCargada = llenarCantidad( pruebaCargada, 1);
                    } else {
                        pruebaCargada = verificarSuperior(pruebaCargada);
                        pruebaCargada = llenarCantidad( pruebaCargada, 2);
                    }
                    datosPruebas.add(pruebaCargada);
                }
            }
        }
        cargarAnios(mayor, menor);
    }

    private void cargarAnios(int mayor, int menor) {
        anios = new ArrayList<>();
        for (int i = menor; i <= mayor; i++) {
            anios.add(i);
        }
    }

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

    private Tabla llenarCantidad( Tabla pruebaCargada,  int estado) {
        int cantidad=0;
        cantidad=repositorio.consultarRegistrosActuales(pruebaCargada.getPrueba(), pruebaCargada.getAno());
        if (cantidad == 0) {
            if(estado==1)
                pruebaCargada.setCantidad(repositorio.consultarRegistrosBasico(pruebaCargada.getEsquema() + "." + pruebaCargada.getNombreTabla()));
            else
                pruebaCargada.setCantidad(repositorio.consultarRegistrosSuperior("pruebas." + pruebaCargada.getNombreTabla()));
            repositorio.registrarCantidadTabla(pruebaCargada);
        } else {
            pruebaCargada.setCantidad(cantidad);
        }
        return pruebaCargada;
    }

    
    public void actualizarTablaAnio(ValueChangeEvent e) {
        anio = Integer.parseInt(e.getNewValue().toString());
        validarAnios();
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public List<Integer> getAnios() {
        return anios;
    }

    public void setAnios(List<Integer> anios) {
        this.anios = anios;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getPrueba() {
        return prueba;
    }

    public void setPrueba(String prueba) {
        this.prueba = prueba;
    }

    public List<Tabla> getDatos() {
        return datos;
    }

    public void setDatos(List<Tabla> datos) {
        this.datos = datos;
    }

    public List<Tabla> getDatosPruebas() {
        return datosPruebas;
    }

    public void setDatosPruebas(List<Tabla> datosPruebas) {
        this.datosPruebas = datosPruebas;
    }

}
