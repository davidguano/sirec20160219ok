/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.sirec.ejb.servicios;

import ec.sirec.ejb.entidades.ConvenioPago;
import ec.sirec.ejb.entidades.CuentaPorCobrar;
import ec.sirec.ejb.facade.ConvenioPagoDetFacade;
import ec.sirec.ejb.facade.ConvenioPagoFacade;
import ec.sirec.ejb.facade.CuentaPorCobrarFacade;
import java.math.BigDecimal;
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
    
    public List<CuentaPorCobrar> listarCuentasPendientesPorCi(String vci) throws Exception {
        return cxcDao.listarPor2CamposOrdenada("CuentaPorCobrar", "proCi.proCi", vci, "cxcEstado", "P", "cxcFecha", "asc");
    }
    
    public BigDecimal obtenerTotalAConvenir(List<CuentaPorCobrar> lstCxc) throws Exception{
        BigDecimal bd=new BigDecimal("0");
        for(CuentaPorCobrar cxc:lstCxc){
            if(cxc.getSeleccionado()){
                bd.add(cxc.getCxcValorTotal());
            }
        }
        return bd;
    }
    public void guardarConvenioCab(ConvenioPago cp) throws Exception{
        convenioPagoCabDao.crear(cp);
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
