/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.ejb.servicios;

import ec.sirec.ejb.entidades.CatastroPredialAlcabalaValoracion;
import ec.sirec.ejb.entidades.CatastroPredialPlusvaliaValoracion;
import ec.sirec.ejb.entidades.CatastroPredialValoracion;
import ec.sirec.ejb.entidades.CuentaPorCobrar;
import ec.sirec.ejb.entidades.Patente15xmilValoracion;
import ec.sirec.ejb.entidades.PatenteValoracion;
import ec.sirec.ejb.entidades.Servicios;
import ec.sirec.ejb.facade.CuentaPorCobrarFacade;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author DAVID GUAN
 */
@Stateless
@LocalBean
public class CuentaPorCobrarServicio {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @EJB
    private CuentaPorCobrarFacade cxcDao;
    @EJB
    private PropietarioServicio propietarioServicio;

    public void crearCxc(CuentaPorCobrar cxc) throws Exception {
        cxcDao.crear(cxc);
    }

    public void editarCxc(CuentaPorCobrar cxc) throws Exception {
        cxcDao.editar(cxc);
    }

    public void crearCxcPorImpPredial(CatastroPredialValoracion impPre) throws Exception {
        BigDecimal total = impPre.getCatprevalImpuesto().add(impPre.getCatprevalBomberos()).add(impPre.getCatprevalSolarNoedificado()).add(impPre.getCatprevalTasaAdm());

        if (total.compareTo(BigDecimal.ZERO) == 1) {
            CuentaPorCobrar cxc = new CuentaPorCobrar();
            cxc = buscarCxCPorTipoyClave("PR", impPre.getCatprevalCodigo());
            if (cxc != null) {
                if (cxc.getCxcEstado().equals("P")) {
                    cxc.setCxcAnio(impPre.getCatprevalAnio());
                    cxc.setCxcFecha(java.util.Calendar.getInstance().getTime());
                    cxc.setCxcValorTotal(total);
                    cxc.setCxcSaldo(total);
                    cxc.setCxcFechaVencimiento(obtenerFechaFinAnio(impPre.getCatprevalAnio()));
                    cxcDao.editar(cxc);
                } else {
                    //ya hay pagos a esta cuenta, no se puede cambiar los valores
                }
            } else {
                cxc = new CuentaPorCobrar();
                cxc.setProCi(propietarioServicio.obtenerPropietarioPrincipalPredio(impPre.getCatpreCodigo().getCatpreCodigo()));
                cxc.setCxcAnio(impPre.getCatprevalAnio());
                cxc.setCxcMes(0);
                cxc.setCxcTipo("PR");
                cxc.setCxcReferencia(impPre.getCatpreCodigo().getClaveCatastral());
                cxc.setCxcFecha(java.util.Calendar.getInstance().getTime());
                cxc.setCxcValorTotal(total);
                cxc.setCxcSaldo(total);
                cxc.setCxcFechaVencimiento(obtenerFechaFinAnio(impPre.getCatprevalAnio()));
                cxc.setCxcEstado("P");
                cxc.setCxcCodRef(impPre.getCatprevalCodigo());
                cxc.setCxcActivo(true);
                cxcDao.crear(cxc);
            }
        }

    }

    public void crearCxcPorImpPatente(PatenteValoracion impPat) throws Exception {
        BigDecimal total = impPat.getPatvalTotal();

        if (total.compareTo(BigDecimal.ZERO) == 1) {
            CuentaPorCobrar cxc = new CuentaPorCobrar();
            cxc = buscarCxCPorTipoyClave("PA", impPat.getPatvalCodigo());
            if (cxc != null) {
                if (cxc.getCxcEstado().equals("P")) {
                    cxc.setCxcAnio(impPat.getPatvalAnio());
                    cxc.setCxcFecha(java.util.Calendar.getInstance().getTime());
                    cxc.setCxcValorTotal(total);
                    cxc.setCxcSaldo(total);
                    cxc.setCxcFechaVencimiento(obtenerFechaFinAnio(impPat.getPatvalAnio()));
                    cxcDao.editar(cxc);
                } else {
                    //ya hay pagos a esta cuenta, no se puede cambiar los valores
                }
            } else {
                cxc = new CuentaPorCobrar();
                cxc.setProCi(propietarioServicio.obtenerPropietarioPrincipalPredio(impPat.getPatCodigo().getCatpreCodigo().getCatpreCodigo()));
                cxc.setCxcAnio(impPat.getPatvalAnio());
                cxc.setCxcMes(0);
                cxc.setCxcTipo("PA");
                cxc.setCxcReferencia("AE-MPM" + impPat.getPatCodigo().getPatCodigo());
                cxc.setCxcFecha(java.util.Calendar.getInstance().getTime());
                cxc.setCxcValorTotal(total);
                cxc.setCxcSaldo(total);
                cxc.setCxcFechaVencimiento(obtenerFechaFinAnio(impPat.getPatvalAnio()));
                cxc.setCxcEstado("P");
                cxc.setCxcCodRef(impPat.getPatvalCodigo());
                cxc.setCxcActivo(true);
                cxcDao.crear(cxc);
            }
        }
    }

