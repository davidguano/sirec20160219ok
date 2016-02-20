/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.sirec.ejb.servicios;

import ec.sirec.ejb.entidades.Baja;
import ec.sirec.ejb.entidades.RecaudacionDet;
import ec.sirec.ejb.facade.BajaFacade;
import ec.sirec.ejb.facade.RecaudacionDetFacade;
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
public class BajaServicio {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @EJB
    private BajaFacade bajaDao;
    
    @EJB
    private RecaudacionDetFacade recDetalleDao;
    
    
    public List<RecaudacionDet> listaDetallesARecaudadosPorTipoyCiPropietario(String tipo, String ci) throws Exception {
        return recDetalleDao.listarPor2CamposOrdenada("RecaudacionDet", "recdetTipo", tipo, "cxcCodigo.proCi.proCi",ci ,"recdetCodigo", "asc");
    }
    
    public List<RecaudacionDet> listaDetallesARecaudadosPorTipoyClave(String tipo, String clave) throws Exception {
        return recDetalleDao.listarPor1Campo1ContieneOrdenada("RecaudacionDet", "recdetTipo", tipo, "recdetReferencia",clave ,"recdetCodigo", "asc");
    }
    public List<RecaudacionDet> listaDetallesARecaudadosPorTipoyApellidosProp(String tipo, String apellidos) throws Exception {
        return recDetalleDao.listarPor1Campo1ContieneOrdenada("RecaudacionDet", "recdetTipo", tipo, "cxcCodigo.proCi.proApellidos",apellidos ,"recdetCodigo", "asc");
    }
    
    
    public List<Baja> listaBajasPorTipoyCiPropietario(String tipo, String ci) throws Exception {
        return bajaDao.listarPor2CamposOrdenada("Baja", "recdetCodigo.recdetTipo", tipo, "recdetCodigo.cxcCodigo.proCi.proCi",ci ,"recdetCodigo", "asc");
    }
    
    public List<Baja> listaBajasPorTipoyClave(String tipo, String clave) throws Exception {
        return bajaDao.listarPor1Campo1ContieneOrdenada("Baja", "recdetCodigo.recdetTipo", tipo, "recdetCodigo.recdetReferencia",clave ,"recdetCodigo", "asc");
    }
    public List<Baja> listaBajasPorTipoyApellidosProp(String tipo, String apellidos) throws Exception {
        return bajaDao.listarPor1Campo1ContieneOrdenada("Baja", "recdetCodigo.recdetTipo", tipo, "recdetCodigo.cxcCodigo.proCi.proApellidos",apellidos ,"recdetCodigo", "asc");
    }
    
    public void guardarBaja(Baja vbaja) throws Exception{
        vbaja.setBajEstado("B");
        vbaja.setBajFecha(java.util.Calendar.getInstance().getTime());
        vbaja.setBajNombreArchivo(bajaDao.obtenerNombreBajaPorTipo(vbaja.getRecdetCodigo().getRecdetTipo(), vbaja.getBajTipo()));
        bajaDao.crear(vbaja);
        vbaja.getRecdetCodigo().setRecdetBaja(true);
        recDetalleDao.editar(vbaja.getRecdetCodigo());
        //pendiente definir si la cuenta por cobrar se vuelve a activar
    }
}
