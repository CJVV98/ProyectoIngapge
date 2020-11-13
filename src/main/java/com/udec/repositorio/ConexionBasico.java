/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.repositorio;


import com.udec.controller.Login;
import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Clase encargada de la conexion con las base de datos de saber basico
 * @author Corin Viracacha
 */
public class ConexionBasico implements Serializable{

    private static final String JDBC = "org.postgresql.Driver";
    private static final String URL = "jdbc:postgresql://localhost:5432/resultados_basico?useSSL=false";

    private static final String USER = "postgres";
    private transient  String pass = "P0W3RB00K$";

    private transient Connection conn = null;
    /**
     * Metodo usado para abrir la conexión a base de datos
     * @return conexion
     */
    public Connection abrir() {
        try {
            Class.forName(JDBC);
            conn = DriverManager.getConnection(URL, USER, pass);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE,"Error en conexion con BD", ex);
        }
        return conn;
    }
    /**
     * Metodo para cerrar conexion con la base de datos
     */
    public void cerrar() {
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE,"No se pudo cerrar la conexión", ex);
        }
    }
}
