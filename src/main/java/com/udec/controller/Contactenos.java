/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.controller;

import com.udec.utilitarios.ManejoArchivo;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author ASUS
 */
@ManagedBean(name = "contactenos")
@ViewScoped
public class Contactenos implements Serializable {

    /**
     * Creates a new instance of Contactenos
     */
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Por favor ingrese un correo")
    private String correo;

    @Size(min = 3, max = 100, message = "Ingrese un mensaje valido")
    private String mensaje;

    @Pattern(regexp = "[a-zA-Z ]{3,20}", message = "Ingrese un nombre valido")
    private String nombre;

    @Pattern(regexp = "[a-zA-Z ]{3,20}", message = "Ingrese un apellido valido")
    private String apellido;

    static final String NOMUSUARIO = "ingapgebrechas@gmail.com";
    static final String CONTRASENIA = "Brechas1029";
    private transient Session session;

    public void envio() throws MessagingException {
          enviar("Correo de usuario", "DATOS USUARIO\n" + "Nombre: " + nombre + "\nApellido: " + apellido + "\nCorreo: " + correo + "\nMensaje: " + mensaje);
    }

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
            context.addMessage("message-info", new FacesMessage(FacesMessage.SEVERITY_WARN,
                   "Error", "Error en el envio del mensaje"));
            throw me;
        }
        context.addMessage("message-info", new FacesMessage(FacesMessage.SEVERITY_INFO,
               "Información", "Mensaje enviado exitosamente"));
        limpiarFormulario();
    }

    public void limpiarFormulario() {
        correo = "";
        apellido = "";
        nombre = "";
        mensaje = "";
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}
