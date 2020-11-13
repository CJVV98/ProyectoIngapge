/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.excepciones;


import java.time.LocalDate;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
/**
 *
 * @author Admin
 */
@Provider
public class ConstraintViolationHandler implements ExceptionMapper<ConstraintViolationException>{


    @Override
    public Response toResponse(ConstraintViolationException ex) {  
        ErrorExceptionWrapper api = new ErrorExceptionWrapper( ex.getMessage(), prepareMessage(ex), ex.getStackTrace(), LocalDate.now());
        return Response.status(Response.Status.BAD_REQUEST).entity(api).build();
    }
    
     private String prepareMessage(ConstraintViolationException ex) {
        String msg = "";
        for (ConstraintViolation<?> cv : ex.getConstraintViolations()) {
            msg+=cv.getPropertyPath()+" "+cv.getMessage()+"\n";
        }
        return msg;
    }    
    
}