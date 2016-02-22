/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.ejb.servicios;

import ec.sirec.ejb.entidades.Mejora;
import ec.sirec.ejb.entidades.Servicios;
import ec.sirec.ejb.facade.MejoraFacade;
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
public class MejoraServicio {

    @EJB
    private MejoraFacade mejoraDao;
    private String ENTIDAD_MEJORA = "Mejora";
//    @EJB
//    private CuentaPorCobrarServicio cxcServicio;

    public String crearMejora(Mejora mejora) throws Exception {
        mejoraDao.crear(mejora);
        // cxcServicio.crearCxcPorServicios(mejora);
        return "Se ha creado el mejora " + mejora.getMejCodigo();
    }

    public String editarMejora(Mejora mejora) throws Exception {
        mejoraDao.editar(mejora);
      // cxcServicio.crearCxcPorServicios(mejora);
        return "Se ha modificado el mejora " + mejora.getMejCodigo();
    }

    public List<Mejora> listarMejoras() throws Exception {
        return mejoraDao.listarTodos();
    }
    
}
