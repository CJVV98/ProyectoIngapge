/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.controller;

import com.udec.pojo.Usuario;
import com.udec.repositorio.UsuarioDB;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 *
 * @author angie
 */
@ManagedBean(name = "login")
@ViewScoped
public class Login extends HttpServlet implements Serializable {

    @Pattern(regexp = "[a-zA-Z ]{3,20}", message = "Ingrese un usuario válido")
    private String usuario;
    @Size(min = 3, message = "Ingrese una contraseña válida")
    private String contrasena, contraConf;
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Por favor ingrese un correo válido")
    private String correo;
    private static final AgregarUsuario contacto = new AgregarUsuario();
    private static final UsuarioDB REPOSITORIO = new UsuarioDB();
    HttpSession httpSession;

    public void iniciarSesion() {
        try {
            String redireccion = null;
            int validacion = new UsuarioDB().consultarUsuario(usuario, contrasena);
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            if (validacion == 1) {
                httpSession = request.getSession(true);
                httpSession.setMaxInactiveInterval(1800);
                httpSession.setAttribute("id", validacion);
                context.getExternalContext().redirect("principalAdmin.xhtml");
            } else if (validacion > 1) {
                httpSession = request.getSession(true);
                httpSession.setAttribute("id", validacion);
                context.getExternalContext().redirect("principalUsuario.xhtml");
            } else {
                context.addMessage("message-info", new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Usuario y/o contraseña incorrecta", ""));
            }
        } catch (Exception ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String cerrarSesion() {
        try {
            httpSession.removeAttribute("id");
            httpSession.invalidate();
            return "login.xhtml";
        } catch (Exception ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);   
            return "login.xhtml";
        }
    }

    public void correoContrasenia(String email) throws SQLException, MessagingException {
        Usuario user = REPOSITORIO.buscarCorreo(email);
        contacto.setCorreo(email);
        if (user != null) {
            contacto.enviarUsuarioContrasena("Cambio de contraseña", "¡Se ha solicitado un cambio de contraseña!\n\nEstimado usuario " + user.getNombre()
                    + ", esta es su nueva contraseña de acceso: " + user.getContrasena() + "\n\nIngapge");
        }
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraConf() {
        return contraConf;
    }

    public void setContraConf(String contraConf) {
        this.contraConf = contraConf;
    }
}
