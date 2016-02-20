/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.ejb.servicios;

import ec.sirec.ejb.entidades.Coactiva;
import ec.sirec.ejb.entidades.CuentaPorCobrar;
import ec.sirec.ejb.entidades.SegUsuario;
import ec.sirec.ejb.facade.CoactivaFacade;
import ec.sirec.ejb.facade.CuentaPorCobrarFacade;
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
public class CoactivaServicio {

    @EJB
    private CoactivaFacade coactivaDao;

    @EJB
    private CuentaPorCobrarFacade cxcDao;
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    public List<CuentaPorCobrar> listarCuentasPendientesPorCi(String vci) throws Exception {
        return cxcDao.listarPor2CamposOrdenada("CuentaPorCobrar", "proCi.proCi", vci, "cxcEstado", "P", "cxcFecha", "asc");
    }

    public List<Coactiva> listarCoactivasPorPropietario(String vci) throws Exception {
        return coactivaDao.listarPorCampoOrdenada("Coactiva", "cxcCodigo.proCi.proCi", vci, "coaFecha", "asc");
    }

    public void guardarCoactivas(List<CuentaPorCobrar> listaPendientes, SegUsuario usu) throws Exception {
        if (!listaPendientes.isEmpty()) {
            Date fecha = java.util.Calendar.getInstance().getTime();
            for (CuentaPorCobrar cxc : listaPendientes) {
                if (cxc.getSeleccionado()) {
                    Coactiva coa = new Coactiva();
                    coa.setCoaAnio(cxc.getCxcAnio());
                    coa.setCoaEstado("C");
                    coa.setCoaFecha(fecha);
                    coa.setCoaTotalDeuda(cxc.getCxcValorTotal());
                    coa.setCxcCodigo(cxc);
                    coa.setUsuIdentificacion(usu);
                    coactivaDao.crear(coa);
                    coactivaDao.actualizarCuentasPorCobrar(coa);
                }
            }
        }
    }

    public void activarNotificacion(Coactiva coa, SegUsuario usu) {
        if (coa.getCoaNotificacion1()) {
            if (coa.getCoaNotificacion2()) {
                if (coa.getCoaNotificacion3()) {
                    if (coa.getCoaNotificacion()) {
                        //COACTIVA PROCESADA
                    } else {
                        coa.setCoaEstado("3");
                        coa.setCoaFechaUltNot(java.util.Calendar.getInstance().getTime());
                        coa.setUsuIdentificacion(usu);
                        coactivaDao.editar(coa);
                    }
                } else {
                    coa.setCoaEstado("2");
                    coa.setCoaFechaUltNot(java.util.Calendar.getInstance().getTime());
                    coa.setUsuIdentificacion(usu);
                    coactivaDao.editar(coa);
                }
            } else {
                coa.setCoaEstado("1");
                coa.setCoaFechaUltNot(java.util.Calendar.getInstance().getTime());
                coa.setUsuIdentificacion(usu);
                coactivaDao.editar(coa);
            }
        } else {
            //NO SE ACTIVA LAS NOTIFICACIONES
        }
    }
}
