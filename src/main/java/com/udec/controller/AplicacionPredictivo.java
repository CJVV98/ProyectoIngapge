package com.udec.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.udec.pojo.Aplicacion;
import com.udec.repositorio.PredictivoSuperiorBD;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;


/**
 *
 * @author Angie Paola Manrique
 * @author Alisson Catalina Celeita
 */
@ManagedBean(name = "aplicacionPredictivo")
@ViewScoped
public class AplicacionPredictivo implements Serializable {

    private transient List<Aplicacion> resultado1, resultado2, resultado3, resultado4, resultado5, respuesta;
    private transient List<String> categoriasMadre, categoriasPadre, categoriasTres, categoriasCuatro, categoriasCinco;
    private String cate1, cate2, cate3, cate4, cate5, prueba, label4, genero, ingles, matematicas, ciencias, sociales, lectura;
    private boolean ocultar = false, ocultarLabel = true, ocultarLabel4 = true;
    float cont1 = 0, cont2 = 0, cont3 = 0, cont4 = 0, cont5 = 0;
    float por1 = 0, por2 = 0, por3 = 0, por4 = 0, por5 = 0;
    DecimalFormat df, df1;
    private static final PredictivoSuperiorBD REPOSITORIO = new PredictivoSuperiorBD();

    public AplicacionPredictivo() {
        categoriasCinco = new ArrayList();
        categoriasCuatro = new ArrayList();
        categoriasMadre = new ArrayList();
        categoriasPadre = new ArrayList();
        categoriasTres = new ArrayList();
    }

    @PostConstruct
    public void init() {
        prueba = "sb11_muestra";
        genero = "F";
        cargarCategorias();
        df = new DecimalFormat("#.##");
        df1 = new DecimalFormat();
        df1.setMaximumFractionDigits(0);
    }

     /*
        Carga el género seleccionado por el usuario
    */
    public void cargarGenero(ValueChangeEvent e) {
        genero = e.getNewValue().toString();
        cargarCategorias();
    }

    /*
        Carga la prueba seleccionada por el usuario
    */
    public void cargarPrueba(ValueChangeEvent event) {
        prueba = event.getNewValue().toString();
        cargarCategorias();
    }

    /*
        Carga las categorias según la prueba seleccionada
    */
    public void cargarCategorias() {
        categoriasMadre = REPOSITORIO.consultarCategoria(prueba, "fami_educacionmadre", genero);
        categoriasPadre = REPOSITORIO.consultarCategoria(prueba, "fami_educacionpadre", genero);
        categoriasTres = REPOSITORIO.consultarCategoria(prueba, "fami_estratovivienda", genero);
        switch (prueba) {
            case "sb11_muestra":
                categoriasCuatro = REPOSITORIO.consultarCategoria(prueba, "fami_tienecomputador", genero);
                categoriasCinco = REPOSITORIO.consultarCategoria(prueba, "cole_jornada", genero);
                label4 = "Tiene computador: ";
                ocultarLabel = true;
                ocultarLabel4 = true;
                break;
            case "saberpro_muestra":
                categoriasCuatro = REPOSITORIO.consultarCategoria(prueba, "estu_valormatriculauniversidad", genero);
                label4 = "Valor de matrícula: ";
                ocultarLabel4 = true;
                ocultarLabel = false;
                break;
            default:
                ocultarLabel4 = false;
                ocultarLabel = false;
                break;
        }
    }

    /*
        Genera las probabilidades de respuesta según los porcentajes hallados en el analisis
    */
    public void generarPrediccion() {
        ocultar = true;
        resultado1 = new ArrayList();
        resultado2 = new ArrayList();
        resultado3 = new ArrayList();
        resultado4 = new ArrayList();
        resultado5 = new ArrayList();
        respuesta = new ArrayList();
        resultado1.addAll(REPOSITORIO.consultarPredictivo(cate1, prueba, genero, "fami_educacionmadre"));
        resultado2.addAll(REPOSITORIO.consultarPredictivo(cate2, prueba, genero, "fami_educacionpadre"));
        resultado3.addAll(REPOSITORIO.consultarPredictivo(cate3, prueba, genero, "fami_estratovivienda"));
        switch (prueba) {
            case "sb11_muestra":
                resultado4.addAll(REPOSITORIO.consultarPredictivo(cate4, prueba, genero, "fami_tienecomputador"));
                resultado5.addAll(REPOSITORIO.consultarPredictivo(cate5, prueba, genero, "cole_jornada"));
                calcularProbabilidad11();
                break;
            case "saberpro_muestra":
                resultado4.addAll(REPOSITORIO.consultarPredictivo(cate4, prueba, genero, "estu_valormatriculauniversidad"));
                calcularProbabilidadPro();
                break;
            default:
                calcularProbabilidadTyT();
                break;
        }
    }
    
