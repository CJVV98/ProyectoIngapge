/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and abrir the template in the editor.
 */
package com.udec.repositorio;

import com.udec.controller.Login;
import com.udec.pojo.DatosPrediccion;
import com.udec.pojo.ResultadosPrimaria;
import com.udec.pojo.Tabla;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase encargada de la gestion de base de datos
 *
 * @author Corin Viracacha
 */
public class RepositorioResultados implements Serializable {

    private ConexionBasico conexion = new ConexionBasico();

    /**
     * Metodo encargado de insertar resultados hallados en las pruebas
     *
     * @param primerIndicador primer indicador
     * @param segIndicador segundo indicador
     * @param variables respuesta hallada con sus variables
     * @param resultado porcentaje del resultado
     * @param prueba prueba
     * @param ano año
     */
    public void insertarResultados(String primerIndicador, String segIndicador, String variables, Double resultado, String prueba, int ano) {
        try {
            String query = "INSERT INTO resultados.resultados_pruebas (p_indicador, s_indicador,"
                    + " res_indicador,resultado,prueba,ano) VALUES (?, ?, ?, ?, ?, ?);";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);
            sentenciaP.setString(1, primerIndicador);
            sentenciaP.setString(2, segIndicador);
            sentenciaP.setString(3, variables);
            sentenciaP.setDouble(4, resultado);
            sentenciaP.setString(5, prueba);
            sentenciaP.setInt(6, ano);
            sentenciaP.executeUpdate();
            sentenciaP.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al insertar los resultados de las pruebas", ex);
        }
    }

    /**
     * Metodo para consultar los resultados
     *
     * @param primerIndicador primer indicador
     * @param segundoIndicador segundo indicador
     * @param prueba prueba analizada
     * @param ano año analizo
     * @return listado de resultados
     */
    public List<ResultadosPrimaria> obtenerResultados(String primerIndicador, String segundoIndicador, String prueba, int ano) {
        List<ResultadosPrimaria> resultados = new ArrayList<>();
        try {
            String query = "SELECT res_indicador, resultado FROM resultados.resultados_pruebas WHERE p_indicador = ? and s_indicador= ? and prueba = ? and ano= ?";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);
            sentenciaP.setString(1, primerIndicador);
            sentenciaP.setString(2, segundoIndicador);
            sentenciaP.setString(3, prueba);
            sentenciaP.setInt(4, ano);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                resultados.add(new ResultadosPrimaria(resultado.getString(1), resultado.getDouble(2)));
            }
            resultado.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al insertar los resultados de las pruebas", ex);

        }
        return resultados;
    }

    /**
     * Metodo usado para consultar resultados de mujeres por prueba y año
     *
     * @param ano año a consultar
     * @param prueba prueba a consultar
     * @return lisatdo de resultados
     */
    public List<ResultadosPrimaria> obtenerResultadosGlobales(int ano, String prueba) {
        List<ResultadosPrimaria> resultados = new ArrayList<>();
        try {
            String query = "SELECT s_indicador, resultado, res_indicador FROM resultados.resultados_pruebas WHERE res_indicador LIKE 'Mujer%' and  ano=? and prueba=? order by s_indicador";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);// esto es para preparar lo del mysql eso si lo copie del ejercicio anterior hast
            sentenciaP.setInt(1, ano);
            sentenciaP.setString(2, prueba);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                resultados.add(new ResultadosPrimaria(resultado.getString(1), resultado.getDouble(2), resultado.getString(3)));
            }
            resultado.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar los resultados de los indicadores de las pruebas saber", ex);
        }
        return resultados;
    }

    /**
     * Metodo usado para consultar la cantidad de registros por año
     *
     * @param prueba nombre del prueba
     * @return listado de pruebas por año
     */
    public List<Tabla> cargarAnosAnalizado(String prueba) {
        List<Tabla> anos = new ArrayList<>();
        try {
            String query = "SELECT ano, count(*) as cantidad FROM resultados.resultados_pruebas WHERE prueba=?  GROUP BY ano ORDER BY ano;";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);// esto es para preparar lo del mysql eso si lo copie del ejercicio anterior hast
            sentenciaP.setString(1, prueba);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                anos.add(new Tabla(resultado.getInt(1), resultado.getInt(2)));
            }
            resultado.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar los resultados de los indicadores de las pruebas saber", ex);
        }
        return anos;
    }

    /**
     * Metodo encargado de consultar resultador por prueba y año
     *
     * @param ano, año de consulta
     * @param prueba, prueba a consultar
     * @return listado de resultados
     */
    public List<ResultadosPrimaria> cargarIndicadoresAno(int ano, String prueba) {
        List<ResultadosPrimaria> resultados = new ArrayList<>();
        try {
            String query = "SELECT res_indicador, resultado, s_indicador  FROM resultados.resultados_pruebas WHERE prueba=? and ano=? ORDER BY s_indicador;";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);
            sentenciaP.setString(1, prueba);
            sentenciaP.setInt(2, ano);
            // esto es para preparar lo del mysql eso si lo copie del ejercicio anterior hast
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                resultados.add(new ResultadosPrimaria(resultado.getString(3), resultado.getDouble(2), resultado.getString(1)));
            }
            resultado.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar los resultados de los indicadores de las pruebas saber", ex);

        }
        return resultados;
    }

    /**
     * Metodo encargado de consultar la cantidad de registros por año según la
     * prueba
     *
     * @param prueba prueba a consultar
     * @return listado con la informacion de la prueba
     */
    public List<Tabla> cargarIndicadoresPorAnos(String prueba) {
        List<Tabla> anos = new ArrayList<>();
        try {
            String query = "SELECT ano, count(*) as cantidad FROM resultados.resultados_pruebas WHERE prueba=?  GROUP BY ano ORDER BY ano;";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);// esto es para preparar lo del mysql eso si lo copie del ejercicio anterior hast
            sentenciaP.setString(1, prueba);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                anos.add(new Tabla(resultado.getInt(1), resultado.getInt(2)));
            }
            resultado.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar los resultados de los indicadores de las pruebas saber", ex);

        }
        return anos;
    }

    /**
     * Metodo encargado de consultar el año y las respuestas del indicador
     *
     * @param prueba nombre de la prueba
     * @param indicador indicador a consultar
     * @return listado de resultados
     */
    public List<ResultadosPrimaria> cargarBrechaIndicador(String prueba, String indicador) {
        List<ResultadosPrimaria> resultados = new ArrayList<>();
        try {
            String query = "SELECT ano, res_indicador FROM resultados.resultados_pruebas WHERE prueba=?  and s_indicador=?  order by ano";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);// esto es para preparar lo del mysql eso si lo copie del ejercicio anterior hast
            sentenciaP.setString(1, prueba);
            sentenciaP.setString(2, indicador);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                resultados.add(new ResultadosPrimaria(resultado.getInt(1), resultado.getString(2)));
            }
            resultado.close();
            sentenciaP.close();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar los resultados de los indicadores de las pruebas saber", ex);
        }

        conexion.cerrar();
        return resultados;

    }

    /**
     * Metodo encargado de consultar año, respuesta y resultado por indicador y
     * prueba
     *
     * @param prueba prueba a consultar
     * @param indicador indicador a consultar
     * @return listado de resultados
     */
    public List<ResultadosPrimaria> cargarBrechaIndicadorSE(String prueba, String indicador) {
        List<ResultadosPrimaria> resultados = new ArrayList<>();
        try {
            String query = "SELECT ano, resultado,res_indicador FROM resultados.resultados_pruebas WHERE prueba=?  and s_indicador=?  order by ano";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);// esto es para preparar lo del mysql eso si lo copie del ejercicio anterior hast
            sentenciaP.setString(1, prueba);
            sentenciaP.setString(2, indicador);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                resultados.add(new ResultadosPrimaria(resultado.getDouble(2), resultado.getInt(1), resultado.getString(3)));
            }
            resultado.close();
            sentenciaP.close();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar los resultados de los indicadores de las pruebas saber", ex);

        }
        conexion.cerrar();
        return resultados;
    }

    /**
     * Metodo de consulta de prueba y respuesta segun el indicador y el año
     *
     * @param indicador, indicador a consultar
     * @param ano, año a consultar
     * @return listado de resultados
     */
    public List<ResultadosPrimaria> cargarBrechaIndicadorPuntaje(String indicador, int ano) {
        List<ResultadosPrimaria> resultados = new ArrayList<>();
        try {
            String query = "SELECT prueba, res_indicador FROM resultados.resultados_pruebas WHERE ano=?  and s_indicador=?  and   res_indicador LIKE 'Mujer%'  order by prueba";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);// esto es para preparar lo del mysql eso si lo copie del ejercicio anterior hast
            sentenciaP.setInt(1, ano);
            sentenciaP.setString(2, indicador);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                resultados.add(new ResultadosPrimaria(resultado.getString(1), resultado.getString(2)));
            }
            resultado.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar los resultados de las indicadores de las pruebas saber", ex);
        }
        return resultados;
    }

    /**
     * Metodo encargado de realizar la consulta de la respuesta del indicador
     *
     * @param indicador indicador seleccionado
     * @param anoConsultar año a consultar
     * @param prueba prueba seleccionada
     * @return respuesta del indicador
     */
    public String cargarInformacionAno(String indicador, Integer anoConsultar, String prueba) {
        String resultadoConsulta = "";
        boolean primeraVez = true;
        try {
            String query = "SELECT res_indicador FROM resultados.resultados_pruebas WHERE prueba=?  and s_indicador=? and ano=?";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);// esto es para preparar lo del mysql eso si lo copie del ejercicio anterior hast
            sentenciaP.setString(1, prueba);
            sentenciaP.setString(2, indicador);
            sentenciaP.setInt(3, anoConsultar);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                String resp = resultado.getString(1);
                resp = resp.replace(",", ": ");
                if (primeraVez) {
                    resultadoConsulta = resultadoConsulta + resp + " | ";
                } else {
                    resultadoConsulta = resultadoConsulta + resp;
                }
                primeraVez = false;
            }
            resultado.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar los resultados de las indicadores de las pruebas saber", ex);
        }
        return resultadoConsulta;
    }
    // CRUD de la tabla resultados_indicador

    /**
     * Metodo encargado de insertar resultados del indicador
     *
     * @param indicador indicador seleccionado
     * @param respuesta respuesta del indicador
     * @param resultado resultado del indicador
     * @param prueba prueba
     * @param ano año
     */
    public void insertarResultadosIndicador(String indicador, String respuesta, Double resultado, String prueba, int ano) {
        try {
            String query = "INSERT INTO resultados.resultados_indicador (indicador,"
                    + " res_indicador,resultado,prueba,ano) VALUES (?, ?, ?, ?, ?);";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);
            sentenciaP.setString(1, indicador);
            sentenciaP.setString(2, respuesta);
            sentenciaP.setDouble(3, resultado);
            sentenciaP.setString(4, prueba);
            sentenciaP.setInt(5, ano);
            sentenciaP.executeUpdate();
            sentenciaP.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al insertar informacion del indicador", ex);
        }
    }

    /**
     * Consultar de informacion del indicador por año
     *
     * @param prueba prueba a consultar
     * @param indicador indicador a consultar
     * @return listado de resultados
     */
    public List<ResultadosPrimaria> cargarIndicadorAnos(String prueba, String indicador) {
        List<ResultadosPrimaria> resultados = new ArrayList<>();
        PreparedStatement sentenciaP = null;
        try {
            String query = "SELECT ano, resultado,res_indicador FROM resultados.resultados_indicador WHERE prueba=?  and indicador=?   order by res_indicador, ano";
            sentenciaP = conexion.abrir().prepareStatement(query);// esto es para preparar lo del mysql eso si lo copie del ejercicio anterior hast
            sentenciaP.setString(1, prueba);
            sentenciaP.setString(2, indicador);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                resultados.add(new ResultadosPrimaria(resultado.getDouble(2), resultado.getInt(1), resultado.getString(3)));
            }
            resultado.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar informacion del indicador", ex);
        }
        return resultados;
    }

    /**
     * Cargar informacion del indicador por prueba
     *
     * @param ano año a consultar
     * @param indicador indicador a consultar
     * @return lista de resultados
     */
    public List<ResultadosPrimaria> cargarIndicadorPrueba(int ano, String indicador) {
        List<ResultadosPrimaria> resultados = new ArrayList<>();
        try {
            String query = "SELECT prueba, resultado,res_indicador FROM resultados.resultados_indicador WHERE ano=?  and indicador=?  order by res_indicador, prueba";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);// esto es para preparar lo del mysql eso si lo copie del ejercicio anterior hast
            sentenciaP.setInt(1, ano);
            sentenciaP.setString(2, indicador);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                resultados.add(new ResultadosPrimaria(resultado.getString(1), resultado.getDouble(2), resultado.getString(3)));
            }
            resultado.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar informacion del indicador", ex);
        }
        return resultados;
    }

    /**
     * Metodo para consultar las pruebas registradas
     *
     * @param ano año a consultar
     * @param indicador indicador a consultar
     * @return listado de pruebas
     */
    public List<String> cargarNombrePruebas(int ano, String indicador) {
        List<String> resultados = new ArrayList<>();
        try {
            String query = "SELECT prueba FROM resultados.resultados_indicador WHERE ano=?  and indicador=?   group by prueba order by prueba ";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);// esto es para preparar lo del mysql eso si lo copie del ejercicio anterior hast
            sentenciaP.setInt(1, ano);
            sentenciaP.setString(2, indicador);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                resultados.add(resultado.getString(1));

            }
            resultado.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar informacion del indicador", ex);
        }
        return resultados;
    }

    /**
     * Metodo encargado de consultar la cantidad de predicciones por prueba, año
     * y tipo
     *
     * @param indicador indicador a consultar
     * @param tipo tipo a consultar
     * @param prueba prueba a consultar
     * @return cantidad de registros
     */
    public int consultarCantidadResPredictivo(String indicador, String tipo, String prueba) {
        try {
            String query = "SELECT count(*) FROM resultados.resultados_prediccion WHERE prueba=?  and indicador=? and nucleo=?";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);// esto es para preparar lo del mysql eso si lo copie del ejercicio anterior hast
            sentenciaP.setString(1, prueba);
            sentenciaP.setString(2, indicador);
            sentenciaP.setString(3, tipo);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                return resultado.getInt(1);
            }
            resultado.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar los resultados de las indicadores de las pruebas saber", ex);
        }
        return 0;
    }

    /**
     * Metodo encargado de cargar los resultados del analisis predictivo
     *
     * @param datos datos de prediccion hallados
     * @param anio año analizado
     */
    public void cargarResultadosPredictivo(DatosPrediccion datos, int anio) {
        try {
            String query = "INSERT INTO resultados.resultados_prediccion( genero, indicador, descripcion, punt_max, punt_min, probabilidad, prueba, nucleo, anio)\n"
                    + " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);
            sentenciaP.setString(1, datos.getGenero().trim());
            sentenciaP.setString(2, datos.getIndicador().trim());
            sentenciaP.setString(3, datos.getDescripcion().trim());
            sentenciaP.setInt(4, datos.getMaxPuntaje());
            sentenciaP.setInt(5, datos.getMinPuntaje());
            sentenciaP.setDouble(6, datos.getProbabilidad() * 100);
            sentenciaP.setString(7, datos.getPrueba());
            sentenciaP.setString(8, datos.getNucleo());
            sentenciaP.setInt(9, anio);
            sentenciaP.executeUpdate();
            sentenciaP.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al insertar los resultados de las pruebas", ex);
        }
    }

    /**
     * Metodo de consulta de resultados de prediccion
     *
     * @param nucleo nucleo tematico
     * @param prueba prueba a consultar
     * @param genero genero
     * @return listado de predicciones
     */
    public List<DatosPrediccion> consultarResultadosPred(String nucleo, String prueba, String genero) {
        List<DatosPrediccion> predicciones = new ArrayList<>();
        try {
            String query = "SELECT * FROM resultados.resultados_prediccion WHERE probabilidad>5 and prueba=? and nucleo=?"
                    + " and descripcion<>'No afecta' and punt_max<>1000  and genero=? order by indicador, probabilidad desc;";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);// esto es para preparar lo del mysql eso si lo copie del ejercicio anterior hast
            sentenciaP.setString(1, prueba);
            sentenciaP.setString(2, nucleo);
            sentenciaP.setString(3, genero);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                predicciones.add(new DatosPrediccion(resultado.getString(2), resultado.getString(3), resultado.getString(4),
                        resultado.getInt(5), resultado.getInt(6), resultado.getDouble(7), prueba, nucleo));
            }
            resultado.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar los resultados de las indicadores de las pruebas saber", ex);
        }
        return predicciones;
    }

    /**
     * Metodo de consulta de resultados de prediccion e acuerdo al genero e
     * indicador
     *
     * @param nucleo nucleo tematico
     * @param prueba prueba a consultar
     * @param genero genero
     * @return listado de predicciones
     */
    public List<ResultadosPrimaria> analisisPrediccionGenero(String nucleo, String prueba, String genero) {
        List<ResultadosPrimaria> resultadosConsulta = new ArrayList<>();
        try {
            String query = "SELECT genero,indicador FROM resultados.resultados_prediccion WHERE probabilidad>5 and prueba=? and nucleo=? and descripcion<>'No afecta' and punt_max<>1000 \n"
                    + " and genero=?' group by genero, indicador";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);// esto es para preparar lo del mysql eso si lo copie del ejercicio anterior hast
            sentenciaP.setString(1, prueba);
            sentenciaP.setString(2, nucleo);
            sentenciaP.setString(3, genero);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                resultadosConsulta.add(new ResultadosPrimaria(resultado.getString(1), resultado.getString(2)));
            }
            resultado.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar los resultados de las indicadores de las pruebas saber", ex);
        }
        return resultadosConsulta;
    }

    /**
     * Metodo encargado de realizar filtros sobre la tabla de predicciones
     *
     * @param nucleo nucleo tematico
     * @param prueba prueba
     * @param genero genero
     * @param punMin puntaje minimo
     * @param punMax puntaje maximo
     * @param indicador indicador a consultar
     * @return Objeto prediccion
     */
    public DatosPrediccion filtrarPredicciones(String nucleo, String prueba, String genero, int punMin, int punMax, String indicador) {
        DatosPrediccion prediccion = null;
        try {
            String query = "SELECT * FROM resultados.resultados_prediccion WHERE probabilidad>5 and prueba=? and nucleo=?"
                    + " and descripcion<>'No afecta' and punt_max<? and punt_min>? and genero=? "
                    + " and indicador=? order by indicador, probabilidad desc;";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);// esto es para preparar lo del mysql eso si lo copie del ejercicio anterior hast
            sentenciaP.setString(1, prueba);
            sentenciaP.setString(2, nucleo);
            sentenciaP.setInt(3, punMax);
            sentenciaP.setInt(4, punMin);
            sentenciaP.setString(5, genero);
            sentenciaP.setString(6, indicador);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                prediccion = new DatosPrediccion(resultado.getString(2), resultado.getString(3), resultado.getString(4),
                        resultado.getInt(5), resultado.getInt(6), (double) Math.round(resultado.getDouble(7) * 100d) / 100d, prueba, nucleo);
                break;
            }
            resultado.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar los resultados de las indicadores de las pruebas saber", ex);
        }
        return prediccion;
    }

    /**
     * Metodo encargado de realizar filtros sobre la tabla de predicciones de
     * otra forma
     *
     * @param indicador indicador
     * @param genero genero del estudiante
     * @param puntMin puntaje minimo
     * @param puntMax puntaje maximo
     * @param nucleo nucleo tematico
     * @param prueba prueba cargada
     * @return Objeto prediccion
     */
    public DatosPrediccion consultarFiltrosPred(String indicador, String genero, int puntMin, int puntMax, String nucleo, String prueba) {
        DatosPrediccion prediccion = null;
        try {
            String query = "SELECT * FROM resultados.resultados_prediccion WHERE prueba=? and nucleo=? and indicador=?"
                    + " and  punt_max<=? and punt_min>=? and not(punt_max = 500 and punt_min =0) and genero=? and probabilidad>0 order by probabilidad desc limit 1;";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);// esto es para preparar lo del mysql eso si lo copie del ejercicio anterior hast
            sentenciaP.setString(1, prueba);
            sentenciaP.setString(2, nucleo);
            sentenciaP.setString(3, indicador);
            sentenciaP.setInt(4, puntMax);
            sentenciaP.setInt(5, puntMin);
            sentenciaP.setString(6, genero);
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                prediccion = new DatosPrediccion(resultado.getString(2), resultado.getString(3), resultado.getString(4),
                        resultado.getInt(5), resultado.getInt(6), resultado.getDouble(7), prueba, nucleo);
            }
            resultado.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar los resultados de las indicadores de las pruebas saber", ex);
        }
        return prediccion;
    }

    /**
     * Consulta de predicciones especializada
     *
     * @param datosP objeto de prediccion
     * @return listado de predicciones
     */
    public List<DatosPrediccion> consultarIndicadorPrediccion(DatosPrediccion datosP) {
        List<DatosPrediccion> predicciones = new ArrayList<>();
        try {
            String query = "SELECT * FROM resultados.resultados_prediccion WHERE prueba=? and nucleo=? and indicador=?"
                    + "  and genero=? and probabilidad>=5 order by probabilidad ;";
            PreparedStatement sentenciaP = conexion.abrir().prepareStatement(query);// esto es para preparar lo del mysql eso si lo copie del ejercicio anterior hast
            sentenciaP.setString(1, datosP.getPrueba());
            sentenciaP.setString(2, datosP.getNucleo());
            sentenciaP.setString(3, datosP.getIndicador());
            sentenciaP.setString(4, datosP.getGenero());
            ResultSet resultado = sentenciaP.executeQuery();
            while (resultado.next()) {
                predicciones.add(new DatosPrediccion(resultado.getString(2), resultado.getString(3), resultado.getString(4),
                        resultado.getInt(5), resultado.getInt(6), resultado.getDouble(7), datosP.getPrueba(), datosP.getNucleo()));
            }
            resultado.close();
            sentenciaP.close();
            conexion.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Error al consultar los resultados de las indicadores de las pruebas saber", ex);
        }
        return predicciones;
    }

}
