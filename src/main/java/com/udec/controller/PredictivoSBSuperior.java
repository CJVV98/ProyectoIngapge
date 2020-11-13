/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.controller;

import com.udec.pojo.PredictivoSuperior;
import com.udec.repositorio.PredictivoSuperiorBD;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;
import weka.classifiers.trees.J48;
import weka.core.Instances;

/**
 * @author Angie Paola Manrique
 * @author Alisson Catalina Celeita
 */
@ManagedBean(name = "predictivoSBSuperior")
@ViewScoped

public class PredictivoSBSuperior implements Serializable {

    private transient Instances instances;
    private transient List<String> categorias;
    private transient List<Integer> indices;
    private transient List<PredictivoSuperior> predictivos;
    String prueba, nomPrueba, indicador, nomInd, nucleoC, nucleoR, nucleoS, definicion;
    private PieChartModel graficaLecturaM, graficaInglesM, graficaCienciasM, graficaMatematicasM, graficaSocialesM, graficaInglesH, graficaLecturaH, graficaCienciasH, graficaMatematicasH, graficaSocialesH;
    boolean ocultar11 = true, ocultarTyT = false, ocultarPro = false, ocultar = true;
    PieChartDataSet serieEstudiantes;
    ChartData data;
    private static final PredictivoSuperiorBD REPOSITORIO = new PredictivoSuperiorBD();

    @PostConstruct
    public void init() {
        prueba = "sb11_muestra";
        graficaCienciasM = new PieChartModel();
        graficaMatematicasM = new PieChartModel();
        graficaInglesM = new PieChartModel();
        graficaLecturaM = new PieChartModel();
        graficaSocialesM = new PieChartModel();
        graficaCienciasH = new PieChartModel();
        graficaMatematicasH = new PieChartModel();
        graficaInglesH = new PieChartModel();
        graficaLecturaH = new PieChartModel();
        graficaSocialesH = new PieChartModel();
    }

    /*
     Actualiza los indicadores segun prueba
     */
    public void cargarIndicadores(ValueChangeEvent event) {
        prueba = event.getNewValue().toString();
        if ("sb11_muestra".equals(prueba)) {
            ocultar11 = true;
            ocultarTyT = false;
            ocultarPro = false;
        } else if ("sabertyt_muestra".equals(prueba)) {
            ocultarTyT = true;
            ocultarPro = false;
            ocultar11 = false;
        } else if ("saberpro_muestra".equals(prueba)) {
            ocultarPro = true;
            ocultarTyT = false;
            ocultar11 = false;
        }
    }

    /*
     Genera gráficas y las muestra en el formulario
     */
    public void general() {
        generarGrafica("punt_matematicas", 'F');
        mostrarGrafica(graficaMatematicasM);
        generarGrafica("punt_ingles", 'F');
        mostrarGrafica(graficaInglesM);
        generarGrafica("punt_ciencias", 'F');
        mostrarGrafica(graficaCienciasM);
        generarGrafica("punt_lectura", 'F');
        mostrarGrafica(graficaLecturaM);
        generarGrafica("punt_sociales", 'F');
        mostrarGrafica(graficaSocialesM);
        generarGrafica("punt_matematicas", 'M');
        mostrarGrafica(graficaMatematicasH);
        generarGrafica("punt_ingles", 'M');
        mostrarGrafica(graficaInglesH);
        generarGrafica("punt_ciencias", 'M');
        mostrarGrafica(graficaCienciasH);
        generarGrafica("punt_lectura", 'M');
        mostrarGrafica(graficaLecturaH);
        generarGrafica("punt_sociales", 'M');
        mostrarGrafica(graficaSocialesH);
        actualizarNombres();
    }

