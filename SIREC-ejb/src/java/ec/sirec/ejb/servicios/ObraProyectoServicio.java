/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.ejb.servicios;


import ec.sirec.ejb.entidades.ObraProyecto;
import ec.sirec.ejb.facade.ObraProyectoFacade;
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
public class ObraProyectoServicio {

  
    @EJB
    private ObraProyectoFacade obraProyectoDao;
    private final String ENTIDAD_OBRA_PROYECTO = "ObraProyecto";


    public String crearObraProyecto(ObraProyecto obraProyecto) throws Exception {
        obraProyectoDao.crear(obraProyecto);
         return "Se ha guardado la obra/Proyecto excitosamente!";
    }
    public void editarObraProyecto(ObraProyecto obraProyecto) throws Exception {
        obraProyectoDao.editar(obraProyecto);
    }
    
    public String eliminarObraProyecto(ObraProyecto obraProyecto) throws Exception {
        obraProyectoDao.eliminar(obraProyecto);
         return "Se ha eliminado el registro!";
    }
    
    public List<ObraProyecto> listarObrasProyectos() throws Exception {
        return obraProyectoDao.listarTodos();
    }
         
}
