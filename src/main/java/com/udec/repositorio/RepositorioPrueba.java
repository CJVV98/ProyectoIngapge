/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.repositorio;


import com.udec.controller.Login;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Instances;
import weka.experiment.InstanceQuery;

/**
 * Clase encargada de la conexion entre weka y base de datos
 * @author Admin
 */
public class RepositorioPrueba {

    private ConexionBasico conexion = new ConexionBasico();
      
    public RepositorioPrueba() {
    }
    /**
     * Metodo para consultar indicador
     * @param funcion, corresponde al query de consulta
     * @return instancias encontradas
     */
    public Instances consultarIndicador(String funcion) {
        try {
            InstanceQuery query = new InstanceQuery();
            query.setDatabaseURL("jdbc:postgresql://localhost:5432/resultados_basico");
            query.setUsername("postgres");
            query.setPassword("P0W3RB00K$");
            query.connectToDatabase();
            query.setQuery(funcion);
            return query.retrieveInstances();
        } catch (Exception ex) {
               Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error en acceso a la base de datos", ex);

        }
        return null;
    }
    /**
     * Metodo encargado de identificar que tipo de consulta ha de realizar
     * @param indicador descripcion del indicador
     * @param ano año a consultar
     * @param prueba prueba a consultar
     * @return instancias
     */
    public Instances seleccionar(String indicador, int ano, String prueba) {
        String consulta;
        if (indicador.contains("punt") || ano>= 2017) {
            consulta = "SELECT estu_genero, " + indicador + " FROM "+prueba+".resultados" + ano;
        } else {
            consulta = "SELECT estu_genero, " + indicador + " FROM "+prueba+".resultadosse" + ano;
        }
        return consultarIndicador(consulta+" where "+indicador+" is not null ");
    }
    /**
     * Metodo encargado de realizar la consulta por indicador
     * @param indicador indicador a consultar
     * @param ano año a consultar
     * @param prueba prueba a consultar
     * @return instancias
     */
     public Instances seleccionarIndicador(String indicador, int ano, String prueba) {
            String consulta;
            String validacion="";
            if(indicador.contains("fami_educacion") && ano>= 2017)
                validacion="and "+indicador+" <> 'Universitario - Posgrado'";
            if (indicador.contains("punt") || ano>= 2017) {
                consulta = "SELECT estu_genero, " + indicador + " FROM "+prueba+".resultados" + ano ;
            } else {
                consulta = "SELECT estu_genero, " + indicador + " FROM "+prueba+".resultadosse" + ano;
            }
            return consultarIndicador(consulta+" where "+indicador+" is not null and estu_genero='Mujer' "+validacion);
    }
    
    public ConexionBasico getConexion() {
        return conexion;
    }

    public void setConexion(ConexionBasico conexion) {
        this.conexion = conexion;
    }
}