    /*
     Genera gráficas según los resultados del analisis
     */
    public void generarGrafica(String nucleo, char genero) {
        serieEstudiantes = new PieChartDataSet();
        data = new ChartData();
        List<Number> cantRegis;
        try {
            generarAnalisis(genero, nucleo);
        } catch (Exception ex) {
            Logger.getLogger(PredictivoSBSuperior.class.getName()).log(Level.SEVERE, null, ex);
        }
        cantRegis = new ArrayList<>();

        estiloGraficas(genero);

        List<String> labels = new ArrayList<>();
        for (PredictivoSuperior pre : predictivos) {
            if (Integer.parseInt(pre.getRegistro()) > 0) {
                labels.add("Respuesta: " + pre.getCategoria() + "-Puntaje: " + pre.getPuntaje());
                cantRegis.add(Integer.parseInt(pre.getRegistro()));
            }
        }
        serieEstudiantes.setData(cantRegis);
        data.addChartDataSet(serieEstudiantes);
        data.setLabels(labels);
        ocultar = false;
    }

    /*
     Guarda los colores y estilos de las gráficas
     */
    public void estiloGraficas(char genero) {
        List<String> colores = new ArrayList<>();
        if (genero == 'F') {
            colores.add("rgb(153, 153, 255)");
            colores.add("rgb(252, 182, 252)");
            colores.add("rgb(149, 72, 225)");
            colores.add("rgb(201, 145, 229)");
            colores.add("rgb(153, 204, 255)");
            colores.add("rgb(192, 192, 192)");
            colores.add("rgb(204, 153, 255)");
            colores.add("rgb(0, 76, 153)");
        } else {
            colores.add("rgb(49, 166, 166)");
            colores.add("rgb(0, 102, 204)");
            colores.add("rgb(0, 51, 102)");
            colores.add("rgb(0, 255, 255)");
            colores.add("rgb(204, 255, 255)");
            colores.add("rgb(204, 255, 204)");
            colores.add("rgb(153, 204, 255)");
            colores.add("rgb(0, 76, 153)");
        }
        serieEstudiantes.setBackgroundColor(colores);
    }

    /*
     Genera análisis según el algoritmo J48
     */
    public void generarAnalisis(char genero, String nucleo) throws Exception {
        String[] options = new String[4];
        options[0] = "-C";
        options[1] = "0.8";
        options[2] = "-M";
        options[3] = "2";
        J48 arbol = new J48();
        arbol.setOptions(options);
        predictivos = new ArrayList<>();
        categorias = new ArrayList<>();
        List<String> cate = new ArrayList<>();
        indices = new ArrayList<>();
        instances = REPOSITORIO.listarIndicador(indicador, prueba, nucleo, genero);
        instances.setClassIndex(instances.numAttributes() - 1);
        arbol.buildClassifier(instances);
        String separador = Pattern.quote(")");
        String[] nodos = arbol.toString().split(separador);
        for (int i = 0; i < nodos.length - 1; i++) {
            PredictivoSuperior pred = new PredictivoSuperior();
            String[] aux1 = nodos[i].split("= ");
            String[] aux2 = aux1[1].split(":");
            pred.setPuntaje(aux2[0]);
            String[] aux3 = nodos[i].split(": ");
            String separador1 = Pattern.quote(" (");
            String[] aux4 = aux3[1].split(separador1);
            pred.setCategoria(aux4[0].trim());
            cate.add(aux4[0].trim());
            String separador2 = Pattern.quote(".");
            String[] aux5 = aux4[1].split(separador2);
            pred.setRegistro(aux5[0]);
            predictivos.add(pred);
        }
        elegirCategorias(cate, nucleo, genero);
    }

    /*
     Determina las categorías más probables por indicador
     */
    public void elegirCategorias(List<String> cate, String nucleo, char genero) {
        for (int i = 0; i < cate.size(); i++) {
            int cont = 0;
            for (int j = i; j < cate.size(); j++) {
                if (cate.get(i).equals(cate.get(j))) {
                    cont++;
                }
            }
            if (cont == 1) {
                categorias.add(cate.get(i));
            }
        }
        compararMayor(cate, nucleo, genero);
    }

    /*
     Compara y determina las mayores probabilidades
     */
    public void compararMayor(List<String> cate, String nucleo, char genero) {
        for (String categoria : categorias) {
            int may = 0, ind = 0;
            for (int j = 0; j < cate.size(); j++) {
                if (categoria.equals(cate.get(j))) {
                    if (Integer.parseInt(predictivos.get(j).getRegistro()) > may) {
                        may = Integer.parseInt(predictivos.get(j).getRegistro());
                        ind = j;
                    }
                }
            }
            indices.add(ind);
        }
        eliminar(nucleo, genero);
    }