    /*
        carga respuesta que se va a mostrar para las pruebas saber 11
    */
    public void calcularProbabilidad11() {
        limpiar();
        llenar11(resultado1);
        llenar11(resultado2);
        llenar11(resultado3);
        llenar11(resultado4);
        llenar11(resultado5);
        cargarRespuesta(5, "Matemáticas", "Ciencias naturales", "Ciencias sociales");
    }

    /*
        Calcula porcentajes de respuesta saber 11
    */
    public void llenar11(List<Aplicacion> resultado) {
        for (Aplicacion apli : resultado) {
            switch (apli.getNucleo()) {
                case "Ingles":
                    cont1 = cont1 + apli.getPuntaje();
                    por1 = por1 + apli.getRegistro();
                    break;
                case "Matematicas":
                    cont2 = cont2 + apli.getPuntaje();
                    por2 = por2 + apli.getRegistro();
                    break;
                case "Lectura critica":
                    cont3 = cont3 + apli.getPuntaje();
                    por3 = por3 + apli.getRegistro();
                    break;
                case "Ciencias naturales":
                    cont4 = cont4 + apli.getPuntaje();
                    por4 = por4 + apli.getRegistro();
                    break;
                default:
                    cont5 = cont5 + apli.getPuntaje();
                    por5 = por5 + apli.getRegistro();
                    break;
            }
        }
    }

    public void limpiar() {
        cont1 = 0;
        por1 = 0;
        cont2 = 0;
        por2 = 0;
        cont3 = 0;
        por3 = 0;
        cont4 = 0;
        por4 = 0;
        cont5 = 0;
        por5 = 0;
    }

     /*
        carga respuesta que se va a mostrar para las pruebas saber TyT
    */
    public void calcularProbabilidadTyT() {
        limpiar();
        llenarTyTyPro(resultado1);
        llenarTyTyPro(resultado2);
        llenarTyTyPro(resultado3);
        cargarRespuesta(3, "Razonamiento cuantitativo", "Comunicación escrita", "Competencias ciudadanas");
    }

    /*
        Calcula porcentajes de respuesta saber TyT y Pro
    */
    public void llenarTyTyPro(List<Aplicacion> resultado) {
        for (Aplicacion apli : resultado) {
            switch (apli.getNucleo()) {
                case "Ingles":
                    cont1 = cont1 + apli.getPuntaje();
                    por1 = por1 + apli.getRegistro();
                    break;
                case "Razonamiento Cuantitativo":
                    cont2 = cont2 + apli.getPuntaje();
                    por2 = por2 + apli.getRegistro();
                    break;
                case "Lectura critica":
                    cont3 = cont3 + apli.getPuntaje();
                    por3 = por3 + apli.getRegistro();
                    break;
                case "Comunicacion escrita":
                    cont4 = cont4 + apli.getPuntaje();
                    por4 = por4 + apli.getRegistro();
                    break;
                default:
                    cont5 = cont5 + apli.getPuntaje();
                    por5 = por5 + apli.getRegistro();
                    break;
            }
        }
    }

    /*
        Llena la información a mostrar como respuesta
    */
    public void cargarRespuesta(int divisor, String nucleo2, String nucleo4, String nucleo5) {
        cont1 = cont1 / divisor;
        por1 = por1 / divisor;
        cont2 = cont2 / divisor;
        por2 = por2 / divisor;
        cont3 = cont3 / divisor;
        por3 = por3 / divisor;
        cont4 = cont4 / divisor;
        por4 = por4 / divisor;
        cont5 = cont5 / divisor;
        por5 = por5 / divisor;
        respuesta.add(new Aplicacion("Inglés", Integer.parseInt(df1.format(cont1)), Float.parseFloat(df.format(por1).replace(',', '.'))));
        respuesta.add(new Aplicacion(nucleo2, Integer.parseInt(df1.format(cont2)), Float.parseFloat(df.format(por2).replace(',', '.'))));
        respuesta.add(new Aplicacion("Lectura critica", Integer.parseInt(df1.format(cont3)), Float.parseFloat(df.format(por3).replace(',', '.'))));
        respuesta.add(new Aplicacion(nucleo4, Integer.parseInt(df1.format(cont4)), Float.parseFloat(df.format(por4).replace(',', '.'))));
        respuesta.add(new Aplicacion(nucleo5, Integer.parseInt(df1.format(cont5)), Float.parseFloat(df.format(por5).replace(',', '.'))));
    }

