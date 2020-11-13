/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.repositorio;

import com.udec.controller.Login;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase conexion con la base de datos general del proyecto
 * @author Angie Manrique
 * @author Corin Viracacha
 * @author Alisson Celeita
 */
public class ConexionGlobal {

    static Connection conexion = null;
    private static final String URL = "jdbc:postgresql://localhost:5432/ingapge";
    private static final String USUARIO = "postgres";
    private static final String CONTRASENIA = "P0W3RB00K$";
    
   /**
    * Metodo para abrir la conexión
    * @return conexión
    * @throws SQLException 
    */
    public static Connection abrir() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENIA);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error: No se logró ls conexión", e);
        }
        return conexion;
    }
    /**
     * Metodo encargado de cerrar la conexión con base de datos
     */
    public static void cerrar() {
        try {
            if (conexion != null) {
                conexion.close();
            }
        } catch (Exception e) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error: No se logró cerrar conexión", e);
        }
    }
}