    /*
     Elimina registros menos probables
     */
    public void eliminar(String nucleo, char genero) {
        List<Integer> aux = new ArrayList<>();
        for (int i = 0; i < predictivos.size(); i++) {
            aux.add(i);
        }
        aux.removeAll(indices);
        for (int j = 0; j < aux.size(); j++) {
            for (int i = 0; i < predictivos.size(); i++) {
                if (i == aux.get(j)) {
                    predictivos.set(i, null);
                }
            }
        }
        predictivos.removeAll(Collections.singleton(null));
        int cantR = 1;
        for (PredictivoSuperior pred : predictivos) {
            cantR = cantR + Integer.parseInt(pred.getRegistro());
        }
        for (PredictivoSuperior pred : predictivos) {
            pred.setRegistro(String.valueOf(Integer.parseInt(pred.getRegistro()) * 100 / cantR));
        }
    }

    /*
     Actualiza los nombres de los núcleos de las pruebas y sus respectivos indicadores
     */
    public void actualizarNombres() {
        switch (prueba) {
            case "sb11_muestra":
                nomPrueba = "SABER 11";
                nucleoC = "Ciencias naturales";
                nucleoR = "Matemáticas";
                nucleoS = "Ciencias sociales";
                break;
            case "sabertyt_muestra":
                nomPrueba = "SABER TyT";
                nucleoC = "Comunicación escrita";
                nucleoR = "Razonamiento cuantitativo";
                nucleoS = "Competencias ciudadanas";
                break;
            default:
                nomPrueba = "SABER PRO";
                nucleoC = "Comunicación escrita";
                nucleoR = "Razonamiento cuantitativo";
                nucleoS = "Competencias ciudadanas";
                break;
        }
        switch (indicador) {
            case "fami_educacionmadre":
                nomInd = "Educación madre";
                definicion = "Educación madre: Este indicador muestra con qué niveles educativos cuentan las madres de los estudiantes, estando clasificados en los siguientes atributos: ninguno, no sabe, no aplica, primaria incompleta, primaria completa, secundaria incompleta, secundaria completa, técnica y tecnológica incompleta, técnica y tecnológica completa, educación profesional incompleta, educación profesional completa y postgrado. ";
                break;
            case "fami_educacionpadre":
                nomInd = "Educación padre";
                definicion = "Educación padre: Este indicador muestra con qué niveles educativos cuentan los padres de los estudiantes, estando clasificados en los siguientes atributos: ninguno, no sabe, no aplica, primaria incompleta, primaria completa, secundaria incompleta, secundaria completa, técnica y tecnológica incompleta, técnica y tecnológica completa, educación profesional incompleta, educación profesional completa y postgrado. ";
                break;
            case "fami_estratovivienda":
                nomInd = "Estrato de vivienda";
                definicion = "Estrato de vivienda: Indica la clasificación socioeconómica jerarquizada del país, sus categorías comprenden desde el estrato 1 hasta el estrato 6.";
                break;
            case "fami_tienecomputador":
                nomInd = "Tiene computador";
                definicion = "Tiene computador: Este indicador nos señala qué estudiantes cuentan con computador en sus hogares y quiénes no.";
                break;
            case "cole_jornada":
                nomInd = "Jornada del colegio";
                definicion = "Jornada del colegio: Este indicador refleja dentro de sus categorías los diferentes horarios de asistencia a clase o jornadas que tienen los colegios en el país.";
                break;
            default:
                nomInd = "Valor matrícula universidad";
                definicion = "Valor matricula universidad: Este indicador representa el rango de valores pagados por los estudiantes en la matricula del programa que están cursando en la universidad cada semestre. Las diferentes categorías comprenden un rango entre menos de 500 mil y 7 millones de pesos colombianos, o si no realizó el pago.";
                break;
        }
    }

    /*
     Permite mostrar el contenido de las gráficas al usuario
     */
    public void mostrarGrafica(PieChartModel grafica) {
        grafica.setData(data);
    }

    public String getPrueba() {
        return prueba;
    }

