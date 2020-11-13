/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.excepciones;
import java.time.LocalDate;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author Admin
 */
public class ErrorExceptionWrapper{
    
    private String error;
    private String descripcion;
    private StackTraceElement[] trace;
    private LocalDate fecha;
    private Status codigo;

    public ErrorExceptionWrapper() {
    }

    public ErrorExceptionWrapper(String error, String descripcion, StackTraceElement[] trace, LocalDate fecha, Status codigo) {
        this.error = error;
        this.descripcion = descripcion;
        this.trace = trace;
        this.fecha = fecha;
        this.codigo=codigo;
    }

    public ErrorExceptionWrapper(String error, String descripcion, StackTraceElement[] trace, LocalDate fecha) {
        this.error = error;
        this.descripcion = descripcion;
        this.trace = trace;
        this.fecha = fecha;
    }
    
    public Status getCodigo() {
        return codigo;
    }

    public void setCodigo(Status codigo) {
        this.codigo = codigo;
    }


    public StackTraceElement[] getTrace() {
        return trace;
    }

    public void setTrace(StackTraceElement[] trace) {
        this.trace = trace;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
