/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.controller;

import com.udec.pojo.Notificacion;
import com.udec.repositorio.ArchivosBD;
import com.udec.repositorio.ConexionSuperior;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;

/**
 * Clase que gestiona la carga de archivos a la base de datos
 *
 * @author Angie Paola Manrique
 * @author Alisson Catalina Celeita
 */
@ManagedBean(name = "cargaArchivos")
@ViewScoped
public class CargaArchivos implements Serializable {

    private transient Part direccion;
    String create, alter, nombre, guardar, anio;
    private static final ConexionSuperior BD = new ConexionSuperior();
    private static final ArchivosBD FILES = new ArchivosBD();
    private static final Template NOTIFICACION = new Template();
    private Date fecha = new Date();
    private transient List<Notificacion> notificaciones;

    /*
     Permite la lectura del archivo y la posterior carga de registros
     */
    public void evaluacionArchivo() {
        try {
            nombre = direccion.getSubmittedFileName();
            nombre = nombre.toLowerCase();
            InputStream archivoRes = direccion.getInputStream();
            String carpeta = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/originales_superior/");
            File file = new File(carpeta, nombre);
            Files.copy(archivoRes, file.toPath());
            validacionesIniciales();
            int completo = FILES.consultarPeriodo(nombre);
            if (completo == 0) {
                int existencia = validarNombre();
                if (existencia > 0) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
                    String carpetaFinal = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/pruebas_superior/");
                    guardar = carpetaFinal + nombre + ".txt";
                    File file2 = new File(guardar);
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2), "utf-8"));
                    hacerTratamiento(in, bw);
                    in.close();
                    bw.close();
                    validacion();
                } else {
                    crearMensaje("Esta prueba ya esta cargada completamente o no ha sido cargado el primer periodo", "Error", FacesMessage.SEVERITY_ERROR);
                }
            } else {
                crearMensaje("Ya han sido cargados estos registros", "Error", FacesMessage.SEVERITY_ERROR);
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(CargaArchivos.class.getName()).log(Level.SEVERE, "Error", ex);
            crearMensaje("Este archivo no puede ser tratado, intentelo de nuevo", "Error", FacesMessage.SEVERITY_ERROR);
        }
    }

    /*
     Arregla los caracteres que contienen los registros previo a su carga
     */
    public void hacerTratamiento(BufferedReader in, BufferedWriter bw) throws IOException {
        String linea = "";
        int ban = 0, b = 0;
        while ((linea = in.readLine()) != null) {
            b++;
            if (b == 1) {
                if (linea.contains("¬")) {
                    ban = 1;
                } else if (linea.contains("|")) {
                    ban = 0;
                }
            }
            linea = linea.replace(",", ".").replace("Ñ", "N").replace("ñ", "n").replace(":", "").replace("-", "").replace("(", "")
                    .replace(")", "").replace("\"", "").replace("'", "").replace("(", "").replace(")", "").replace(" TYT", "").replace(";", "n").replace("TÉCNICO", "TECNICO")
                    .replace("TECNOLÓGICO", "TECNOLOGICO");
            for (int i = 10; i < 31; i++) {
                linea = linea.replace(i + " TEC", "TEC");
            }
            if (ban == 1) {
                linea = linea.replace("¬", ",").replace("|", "");
            } else {
                linea = linea.replace("|", ",").replace("¬", "");
            }
            linea = linea + "\n";
            bw.write(linea);
            if (b == 1) {
                create = linea;
            }
        }
    }

    /*
     Valida el formato del archivo y el nombre del mismo
     */
    public void validacionesIniciales() {
        if (!nombre.contains(".txt")) {
            crearMensaje("El tipo de archivo debe ser txt", "Error", FacesMessage.SEVERITY_ERROR);
        } else {
            if (!nombre.contains("sb11") && !nombre.contains("sabertyt") && !nombre.contains("saberpro")) {
                crearMensaje("Este archivo no puede ser tratado", "Error", FacesMessage.SEVERITY_ERROR);
            } else {
                String[] auxNom = nombre.split(".tx");
                nombre = auxNom[0];
            }
        }
        if (nombre.contains("_genericas")) {
            nombre = nombre.replaceAll("_genericas", "");
        } else if (nombre.contains("_genéricas")) {
            nombre = nombre.replaceAll("_genéricas", "");
        }
    }

    /*
     Valida la existencia de registros en la base de datos según el nombre del archivo
     */
    public int validarNombre() {
        if (nombre.contains("saberpro")) {
            return 1;
        } else {
            int existencia;
            if (nombre.charAt(nombre.length() - 1) == '1') {
                anio = "_2018";
                nombre = nombre.substring(0, nombre.length() - 1);
                existencia = FILES.existenciaRegistros(nombre);
                if (existencia > 0) {
                    return 0;
                } else {
                    return 1;
                }
            } else {
                String auxNom = nombre.substring(0, nombre.length() - 1);
                existencia = FILES.existenciaRegistros(auxNom);
                String[] aux = nombre.split("_");
                anio = "_" + aux[1];
                if (existencia > 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }

    /*
     Valida el período y prueba que va a ser cargada 
     */
    public void validacion() throws SQLException {
        try {
            if (nombre.contains("sb11")) {
                if (nombre.length() == 10 && nombre.charAt(9) == '2') {
                    creartablaAux();
                } else {
                    creartabla();
                }
            } else if (nombre.contains("tyt")) {
                if (nombre.length() == 14 && nombre.charAt(13) == '2') {
                    creartablaAux();
                } else {
                    creartabla();
                }
            } else if (nombre.contains("pro")) {
                creartabla();
            }
        } catch (Exception ex) {
            Logger.getLogger(CargaArchivos.class.getName()).log(Level.SEVERE, "Error", ex);
        }
    }

    /*
     Este método permite la carga de registros de segundo período con ayuda de una tabla auxiliar
     */
    public void creartablaAux() throws SQLException {
        try {
            alter = "";
            create = create.replace(",", " varchar(200), ");
            create = create + " varchar(200)";
            try {
                Thread.sleep(80000);
            } catch (InterruptedException e) {
            }
            BD.crear(create, guardar, nombre);
            List<String> columnaseliminar = FILES.columnaseliminar(nombre);
            String[] auxAnio = nombre.split("_");
            String anio = auxAnio[1];
            anio = anio.substring(0, anio.length() - 1);
            List<String> columnasfinal = FILES.columnasSegundoPeriodo(nombre, anio);
            columnaseliminar.removeAll(columnasfinal);
            for (String x : columnaseliminar) {
                BD.editar(x, nombre);
            }
            List<String> colum = FILES.columnaseliminar(nombre);
            int conta = 0;
            String columnas = "";
            for (String col : colum) {
                if (conta == 0) {
                    columnas = col;
                } else {
                    columnas = columnas + ", " + col;
                }
                conta++;
            }
            columnas = columnas.replace("[", "").replace("]", "");
            BD.copiarTabla(nombre, columnas);
            BD.eliminarTabla(nombre);
            FILES.cargarCompleta(nombre.substring(0, nombre.length() - 1));
            crearMensaje("Se han cargado exitosamente los registros", "Proceso finalizado", FacesMessage.SEVERITY_INFO);
        } catch (SQLException e) {
            BD.eliminarTabla(nombre);
            crearMensaje("Error en el proceso de creación y copia de registros", "Error", FacesMessage.SEVERITY_ERROR);
            Logger.getLogger(CargaArchivos.class.getName()).log(Level.SEVERE, "Error en el proceso de creación", e);
        }
    }

    /*
     Crea la tabla donde se almacenan los registros de cada prueba
     */
    public void creartabla() throws SQLException {
        try {
            alter = "";
            create = create.replace(",", " varchar(200),");
            create = create + " varchar(200)";
            try {
                Thread.sleep(80000);
            } catch (InterruptedException e) {
            }
            BD.crear(create, guardar, nombre);
            List<String> columnaseliminar = FILES.columnaseliminar(nombre);
            List<String> columnasfinal = FILES.columnasfinal(nombre, anio);
            columnaseliminar.removeAll(columnasfinal);
            for (String x3 : columnaseliminar) {
                BD.editar(x3, nombre);
            }
            if (nombre.contains("pro")) {
                FILES.cargarCompleta(nombre);
            }
            crearMensaje("Proceso finalizado", "Info", FacesMessage.SEVERITY_INFO);
        } catch (SQLException e) {
            BD.eliminarTabla(nombre);
            crearMensaje("Error en el proceso de creación y copia de registros", "Error", FacesMessage.SEVERITY_ERROR);
            Logger.getLogger(CargaArchivos.class.getName()).log(Level.SEVERE, "Error en el proceso de creación", e);
        }
    }

    /*
     Método para generar mensajes
     */
    private void crearMensaje(String mensaje, String encabezadoMensaje, FacesMessage.Severity tipoError) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage("message-info", new FacesMessage(tipoError,
                encabezadoMensaje, mensaje));

    }

    /*
     Método que obtiene la dirección del archivo
     */
    public Part getDireccion() {
        return direccion;
    }

    /*
     Método para darle valor a la dirección del archivo
     */
    public void setDireccion(Part direccion) {
        this.direccion = direccion;
    }
}