    public void setPrueba(String prueba) {
        this.prueba = prueba;
    }

    public PieChartModel getGraficaLecturaM() {
        return graficaLecturaM;
    }

    public void setGraficaLecturaM(PieChartModel graficaLecturaM) {
        this.graficaLecturaM = graficaLecturaM;
    }

    public PieChartModel getGraficaInglesM() {
        return graficaInglesM;
    }

    public void setGraficaInglesM(PieChartModel graficaInglesM) {
        this.graficaInglesM = graficaInglesM;
    }

    public PieChartModel getGraficaCienciasM() {
        return graficaCienciasM;
    }

    public void setGraficaCienciasM(PieChartModel graficaCienciasM) {
        this.graficaCienciasM = graficaCienciasM;
    }

    public PieChartModel getGraficaMatematicasM() {
        return graficaMatematicasM;
    }

    public void setGraficaMatematicasM(PieChartModel graficaMatematicasM) {
        this.graficaMatematicasM = graficaMatematicasM;
    }

    public PieChartModel getGraficaSocialesM() {
        return graficaSocialesM;
    }

    public void setGraficaSocialesM(PieChartModel graficaSocialesM) {
        this.graficaSocialesM = graficaSocialesM;
    }

    public PieChartModel getGraficaInglesH() {
        return graficaInglesH;
    }

    public void setGraficaInglesH(PieChartModel graficaInglesH) {
        this.graficaInglesH = graficaInglesH;
    }

    public PieChartModel getGraficaLecturaH() {
        return graficaLecturaH;
    }

    public void setGraficaLecturaH(PieChartModel graficaLecturaH) {
        this.graficaLecturaH = graficaLecturaH;
    }

    public PieChartModel getGraficaCienciasH() {
        return graficaCienciasH;
    }

    public void setGraficaCienciasH(PieChartModel graficaCienciasH) {
        this.graficaCienciasH = graficaCienciasH;
    }

    public PieChartModel getGraficaMatematicasH() {
        return graficaMatematicasH;
    }

    public void setGraficaMatematicasH(PieChartModel graficaMatematicasH) {
        this.graficaMatematicasH = graficaMatematicasH;
    }

    public PieChartModel getGraficaSocialesH() {
        return graficaSocialesH;
    }

    public void setGraficaSocialesH(PieChartModel graficaSocialesH) {
        this.graficaSocialesH = graficaSocialesH;
    }

    public boolean isOcultar11() {
        return ocultar11;
    }

    public void setOcultar11(boolean ocultar11) {
        this.ocultar11 = ocultar11;
    }

    public boolean isOcultarTyT() {
        return ocultarTyT;
    }

    public void setOcultarTyT(boolean ocultarTyT) {
        this.ocultarTyT = ocultarTyT;
    }

    public boolean isOcultarPro() {
        return ocultarPro;
    }

    public void setOcultarPro(boolean ocultarPro) {
        this.ocultarPro = ocultarPro;
    }

    public boolean isOcultar() {
        return ocultar;
    }

    public void setOcultar(boolean ocultar) {
        this.ocultar = ocultar;
    }

    public String getIndicador() {
        return indicador;
    }

    public void setIndicador(String indicador) {
        this.indicador = indicador;
    }

    public String getNomPrueba() {
        return nomPrueba;
    }

    public void setNomPrueba(String nomPrueba) {
        this.nomPrueba = nomPrueba;
    }

    public String getNomInd() {
        return nomInd;
    }

    public void setNomInd(String nomInd) {
        this.nomInd = nomInd;
    }

    public String getNucleoC() {
        return nucleoC;
    }

    public void setNucleoC(String nucleoC) {
        this.nucleoC = nucleoC;
    }

    public String getNucleoR() {
        return nucleoR;
    }

    public void setNucleoR(String nucleoR) {
        this.nucleoR = nucleoR;
    }

    public String getNucleoS() {
        return nucleoS;
    }

    public void setNucleoS(String nucleoS) {
        this.nucleoS = nucleoS;
    }

    public String getDefinicion() {
        return definicion;
    }

    public void setDefinicion(String definicion) {
        this.definicion = definicion;
    }
    
}