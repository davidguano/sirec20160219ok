/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.web.tesoreria;

import ec.sirec.ejb.entidades.Coactiva;
import ec.sirec.ejb.entidades.CuentaPorCobrar;
import ec.sirec.ejb.entidades.Propietario;
import ec.sirec.ejb.servicios.CoactivaServicio;
import ec.sirec.ejb.servicios.PropietarioServicio;
import ec.sirec.web.base.BaseControlador;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author DAVID GUAN
 */
@ManagedBean
@ViewScoped
public class CoactivaControlador extends BaseControlador {

    private static final Logger LOGGER = Logger.getLogger(CoactivaControlador.class.getName());

    @EJB
    private CoactivaServicio coactivaServicio;

    @EJB
    private PropietarioServicio propietarioServicio;

    private Propietario propietarioBusqueda;

    private List<CuentaPorCobrar> listaCuentasPendientes;

    private List<Coactiva> listaCoactivas;

    private Coactiva coactivaActual;

    /**
     * Creates a new instance of CoactivaControlador
     */
    public CoactivaControlador() {
    }

    @PostConstruct
    public void inicializar() {
        try {
            propietarioBusqueda = new Propietario();
            listaCuentasPendientes = new ArrayList<CuentaPorCobrar>();
            listaCoactivas = new ArrayList<Coactiva>();
            coactivaActual = new Coactiva();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public List<Propietario> obtenerPropietarioPorCedula(String textoBusqueda) {
        List<Propietario> lstPro = new ArrayList<Propietario>();
        try {

            lstPro = propietarioServicio.listarPorCedulaContiene(textoBusqueda);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return lstPro;
    }

    public List<Propietario> obtenerPropietarioPorApellidos(String textoBusqueda) {
        List<Propietario> lstPro = new ArrayList<Propietario>();
        try {
            lstPro = propietarioServicio.listarPorApellidosContiene(textoBusqueda);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return lstPro;
    }

    public List<Propietario> obtenerPropietarioPorNombres(String textoBusqueda) {
        List<Propietario> lstPro = new ArrayList<Propietario>();
        try {
            lstPro = propietarioServicio.listarPorNombresContiene(textoBusqueda);

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return lstPro;
    }

    public List<Propietario> obtenerPropietarioPorClaveCatastro(String textoBusqueda) {
        List<Propietario> lstPro = new ArrayList<Propietario>();
        try {
            lstPro = propietarioServicio.listarPropietariosPorClaveCatastralContiene(textoBusqueda);

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return lstPro;
    }

    public List<Propietario> obtenerPropietarioPorClavePatente(String textoBusqueda) {
        List<Propietario> lstPro = new ArrayList<Propietario>();
        try {
            lstPro = propietarioServicio.listarPropietariosPorClavePatenteContiene(textoBusqueda);

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return lstPro;
    }

    public void seleccionarPropietario() {
        try {
            listarCuentasPendientesPorPropietario();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void listarCuentasPendientesPorPropietario() {
        try {
            if (propietarioBusqueda != null) {
                listaCuentasPendientes = new ArrayList<CuentaPorCobrar>();
                listaCuentasPendientes = coactivaServicio.listarCuentasPendientesPorCi(propietarioBusqueda.getProCi());
                listaCoactivas = new ArrayList<Coactiva>();
                listaCoactivas = coactivaServicio.listarCoactivasPorPropietario(propietarioBusqueda.getProCi());
            } else {
                addWarningMessage("Seleccione primero un propietario");
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void guardarCoactivas() {
        try {
            coactivaServicio.guardarCoactivas(listaCuentasPendientes, obtenerUsuarioAutenticado());
            addSuccessMessage("Proceso de Coactiva Iniciado correctamente.");
            listarCuentasPendientesPorPropietario();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    public void enviarNotificacion(Coactiva coa){
        try {
           coactivaServicio.activarNotificacion(coa, obtenerUsuarioAutenticado());
            addSuccessMessage("Notificacion registrada correctamente");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public Propietario getPropietarioBusqueda() {
        return propietarioBusqueda;
    }

    public void setPropietarioBusqueda(Propietario propietarioBusqueda) {
        this.propietarioBusqueda = propietarioBusqueda;
    }

    public List<CuentaPorCobrar> getListaCuentasPendientes() {
        return listaCuentasPendientes;
    }

    public void setListaCuentasPendientes(List<CuentaPorCobrar> listaCuentasPendientes) {
        this.listaCuentasPendientes = listaCuentasPendientes;
    }

    public List<Coactiva> getListaCoactivas() {
        return listaCoactivas;
    }

    public void setListaCoactivas(List<Coactiva> listaCoactivas) {
        this.listaCoactivas = listaCoactivas;
    }

    public Coactiva getCoactivaActual() {
        return coactivaActual;
    }

    public void setCoactivaActual(Coactiva coactivaActual) {
        this.coactivaActual = coactivaActual;
    }

}
