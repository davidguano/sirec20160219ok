/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.sirec.ejb.servicios;

import ec.sirec.ejb.entidades.CatastroPredial;
import ec.sirec.ejb.entidades.CatastroPredialAlcabalaValoracion;
import ec.sirec.ejb.facade.CatastroPredialAlcabalaValoracionFacade;
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
public class CatastroPredialAlcabalaValoracionServicio {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @EJB
    private CatastroPredialAlcabalaValoracionFacade catastroPredialAlcabalaValoracionDao;
    private final String ENTIDAD_CATASTRO_PREDIAL_ALCABALA_VALORACION="CatastroPredialAlcabalaValoracion";
    @EJB
    private CuentaPorCobrarServicio cxcServicio;
    
    public String crearCatastroPredialAlcabalaValoracion(CatastroPredialAlcabalaValoracion catastroPredialAlcabalaValoracion) throws Exception {
        catastroPredialAlcabalaValoracionDao.crear(catastroPredialAlcabalaValoracion);
        return "Se ha creado la aplicación" + catastroPredialAlcabalaValoracion.getCatprealcvalCodigo();
    }

    public String editarCatastroPredialAlcabalaValoracion(CatastroPredialAlcabalaValoracion catastroPredialAlcabalaValoracion) throws Exception {
        catastroPredialAlcabalaValoracionDao.editar(catastroPredialAlcabalaValoracion);
        cxcServicio.crearCxcPorImpAlcabala(catastroPredialAlcabalaValoracion);
        return "Se ha modificado la aplicación" + catastroPredialAlcabalaValoracion.getCatprealcvalCodigo();
    }
    
    public CatastroPredialAlcabalaValoracion buscarCatastroPredialAlcabalaValoracion(Integer codigo) throws Exception {
        return catastroPredialAlcabalaValoracionDao.buscarPorCampo(ENTIDAD_CATASTRO_PREDIAL_ALCABALA_VALORACION, "catprealcvalCodigo", codigo);         
    }
    
    public List<Object[]> listarAlcabalaEmitidaXAño(int anio) {
        
        return catastroPredialAlcabalaValoracionDao.listaAlcabalasEmitidas(anio);
    }
}