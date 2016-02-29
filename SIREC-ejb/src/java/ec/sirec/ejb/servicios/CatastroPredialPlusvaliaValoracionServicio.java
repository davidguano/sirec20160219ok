/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.sirec.ejb.servicios;

import ec.sirec.ejb.entidades.CatalogoDetalle;
import ec.sirec.ejb.entidades.CatastroPredial;
import ec.sirec.ejb.entidades.CatastroPredialPlusvaliaValoracion;
import ec.sirec.ejb.facade.CatastroPredialPlusvaliaValoracionFacade;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;

/**
 *
 * @author vespinoza
 */
@Stateless
@LocalBean
public class CatastroPredialPlusvaliaValoracionServicio {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @EJB
    private CatastroPredialPlusvaliaValoracionFacade catastroPredialPlusvaliaValoracionDao;
    private final String ENTIDAD_CATASTRO_PREDIAL_PLUSVALIA_VALORACION="CatastroPredialPlusvaliaValoracion";
    @EJB
    private CuentaPorCobrarServicio cxcServicio;
    public String crearCatastroPredialPlusvaliaValoracion(CatastroPredialPlusvaliaValoracion catastroPredialPlusvaliaValoracion) throws Exception {
        catastroPredialPlusvaliaValoracionDao.crear(catastroPredialPlusvaliaValoracion);
        return "Se ha creado la aplicación" + catastroPredialPlusvaliaValoracion.getCatprepluvalCodigo();
    }

    public String editarCatastroPredialPlusvaliaValoracion(CatastroPredialPlusvaliaValoracion catastroPredialPlusvaliaValoracion) throws Exception {
        catastroPredialPlusvaliaValoracionDao.editar(catastroPredialPlusvaliaValoracion);
        cxcServicio.crearCxcPorImpPlusvalia(catastroPredialPlusvaliaValoracion);
        return "Se ha modificado la aplicación" + catastroPredialPlusvaliaValoracion.getCatprepluvalCodigo();
    }
    
     public CatastroPredialPlusvaliaValoracion buscarPorCatastroPredial(CatastroPredial catastroPredial) throws Exception {
        return catastroPredialPlusvaliaValoracionDao.buscarPorCampo(ENTIDAD_CATASTRO_PREDIAL_PLUSVALIA_VALORACION, "catpreCodigo", catastroPredial);
    }
     
     public CatastroPredialPlusvaliaValoracion buscarCatastroPredialPlusvaliaValoracion(Integer codigo) throws Exception {
        return catastroPredialPlusvaliaValoracionDao.buscarPorCampo(ENTIDAD_CATASTRO_PREDIAL_PLUSVALIA_VALORACION, "catprepluvalCodigo", codigo);         
    }
    
     public List<Object[]> listarPlusvaliaEmitidaXAño(int anio) {
        return catastroPredialPlusvaliaValoracionDao.listaPlusvaliaEmitidas(anio);
    }
     
    public List<Object[]> listarPlusvaliaEmitidaXTipoTarifa(CatalogoDetalle catastroDetalle) {
        return catastroPredialPlusvaliaValoracionDao.listaPlusvaliaXTipoTarifa(catastroDetalle);
    }
    
     public List<Object[]> listarPlusvaliaEmitidaXParroquia(CatalogoDetalle catastroDetalle) {
        return catastroPredialPlusvaliaValoracionDao.listaPlusvaliaXParroquia("catdet_parroquia", catastroDetalle);
    }
     
     public List<Object[]> listarPlusvaliaEmitidaXSector(CatalogoDetalle catastroDetalle) {
        return catastroPredialPlusvaliaValoracionDao.listaPlusvaliaXParroquia("catdet_sector", catastroDetalle);
    } 
}