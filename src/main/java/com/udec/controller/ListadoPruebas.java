/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.controller;

import com.udec.pojo.Prueba;
import com.udec.pojo.Tabla;
import com.udec.repositorio.RepositorioTablas;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;


/**
 * Clase encargada de listar las pruebas existentes en BD
 * @author Corin Viracacha
 */
@ManagedBean(name = "listadoPruebas")
@ViewScoped
public class ListadoPruebas implements Serializable {
   private static List<Prueba> existencia;

    @PostConstruct
    public void iniciar() {
        existencia = new ArrayList<>();
        existencia = cargarTabla();
    }

    public List<Prueba> getExistencia() {
        return existencia;
    }

    public void setExistencia(List<Prueba> existencia) {
        ListadoPruebas.existencia = existencia;
    }
   /**
    * Metodo encargado de cargar las pruebas(tablas) registradas en BD
    * @return listado de pruebas
    */
    public List<Prueba> cargarTabla() {
        try {
            RepositorioTablas repositorio = new RepositorioTablas();
            List<Tabla> datos = repositorio.obtenerTablasPrimaria();
            datos = repositorio.obtenerTablasSuperior(datos);
            for (int ano = 2014; ano < 2020; ano++) {
                Prueba prueba = new Prueba();
                prueba.setAno(ano);
                for (Tabla resultadoPrueba : datos) {
                    if (resultadoPrueba.getNombreTabla().indexOf(String.valueOf(ano)) > 0) {
                        prueba = validarSaber359(prueba, resultadoPrueba);
                        prueba = validarSaberSuperior(prueba, resultadoPrueba);
                    }
                }
                existencia.add(prueba);
            }
            return existencia;
        } catch (Exception ex) {
            Logger.getLogger(ListadoPruebas.class.getName()).log(Level.SEVERE, null, ex);
        }
        return existencia.subList(0, 0);
    }
    /**
     * Validar cuales pruebas de saber basico estan cargadas
     * @param prueba Objeto con los datos de la prueba
     * @param resultadoPrueba tabla cargada
     * @return Objeto Prueba, con la modificación de si existe o no
     */
    public Prueba validarSaber359(Prueba prueba, Tabla resultadoPrueba) {
        switch (resultadoPrueba.getEsquema()) {
            case "saber3":
                prueba.setSaber3(true);
                break;

            case "saber5":
                prueba.setSaber5(true);
                break;

            case "saber9":
                prueba.setSaber9(true);
                break;
            default:
                break;
        }
        return prueba;
    }
    /**
     * Validar cuales pruebas de saber superior estan cargadas
     * @param prueba, nombre de la prueba
     * @param resultadoPrueba, datos sobre la tabla
     * @return Datos de Prueba
     */
    public Prueba validarSaberSuperior(Prueba prueba, Tabla resultadoPrueba) {
        String[] nombreTabla = resultadoPrueba.getNombreTabla().split("_");
        switch (nombreTabla[0]) {
            case "sb11":
                prueba.setSaber11(true);
                break;

            case "saberpro":
                prueba.setSaberPro(true);
                break;

            case "sabertyt":
                prueba.setSaberT(true);
                break;

            default:
                break;
        }
        return prueba;
    }
}
