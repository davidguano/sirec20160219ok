/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.ejb.servicios;

import ec.sirec.ejb.entidades.ConvenioPago;
import ec.sirec.ejb.entidades.ConvenioPagoDet;
import ec.sirec.ejb.entidades.CuentaPorCobrar;
import ec.sirec.ejb.entidades.Interes;
import ec.sirec.ejb.facade.ConvenioPagoDetFacade;
import ec.sirec.ejb.facade.ConvenioPagoFacade;
import ec.sirec.ejb.facade.CuentaPorCobrarFacade;
import ec.sirec.ejb.facade.InteresFacade;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author DAVID GUAN
 */
@Stateless
@LocalBean
public class ConvenioPagoServicio {

    @EJB
    private ConvenioPagoFacade convenioPagoCabDao;

    @EJB
    private ConvenioPagoDetFacade convenioPagoDetDao;

    @EJB
    private CuentaPorCobrarFacade cxcDao;

    @EJB
    private InteresFacade interesDao;

    public List<CuentaPorCobrar> listarCuentasPendientesPorCi(String vci) throws Exception {
        return cxcDao.listarPor2CamposOrdenada("CuentaPorCobrar", "proCi.proCi", vci, "cxcEstado", "P", "cxcFecha", "asc");
    }

    public BigDecimal obtenerTotalAConvenir(List<CuentaPorCobrar> lstCxc) throws Exception {
        BigDecimal bd = new BigDecimal("0");
        for (CuentaPorCobrar cxc : lstCxc) {
            if (cxc.getSeleccionado()) {
               bd=bd.add(obtenerTotalConInteres(cxc.getCxcFecha(), cxc.getCxcValorTotal()));
            }
        }
        return bd;
    }
    public CuentaPorCobrar obtenerCxCAConvenir(List<CuentaPorCobrar> lstCxc) throws Exception {
        CuentaPorCobrar cxc1=new CuentaPorCobrar();
        for (CuentaPorCobrar cxc : lstCxc) {
            if (cxc.getSeleccionado()) {
               cxc1=cxc;
            }
        }
        return cxc1;
    }

    public BigDecimal obtenerTotalConInteres(Date fechaEmision, BigDecimal total1) throws Exception {
        BigDecimal total = new BigDecimal("0");
        int anio = fechaEmision.getYear() + 1900;
        int mes = fechaEmision.getMonth() + 1;
        if (interesDao.existePor2Campos("Interes", "intAnio", anio, "intMes", mes)) {
            Interes inte=interesDao.buscarPor2Campos("Interes", "intAnio", anio, "intMes", mes);
            total=total1.add(total1.multiply(new BigDecimal(inte.getIntValor()*0.01)));
        } else {
            total = total1;
        }

        return total;
    }

    public void guardarConvenioCab(ConvenioPago cp) throws Exception {
        convenioPagoCabDao.crear(cp);
    }

    public void guardarConvenioDetalle(ConvenioPago conv, Date fechaInicial, int numCuota, BigDecimal valorCuota) {
        ConvenioPagoDet det = new ConvenioPagoDet();
        det.setConpagCodigo(conv);
        det.setConpagdetValorCuota(valorCuota);
        det.setConpagdetNumCuota(numCuota);
        det.setConpagdetFechaPago(sumarRestarDiasFecha(fechaInicial, numCuota * 30));
        det.setConpagdetTasaInteres(1.024 * numCuota);
        det.setConpagdetValorInteres(valorCuota.multiply(new BigDecimal(1.024 * numCuota * 0.01)));
        det.setConpagdetValor(valorCuota.add(det.getConpagdetValorInteres()));
        convenioPagoDetDao.crear(det);
    }

    public Date sumarRestarDiasFecha(Date fecha, int dias) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha); // Configuramos la fecha que se recibe
        calendar.add(Calendar.DAY_OF_YEAR, dias);  // numero de días a añadir, o restar en caso de días<0
        return calendar.getTime(); // Devuelve el objeto Date con los nuevos días añadidos

    }

    public List<ConvenioPago> listarConveniosPorCi(String ci) throws Exception {
        return convenioPagoCabDao.listarPorCampoOrdenada("ConvenioPago", "cxcCodigo.proCi.proCi", ci, "conpagFecha", "asc");
    }

    public List<ConvenioPagoDet> listarDetallesDeConvenio(ConvenioPago con) throws Exception {
        return convenioPagoDetDao.listarPorCampoOrdenada("ConvenioPagoDet", "conpagCodigo", con, "conpagdetNumCuota", "asc");
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
