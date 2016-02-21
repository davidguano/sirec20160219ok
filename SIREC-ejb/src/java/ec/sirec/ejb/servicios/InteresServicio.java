/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.sirec.ejb.servicios;

import ec.sirec.ejb.entidades.Interes;
import ec.sirec.ejb.facade.InteresFacade;
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
public class InteresServicio {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @EJB
    private InteresFacade interesDao;
    
    public boolean existeInteres(int anio, int mes) throws Exception{
        return interesDao.existePor2Campos("Interes", "intAnio", anio, "intMes", mes);
    }
    public void crearInteres(Interes inte) throws Exception{
        interesDao.crear(inte);
    }
     public void editarInteres(Interes inte) throws Exception{
        interesDao.editar(inte);
    }
     public List<Interes> listarInteres() throws Exception {
         return interesDao.listarOrdenada("Interes", "intAnio", "asc");
     }
}