    public void crearCxcPorImpPatente15mil(Patente15xmilValoracion impPat15) throws Exception {
        BigDecimal total = impPat15.getPat15valTotal();

        if (total.compareTo(BigDecimal.ZERO) == 1) {
            CuentaPorCobrar cxc = new CuentaPorCobrar();
            cxc = buscarCxCPorTipoyClave("PM", impPat15.getPat15valCodigo());
            if (cxc != null) {
                if (cxc.getCxcEstado().equals("P")) {
                    cxc.setCxcAnio(impPat15.getPat15valAnioDecla());
                    cxc.setCxcFecha(java.util.Calendar.getInstance().getTime());
                    cxc.setCxcValorTotal(total);
                    cxc.setCxcSaldo(total);
                    cxc.setCxcFechaVencimiento(obtenerFechaFinAnio(impPat15.getPat15valAnioDecla()));
                    cxcDao.editar(cxc);
                } else {
                    //ya hay pagos a esta cuenta, no se puede cambiar los valores
                }
            } else {
                cxc = new CuentaPorCobrar();
                cxc.setProCi(propietarioServicio.obtenerPropietarioPrincipalPredio(impPat15.getPatCodigo().getCatpreCodigo().getCatpreCodigo()));
                cxc.setCxcAnio(impPat15.getPat15valAnioDecla());
                cxc.setCxcMes(0);
                cxc.setCxcTipo("PM");
                cxc.setCxcReferencia("AE-MPM" + impPat15.getPatCodigo().getPatCodigo());
                cxc.setCxcFecha(java.util.Calendar.getInstance().getTime());
                cxc.setCxcValorTotal(total);
                cxc.setCxcSaldo(total);
                cxc.setCxcFechaVencimiento(obtenerFechaFinAnio(impPat15.getPat15valAnioDecla()));
                cxc.setCxcEstado("P");
                cxc.setCxcCodRef(impPat15.getPat15valCodigo());
                cxc.setCxcActivo(true);
                cxcDao.crear(cxc);
            }
        }
    }

    public void crearCxcPorImpPlusvalia(CatastroPredialPlusvaliaValoracion impPlus) throws Exception {
        BigDecimal total = impPlus.getCatprepluvalImpuesto().add(impPlus.getCatprepluvalTasaproc());

        if (total.compareTo(BigDecimal.ZERO) == 1) {
            CuentaPorCobrar cxc = new CuentaPorCobrar();
            cxc = buscarCxCPorTipoyClave("PL", impPlus.getCatprepluvalCodigo());
            if (cxc != null) {
                if (cxc.getCxcEstado().equals("P")) {
                    cxc.setCxcAnio(impPlus.getCatprepluvalAnio());
                    cxc.setCxcFecha(java.util.Calendar.getInstance().getTime());
                    cxc.setCxcValorTotal(total);
                    cxc.setCxcSaldo(total);
                    cxc.setCxcFechaVencimiento(obtenerFechaFinAnio(impPlus.getCatprepluvalAnio()));
                    cxcDao.editar(cxc);
                } else {
                    //ya hay pagos a esta cuenta, no se puede cambiar los valores
                }
            } else {
                cxc = new CuentaPorCobrar();
                cxc.setProCi(propietarioServicio.obtenerPropietarioPrincipalPredio(impPlus.getCatpreCodigo().getCatpreCodigo()));
                cxc.setCxcAnio(impPlus.getCatprepluvalAnio());
                cxc.setCxcMes(0);
                cxc.setCxcTipo("PL");
                cxc.setCxcReferencia(impPlus.getCatpreCodigo().getClaveCatastral());
                cxc.setCxcFecha(java.util.Calendar.getInstance().getTime());
                cxc.setCxcValorTotal(total);
                cxc.setCxcSaldo(total);
                cxc.setCxcFechaVencimiento(obtenerFechaFinAnio(impPlus.getCatprepluvalAnio()));
                cxc.setCxcEstado("P");
                cxc.setCxcCodRef(impPlus.getCatprepluvalCodigo());
                cxc.setCxcActivo(true);
                cxcDao.crear(cxc);
            }
        }

    }

