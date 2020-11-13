/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.excepciones;

import java.sql.SQLException;

/**
 *
 * @author Admin
 */
public class SQLConectionException extends SQLException{
    private static final long serialVersionUID = 1L;
    
    public SQLConectionException(String mensaje) {
        super(mensaje);
    }

    public SQLConectionException() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
