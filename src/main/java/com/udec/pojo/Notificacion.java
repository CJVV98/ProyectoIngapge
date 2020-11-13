/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.pojo;

import java.util.Date;

/**
 *
 * @author Admin
 */
public class Notificacion {
    private int id;
    private String prueba;
    private int ano;
    private String mensaje;
    private String tipo;
    private Date fecha;
    private String tipoResultado;
    private String nombre;
 
    public Notificacion(int id, String prueba, int ano, String mensaje, String tipo, Date fecha, String tipoResultado, String nombre) {
        this.id = id;
        this.prueba = prueba;
        this.ano = ano;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.fecha=fecha;
        this.tipoResultado=tipoResultado;
        this.nombre=nombre;
    }
    
    public Notificacion(String mensaje, Date fecha){
        this.mensaje= mensaje;
        this.fecha= fecha;
    }

    public Notificacion() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getTipoResultado() {
        return tipoResultado;
    }

    public void setTipoResultado(String tipoResultado) {
        this.tipoResultado = tipoResultado;
    }
    
    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

  
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrueba() {
        return prueba;
    }

    public void setPrueba(String prueba) {
        this.prueba = prueba;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    
    
}
