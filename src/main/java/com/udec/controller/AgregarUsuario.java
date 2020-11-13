/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.controller;

import com.udec.repositorio.UsuarioDB;
import com.udec.pojo.Usuario;
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
import javax.ws.rs.WebApplicationException;

/**
 * Clase encargada del registro de usuarios
 * @author ASUS
 */
@ManagedBean(name = "agregar_usuario")
@ViewScoped
public class AgregarUsuario implements Serializable {
   // nombre del usuario
    private String nombre;
    // apellido del usuario
    private String apellido;
    // correo del usuario
    private String correo;
    // usuario del usuario
    private String usuario;
    // contraseña del usuario
    private String contrasena;
    //Repositorio de Base de datos
    private static final UsuarioDB REPOSITORIO = new UsuarioDB();
    // Usuario de correo
    private static final String USERNAME = "ingapgebrechas@gmail.com";
    //Contraseña de usuario
    private static final String CONTRASENIA = "Brechas1029";
    // Sesion de usuario administrador
    private transient Session session;
    // Mensaje del correo
    private String mensaje;
    
    private transient FacesContext context = FacesContext.getCurrentInstance();
    
   /**
    * Metodo encargado de validar y registrar los datos de los usuarios
    */
    public void registrar(){
        context = FacesContext.getCurrentInstance();
        if (correo.contains("@outlook") || correo.contains("@gmail") || correo.contains("@hotmail") || correo.contains("@yahoo")) {
            context.addMessage(null, new FacesMessage("Correo incorrecto. Debe ser un correo institucional.", "Error"));
        } else {
            try {
                String[] aux = correo.split("@");
                usuario = aux[0];
                Usuario usua = new Usuario(nombre, apellido, correo, usuario, contrasena);
                Usuario usu1 = REPOSITORIO.buscarCorreo(correo);
                if (usu1 != null) {
                    context.addMessage("message-info", new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Correo ya existente", "Registre otro correo"));
                } else {
                    Usuario usu2 = REPOSITORIO.buscarUsuario(usuario);
                    if (usu2 != null) {
                        context.addMessage("message-info", new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Usuario ya existente", "Registre otro usuario"));
                    } else {
                        REPOSITORIO.crear(usua);
                        context.addMessage("message-info", new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Registro exitoso", "Usuario: " + usuario + " registrado"));
                        enviarUsuarioContrasena("Datos de acceso a Ingapge", "Buen día " + nombre + " " + apellido + "\nIngrese con los siguientes datos a Ingapge"
                                + "\nUsuario: " + usuario + "\nContraseña: " + contrasena);
                        limpiarFormulario();
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(AgregarUsuario.class.getName()).log(Level.SEVERE, null, ex);
                throw new WebApplicationException();
            }
        }
    }
    /**
     * Metodo usado para enviar correo con datos del usuario creado
     * @param asunto corresponde al asunto del correo
     * @param cuerpo contenido del correo
     */
    public void enviarUsuarioContrasena(String asunto, String cuerpo) {
        Properties props = System.getProperties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");    //Usar autenticación mediante usuario y clave
        props.put("mail.smtp.starttls.enable", "true"); //Para conectar de manera segura al servidor SMTP
        props.put("mail.smtp.port", "25");//El servidor SMTP de Google
        props.put("mail.smtp.user", USERNAME);
        props.put("mail.smtp.clave", CONTRASENIA);
        session = Session.getDefaultInstance(props);
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(correo));   //Se podrían añadir varios de la misma manera
            message.setSubject(asunto);
            message.setText(cuerpo);
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", USERNAME, CONTRASENIA);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException me) {
            Logger.getLogger(AgregarUsuario.class.getName()).log(Level.SEVERE, "Error", me);
        }
    }
    /**
     * Limpia el formulario de registro
     */
    private void limpiarFormulario() {
        this.apellido = "";
        this.contrasena = "";
        this.correo = "";
        this.nombre = "";
        this.usuario = "";
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

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }    
}
