/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.ejb.servicios;

import ec.sirec.ejb.entidades.Servicios;
import ec.sirec.ejb.facade.ServiciosFacade;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;

/**
 *
 * @author vespinoza
 */
@Stateless
@LocalBean
public class ServiciosServicio {

    @EJB
    private ServiciosFacade serviciosDao;
    private String ENTIDAD_SERVICIOS = "Servicios";
    @EJB
    private CuentaPorCobrarServicio cxcServicio;

    public String crearServicios(Servicios servicios) throws Exception {
        serviciosDao.crear(servicios);
         cxcServicio.crearCxcPorServicios(servicios);
        return "Se ha creado el servicio " + servicios.getSerCodigo();
    }

    public String editarTasa(Servicios servicios) throws Exception {
        serviciosDao.editar(servicios);
       cxcServicio.crearCxcPorServicios(servicios);
        return "Se ha modificado el servicio " + servicios.getSerCodigo();
    }
    
//    public List<Tasa> listarTasa() throws Exception {
//        return tasaDao.listarTodos();
//    }
//
//    public String eliminarTasa(Tasa tasa) throws Exception {
//        tasaDao.eliminarGenerico(ENTIDAD_TASA, "tasCodigo", tasa.getTasCodigo());
//        return "se ha eliminado la Tasa" + tasa;
//    }
    
//    public Tasa cargarObjetoTasa(Integer vtasa) throws Exception {
//        return tasaDao.buscarPorCampo(ENTIDAD_TASA, "tasCodigo", vtasa);         
//    }
    
}
