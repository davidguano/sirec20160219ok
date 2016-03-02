/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.ejb.servicios;

import ec.sirec.ejb.entidades.CatalogoDetalle;
import ec.sirec.ejb.entidades.CatastroPredialValoracion;
import ec.sirec.ejb.entidades.Cementerio;
import ec.sirec.ejb.entidades.Patente15xmilValoracion;
import ec.sirec.ejb.entidades.PatenteValoracion;
import ec.sirec.ejb.entidades.RecaudacionCab;
import ec.sirec.ejb.entidades.RecaudacionDet;
import ec.sirec.ejb.facade.CuentaPorCobrarFacade;
import ec.sirec.ejb.facade.RecaudacionCabFacade;
import ec.sirec.ejb.facade.RecaudacionDetFacade;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author dguano
 */
@Stateless
@LocalBean
public class RecaudacionServicio {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @EJB
    private RecaudacionCabFacade recCabeceraDao;
    @EJB
    private RecaudacionDetFacade recDetalleDao;
    @EJB
    private CatalogoDetalleServicio catDetServicio;
    @EJB
    private CementerioServicio cemServicio;

    public void crearCabecera(RecaudacionCab vcabecera) throws Exception {
        recCabeceraDao.crear(vcabecera);
    }

    public void editarCabecera(RecaudacionCab vcabecera) throws Exception {
        recCabeceraDao.editar(vcabecera);
    }

    public void crearDetalle(RecaudacionDet vdetalle) throws Exception {
        recDetalleDao.crear(vdetalle);
    }

    public void editarDetalle(RecaudacionDet vdetalle) throws Exception {
        recDetalleDao.editar(vdetalle);
    }

    public List<RecaudacionCab> listaRecaudacionesPorPropietario(String vci) throws Exception {
        return recCabeceraDao.listarPorCampoOrdenada("RecaudacionCab", "proCi.proCi", vci, "recFecha", "desc");
    }

    public List<RecaudacionDet> listaDetallesARecaudarPorCiAnio(String vci, Integer vAnio) throws Exception {
        return recDetalleDao.listaDetallesARecaudarPorCiAnio(vci, vAnio);
    }

    public List<RecaudacionDet> listaDetallesARecaudados(RecaudacionCab vrec) throws Exception {
        return recDetalleDao.listarPorCampoOrdenada("RecaudacionDet", "recCodigo", vrec, "recdetCodigo", "asc");
    }

    public BigDecimal obtenerTotalRecaudacion(List<RecaudacionDet> lstDets) throws Exception {
        BigDecimal t = BigDecimal.ZERO;
        if (!lstDets.isEmpty()) {
            for (RecaudacionDet det : lstDets) {
                if (det.getActivo()) {
                    if (det.getRecdetValor() != null) {
                        t = t.add(det.getRecdetValor());
                    }
                }
            }
        }
        return t;
    }

    public String guardarRecaudacion(RecaudacionCab vcabecera, List<RecaudacionDet> lstDets) throws Exception {
        String s = "";
        vcabecera.setRecTotal(obtenerTotalRecaudacion(lstDets));
        vcabecera.setRecFecha(java.util.Calendar.getInstance().getTime());
        vcabecera.setRecEstado("R");
        if (vcabecera.getRecTotal() != BigDecimal.ZERO) {
            this.crearCabecera(vcabecera);
            if (!lstDets.isEmpty()) {
                List<RecaudacionDet> lstActivos = new ArrayList<RecaudacionDet>();
                for (RecaudacionDet det : lstDets) {
                    if (det.getActivo()) {
                        det.setRecCodigo(vcabecera);
                        det.setRecdetBaja(false);
                        if (det.getRecdetValor() == null) {
                            det.setRecdetValor(BigDecimal.ZERO);
                        }
                        this.recDetalleDao.crear(det);
                        if (det.getRecdetTipo().equals("CE")) {
                            this.actualizarFechaContratoCementerio(det.getRecdetCodref(), det.getNumAniosCem());
                        }
                        lstActivos.add(det);
                    }
                }
                recDetalleDao.actualizarCuentasPorCobrar(lstActivos);
                s = "Recaudacion correcta";
            } else {
                s = "Nada para recaudar.";
            }
        }
        return s;
    }

    public void editarRecaudacion(RecaudacionCab vcabecera, List<RecaudacionDet> lstDets) throws Exception {
        vcabecera.setRecTotal(obtenerTotalRecaudacion(lstDets));
        this.editarCabecera(vcabecera);
        if (!lstDets.isEmpty()) {
            for (RecaudacionDet det : lstDets) {
                if (det.getActivo()) {
                    if (det.getRecdetValor() == null) {
                        det.setRecdetValor(BigDecimal.ZERO);
                    }
                    this.recDetalleDao.editar(det);
                }
            }
        }
    }

    public List<Integer> listaAnios() throws Exception {
        List<Integer> lst = new ArrayList<Integer>();
        List<CatalogoDetalle> lstCatDet = new ArrayList<CatalogoDetalle>();
        lstCatDet = catDetServicio.listarPorNemonicoCatalogo("ANIOS");
        if (!lstCatDet.isEmpty()) {
            for (CatalogoDetalle det : lstCatDet) {
                lst.add(Integer.valueOf(det.getCatdetTexto()));
            }
        }
        return lst;
    }

    public Cementerio buscarCementerioPorId(Integer idCem) throws Exception {
        return cemServicio.buscarCementerioPorId(idCem);
    }

