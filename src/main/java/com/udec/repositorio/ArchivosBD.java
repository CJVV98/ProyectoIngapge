/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.repositorio;

import com.udec.controller.Login;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que realiza las diferentes funciones en base de datos para la carga de
 * archivos
 *
 * @author Angie Paola Manrique
 * @author Alisson Catalina Celeita
 */
public class ArchivosBD {

    Connection conn = null;
    Statement stm;
    ResultSet rs;
    int resultUpdate = 0;

    /*
     Método que permite la eliminación innecesarias de la tabla de resultados de las pruebas
     */
    public List<String> columnaseliminar(String nom) {
        List<String> listadoColumnas = new ArrayList();
        String query = "select column_name AS columna from information_schema.columns where table_name='" + nom + "';";
        try {
            conn = ConexionSuperior.abrir();
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            if (!rs.next()) {
                ConexionSuperior.cerrar();
                return listadoColumnas.subList(0, 0);
            } else {
                do {
                    String columna = rs.getString("columna");
                    listadoColumnas.add(columna);
                } while (rs.next());
                stm.close();
                rs.close();
                ConexionSuperior.cerrar();
                return listadoColumnas;
            }
        } catch (Exception e) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error en la base de datos.", e);
            return listadoColumnas.subList(0, 0);
        }
    }

    /*
     Devuelve las columnas definitivas de la tabla
     */
    public List<String> columnasfinal(String nom, String anio) {
        String[] aux1 = nom.split("_");
        String auxf = aux1[0];
        List<String> listadoColumnas = new ArrayList();
        String query = "select column_name AS columna from information_schema.columns where table_name ='" + auxf + anio+"';";
        try {
            conn = ConexionSuperior.abrir();
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            if (!rs.next()) {
                ConexionSuperior.cerrar();
                return listadoColumnas.subList(0, 0);
            } else {
                do {
                    String columna = rs.getString("columna");
                    listadoColumnas.add(columna);
                } while (rs.next());
                rs.close();
                stm.close();
                ConexionSuperior.cerrar();
                return listadoColumnas;
            }
        } catch (Exception e) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error en la base de datos.", e);
            return listadoColumnas.subList(0, 0);
        }
    }

    /*
     Trae las columnas que contiene el primer período de la prueba para así crear las del segundo período
     */
    public List<String> columnasSegundoPeriodo(String nom, String anio) {
        String[] aux1 = nom.split("_");
        String auxf = aux1[0];
        List<String> listadoColumnas = new ArrayList();
        String query = "select column_name AS columna from information_schema.columns where table_name ='" + auxf + "_" + anio + "';";
        try {
            conn = ConexionSuperior.abrir();
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            if (!rs.next()) {
                ConexionSuperior.cerrar();
                return listadoColumnas.subList(0, 0);
            } else {
                do {
                    String columna = rs.getString("columna");
                    listadoColumnas.add(columna);
                } while (rs.next());
                stm.close();;
                rs.close();
                ConexionSuperior.cerrar();
                return listadoColumnas;
            }
        } catch (Exception e) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error en la base de datos.", e);
            return listadoColumnas.subList(0, 0);
        }
    }

    /*
     Consulta la cantidad de registros que contiene una tabla
     */
    public int existenciaRegistros(String nom) {
        int cantidad;
        String query = "select count (*) from pruebas." + nom + ";";
        try {
            conn = ConexionSuperior.abrir();
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            if (!rs.next()) {
                ConexionSuperior.cerrar();
                return 0;
            } else {
                cantidad = rs.getInt("count");
                rs.close();
                stm.close();
                ConexionSuperior.cerrar();
                return cantidad;
            }
        } catch (Exception e) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error en la base de datos.", e);
            return 0;
        }
    }

    /*
     Actualiza el periodo cuando ya se ha realizado la carga completa de los resultados en cada año de las pruebas
     */
    public void cargarCompleta(String nom) {
        try {
            conn = ConexionSuperior.abrir();
            stm = conn.createStatement();
            resultUpdate = stm.executeUpdate("update pruebas." + nom + " set periodo='completo';");
            if (resultUpdate != 0) {
                stm.close();
                ConexionGlobal.cerrar();
            } else {
                stm.close();
                ConexionGlobal.cerrar();
            }
        } catch (Exception e) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error en la base de datos.", e);
        }
    }

    /*
     Consulta si las pruebas han sido cargadas por completo, o si falta algún periodo
     */
    public int consultarPeriodo(String nom) {
        String nombre = nom;
        if (!nombre.contains("saberpro")) {
            nombre = nombre.substring(0, nombre.length() - 1);
        }
        String query = "Select count (*) from pruebas." + nombre + " where periodo='completo';";
        try {
            conn = ConexionSuperior.abrir();
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            if (!rs.next()) {
                rs.close();
                stm.close();
                ConexionSuperior.cerrar();
                return 0;
            } else {
                int dato=rs.getInt("count");
                rs.close();
                stm.close();
                ConexionSuperior.cerrar();
                return dato;
            }
        } catch (Exception e) {
            //Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error en la base de datos.", e);
            return 0;
        }
    }
}
