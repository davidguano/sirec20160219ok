/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.ejb.servicios;


import ec.sirec.ejb.entidades.CatastroPredialAlcabalaValoracion;
import ec.sirec.ejb.entidades.CpAlcabalaValoracionExtras;
import ec.sirec.ejb.facade.CpAlcabalaValoracionExtrasFacade;
import java.math.BigDecimal;
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
public class CpAlcabalaValoracionExtrasServicio {

    @EJB
    private CpAlcabalaValoracionExtrasFacade cpAlcabalaValoracionExtrasDao;
    private String ENTIDAD_CPALCABALA_VALORACION_EXTRAS = "CpAlcabalaValoracionExtras";
    
    
     public String crearCpAlcabalaValoracionExtras(CpAlcabalaValoracionExtras cpAlcabalaValoracionExtras) throws Exception {
        cpAlcabalaValoracionExtrasDao.crear(cpAlcabalaValoracionExtras);
        return "Se ha creado el CpAlcabalaValoracionExtras" + cpAlcabalaValoracionExtras.getCatprealcvalCodigo();
    }

    public String editarCpAlcabalaValoracionExtras(CpAlcabalaValoracionExtras cpAlcabalaValoracionExtras) throws Exception {
        cpAlcabalaValoracionExtrasDao.editar(cpAlcabalaValoracionExtras);
        return "Se ha modificado el CpAlcabalaValoracionExtras" + cpAlcabalaValoracionExtras.getCatprealcvalCodigo();
    }
    
    public BigDecimal obteneValorTipoAdicionalAlcabala(Integer codigoAl, String TipoImp, String tipo) throws Exception {
        return cpAlcabalaValoracionExtrasDao.obteneValorTipoAdicionalAlcabala(codigoAl, TipoImp, tipo);
    }
    
    public List<CpAlcabalaValoracionExtras> listarCpValoracionExtrasXALCA(CatastroPredialAlcabalaValoracion catPreALCAValCodigo) throws Exception {
     return cpAlcabalaValoracionExtrasDao.listarPorCampoOrdenada(ENTIDAD_CPALCABALA_VALORACION_EXTRAS,"catprealcvalCodigo", catPreALCAValCodigo,"cpalcvalextCodigo", "asc");
    }
    
     public String eliminarCpValoracionExtrar(CatastroPredialAlcabalaValoracion catPreALCAValCodigo) throws Exception {
        List<CpAlcabalaValoracionExtras> listarCp = listarCpValoracionExtrasXALCA(catPreALCAValCodigo);
          for (int i = 0; i < listarCp.size(); i++) {  
              cpAlcabalaValoracionExtrasDao.eliminarGenerico(ENTIDAD_CPALCABALA_VALORACION_EXTRAS, "cpalcvalextCodigo", listarCp.get(i).getCpalcvalextCodigo());
          }                
        return "se han eliminado los CpValALCA";
    }
}