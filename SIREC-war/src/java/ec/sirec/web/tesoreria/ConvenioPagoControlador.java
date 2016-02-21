/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.web.tesoreria;

import ec.sirec.ejb.entidades.ConvenioPago;
import ec.sirec.ejb.entidades.ConvenioPagoDet;
import ec.sirec.ejb.entidades.CuentaPorCobrar;
import ec.sirec.ejb.entidades.Propietario;
import ec.sirec.ejb.servicios.ConvenioPagoServicio;
import ec.sirec.ejb.servicios.PropietarioServicio;
import ec.sirec.web.base.BaseControlador;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class ConvenioPagoControlador extends BaseControlador {

    private static final Logger LOGGER = Logger.getLogger(ConvenioPagoControlador.class.getName());

    @EJB
    private ConvenioPagoServicio convenioPagoServicio;

    @EJB
    private PropietarioServicio propietarioServicio;

    private Propietario propietarioBusqueda;

    private List<CuentaPorCobrar> listaCuentasPendientes;

    private ConvenioPago convenioActual;

    private List<ConvenioPago> listaConvenioPago;

    private List<ConvenioPagoDet> listaConvenioPagoDetalle;

    /**
     * Creates a new instance of ConvenioPagoControlador
     */
    public ConvenioPagoControlador() {
    }

    @PostConstruct
    public void inicializar() {
        try {
            propietarioBusqueda = new Propietario();
            listaCuentasPendientes = new ArrayList<CuentaPorCobrar>();
            convenioActual = new ConvenioPago();
            convenioActual.setConpagEstado("C");
            listaConvenioPago = new ArrayList<ConvenioPago>();
            listaConvenioPagoDetalle = new ArrayList<ConvenioPagoDet>();

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

    public void seleccionarPropietario() {
        try {
            listarCuentasPendientesPorPropietario();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void totalizar() {
        try {
            convenioActual.setConpagTotal(convenioPagoServicio.obtenerTotalAConvenir(listaCuentasPendientes));
            //programaciontemporal
            convenioActual.setCxcCodigo(convenioPagoServicio.obtenerCxCAConvenir(listaCuentasPendientes));
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void obtenerSaldo() {
        try {
            if (convenioActual.getConpagTotal() != null && convenioActual.getConpagPorcentaje() != null) {
                BigDecimal abono = convenioActual.getConpagTotal().multiply(new BigDecimal(convenioActual.getConpagPorcentaje() * 0.01)).setScale(2, RoundingMode.CEILING);
                convenioActual.setConpagSaldo(convenioActual.getConpagTotal().subtract(abono));
                
                
                
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void guardarConvenio() {
        try {
            if (convenioActual.getConpagNumCuotas() > 0 && convenioActual.getConpagSaldo() != null) {
                convenioActual.setUsuIdentificacion(obtenerUsuarioAutenticado());
                convenioPagoServicio.guardarConvenioCab(convenioActual);
                for (int i = 1; i <= convenioActual.getConpagNumCuotas(); i++) {
                    convenioPagoServicio.guardarConvenioDetalle(convenioActual, convenioActual.getConpagFecha(), i, convenioActual.getConpagSaldo().divide(new BigDecimal(convenioActual.getConpagNumCuotas()),2, RoundingMode.HALF_UP));
                }
                addSuccessMessage("Convenio creado exitosamente");
            }
            listaConvenioPago = new ArrayList<ConvenioPago>();
            listaConvenioPago = convenioPagoServicio.listarConveniosPorCi(propietarioBusqueda.getProCi());

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void listarCuentasPendientesPorPropietario() {
        try {
            if (propietarioBusqueda != null) {
                listaCuentasPendientes = new ArrayList<CuentaPorCobrar>();
                listaCuentasPendientes = convenioPagoServicio.listarCuentasPendientesPorCi(propietarioBusqueda.getProCi());
                listaConvenioPago = new ArrayList<ConvenioPago>();
                listaConvenioPago = convenioPagoServicio.listarConveniosPorCi(propietarioBusqueda.getProCi());
                listaConvenioPagoDetalle = new ArrayList<ConvenioPagoDet>();
            } else {
                addWarningMessage("Seleccione primero un propietario");
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public void verDetallesDeConvenio(ConvenioPago con) {
        try {
             listaConvenioPagoDetalle = new ArrayList<ConvenioPagoDet>();
             listaConvenioPagoDetalle=convenioPagoServicio.listarDetallesDeConvenio(con);
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

    public ConvenioPago getConvenioActual() {
        return convenioActual;
    }

    public void setConvenioActual(ConvenioPago convenioActual) {
        this.convenioActual = convenioActual;
    }

    public List<ConvenioPago> getListaConvenioPago() {
        return listaConvenioPago;
    }

    public void setListaConvenioPago(List<ConvenioPago> listaConvenioPago) {
        this.listaConvenioPago = listaConvenioPago;
    }

    public List<ConvenioPagoDet> getListaConvenioPagoDetalle() {
        return listaConvenioPagoDetalle;
    }

    public void setListaConvenioPagoDetalle(List<ConvenioPagoDet> listaConvenioPagoDetalle) {
        this.listaConvenioPagoDetalle = listaConvenioPagoDetalle;
    }
    
    

}