    public void crearCxcPorImpAlcabala(CatastroPredialAlcabalaValoracion impAlc) throws Exception {
        BigDecimal total = impAlc.getCatprealcvalTotal();

        if (total.compareTo(BigDecimal.ZERO) == 1) {
            CuentaPorCobrar cxc = new CuentaPorCobrar();
            cxc = buscarCxCPorTipoyClave("AL", impAlc.getCatprealcvalCodigo());
            if (cxc != null) {
                if (cxc.getCxcEstado().equals("P")) {
                    cxc.setCxcAnio(impAlc.getCatprealcvalAnio());
                    cxc.setCxcFecha(java.util.Calendar.getInstance().getTime());
                    cxc.setCxcValorTotal(total);
                    cxc.setCxcSaldo(total);
                    cxc.setCxcFechaVencimiento(obtenerFechaFinAnio(impAlc.getCatprealcvalAnio()));
                    cxcDao.editar(cxc);
                } else {
                    //ya hay pagos a esta cuenta, no se puede cambiar los valores
                }
            } else {
                cxc = new CuentaPorCobrar();
                cxc.setProCi(propietarioServicio.obtenerPropietarioPrincipalPredio(impAlc.getCatpreCodigo().getCatpreCodigo()));
                cxc.setCxcAnio(impAlc.getCatprealcvalAnio());
                cxc.setCxcMes(0);
                cxc.setCxcTipo("AL");
                cxc.setCxcReferencia(impAlc.getCatpreCodigo().getClaveCatastral());
                cxc.setCxcFecha(java.util.Calendar.getInstance().getTime());
                cxc.setCxcValorTotal(total);
                cxc.setCxcSaldo(total);
                cxc.setCxcFechaVencimiento(obtenerFechaFinAnio(impAlc.getCatprealcvalAnio()));
                cxc.setCxcEstado("P");
                cxc.setCxcCodRef(impAlc.getCatprealcvalCodigo());
                cxc.setCxcActivo(true);
                cxcDao.crear(cxc);
            }
        }

    }

    public void crearCxcPorServicios(Servicios ser) throws Exception {
        BigDecimal total = ser.getSerTotal();

        if (total.compareTo(BigDecimal.ZERO) == 1) {
            CuentaPorCobrar cxc = new CuentaPorCobrar();
            cxc = buscarCxCPorTipoyClave("SE", ser.getSerCodigo());
            if (cxc != null) {
                if (cxc.getCxcEstado().equals("P")) {
                    cxc.setCxcAnio(java.util.Calendar.getInstance().getTime().getYear()+1900);
                    cxc.setCxcFecha(java.util.Calendar.getInstance().getTime());
                    cxc.setCxcValorTotal(total);
                    cxc.setCxcSaldo(total);
                    cxc.setCxcFechaVencimiento(obtenerFechaFinAnio(java.util.Calendar.getInstance().getTime().getYear()+1900));
                    cxcDao.editar(cxc);
                } else {
                    //ya hay pagos a esta cuenta, no se puede cambiar los valores
                }
            } else {
                cxc = new CuentaPorCobrar();
                cxc.setProCi(ser.getProCi());
                cxc.setCxcAnio(java.util.Calendar.getInstance().getTime().getYear()+1900);
                cxc.setCxcMes(0);
                cxc.setCxcTipo("SE");
                cxc.setCxcReferencia(ser.getProCi().getProCi());
                cxc.setCxcFecha(java.util.Calendar.getInstance().getTime());
                cxc.setCxcValorTotal(total);
                cxc.setCxcSaldo(total);
                cxc.setCxcFechaVencimiento(obtenerFechaFinAnio(java.util.Calendar.getInstance().getTime().getYear()+1900));
                cxc.setCxcEstado("P");
                cxc.setCxcCodRef(ser.getSerCodigo());
                cxc.setCxcActivo(true);
                cxcDao.crear(cxc);
            }
        }
    }

    public CuentaPorCobrar buscarCxCPorTipoyClave(String tipo, Integer codRef) throws Exception {
        return cxcDao.buscarPor2Campos("CuentaPorCobrar", "cxcTipo", tipo, "cxcCodRef", codRef);
    }

    public Date obtenerFechaFinAnio(int anio) throws Exception {
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date date = formatter.parse("12/31/"+anio);
        return date;
    }
}

