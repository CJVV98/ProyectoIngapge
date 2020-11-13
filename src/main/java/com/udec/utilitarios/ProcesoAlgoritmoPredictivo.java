/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.utilitarios;

import com.udec.controller.AnalisisPredictivoBasico;
import com.udec.pojo.DatosPrediccion;
import com.udec.repositorio.RepositorioPrueba;
import com.udec.repositorio.RepositorioResultados;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.Utils;

/**
 * Clase encargada de realizar el analisis predictivo
 * @author Corin Viracacha
 */
public class ProcesoAlgoritmoPredictivo {

    private List<String> arbolOrganizado;
    private List<DatosPrediccion> menores;
    private List<DatosPrediccion> mayores;
    private String[] indicadores = {"fami_educacionpadre", "fami_educacionmadre", "fami_tieneinternet", "fami_tieneconsolavideojuegos", "fami_tienecomputador"};
    private double[] porClasCorrecta = new double[2];
    private String tipo;
    private String prueba;
    /**
     * Metodo encargado de validar si existen registros del análisis
     * @param tipo tipo de prueba(lenguaje o matematicas)
     * @param prueba nombre de la prueba
     */
    public void iniciarAnalisis(String tipo, String prueba) {
        RepositorioResultados repository = new RepositorioResultados();
        menores = new ArrayList<>();
        mayores = new ArrayList<>();
        this.tipo = tipo;
        this.prueba = prueba;
        for (int i = 0; i < indicadores.length; i++) {
            menores = new ArrayList<>();
            mayores = new ArrayList<>();
            if (!validarConsultaResultados(indicadores[i], tipo, prueba, repository)) {
                if (i < 2) {
                    iniciarAnalisisAlgoritmo(indicadores[i], repository, indicadores[i], "No aplica");
                } else {
                    iniciarAnalisisAlgoritmo(indicadores[i], repository, indicadores[i], "NA");
                }
            }
        }
    }
    /**
     * Metodo encargado de cargar los datos para el análisis
     * @param indicador nombre del indicador
     * @param repository repositorio de base de datos
     * @param indicadorAnalisis segundo indicador
     * @param validacion validacion del indicador
     */
    public void iniciarAnalisisAlgoritmo(String indicador, RepositorioResultados repository, String indicadorAnalisis, String validacion) {
        try {
            String query = "SELECT estu_genero, " + tipo + " , " + indicador + " FROM " + prueba + ".resultados2017"
                    + " WHERE estu_genero<>'NA' AND " + indicador + " <> '" + validacion + "' ;";
            Instances instancias = new RepositorioPrueba().consultarIndicador(query);
            String[] options2 = Utils.splitOptions("-C 0.25 -M 2");
            J48 myTree = new J48();
            myTree.setOptions(options2);
            instancias.setClassIndex(0);
            myTree.buildClassifier(instancias);
            String arbol = myTree.toString();
            arbolOrganizado = new ArrayList<String>(Arrays.asList(arbol.split("\n")));
            Evaluation evaluacion = new Evaluation(instancias);
            evaluacion.evaluateModel(myTree, instancias);
            String matrizC = evaluacion.toMatrixString();
            List<String> matrizOrganizada = new ArrayList<String>(Arrays.asList(matrizC.split("\n")));
            analizarMatrizConfusion(matrizOrganizada);
            eliminarFilas();
            analizarPuntajes(arbolOrganizado, repository, indicadorAnalisis);
        } catch (Exception ex) {
            Logger.getLogger(AnalisisPredictivoBasico.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    /**
     * Eliminar filas innecesarias del arbol
     */
    private void eliminarFilas() {
        for (int i = 0; i < 4; i++) {
            if (i < 3) {
                arbolOrganizado.remove(0);
            }
            arbolOrganizado.remove(arbolOrganizado.size() - 1);
        }
    }
    /**
     * Analizar el arbol hallado
     * @param arbolOrganizado listado del arbol organizado
     * @param repository hace referencia a la clase repositorio de la base de datos
     * @param indicadorAnalisis se refiere al indicador analizado
     */
    private void analizarPuntajes(List<String> arbolOrganizado, RepositorioResultados repository, String indicadorAnalisis) {
        String indicador = "No afecta";
        int validarIndicador = 0;
        int validarNodo = 0;
        int puntaje = 0;
        double puntajeClasificacion;
        for (int i = 0; i < arbolOrganizado.size(); i++) {
            int[] puntajesObt = new int[2];
            validarNodo = contarPosicionNodo(arbolOrganizado.get(i));
            indicador = validarListado(arbolOrganizado.get(i), "|", "No afecta", indicador);
            if (arbolOrganizado.get(i).contains("<") || arbolOrganizado.get(i).contains(">")) {
                indicador = validarPosicionNodo(indicador, validarNodo, validarIndicador);
                if (arbolOrganizado.get(i).contains(":")) {
                    cargarNodo(arbolOrganizado.get(i), indicador, indicador + i, indicadorAnalisis, repository);
                } else {
                    puntaje = Integer.parseInt(arbolOrganizado.get(i).replaceAll("[^0-9]", ""));
                    validarMenorMayor(arbolOrganizado.get(i), puntaje, indicador + i);
                    validarFinRama(arbolOrganizado.get(i).contains("<"));
                }
            } else {
                validarIndicador = contarPosicionNodo(arbolOrganizado.get(i));
                if (arbolOrganizado.get(i).contains(":")) {
                    String[] respuesta = arbolOrganizado.get(i).split("=");
                    String[] respuestaPuntaje = respuesta[1].split(":");
                    String[] genero = (respuestaPuntaje[1].split("\\("));
                    validarListado(indicador, "No afecta", indicador, "");
                    indicador = respuestaPuntaje[0];
                    puntajeClasificacion = calcularPuntajeClasificacion(respuestaPuntaje, genero[0]);
                    puntajesObt = calcularResultadosIndicador();
                    DatosPrediccion datos = new DatosPrediccion(genero[0], indicadorAnalisis, indicador, puntajesObt[0], puntajesObt[1], (double) Math.round(puntajeClasificacion * 100d) / 100d, prueba, tipo);
                    repository.cargarResultadosPredictivo(datos, 2017);
                } else {
                    String[] respuesta = arbolOrganizado.get(i).split("=");
                    validarListado(indicador, "No afecta", indicador, "");
                    indicador = respuesta[1];
                }
            }
        }
    }
    /**
     * Metodo usado para determinar el porcentaje de clasificación
     * @param respuestaPuntaje respuesta del puntaje en la prueba
     * @param genero genero del estudiante
     * @return puntaje en la prueba
     */
    private double calcularPuntajeClasificacion(String[] respuestaPuntaje, String genero) {
        double clasificacionCorrecta = 0;
        if (genero.contains("Hom")) {
            clasificacionCorrecta = porClasCorrecta[0];
        } else {
            clasificacionCorrecta = porClasCorrecta[1];
        }
        if (respuestaPuntaje[1].contains("/")) {
            String[] puntaje = respuestaPuntaje[1].split("/");
            puntaje[0] = puntaje[0].replaceAll("[^0-9.]+", "");
            puntaje[1] = puntaje[1].replaceAll("[^0-9.]+", "");
            return (Double.parseDouble(puntaje[0]) - Double.parseDouble(puntaje[1])) / clasificacionCorrecta;
        } else {
            return (Double.parseDouble(respuestaPuntaje[1].replaceAll("[^0-9.]", ""))) / clasificacionCorrecta;
        }

    }
    /**
     * Metodo usado para calcular los resultados del arbol
     * @param estado estado del analisis
     * @return puntajes
     */
    private int[] calcularResultados(boolean estado) {
        int[] puntajes = new int[2];
        if (estado) {
            int tamanoMayores = mayores.size();
            puntajes[1] = menores.get(menores.size() - 1).getPuntaje();
            if (tamanoMayores == 0) {
                puntajes[0] = 0;
            } else {
                puntajes[0] = mayores.get(mayores.size() - 1).getPuntaje();
                if (puntajes[0] == puntajes[1]) {
                    mayores.remove(mayores.size() - 1);
                    puntajes = obtenerPuntajeMayor(puntajes);
                }
            }
            menores.remove(menores.size() - 1);
        } else {
            int tamanoMenores = menores.size();
            puntajes[0] = mayores.get(mayores.size() - 1).getPuntaje();
            if (tamanoMenores == 0) {
                puntajes[1] = 1000;
            } else {
                puntajes[1] = menores.get(menores.size() - 1).getPuntaje();
                if (puntajes[0] == puntajes[1]) {
                    menores.remove(menores.size() - 1);
                    puntajes = obtenerPuntajeMenor(puntajes);
                }
            }
            mayores.remove(mayores.size() - 1);
        }
        return analizarPuntajes(puntajes);
    }
    /**
     * Determina el puntaje menor
     * @param puntajes vector de puntajes hallados
     * @return vector con las modificaciones
     */
    private int[] obtenerPuntajeMenor(int[] puntajes) {
        if (menores.size() > 0) {
            puntajes[1] = menores.get(menores.size() - 1).getPuntaje();
        } else {
            puntajes[1] = 1000;
        }
        return puntajes;
    }
    /**
     * Determinar el puntaje mayor
     * @param puntajes listado de puntajes
     * @return listado de puntajes con el mayor
     */
    private int[] obtenerPuntajeMayor(int[] puntajes) {
        if (mayores.size() > 0) {
            puntajes[0] = mayores.get(mayores.size() - 1).getPuntaje();
        } else {
            puntajes[0] = 0;
        }
        return puntajes;
    }
    /**
     * Validar datos del nodo 
     * @param nodo, nodo del arbol
     * @param validacion, validacion 
     * @param indicador, indicador a analizar
     * @param datoIndicador, respuesta del indicador
     * @return Devolver la respuesta del indicador
     */
    private String validarListado(String nodo, String validacion, String indicador, String datoIndicador) {
        if (!nodo.contains(validacion)) {
            borrarDatos(indicador);
            if (validacion.contains("|")) {
                borrarDatos(datoIndicador);
                return "No afecta";
            }
        }
        return datoIndicador;
    }
    /**
     * Metodo que organiza que metodos son usados para el calculo de resultados
     * @return vector con puntajes
     */
    private int[] calcularResultadosIndicador() {
        int[] puntajes = new int[2];
        puntajes = obtenerPuntajeMenor(puntajes);
        puntajes = obtenerPuntajeMayor(puntajes);
        return analizarPuntajes(puntajes);
    }
    /**
     * Validar si es un resultado menor o mayor
     * @param nodo información del nodo
     * @param puntaje puntaje del nodo
     * @param indicador indicador analizado
     */
    private void validarMenorMayor(String nodo, int puntaje, String indicador) {
        if (nodo.contains("<")) {
            menores.add(new DatosPrediccion(puntaje, indicador));
        } else {
            mayores.add(new DatosPrediccion(puntaje, indicador));
        }
    }
    /**
     * Metodo encargado de construir nodo
     * @param nodo,Información del nodo
     * @param indicadorAnalizado, indicador del primer indicador analizado
     * @param indicador, analisis del segundo indicador
     * @param descripcionIndicador, descripción del indicador 
     * @param repository, repositorio de la base de datos
     */
    private void cargarNodo(String nodo, String indicadorAnalizado, String indicador, String descripcionIndicador, RepositorioResultados repository) {
        int puntaje = 0;
        double puntajeClasificacion;
        int[] puntajesObt = new int[2];
        String[] respuestaPuntaje = nodo.split(":");
        String[] genero = (respuestaPuntaje[1].split("\\("));
        puntajeClasificacion = calcularPuntajeClasificacion(respuestaPuntaje, genero[0]);
        puntaje = Integer.parseInt(respuestaPuntaje[0].replaceAll("[^0-9]", ""));
        validarMenorMayor(nodo, puntaje, indicador);
        puntajesObt = calcularResultados(nodo.contains("<"));
        DatosPrediccion datos = new DatosPrediccion(genero[0], descripcionIndicador, indicadorAnalizado, puntajesObt[0], puntajesObt[1], (double) Math.round(puntajeClasificacion * 100d) / 100d, prueba, tipo);
        repository.cargarResultadosPredictivo(datos, 2017);
    }

    /**
     * Validar consulta de resultados
     * @param indicador nombre del indicador
     * @param tipo tipo de nucleo tematico
     * @param prueba, nombre de la prueba 
     * @param repository, repositorio de base de datos
     * @return validacion de existencia
     */
    private boolean validarConsultaResultados(String indicador, String tipo, String prueba, RepositorioResultados repository) {
        return (repository.consultarCantidadResPredictivo(indicador, tipo, prueba) > 0);    
    }
    
    /**
     * Analizar puntajes 
     * @param puntajes vector de puntajes 
     * @return vector de puntajes modificado
     */
    private int[] analizarPuntajes(int[] puntajes) {
        int puntaje1 = puntajes[0];
        int puntaje2 = puntajes[1];
        if ((puntaje1 > puntaje2)) {
            puntajes[0] = puntaje1;
            puntajes[1] = puntaje2;
        }else{
            puntajes[1] = puntaje1;
            puntajes[0] = puntaje2;
        }
        return puntajes;
    }
    /**
     * Determinar la matriz de confusión 
     * @param matrizOrganizada listado de la matriz de confusión 
     */
    private void analizarMatrizConfusion(List<String> matrizOrganizada) {
        for (int i = 0; i < 2; i++) {
            String datosMatriz = matrizOrganizada.get(i + 3).replace("|", "-");
            String[] datos = datosMatriz.split("-");
            String[] numeros = datos[0].trim().split("\\s+");
            if (i == 0) {
                porClasCorrecta[i] = Double.parseDouble(numeros[0]);
            } else {
                porClasCorrecta[i] = Double.parseDouble(numeros[1]);
            }
        }
    }
    /**
     * Borrar datos innecesarios de las listas de menores y mayores
     * @param indicador, nombre del indicador
     */
    private void borrarDatos(String indicador) {
        for (int i = 0; i < menores.size(); i++) {
            if (menores.get(i).getDatoIndicador().contains(indicador)) {
                menores.remove(i);
                i--;
            }
        }
        for (int i = 0; i < mayores.size(); i++) {
            if (mayores.get(i).getDatoIndicador().contains(indicador)) {
                mayores.remove(i);
                i--;
            }
        }
    }
    /**
     * Contar posicion del nodo dentro del arbol
     * @param nodo informacion del nodo
     * @return posicion del nodo
     */
    private int contarPosicionNodo(String nodo) {
        int contador = 0;
        for (int i = 0; i < nodo.length(); i++) {
            if (nodo.charAt(i) == '|') {
                contador++;
            }
        }
        return contador;
    }
    /**
     * Metodo encargado de validar la posición del nodo
     * @param indicador, datos del indicador
     * @param validarNodo, validacion de la posicion del nodo
     * @param validarIndicador, validacion de la prediccion respecto al nodo
     * @return datos del indicador
     */
    private String validarPosicionNodo(String indicador, int validarNodo, int validarIndicador) {
        if (!indicador.contains("No afecta") && validarNodo < validarIndicador) {      
                borrarDatos(indicador);
                return "No afecta";
        }
        return indicador;
    }
    /**
     * Validar cuando termina la rama
     * @param estado validacion de si es menor o mayor el puntaje
     */
    private void validarFinRama(boolean estado) {
        int ultimo = 0;
        if (estado) {
            if (menores.size() > 0) {
                ultimo = menores.get(menores.size() - 1).getPuntaje();
                if (mayores.size() > 0) {
                    for (int i = mayores.size() - 1; i >= 0; i--) {
                        if (ultimo == mayores.get(i).getPuntaje()) {
                            mayores.remove(i);
                            break;
                        }
                    }
                }
            }
        } else {
            if (mayores.size() > 0) {
                ultimo = mayores.get(mayores.size() - 1).getPuntaje();
                if (menores.size() > 0) {
                    for (int i = menores.size() - 1; i >= 0; i--) {
                        if (ultimo == menores.get(i).getPuntaje()) {
                            menores.remove(i);
                            break;
                        }
                    }
                }
            }
        }
    }

    public List<String> getArbolOrganizado() {
        return arbolOrganizado;
    }

    public void setArbolOrganizado(List<String> arbolOrganizado) {
        this.arbolOrganizado = arbolOrganizado;
    }

    public List<DatosPrediccion> getMenores() {
        return menores;
    }

    public void setMenores(List<DatosPrediccion> menores) {
        this.menores = menores;
    }

    public List<DatosPrediccion> getMayores() {
        return mayores;
    }

    public void setMayores(List<DatosPrediccion> mayores) {
        this.mayores = mayores;
    }

    public String[] getIndicadores() {
        return indicadores;
    }

    public void setIndicadores(String[] indicadores) {
        this.indicadores = indicadores;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPrueba() {
        return prueba;
    }

    public void setPrueba(String prueba) {
        this.prueba = prueba;
    }

    public double[] getPorClasCorrecta() {
        return porClasCorrecta;
    }

    public void setPorClasCorrecta(double[] porClasCorrecta) {
        this.porClasCorrecta = porClasCorrecta;
    }

}
