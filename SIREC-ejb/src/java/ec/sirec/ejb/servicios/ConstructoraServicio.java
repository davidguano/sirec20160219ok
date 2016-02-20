/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.ejb.servicios;

import ec.sirec.ejb.entidades.CatalogoDetalle;
import ec.sirec.ejb.entidades.Constructora;
import ec.sirec.ejb.facade.ConstructoraFacade;
import ec.sirec.ejb.util.Utilitarios;
import java.util.Calendar;
import java.util.Date;
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
public class ConstructoraServicio {

  
    @EJB
    private ConstructoraFacade constructoraDao;
    private final String ENTIDAD_CONSTRUCTORA = "Constructora";

    @EJB
    private CatalogoDetalleServicio catalogoDetServicio;

    public void crearConstructora(Constructora constructora) throws Exception {
        constructoraDao.crear(constructora);
    }
    public void editarConstructora(Constructora constructora) throws Exception {
        constructoraDao.editar(constructora);
    }
    
    public String eliminarConstructora(Constructora constructora) throws Exception {
        constructoraDao.eliminar(constructora);
         return "Se ha eliminado el registro!";
    }
    
     public List<CatalogoDetalle> listarCiudadesPorTexto(String texto) throws Exception {
        return catalogoDetServicio.listarPorNemonicoyTextoContiene("CIUDADES", texto);
    }
     
     public String esCedulaRucValida(String vcedula, boolean flagEditar) throws Exception {
        String c = "";
        if (vcedula.substring(0, 4).equals("9999")) {
            return "valida";
        }
        if (vcedula.length() == 13) {
            c = vcedula.substring(0, 10);
        } else {
            c = vcedula;
        }
        if (existeContructoraPorCedula(c)) {
            if(flagEditar){
                return "valida";
            }else{
                return "Ya existe esta cedula";
            }
            
        } else {
            if (Utilitarios.validarCedula(c)) {
                return "valida";
            } else {
                return "Cedula no valida";
            }
        }

    }
     
     public boolean existeContructoraPorCedula(String vcedula) throws Exception {        
        return constructoraDao.existePorCampo(ENTIDAD_CONSTRUCTORA, "conIdentificacion", vcedula);
    }
     
     public boolean esFechaNacimientoValida(Date vfechaNac) throws Exception {
        if (vfechaNac != null) {
            Calendar fn = java.util.Calendar.getInstance();
            fn.setTime(vfechaNac);
            Calendar fa = java.util.Calendar.getInstance();
            if ((fa.getTimeInMillis() - fn.getTimeInMillis()) > 360000) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
     
     public List<Constructora> listarConstructoraTodos() throws Exception {
        return constructoraDao.listarOrdenada(ENTIDAD_CONSTRUCTORA, "conApellidos", "asc");
    }
     
     public List<Constructora> listarConstructoraXTipo(String tipo) throws Exception {
        return constructoraDao.listarPorCampoOrdenada(ENTIDAD_CONSTRUCTORA, "conTipo", tipo , "conApellidos", "asc");
    }
}
