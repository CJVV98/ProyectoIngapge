/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.utilitarios;

import com.udec.controller.Login;
import com.udec.pojo.Notificacion;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;

/**
 * Clase encargada de consultar correos
 * @author Corin Viracahca
 */
public class NotificacionesCorreo {

    private transient List<Notificacion> listadoNotificaciones;
    /**
     * Metodo encargado de la consulta de correos
     * @return listado de correos
     */
    public List<Notificacion>  consultarCorreos() {
        listadoNotificaciones = new ArrayList<>();
        Properties propiedades = System.getProperties();
        propiedades.setProperty("mail.store.protocol", "imaps");
        try {
            Session session = Session.getDefaultInstance(propiedades, null);
            session.setDebug(true);
            Store store = session.getStore("imaps");
            //Permite almacenar en un fichero los valores de propiedad
            store.connect("imap.gmail.com", "ingapgebrechas@gmail.com", "Brechas1029");
            Folder inbox = store.getFolder("registros");
            inbox.open(Folder.READ_ONLY);
            //Marcar bandera de los mensajes
            FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            Message messages[] = inbox.search(ft);
            for (Message mensaje : messages) {
                MimeMessage message = (MimeMessage) mensaje;
                if (message.getContentType().contains("TEXT")) {
                    listadoNotificaciones.add(obtenerMensaje(message));
                }
            }
        } catch (NoSuchProviderException e) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error en consulta de correo", e);
        } catch (MessagingException e) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error en mensaje de correo", e);
        }
        return listadoNotificaciones;
    }
    /**
     * Metodo para determinar el contenido del mensaje del correo
     * @param message Mensaje
     * @return datos del mensaje
     */
    private Notificacion obtenerMensaje(MimeMessage message) {
        Notificacion notificacion = new Notificacion();
        try {
            if (message.getSubject().contains("Correo")) {
                String[] datos = message.getContent().toString().split("Correo:");
                String[] datosNombre = datos[0].split("Nombre:");
                String[] datosMensaje = datosNombre[1].split("Apellido:");
                notificacion.setMensaje("Correo de: " + datosMensaje[0] + " " + datosMensaje[1]);
                notificacion.setTipo("email1");
            } else {
                notificacion.setMensaje("Correo: " + message.getFrom()[0].toString());
                notificacion.setTipo("email2");
            }
            notificacion.setFecha(message.getReceivedDate());
        } catch (MessagingException ex) {
            Logger.getLogger(NotificacionesCorreo.class.getName()).log(Level.SEVERE, "Error en mensaje", ex);
        } catch (IOException ex) {
            Logger.getLogger(NotificacionesCorreo.class.getName()).log(Level.SEVERE, "Error en lectura de correo ", ex);
        }
        return notificacion;
    }
}
