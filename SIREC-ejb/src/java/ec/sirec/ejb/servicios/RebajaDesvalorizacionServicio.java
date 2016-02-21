/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.ejb.servicios;



import ec.sirec.ejb.entidades.RebajaDesvalorizacion;
import ec.sirec.ejb.facade.RebajaDesvalorizacionFacade;
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

}
