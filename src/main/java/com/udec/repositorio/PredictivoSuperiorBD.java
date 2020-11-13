/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.repositorio;

import com.udec.pojo.Aplicacion;
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
 *
 * @author Angie Paola Manrique
 * @author Alisson Catalina Celeita
 */
public class PredictivoSuperiorBD {

    Instances resultadosPrueba;
    Connection conn = null;
    Statement stm;
    ResultSet rs;
    int resultUpdate = 0;

    /*
        Consulta resultados de las pruebas según indicador
    */
    public Instances listarIndicador(String indicador, String prueba, String nucleo, char genero) throws SQLException {
        try {
            InstanceQuery query = new InstanceQuery();
            query.setUsername("postgres");
            query.setPassword("P0W3RB00K$");
            query.setDatabaseURL("jdbc:postgresql://localhost:5432/resultados_superior");
            query.setQuery("select estu_genero, " + nucleo + ", " + indicador + " from predictivo." + prueba + " where estu_genero='" + genero + "'");
            resultadosPrueba = query.retrieveInstances();
            query.close();
        } catch (Exception ex) {
            Logger.getLogger(ex.getMessage());
            resultadosPrueba.clear();
        }
        return resultadosPrueba;
    }

    /*
        Consulta resultados analisis predictivo
    */
    public List<Aplicacion> consultarPredictivo(String categoria, String prueba, String genero, String indicador) {
        List<Aplicacion> apli = new ArrayList<>();
        String query = "SELECT puntaje, registro, nucleo FROM predictivo.aplicacion where prueba='" + prueba + "' and genero='" + genero + "' and indicador='" + indicador + "' and categoria like '%" + categoria + "%'";
        try {
            conn = ConexionSuperior.abrir();
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            if (!rs.next()) {
                ConexionSuperior.cerrar();
                return apli.subList(0, 0);
            } else {
                do {
                    Aplicacion ap = new Aplicacion();
                    ap.setNucleo(rs.getString("nucleo"));
                    ap.setRegistro(Integer.parseInt(rs.getString("registro")));
                    ap.setPuntaje(Integer.parseInt(rs.getString("puntaje")));
                    apli.add(ap);
                } while (rs.next());
                ConexionSuperior.cerrar();
                return apli;
            }
        } catch (Exception e) {
            Logger.getLogger(PredictivoSuperiorBD.class.getName()).log(Level.SEVERE, "Error en la base de datos.", e);
            return apli.subList(0, 0);
        }
    }

    /*
        Consulta categorías de indicador para análisis predictivo
    */
    public List<String> consultarCategoria(String prueba, String indicador, String genero) {
        List<String> categorias = new ArrayList<>();
        String query = "SELECT categoria FROM predictivo.aplicacion where prueba='" + prueba + "' and indicador='" + indicador + "' and nucleo='Lectura critica' and genero='" + genero + "'";
        try {
            conn = ConexionSuperior.abrir();
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            if (!rs.next()) {
                ConexionSuperior.cerrar();
                return categorias.subList(0, 0);
            } else {
                do {
                    String cate = rs.getString("categoria");
                    categorias.add(cate);
                } while (rs.next());
                ConexionSuperior.cerrar();
                return categorias;
            }
        } catch (Exception e) {
            Logger.getLogger(PredictivoSuperiorBD.class.getName()).log(Level.SEVERE, "Error en la base de datos.", e);
            return categorias.subList(0, 0);
        }
    }

}