/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.ejb.servicios;



import ec.sirec.ejb.entidades.RebajaDesvalorizacion;
import ec.sirec.ejb.facade.RebajaDesvalorizacionFacade;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author vespinoza
 */
@Stateless
@LocalBean
public class RebajaDesvalorizacionServicio {

    @EJB
    private RebajaDesvalorizacionFacade rebajaDesvalorizacionDao;
    private String ENTIDAD_REBAJA_DESVALORIZACION = "RebajaDesvalorizacion";

    public RebajaDesvalorizacion obtenerRebajaDesvalorizacion(Integer anio) throws Exception {
        return rebajaDesvalorizacionDao.buscarPorCampo(ENTIDAD_REBAJA_DESVALORIZACION, "anio", anio);
    }

     public List<RebajaDesvalorizacion> listarDesvalorizacion() throws Exception {
        return rebajaDesvalorizacionDao.listarOrdenada(ENTIDAD_REBAJA_DESVALORIZACION, "anio", "desc");
    }
        
    public String crearDesvalorizacion(RebajaDesvalorizacion rebajaDesvalorizacion) throws Exception {
        rebajaDesvalorizacionDao.crear(rebajaDesvalorizacion);
        return "Se ha creado la rebajaDesvalorizacion" + rebajaDesvalorizacion.getRebdesCodigo();
    }

    public String editarDesvalorizacion(RebajaDesvalorizacion rebajaDesvalorizacion) throws Exception {
        rebajaDesvalorizacionDao.editar(rebajaDesvalorizacion);
        return "Se ha modificado la aplicación" + rebajaDesvalorizacion.getRebdesCodigo();
    }

     public String eliminarDesvalorizacion(RebajaDesvalorizacion rebajaDesvalorizacion) throws Exception {
        rebajaDesvalorizacionDao.eliminarGenerico(ENTIDAD_REBAJA_DESVALORIZACION, "rebdesCodigo", rebajaDesvalorizacion.getRebdesCodigo());
        return "se ha eliminado el permiso" + rebajaDesvalorizacion.getRebdesCodigo();
    }
    
    public boolean existeAnio(Integer anio) throws Exception {
        return rebajaDesvalorizacionDao.existePorCampo(ENTIDAD_REBAJA_DESVALORIZACION, "anio", anio);
    } 
     
}
