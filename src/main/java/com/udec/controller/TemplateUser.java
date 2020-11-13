/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.controller;

import com.udec.repositorio.UsuarioDB;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.primefaces.PrimeFaces;

/**
 * Clase usada para cargar la master del usuario
 *
 * @author Corin Viracacha, Angie Manrique, Alisson Celeita
 */
@ManagedBean(name = "templateUser")
@ViewScoped
public class TemplateUser implements Serializable {

   private String nombre;
    private int validacion = 0;
    FacesContext context = FacesContext.getCurrentInstance();
    private static final UsuarioDB BD = new UsuarioDB();
    private String mensaje="";
    static final String NOMUSUARIO = "ingapgebrechas@gmail.com";
    static final String CONTRASENIA = "Brechas1029";
    private transient Session session;

    /**
     * Metodo encargado de validar la sesión del usuario
     * @throws IOException
     */
    public TemplateUser() throws IOException {
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession;
        httpSession = request.getSession();
        try {
            validacion = Integer.parseInt(httpSession.getAttribute("id").toString());
            if (validacion == 1 || validacion == 0) {
                PrimeFaces.current().executeScript("window.location='login.xhtml'");
            } else {
                nombre = BD.buscarPorId(validacion);
            }
        } catch (Exception ex) {
            PrimeFaces.current().executeScript("window.location='login.xhtml'");
            Logger.getLogger(Template.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Metodo encargado del envio de mensajes
     * @throws MessagingException 
     */
    public void envio() throws MessagingException {
        if (mensaje.length() > 10) {
           enviar("Correo de usuario", "DATOS USUARIO\n" + "Nombre: user \nApellido: " + nombre + "\nCorreo: " + nombre + "\nMensaje: " + mensaje);
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            mensaje="";
            context.addMessage("message-info-template", new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Error", "Digite un mensaje valido"));
        }
        
    }
    /**
     * Metodo encargado de enviar el correo por gmail
     * @param asunto del mensaje
     * @param cuerpo del mensaje
     * @throws MessagingException excepcion 
     */
    public void enviar(String asunto, String cuerpo) throws MessagingException {
        FacesContext context = FacesContext.getCurrentInstance();
        Properties props = System.getProperties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");    //Usar autenticación mediante usuario y clave
        props.put("mail.smtp.starttls.enable", "true"); //Para conectar de manera segura al servidor SMTP
        props.put("mail.smtp.port", "25");//El servidor SMTP de Google
        props.put("mail.smtp.user", NOMUSUARIO);
        props.put("mail.smtp.clave", CONTRASENIA);
        session = Session.getDefaultInstance(props);
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(NOMUSUARIO));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(NOMUSUARIO));
            message.setSubject(asunto);
            message.setText(cuerpo);
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", NOMUSUARIO, CONTRASENIA);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException me) {
            context.addMessage("message-info-template", new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Error", "Error en el envio del mensaje"));
            throw me;
        }
        context.addMessage("message-info-template", new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Información", "Mensaje enviado exitosamente"));
        mensaje = "";
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
