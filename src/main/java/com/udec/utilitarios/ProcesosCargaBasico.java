/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.utilitarios;

import com.udec.controller.Login;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.Part;


/**
 * Clase encargada de la carga de resultados
 * @author Corin Viracacha
 */
public class ProcesosCargaBasico {

    /**
     * Metodo encargado de validar el archivo
     * @param archivo archivo cargado
     * @param tipoMensaje indica si es puntajes o economico
     * @return los mensajes de error
     */
    public String[] validarResultados(Part archivo, String tipoMensaje) {
        String[] mensaje = new String[2];
        mensaje[1]="";
        if (tipoMensaje.contains("puntajes")) 
            mensaje[0] = "Archivo de resultados academicos";
        else 
            mensaje[0]  = "Archivo socioeconomico";
        try {
            if (archivo == null || archivo.getSize() <= 0 || archivo.getContentType().isEmpty()) {
                mensaje[1] =  mensaje[0]  + " no cargado ";
            } else if (!archivo.getSubmittedFileName().endsWith("zip")) {
                mensaje[1] =  mensaje[0]  + " debe estar en formato ZIP";
            } else if (archivo.getSize() > 90000000) {
                mensaje[1] =  mensaje[0]  + " peso maximo 90 Megas";
            }
        } catch (Exception ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al validar archivo", ex);
        }
        return mensaje;
    }
    /**
     * Metodo usado para guardar archivos
     * @param archivoResultados inputStream contiene el archivo cargado
     * @param folder folder donde se va a guardar
     * @param nombre nombre del archivo
     * @return url del archivo guardado
     */
    public String guardarArchivo(InputStream archivoResultados, String folder, String nombre) {
        try {
            InputStream archivoRes = archivoResultados;
            File archivo = new File(folder,nombre + ".zip");
            Files.copy(archivoRes, archivo.toPath());
            return archivo.getPath();
        } catch (IOException ex) {
            Logger.getLogger(ProcesosCargaBasico.class.getName()).log(Level.SEVERE, null, ex);
        }
       return "";
    }
}
