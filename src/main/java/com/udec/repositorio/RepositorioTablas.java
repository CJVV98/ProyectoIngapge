/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.repositorio;

import com.udec.controller.Login;
import com.udec.pojo.Notificacion;
import com.udec.pojo.Tabla;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase encargada del manejo de la informacion de las tablas
 *
 * @author Corin Viracacha
 */
public class RepositorioTablas {

    Connection conn = null;
    private ConexionBasico conexion = new ConexionBasico();
    private Connection conexionGlobal = null;

    /**
     * Metodo encargado de registrar una prueba cargada
     *
     * @param ano año
     * @param nombre nombre de la tabla
     * @param esquema esquema es el nombre de la prueba
     * @param url ruta del archivo
     * @param tipo tipo de resultados
     */
    public void insertarTabla(int ano, String nombre, String esquema, String url, String tipo) {
        try {
            String query = "INSERT INTO tablas.listado_tablas (ano, nombre,"
                    + " url,esquema, tipo) VALUES (?, ?, ?, ?, ?);";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);
            sentenciaP.setInt(1, ano);
            sentenciaP.setString(2, nombre);
            sentenciaP.setString(3, url);
            sentenciaP.setString(4, esquema);
            sentenciaP.setString(5, tipo);
            sentenciaP.executeUpdate();
            sentenciaP.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al insertar datos", ex);
        }
    }

    /**
     * Modificar datos del listado de tablas
     *
     * @param id id de la tabla
     * @param ruta ruta del archivo
     */
    public void modificarDatos(int id, String ruta) {
        try {
            String query = "UPDATE tablas.listado_tablas SET  tratamiento=0 WHERE id=? ";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);
            sentenciaP.setInt(1, id);
            sentenciaP.executeUpdate();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al insertar datos", ex);

        }
    }

    /**
     * Eliminar prueba de la tabla
     *
     * @param id,de la prueba
     */
    public void eliminarProceso(int id) {
        try {
            String query = "DELETE  FROM tablas.listado_tablas WHERE id=? ";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);
            sentenciaP.setInt(1, id);
            sentenciaP.executeUpdate();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al eliminar datos", ex);

        }
    }

    /**
     * Metodo encargado de consultar las tablas de saber primaria
     *
     * @return Listado de tablas
     */
    public List<Tabla> obtenerTablasPrimaria() {
        List<Tabla> listado = new ArrayList<>();
        try {
            String query = "SELECT table_schema as esquema, table_name AS tabla FROM information_schema.tables "
                    + "WHERE table_schema  LIKE '" + "saber%" + "' order by esquema,tabla";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                listado.add(new Tabla(resultado.getString("esquema"), resultado.getString("tabla")));
            }
            resultado.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar informacion sobre los nombres de las tablas de BD", ex);
        }
        return listado;

    }

    /**
     * Metodo encargado de consultar pruebas registradas
     *
     * @param esquema nombre de la prueba
     * @return listado de pruebas
     */
    public List<Tabla> consultaPruebasRegistradas(String esquema) {
        List<Tabla> resultadosPruebas = new ArrayList<>();
        try {
            String query = "SELECT esquema, ano FROM  tablas.listado_tablas WHERE  (tratamiento>-1) and esquema=?";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);
            sentenciaP.setString(1, esquema);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                resultadosPruebas.add(new Tabla(resultado.getString("esquema"), resultado.getInt("ano")));
            }
            resultado.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar informacion sobre los nombres de las tablas de BD", ex);

        }
        return resultadosPruebas;
    }

    /**
     * Consultar los años cargados y la cantidad de registros por año
     *
     * @param esquema nombre de la prueba
     * @return listado de pruebas
     */
    public List<Tabla> cargarAnosPresentacion(String esquema) {
        List<Tabla> anos = new ArrayList<>();
        try {
            String query = "SELECT ano, count(*) as cantidad FROM  tablas.listado_tablas WHERE  tratamiento=1 and (esquema='saber359' or esquema=?) group by ano order by ano";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);// 
            sentenciaP.setString(1, esquema);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                anos.add(new Tabla(resultado.getInt("ano"), resultado.getInt("cantidad")));
            }
            resultado.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar informacion sobre la tabla listado_tablas", ex);

        }
        return anos;
    }

    /**
     * Metodo encargado de obtener los resultados de las pruebas superior
     *
     * @param listado listado de pruebas
     * @return listado pruebas
     */
    public List<Tabla> obtenerTablasSuperior(List<Tabla> listado) {
        try {
            conn = ConexionSuperior.abrir();
            Statement stm = conn.createStatement();
            String query = "SELECT table_schema as esquema, table_name AS tabla FROM information_schema.tables "
                    + "WHERE table_schema = '" + "pruebas" + "' and table_name  LIKE '" + "s%" + "' ";
            ResultSet resultado = stm.executeQuery(query);

            while (resultado.next()) {
                listado.add(new Tabla(resultado.getString("esquema"), resultado.getString("tabla")));
            }
            resultado.close();
            stm.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar informacion sobre las tablas de base de datos", ex);
        }
        return listado;
    }

    /**
     * Metodo encargado de consultar la cantidad de mujeres registradas en
     * pruebas saber basico
     *
     * @param nombreTabla nombre de la tabla
     * @return cantidad de mujeres
     */
    public int consultarMujeresBasico(String nombreTabla) {
        String query = "SELECT  count(*) FROM  " + nombreTabla + " WHERE estu_genero='Mujer'";
        try {
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                return resultado.getInt(1);
            }
            resultado.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar informacion sobre las tablas de base de datos", ex);
        }
        return 0;
    }

    /**
     * Metodo encargado de consultar la cantidad de mujeres registradas en
     * pruebas saber superior
     *
     * @param nombreTabla nombre de la tabla
     * @return cantidad de mujeres
     */
    public int consultarMujeresSuperior(String nombreTabla) {
        String query = "SELECT count(*) FROM " + nombreTabla + " WHERE estu_genero = 'F' ";
        try {
            conn = ConexionSuperior.abrir();
            Statement stm = conn.createStatement();
            ResultSet resultado = stm.executeQuery(query);
            while (resultado.next()) {
                return resultado.getInt(1);
            }
            resultado.close();
            stm.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar informacion sobre las tablas de base de datos", ex);
        }
        return 0;
    }

    /**
     * Metodo encargado de consultar la cantidad de registros en saber basico
     * @param nombreTabla nombre de la tabla
     * @return cantidad de registros
     */
    public int consultarRegistrosBasico(String nombreTabla) {
        String query = "SELECT count(*) FROM " + nombreTabla + ";";
        try {
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                return resultado.getInt(1);
            }
            resultado.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar información en la base de datos", ex);
        }
        return 0;
    }

    /**
     * Metodo para consultar la cantidad de mujeres
     * @param prueba, prueba seleccionada
     * @param ano, año seleccionado
     * @return cantidad de registros
     */
    public Integer consultarMujeres(String prueba, int ano) {
        Integer cantidad = 0;
        String query = "SELECT cantidad FROM  general.cantidad_mujeres  WHERE prueba='" + prueba + "' and ano=" + ano + " ";
        try {
            conexionGlobal = ConexionGlobal.abrir();
            PreparedStatement sentenciaP = conexionGlobal.prepareStatement(query);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                cantidad = resultado.getInt(1);
            }
            resultado.close();
            sentenciaP.close();
            conexionGlobal.close();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al insertar los resultados de las pruebas", ex);
        }
        return cantidad;
    }

    /**
     * Metodo encargado de registrar la cantidad de mujeres
     * @param datosPrueba hace referencia a los datos de prueba
     */
    public void registrarCantidadMujeres(Tabla datosPrueba) {
        try {
            String query = "INSERT INTO general.cantidad_mujeres( prueba, ano, cantidad)"
                    + " VALUES ( ?, ?, ?);";
            conexionGlobal = ConexionGlobal.abrir();
            PreparedStatement sentenciaP = conexionGlobal.prepareStatement(query);
            sentenciaP.setString(1, datosPrueba.getPrueba());
            sentenciaP.setInt(2, datosPrueba.getAno());
            sentenciaP.setInt(3, datosPrueba.getCantidad());
            sentenciaP.executeUpdate();
            sentenciaP.close();
            ConexionGlobal.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al insertar los resultados de las pruebas", ex);
        }
    }

    /**
     * Consultar cantidad de registros superior
     * @param nombreTabla nombre de la tabla
     * @return cantidad de registros
     */
    public int consultarRegistrosSuperior(String nombreTabla) {
        String query = "SELECT count(*) FROM " + nombreTabla + ";";
        try {
            conn = ConexionSuperior.abrir();
            Statement stm = conn.createStatement();
            ResultSet resultado = stm.executeQuery(query);
            while (resultado.next()) {
                return resultado.getInt(1);
            }
            resultado.close();
            stm.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar información en la base de datos", ex);
        }
        return 0;
    }
    /**
     * Consultar tablas que tienen error
     * @param consulta tabla a consultar
     * @return notificaciones de tablas no cargadas
     */
    public List<Notificacion> obtenerTablasError(String consulta) {
        List<Notificacion> listadoTablasError = new ArrayList<>();
        String query = "SELECT * FROM tablas." + consulta + " ; ";
        try {
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                listadoTablasError.add(new Notificacion(resultado.getInt(1), resultado.getString(4), resultado.getInt(5),
                        resultado.getString(2), resultado.getString(3), resultado.getDate(6), resultado.getString(7),
                        resultado.getString(8)));
            }
            resultado.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar informacion sobre las tablas de base de datos", ex);
        }
        return listadoTablasError;
    }
    /**
     * Consultar registro actuales por cada tabla
     * @param prueba prueba seleccionada
     * @param ano año seleccionado
     * @return cantidad de registros
     */
    public Integer consultarRegistrosActuales(String prueba, int ano) {
        Integer cantidad = 0;
        String query = "SELECT cantidad FROM  general.listado_tablas  WHERE prueba='" + prueba + "' and ano=" + ano + " ";
        try {
            conexionGlobal = ConexionGlobal.abrir();
            PreparedStatement sentenciaP = conexionGlobal.prepareStatement(query);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                cantidad = resultado.getInt(1);
            }
            resultado.close();
            sentenciaP.close();
            conexionGlobal.close();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al insertar los resultados de las pruebas", ex);
        }
        return cantidad;
    }
    /**
     * Registro de la cantidad de datos en las pruebas
     * @param datosPrueba datos de la prueba 
     */
    public void registrarCantidadTabla(Tabla datosPrueba) {
        try {
            String query = "INSERT INTO general.listado_tablas(prueba, ano, cantidad)"
                    + " VALUES ( ?, ?, ?);";
            conexionGlobal = ConexionGlobal.abrir();
            PreparedStatement sentenciaP = conexionGlobal.prepareStatement(query);
            sentenciaP.setString(1, datosPrueba.getPrueba());
            sentenciaP.setInt(2, datosPrueba.getAno());
            sentenciaP.setInt(3, datosPrueba.getCantidad());
            sentenciaP.executeUpdate();
            sentenciaP.close();
            ConexionGlobal.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al insertar los resultados de las pruebas", ex);
        }
    }
}
