/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.repositorio;

import com.udec.controller.Login;
import com.udec.pojo.Usuario;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 *
 * @author angie
 */
public class UsuarioDB {

    Connection conn = null;
    Statement stm;
    ResultSet rs;
    int resultUpdate = 0;
    private static final Login LOGIN = new Login();

    public void crear(Usuario usuario) {
        String query = "INSERT INTO general.usuario(nombre, apellido, correo_institucional,usuario,contrasena) VALUES ('"
                + usuario.getNombre() + "', '" + usuario.getApellido() + "', '" + usuario.getCorreo() + "', '" + usuario.getUsuario() + "', '" + usuario.getContrasena() + "')";
        try {
            conn = ConexionGlobal.abrir();
            stm = conn.createStatement();
            resultUpdate = stm.executeUpdate(query);
            if (resultUpdate != 0) {
                ConexionGlobal.cerrar();
            } else {
                ConexionGlobal.cerrar();
            }
        } catch (Exception e) {
            Logger.getLogger(UsuarioDB.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Usuario buscarCorreo(String correo) throws SQLException {
        Usuario usu;
        String query = "SELECT * from general.usuario WHERE correo_institucional='" + correo + "' ;";
        try {
            conn = ConexionGlobal.abrir();
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            if (!rs.next()) {
                ConexionGlobal.cerrar();
                return null;
            } else {
                usu = new Usuario(rs.getInt("id_usuario"), rs.getString("usuario"), rs.getString("apellido"), rs.getString("correo_institucional"), rs.getString("usuario"), rs.getString("contrasena"));
                ConexionGlobal.cerrar();
                return usu;
            }
        } catch (Exception e) {
            Logger.getLogger(UsuarioDB.class.getName()).log(Level.SEVERE, null, e);
            throw e;
        }
    }

    public Usuario buscarUsuario(String usuario) throws SQLException {
        Usuario usu = null;
        String query = "SELECT * from general.usuario WHERE usuario='" + usuario + "' ;";
        try {
            conn = ConexionGlobal.abrir();
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            if (!rs.next()) {
                ConexionGlobal.cerrar();
                return null;
            } else {
                usu = new Usuario(rs.getInt("id_usuario"), rs.getString("usuario"), rs.getString("apellido"), rs.getString("correo_institucional"), rs.getString("usuario"), rs.getString("contrasena"));
                ConexionGlobal.cerrar();
                return usu;
            }
        } catch (Exception e) {
            Logger.getLogger(UsuarioDB.class.getName()).log(Level.SEVERE, null, e);
        }
        return usu;
    }

    public int consultarUsuario(String usuario, String contrasena) throws SQLException {
        int encontrado = 0;
        String query = "SELECT * from general.usuario WHERE usuario='" + usuario + "' and contrasena='" + contrasena + "';";
        try {
            conn = ConexionGlobal.abrir();
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            if (rs.next()) {
                encontrado=rs.getInt("id_usuario");
                ConexionGlobal.cerrar();
                return encontrado;
            }
        } catch (Exception e) {
            Logger.getLogger(UsuarioDB.class.getName()).log(Level.SEVERE, null, e);
            throw e;
        }
        return encontrado;
    }

    public List<Usuario> listarTodos() {
        List<Usuario> listadoUsuarios = new ArrayList();
        String query = "SELECT *from general.usuario;";
        try {
            conn = ConexionGlobal.abrir();
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            if (!rs.next()) {
                ConexionGlobal.cerrar();
                return listadoUsuarios.subList(0, 0);
            } else {
                do {
                    Usuario usu = new Usuario(rs.getInt("id_usuario"), rs.getString("nombre"), rs.getString("apellido"), rs.getString("correo_institucional"), rs.getString("usuario"), rs.getString("contrasena"));
                    if (usu.getIdUsuario() != 3) {
                        listadoUsuarios.add(usu);
                    }
                } while (rs.next());
                ConexionGlobal.cerrar();
                return listadoUsuarios;
            }
        } catch (Exception e) {
            Logger.getLogger(UsuarioDB.class.getName()).log(Level.SEVERE, null, e);
            return listadoUsuarios.subList(0, 0);
        }
    }

    public void eliminar(int id) {
        String query = "delete from general.usuario where id_usuario=" + id + ";";
        try {
            conn = ConexionGlobal.abrir();
            stm = conn.createStatement();
            resultUpdate = stm.executeUpdate(query);
            if (resultUpdate != 0) {
                ConexionGlobal.cerrar();
            } else {
                ConexionGlobal.cerrar();
            }
        } catch (Exception e) {
            Logger.getLogger(UsuarioDB.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public String buscarContrasenia(String usuario) {
        String contrasenia;
        String query = "select contrasena from general.usuario WHERE usuario='" + usuario + "' ;";
        try {
            conn = ConexionGlobal.abrir();
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            if (!rs.next()) {
                ConexionGlobal.cerrar();
                return null;
            } else {
                contrasenia = rs.getString("contrasena");
                ConexionGlobal.cerrar();
                return contrasenia;
            }
        } catch (Exception e) {
            Logger.getLogger(UsuarioDB.class.getName()).log(Level.SEVERE, "Error en la base de datos.", e);
            return null;
        }
    }
    
    public void editar(Usuario usuario) throws SQLException, MessagingException {
        String contraAntigua = buscarContrasenia(usuario.getUsuario());
        String contraNueva = usuario.getContrasena();
        String query = "update general.usuario set nombre='" + usuario.getNombre() + "', apellido= '"
                + usuario.getApellido() + "', contrasena='" + usuario.getContrasena() + "' " + "where id_usuario=" + usuario.getIdUsuario() + ";";
        try {
            conn = ConexionGlobal.abrir();
            stm = conn.createStatement();
            resultUpdate = stm.executeUpdate(query);

            if (resultUpdate != 0) {
                ConexionGlobal.cerrar();
            } else {
                ConexionGlobal.cerrar();
            }
        } catch (Exception e) {
            Logger.getLogger(UsuarioDB.class.getName()).log(Level.SEVERE, null, e);
        }
        if (!contraNueva.equals(contraAntigua)) {
            LOGIN.correoContrasenia(usuario.getCorreo());
        }
    }
    
    public String buscarPorId(int id) {
        String nombre;
        String query = "select nombre from general.usuario WHERE id_usuario='" + id + "' ;";
        try {
            conn = ConexionGlobal.abrir();
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            if (!rs.next()) {
                ConexionGlobal.cerrar();
                return null;
            } else {
                nombre = rs.getString("nombre");
                ConexionGlobal.cerrar();
                return nombre;
            }
        } catch (Exception e) {
            Logger.getLogger(UsuarioDB.class.getName()).log(Level.SEVERE, "Error en la base de datos.", e);
            return null;
        }
    }
}
