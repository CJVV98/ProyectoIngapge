/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.controller;

import com.udec.repositorio.UsuarioDB;
import com.udec.pojo.Usuario;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;

/**
 *
 * @author ASUS
 */
@ManagedBean(name = "listado_usuarios")
@ViewScoped
public class ListadoUsuarios implements Serializable {
    private transient List<Usuario> usuarios;
    private transient FacesContext context;
    private static final UsuarioDB REPOSITORIO = new UsuarioDB();    

    public ListadoUsuarios() {
        usuarios = new ArrayList();
    }

    @PostConstruct
    public void llenar() {
        usuarios = REPOSITORIO.listarTodos();
    }

    public void eliminar(int id) {
        context = FacesContext.getCurrentInstance();
        REPOSITORIO.eliminar(id);
        context.addMessage("message-info", new FacesMessage(FacesMessage.SEVERITY_WARN,
                "Eliminación exitosa", "Se ha eliminado exitosamente"));
        llenar();
    }

    public void editar(Usuario usuario) {
        usuario.setEditar(true);
    }

    public void guardar(Usuario usuario) throws SQLException, MessagingException {
        context = FacesContext.getCurrentInstance();
        REPOSITORIO.editar(usuario);        
        context.addMessage("message-info", new FacesMessage(FacesMessage.SEVERITY_WARN,
                "Edición exitosa", "Se ha editado exitosamente"));
        usuario.setEditar(false);        
    }

    public void cancelar(Usuario usuario) {
        usuario.setEditar(false);
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }
}
