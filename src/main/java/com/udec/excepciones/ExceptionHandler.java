/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.excepciones;
import java.time.LocalDate;
import javax.transaction.RollbackException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
/**
 *
 * @author Admin
 */
public class ExceptionHandler implements ExceptionMapper<Exception> {
    
    @Override
    public Response toResponse(Exception ex) {
        ErrorExceptionWrapper api;
        if(ex instanceof SQLConectionException){
            api = new ErrorExceptionWrapper(ex.getMessage(),"El recurso al que desea acceder no se encuentra disponible",ex.getStackTrace(),
                    LocalDate.now(), Response.Status.INTERNAL_SERVER_ERROR); 
        }else if(ex instanceof NotFoundException){
            api = new ErrorExceptionWrapper(ex.getMessage(),
                "Objeto no encontrado",ex.getStackTrace(),LocalDate.now(), Response.Status.NOT_FOUND);     
        }else if (ex instanceof BadRequestException){
            api = new ErrorExceptionWrapper(ex.getMessage(),
                "Parametros o metodo incorrecto",ex.getStackTrace(),LocalDate.now(), Response.Status.BAD_REQUEST);  
        }else if(ex instanceof WebApplicationException){
            api = new ErrorExceptionWrapper(ex.getMessage(),
                "Error interno del aplicativo",ex.getStackTrace(),LocalDate.now(), Response.Status.INTERNAL_SERVER_ERROR);    
        }else if(ex instanceof RollbackException){           
            api = new ErrorExceptionWrapper(ex.getMessage(),
                "Error interno en el llamado a un Entity",ex.getStackTrace(),LocalDate.now(), Response.Status.INTERNAL_SERVER_ERROR);        
        }else {   
            api = new ErrorExceptionWrapper(ex.getMessage(),
                "Error de aplicativo",ex.getStackTrace(),LocalDate.now(), Response.Status.INTERNAL_SERVER_ERROR);
        }
        return Response.status(api.getCodigo()).entity(api).build();
    }
    
}
