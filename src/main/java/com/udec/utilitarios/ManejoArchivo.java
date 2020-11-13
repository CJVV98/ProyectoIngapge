/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.utilitarios;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;

/**
 * Clase encargada para el manejo de archivos
 * @author Corin Viracacha
 */
public class ManejoArchivo {

    /**
     * Metodo para obtener el archivo de colores
     * @return listado de colores
     */
    public List<String> obtener() {
        List<String> listado = new ArrayList<>();
        FileInputStream archivo;
        String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/files/colores.txt");
        try {
            archivo = new FileInputStream(path);
            ObjectInputStream objeto;
            objeto = new ObjectInputStream(archivo);
            listado = (ArrayList) objeto.readObject();
            objeto.close();
            archivo.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ManejoArchivo.class.getName()).log(Level.SEVERE, null, ex);
        } catch(IOException ex){
             Logger.getLogger(ManejoArchivo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
             Logger.getLogger(ManejoArchivo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listado;
    }
}
