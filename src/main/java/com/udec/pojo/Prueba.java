/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.pojo;

/**
 *
 * @author Admin
 */
public class Prueba {
    private int ano;
    private boolean saber3;
    private boolean saber5;
    private boolean saber9;
    private boolean saber11;
    private boolean saberT;
    private boolean saberPro;

    public Prueba(int ano, boolean saber3, boolean saber5, boolean saber9, boolean saber11, boolean saberT, boolean saberPro) {
        this.ano = ano;
        this.saber3 = saber3;
        this.saber5 = saber5;
        this.saber9 = saber9;
        this.saber11 = saber11;
        this.saberT = saberT;
        this.saberPro = saberPro;
    }

    public Prueba() {
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public boolean isSaber3() {
        return saber3;
    }

    public void setSaber3(boolean saber3) {
        this.saber3 = saber3;
    }

    public boolean isSaber5() {
        return saber5;
    }

    public void setSaber5(boolean saber5) {
        this.saber5 = saber5;
    }

    public boolean isSaber9() {
        return saber9;
    }

    public void setSaber9(boolean saber9) {
        this.saber9 = saber9;
    }

    public boolean isSaber11() {
        return saber11;
    }

    public void setSaber11(boolean saber11) {
        this.saber11 = saber11;
    }

    public boolean isSaberT() {
        return saberT;
    }

    public void setSaberT(boolean saberT) {
        this.saberT = saberT;
    }

    public boolean isSaberPro() {
        return saberPro;
    }

    public void setSaberPro(boolean saberPro) {
        this.saberPro = saberPro;
    }
    
    
    
}
