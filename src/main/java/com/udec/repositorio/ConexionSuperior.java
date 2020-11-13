/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.repositorio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Esta clase se encarga de la conexión y respectivas funciones con la base de
 * datos que almacena los resultados de las pruebas de educación superior
 *
 * @author Angie Paola Manrique
 * @author Alisson Catalina Celeita
 */
public class ConexionSuperior {

    static Connection conexion = null;
    private static final String URL = "jdbc:postgresql://localhost:5432/resultados_superior";
    private static final String USUARIO = "postgres";
    private static final String CONTRASENIA = "P0W3RB00K$";
    PreparedStatement stmm;

    /*
     Este método se encarga de establecer la conexión con la base de datos
     */
    public static Connection abrir() {
        try {
            Class.forName("org.postgresql.Driver");
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENIA);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(ConexionSuperior.class.getName()).log(Level.SEVERE, "Error en la base de datos.", e);
        }
        return conexion;
    }

    /*
     Este método se encarga de cerrar la conexión con la base de datos
     */
    public static void cerrar() {
        try {
            if (conexion != null) {
                conexion.close();
            }
        } catch (Exception e) {
            Logger.getLogger(ConexionSuperior.class.getName()).log(Level.SEVERE, "Error en la base de datos.", e);
        }
    }

    /*
     Este método se encarga de la creación de tablas donde se guardan los resultados
     */
    public void crear(String crear, String guardar, String nom) throws SQLException {
        String query1 = "CREATE TABLE pruebas." + nom + " (" + crear + ");";
        String query2 = "copy pruebas." + nom + " from '" + guardar + "' USING DELIMITERS ',' with csv header";
        try {
            Class.forName("org.postgresql.Driver");
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENIA);
            stmm = conexion.prepareStatement(query1);
            stmm.executeUpdate();
            
            stmm = conexion.prepareStatement(query2);
            stmm.executeUpdate();
            stmm.close();
        } catch (Exception e) {
            conexion.rollback();
            Logger.getLogger(ConexionSuperior.class.getName()).log(Level.SEVERE, "Error en la base de datos.", e);
        }
    }

    /*
     Este método de la eliminación de columnas innecesarias en la tabla
     */
    public void editar(String alter, String nom) throws SQLException {
        String query = "ALTER TABLE pruebas." + nom + " DROP COLUMN " + alter + ";";
        try {
            Class.forName("org.postgresql.Driver");
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENIA);
            stmm = conexion.prepareStatement(query);
            stmm.executeUpdate();
            stmm.close();
        } catch (Exception e) {
            conexion.rollback();
            Logger.getLogger(ConexionSuperior.class.getName()).log(Level.SEVERE, "Error en la base de datos.", e);
        }
    }

    /*
     Realiza una copia de los resultados para el segundo período de las pruebas
     */
    public void copiarTabla(String nom, String columnas) throws SQLException {
        String query = "INSERT INTO pruebas." + nom.substring(0, nom.length() - 1) + "(" + columnas + ") (SELECT " + columnas + " FROM pruebas." + nom + ");";     
        try {
            Class.forName("org.postgresql.Driver");
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENIA);
            stmm = conexion.prepareStatement(query);
            stmm.executeUpdate();
            stmm.close();
        } catch (Exception e) {
            conexion.rollback();
            Logger.getLogger(ConexionSuperior.class.getName()).log(Level.SEVERE, "Error en la base de datos.", e);
        }
    }

    /*
     Permite la eliminación de las tablas necesarias
     */
    public void eliminarTabla(String nom) throws SQLException {
        String query = "DROP TABLE pruebas." + nom;
        try {
            Class.forName("org.postgresql.Driver");
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENIA);
            stmm = conexion.prepareStatement(query);
            stmm.executeUpdate();
            stmm.close();
        } catch (Exception e) {
            conexion.rollback();
            Logger.getLogger(ConexionSuperior.class.getName()).log(Level.SEVERE, "Error en la base de datos.", e);
        }
    }
}
