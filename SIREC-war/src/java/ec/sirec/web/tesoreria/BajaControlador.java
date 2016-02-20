/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.web.tesoreria;

import ec.sirec.ejb.entidades.Baja;
import ec.sirec.ejb.entidades.RecaudacionDet;
import ec.sirec.ejb.servicios.BajaServicio;
import ec.sirec.web.base.BaseControlador;
import ec.sirec.web.util.UtilitariosCod;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 *
 * @author DAVID GUAN
 */
@ManagedBean
@ViewScoped
public class BajaControlador extends BaseControlador {

    /**
     * Creates a new instance of BajaControlador
     */
    private static final Logger LOGGER = Logger.getLogger(BajaControlador.class.getName());

    @EJB
    private BajaServicio bajaServicio;

    private String tipoImpuesto;

    private String claveCat;
    private String clavePat;
    private String ciProp;
    private String apellidosProp;

    private List<RecaudacionDet> listaRecaudacionesTitulos;

    private Baja bajaActual;

    private List<Baja> listaBajas;

    public BajaControlador() {
    }

    @PostConstruct
    public void inicializar() {
        try {
            tipoImpuesto = "PR";
            claveCat = "";
            clavePat = "";
            ciProp = "";
            apellidosProp = "";
            listaRecaudacionesTitulos = new ArrayList<RecaudacionDet>();
            bajaActual = new Baja();
            bajaActual.setBajTipo("3E");
            listaBajas = new ArrayList<Baja>();

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void listarRecaudacionesBajasPorTipoYCedula() {
        try {
            listaRecaudacionesTitulos = new ArrayList<RecaudacionDet>();
            listaRecaudacionesTitulos = bajaServicio.listaDetallesARecaudadosPorTipoyCiPropietario(tipoImpuesto, ciProp);
            listaBajas = new ArrayList<Baja>();
            listaBajas = bajaServicio.listaBajasPorTipoyCiPropietario(tipoImpuesto, ciProp);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void listarRecaudacionesBajasPorTipoYApellidos() {
        try {
            listaRecaudacionesTitulos = new ArrayList<RecaudacionDet>();
            listaRecaudacionesTitulos = bajaServicio.listaDetallesARecaudadosPorTipoyApellidosProp(tipoImpuesto, apellidosProp);
            listaBajas = new ArrayList<Baja>();
            listaBajas = bajaServicio.listaBajasPorTipoyApellidosProp(tipoImpuesto, apellidosProp);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void listarRecaudacionesBajasPorTipoYClaveCat() {
        try {
            listaRecaudacionesTitulos = new ArrayList<RecaudacionDet>();
            listaRecaudacionesTitulos = bajaServicio.listaDetallesARecaudadosPorTipoyClave(tipoImpuesto, claveCat);
            listaBajas = new ArrayList<Baja>();
            listaBajas = bajaServicio.listaBajasPorTipoyClave(tipoImpuesto, claveCat);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void listarRecaudacionesBajasPorTipoYClavePat() {
        try {
            listaRecaudacionesTitulos = new ArrayList<RecaudacionDet>();
            listaRecaudacionesTitulos = bajaServicio.listaDetallesARecaudadosPorTipoyClave(tipoImpuesto, clavePat);
            listaBajas = new ArrayList<Baja>();
            listaBajas = bajaServicio.listaBajasPorTipoyClave(tipoImpuesto, clavePat);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void seleccionarTitulo(RecaudacionDet det) {
        try {
            if (det.getRecdetBaja() == null || det.getRecdetBaja() == false) {
                bajaActual.setRecdetCodigo(det);
            }else{
                addErrorMessage("Titulo ya ha sido anulado.");
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void guardarBaja() {
        try {
            if (bajaActual.getRecdetCodigo() != null) {
                bajaActual.setUsuIdentificacion(obtenerUsuarioAutenticado());
                bajaServicio.guardarBaja(bajaActual);
                addSuccessMessage("Baja Registrada Correctamente");
                inicializar();
            } else {
                addErrorMessage("Seleccione un titulo primero.");
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void generarTituloAnuladoVistaPrevia() {
        try {
            if (bajaActual.getRecdetCodigo() != null) {
                if (!bajaActual.getRecdetCodigo().getRecdetTipo().equals("PM")) {
                    generarTituloPorTipo(bajaActual.getRecdetCodigo().getRecdetCodref(), bajaActual.getRecdetCodigo().getRecdetTipo());
                } else {
                    //titulo se genera desde el registro de patente.
                }
            } else {
                addErrorMessage("Seleccione un titulo primero.");
            }

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public String generarTituloPorTipo(Integer vcodValoracion, String tipo) throws Exception {
        //Conexion con local datasource
        UtilitariosCod util = new UtilitariosCod();
        Connection conexion = util.getConexion();
        byte[] fichero = null;
        JasperReport jasperReport = null;
        Map parameters = new HashMap();
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
            ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
            session.removeAttribute("reporteInforme");
            parameters.put("cod_valoracion", vcodValoracion);
            parameters.put("logo_gad", servletContext.getRealPath("/imagenes/icons/gadPedroMoncayo.jpg"));
            parameters.put("p_es_baja", true);
            if (tipo.equals("PR")) {
                jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/recaudacion/titulo_predial_urbano.jasper"));

            } else if (tipo.equals("PA")) {
                jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/recaudacion/titulo_patente.jasper"));

            }
            fichero = JasperRunManager.runReportToPdf(jasperReport, parameters, conexion);
            session.setAttribute("reporteInforme", fichero);

        } catch (JRException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        } finally {
            if (conexion != null) {
                conexion.close();
            }
        }
        return null;
    }

    public String getTipoImpuesto() {
        return tipoImpuesto;
    }

    public void setTipoImpuesto(String tipoImpuesto) {
        this.tipoImpuesto = tipoImpuesto;
    }

    public List<RecaudacionDet> getListaRecaudacionesTitulos() {
        return listaRecaudacionesTitulos;
    }

    public void setListaRecaudacionesTitulos(List<RecaudacionDet> listaRecaudacionesTitulos) {
        this.listaRecaudacionesTitulos = listaRecaudacionesTitulos;
    }

    public Baja getBajaActual() {
        return bajaActual;
    }

    public void setBajaActual(Baja bajaActual) {
        this.bajaActual = bajaActual;
    }

    public List<Baja> getListaBajas() {
        return listaBajas;
    }

    public void setListaBajas(List<Baja> listaBajas) {
        this.listaBajas = listaBajas;
    }

    public String getClaveCat() {
        return claveCat;
    }

    public void setClaveCat(String claveCat) {
        this.claveCat = claveCat;
    }

    public String getClavePat() {
        return clavePat;
    }

    public void setClavePat(String clavePat) {
        this.clavePat = clavePat;
    }

    public String getCiProp() {
        return ciProp;
    }

    public void setCiProp(String ciProp) {
        this.ciProp = ciProp;
    }

    public String getApellidosProp() {
        return apellidosProp;
    }

    public void setApellidosProp(String apellidosProp) {
        this.apellidosProp = apellidosProp;
    }

}
