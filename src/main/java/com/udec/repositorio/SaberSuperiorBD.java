/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.repositorio;

import com.udec.pojo.DatosGrafica;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Instances;
import weka.experiment.InstanceQuery;

/**
 * Esta clase se encarga de realizar las diferentes consultas de los resultados
 * de las pruebas Saber Superior
 *
 * @author Angie Paola Manrique
 * @author Alisson Catalina Celeita
 */
public class SaberSuperiorBD {

    Connection conn = null;
    Statement stm;
    ResultSet rs;
    int resultUpdate = 0;
    Instances resultadosPrueba, resultadosNucleo;

    /*
     Trae la cantidad de mujeres en cada año de las diferentes pruebas
     */
    public int listarMujer(String anio, String prueba) {
        int cantidad;
        String query = "select count (*) from pruebas." + prueba + anio + " where estu_genero = 'F'";
        try {
            conn = ConexionSuperior.abrir();
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            if (!rs.next()) {
                ConexionSuperior.cerrar();
                return 0;
            } else {
                cantidad = rs.getInt("count");
                ConexionSuperior.cerrar();
                return cantidad;
            }
        } catch (Exception e) {
            Logger.getLogger(SaberSuperiorBD.class.getName()).log(Level.SEVERE, "Error en la base de datos.", e);
            return 0;
        }
    }

    /*
     Retorna la cantidad de hombres que realizaron en cada año las diferentes pruebas
     */
    public int listarHombre(String anio, String prueba) {
        int cantidad;
        String query = "select count (*) from pruebas." + prueba + anio + " where estu_genero = 'M'";
        try {
            conn = ConexionSuperior.abrir();
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            if (!rs.next()) {
                ConexionSuperior.cerrar();
                return 0;
            } else {
                cantidad = rs.getInt("count");
                ConexionSuperior.cerrar();
                return cantidad;
            }
        } catch (Exception e) {
            Logger.getLogger(SaberSuperiorBD.class.getName()).log(Level.SEVERE, "Error en la base de datos.", e);
            return 0;
        }
    }

    /*
     Consulta el género de las personas que realizaron las pruebas cada año
     */
    public Instances listarGenero(String anio, String prueba) throws SQLException {
        try {
            InstanceQuery query = new InstanceQuery();
            query.setUsername("postgres");
            query.setPassword("1234");
            query.setDatabaseURL("jdbc:postgresql://localhost:5432/resultados_superior");
            query.setQuery("select estu_genero from pruebas." + prueba + anio);
            resultadosPrueba = query.retrieveInstances();
            query.close();
            return resultadosPrueba;
        } catch (Exception ex) {
            Logger.getLogger(SaberSuperiorBD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultadosPrueba;
    }

    /*
     Este método se encarga de consultar las respuestas de cada indicador según el año y prueba
     */
    public Instances listarIndicador(String indicador, String anio, String prueba) throws SQLException {
        try {
            InstanceQuery query = new InstanceQuery();
            query.setUsername("postgres");
            query.setPassword("1234");
            query.setDatabaseURL("jdbc:postgresql://localhost:5432/resultados_superior");
            query.setQuery("select estu_genero," + indicador + " from pruebas." + prueba + anio);
            resultadosPrueba = query.retrieveInstances();
            query.close();
        } catch (Exception ex) {
            Logger.getLogger(ex.getMessage());
            resultadosPrueba.clear();
        }
        return resultadosPrueba;
    }

    /*
     Consulta los puntajes obtenidos en cada núcleo de las pruebas según año e indicador
     */
    public Instances listarNucleo(String indicador, String anio, String prueba, String nucleo) throws SQLException {
        try {
            InstanceQuery query = new InstanceQuery();
            query.setUsername("postgres");
            query.setPassword("1234");
            query.setDatabaseURL("jdbc:postgresql://localhost:5432/resultados_superior");
            query.setQuery("select " + nucleo + "," + indicador + " from pruebas." + prueba + anio);
            resultadosNucleo = query.retrieveInstances();
            query.close();
        } catch (Exception ex) {
            Logger.getLogger(ex.getMessage());
            resultadosNucleo.clear();
        }
        return resultadosNucleo;
    }

    public void insertarAnalisis(DatosGrafica datos) throws SQLException {
        String query = "INSERT INTO pruebas.analisis_" + datos.getPrueba() + " (anio, label_titulo, indicador, centroide, cluster, atributo) VALUES ('"
                + datos.getAnio() + "', '" + datos.getLabel() + "', '" + datos.getIndicador() + "', '" + datos.getCentroide() + "', '" + datos.getCluster() + "', '" + datos.getAtributos() +"')";
        try {
            conn = ConexionSuperior.abrir();
            stm = conn.createStatement();
            resultUpdate = stm.executeUpdate(query);
            if (resultUpdate != 0) {
                ConexionSuperior.cerrar();
            } else {
                ConexionSuperior.cerrar();
            }
        } catch (Exception e) {
            conn.rollback();
            Logger.getLogger(SaberSuperiorBD.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public int consultarExistencia(String anio, String prueba, String indicador) {
        int cantidad;
        String query = "select count (*) from pruebas.analisis_" + prueba + " where anio='" + anio + "' and indicador='" + indicador + "'";
        try {
            conn = ConexionSuperior.abrir();
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            if (!rs.next()) {
                ConexionSuperior.cerrar();
                return 0;
            } else {
                cantidad = rs.getInt("count");
                ConexionSuperior.cerrar();
                return cantidad;
            }
        } catch (Exception e) {
            Logger.getLogger(SaberSuperiorBD.class.getName()).log(Level.SEVERE, "Error en la base de datos.", e);
            return 0;
        }
    }

    public List<DatosGrafica> listarAnalisis(String anio, String indicador, String prueba, String titulo) {
        List<DatosGrafica> listadoAnalisis = new ArrayList();
        String query = "SELECT * from pruebas.analisis_" + prueba + " where anio='" + anio + "' and indicador='" + indicador + "' and label_titulo='" + titulo + "';";
        try {
            conn = ConexionSuperior.abrir();
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            if (!rs.next()) {
                ConexionSuperior.cerrar();
                return listadoAnalisis.subList(0, 0);
            } else {
                do {
                    DatosGrafica datos = new DatosGrafica(rs.getString("centroide"), rs.getString("cluster"), rs.getString("atributo"));
                    listadoAnalisis.add(datos);

                } while (rs.next());
                ConexionSuperior.cerrar();
                return listadoAnalisis;
            }
        } catch (Exception e) {
            Logger.getLogger(SaberSuperiorBD.class.getName()).log(Level.SEVERE, null, e);
            return listadoAnalisis.subList(0, 0);
        }
    }
}