     /*
        carga respuesta que se va a mostrar para las pruebas saber Pro
    */
    public void calcularProbabilidadPro() {
        limpiar();
        llenarTyTyPro(resultado1);
        llenarTyTyPro(resultado2);
        llenarTyTyPro(resultado3);
        llenarTyTyPro(resultado4);
        cargarRespuesta(4, "Razonamiento cuantitativo", "Comunicación escrita", "Competencias ciudadanas");
    }

    public List<Aplicacion> getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(List<Aplicacion> respuesta) {
        this.respuesta = respuesta;
    }

    public String getIngles() {
        return ingles;
    }

    public void setIngles(String ingles) {
        this.ingles = ingles;
    }

    public String getMatematicas() {
        return matematicas;
    }

    public void setMatematicas(String matematicas) {
        this.matematicas = matematicas;
    }

    public String getCiencias() {
        return ciencias;
    }

    public void setCiencias(String ciencias) {
        this.ciencias = ciencias;
    }

    public String getSociales() {
        return sociales;
    }

    public void setSociales(String sociales) {
        this.sociales = sociales;
    }

    public String getLectura() {
        return lectura;
    }

    public void setLectura(String lectura) {
        this.lectura = lectura;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public boolean isOcultarLabel4() {
        return ocultarLabel4;
    }

    public void setOcultarLabel4(boolean ocultarLabel4) {
        this.ocultarLabel4 = ocultarLabel4;
    }

    public boolean isOcultarLabel() {
        return ocultarLabel;
    }

    public void setOcultarLabel(boolean ocultarLabel) {
        this.ocultarLabel = ocultarLabel;
    }

    public String getLabel4() {
        return label4;
    }

    public void setLabel4(String label4) {
        this.label4 = label4;
    }

    public String getCate1() {
        return cate1;
    }

    public void setCate1(String cate1) {
        this.cate1 = cate1;
    }

    public String getCate2() {
        return cate2;
    }

    public void setCate2(String cate2) {
        this.cate2 = cate2;
    }

    public String getCate3() {
        return cate3;
    }

    public void setCate3(String cate3) {
        this.cate3 = cate3;
    }

    public String getCate4() {
        return cate4;
    }

    public void setCate4(String cate4) {
        this.cate4 = cate4;
    }

    public String getCate5() {
        return cate5;
    }

    public void setCate5(String cate5) {
        this.cate5 = cate5;
    }

    public List<String> getCategoriasMadre() {
        return categoriasMadre;
    }

    public void setCategoriasMadre(List<String> categoriasMadre) {
        this.categoriasMadre = categoriasMadre;
    }

    public List<String> getCategoriasPadre() {
        return categoriasPadre;
    }

    public void setCategoriasPadre(List<String> categoriasPadre) {
        this.categoriasPadre = categoriasPadre;
    }

    public List<String> getCategoriasTres() {
        return categoriasTres;
    }

    public void setCategoriasTres(List<String> categoriasTres) {
        this.categoriasTres = categoriasTres;
    }

    public List<String> getCategoriasCuatro() {
        return categoriasCuatro;
    }

    public void setCategoriasCuatro(List<String> categoriasCuatro) {
        this.categoriasCuatro = categoriasCuatro;
    }

    public List<String> getCategoriasCinco() {
        return categoriasCinco;
    }

    public void setCategoriasCinco(List<String> categoriasCinco) {
        this.categoriasCinco = categoriasCinco;
    }

    public List<Aplicacion> getResultado1() {
        return resultado1;
    }

    public void setResultado1(List<Aplicacion> resultado1) {
        this.resultado1 = resultado1;
    }

    public List<Aplicacion> getResultado2() {
        return resultado2;
    }

    public void setResultado2(List<Aplicacion> resultado2) {
        this.resultado2 = resultado2;
    }

    public List<Aplicacion> getResultado3() {
        return resultado3;
    }

    public void setResultado3(List<Aplicacion> resultado3) {
        this.resultado3 = resultado3;
    }

    public List<Aplicacion> getResultado4() {
        return resultado4;
    }

    public void setResultado4(List<Aplicacion> resultado4) {
        this.resultado4 = resultado4;
    }

    public List<Aplicacion> getResultado5() {
        return resultado5;
    }

    public void setResultado5(List<Aplicacion> resultado5) {
        this.resultado5 = resultado5;
    }

    public String getPrueba() {
        return prueba;
    }

    public void setPrueba(String prueba) {
        this.prueba = prueba;
    }

    public boolean isOcultar() {
        return ocultar;
    }

    public void setOcultar(boolean ocultar) {
        this.ocultar = ocultar;
    }

}