    public void actualizarFechaContratoCementerio(Integer idCem, int numAnios) throws Exception {
        {
            DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            int dia = java.util.Calendar.getInstance().getTime().getDate();
            int mes = java.util.Calendar.getInstance().getTime().getMonth() + 1;
            int anio = java.util.Calendar.getInstance().getTime().getYear() + 1900 + numAnios;
            Date date = formatter.parse(dia + "/" + mes + "/" + anio);
            Cementerio cem = this.buscarCementerioPorId(idCem);
            if (cem != null) {
                cem.setCemFechaFinContrato(date);
                cemServicio.editarCementerio(cem);
            }

        }
    }

    public String obtenerVistaPrevia(RecaudacionDet det) throws Exception {
        String s = "";
        if (det.getRecdetTipo().equals("PR")) {
            CatastroPredialValoracion pv = recDetalleDao.buscarCatastroPredialValoracionPorCodigo(det.getRecdetCodref());

            s = "<table style='background:#FFFFFF;'><tr><th style='color:red'><b>RUBROS DE TITULO PREDIAL URBANO</b></th></tr>";
            s = s + "<tr><td><b>Avaluo Terreno:</b>" + pv.getCatprevalAvaluoTerr() + "</tr></td>";
            s = s + "<tr><td><b>Avaluo Edificacion:</b>" + pv.getCatprevalAvaluoEdif() + "</tr></td>";
            s = s + "<tr><td><b>Valor de la propiedad:</b>" + pv.getCatprevalAvaluoTot() + "</tr></td>";
            s = s + "<tr><td><b>Rebaja Hipotecaria:</b>" + "0" + "</tr></td>";
            s = s + "<tr><td><b>Base Imponible:</b>" + pv.getCatprevalBaseImponible() + "</tr></td>";
            s = s + "<tr><td><b>Contribucion por Mejoras:</b>" + pv.getCatprevalContribMejora() + "</tr></td>";
            s = s + "<tr><td><b>Impuesto Predial Urbano:</b>" + pv.getCatprevalImpuesto() + "</tr></td>";
            s = s + "<tr><td><b>Tasa administrativa:</b>" + pv.getCatprevalTasaAdm() + "</tr></td>";
            s = s + "<tr><td><b>Impuesto Solar no edificado:</b>" + pv.getCatprevalSolarNoedificado() + "</tr></td>";
            s = s + "<tr><td><b>Construccion Obsoleta:</b>" + 0 + "</tr></td>";
            s = s + "<tr><td><b>Tasa de Bomberos:</b>" + pv.getCatprevalBomberos() + "</tr></td>";
            s = s + "<tr><td><b>Valor Emitido:</b>" + det.getRecdetValorInicial() + "</tr></td>";
            s = s + "<tr><td><b>Intereses:</b>" + det.getRecdetPorDesc() + "</tr></td>";
            s = s + "<tr><td><b>TOTAL:</b>" + det.getRecdetValor() + "</tr></td>";
        } else if (det.getRecdetTipo().equals("PA")) {
            PatenteValoracion patv=recDetalleDao.buscarPatenteValoracionPorCodigo(det.getRecdetCodref());
            s = "<table style='background:#FFFFFF;'><tr><th style='color:red'><b>RUBROS DE PATENTE MUNICIPAL</b></th></tr>";
            s = s + "<tr><td><b>Clave Catastral:</b>" + patv.getPatCodigo().getCatpreCodigo().getClaveCatastral()+ "</tr></td>";
            s = s + "<tr><td><b>Patente:</b>AE-MPM-" + patv.getPatCodigo().getPatCodigo()+ "</tr></td>";
            s = s + "<tr><td><b>Base Imponible:</b>" + patv.getPatvalPatrimonio()+ "</tr></td>";
            s = s + "<tr><td><b>Impuesto Patente:</b>" + patv.getPatvalImpuesto()+ "</tr></td>";
            s = s + "<tr><td><b>Tasa de Bomberos:</b>" + patv.getPatvalTasaBomb()+ "</tr></td>";
            s = s + "<tr><td><b>Tasa de Procesamiento:</b>" + patv.getPatvalTasaProc()+ "</tr></td>";
            s = s + "<tr><td><b>Subtotal:</b>" + patv.getPatvalSubtotal()+ "</tr></td>";
            s = s + "<tr><td><b>Deducciones:</b>" + patv.getPatvalDeducciones()+ "</tr></td>";
            s = s + "<tr><td><b>TOTAL:</b>" + patv.getPatvalTotal()+ "</tr></td>";
        } else if (det.getRecdetTipo().equals("PM")) {
            Patente15xmilValoracion pat15v=recDetalleDao.buscarPatente15x1000ValoracionPorCodigo(det.getRecdetCodref());
            s = "<table style='background:#FFFFFF;'><tr><th style='color:red'><b>RUBROS DE PATENTE 1.5x1000</b></th></tr>";
            s = s + "<tr><td><b>Clave Catastral:</b>" + pat15v.getPatCodigo().getCatpreCodigo().getClaveCatastral()+ "</tr></td>";
            s = s + "<tr><td><b>Patente:</b>AE-MPM-" + pat15v.getPatCodigo().getPatCodigo()+ "</tr></td>";
            s = s + "<tr><td><b>Base Imponible:</b>" + pat15v.getPat15valBaseImponible()+ "</tr></td>";
            s = s + "<tr><td><b>Impuesto:</b>AE-MPM-" + pat15v.getPat15valImpuesto()+ "</tr></td>";
            
        }else if (det.getRecdetTipo().equals("AL")) {

        } else if (det.getRecdetTipo().equals("PL")) {

        } else if (det.getRecdetTipo().equals("SE")) {

        } else if (det.getRecdetTipo().equals("CE")) {

        }
        s = s + "</table>";
        return s;
    }
}
