/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.sirec.ejb.facade;

import ec.sirec.ejb.entidades.CatastroPredial;
import ec.sirec.ejb.entidades.ObraProyecto;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author dguano
 */
@Stateless
public class ObraProyectoFacade extends AbstractFacade<ObraProyecto> {
    @PersistenceContext(unitName = "SIREC-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ObraProyectoFacade() {
        super(ObraProyecto.class);
    }
    
    
  public ObraProyecto buscarValorMejora(CatastroPredial vvalor1) throws Exception {
        String sql = " select p from Mejora m, ObraProyecto p "
                + " where m.catpreCodigo=:vvalor1 and "
                + " m.obrCodigo=p.obrCodigo";                        
        Query q = getEntityManager().createQuery(sql);
        q.setParameter("vvalor1", vvalor1);
        List<ObraProyecto> resultado = q.getResultList();
        if (resultado.size() > 0) {
            return (ObraProyecto) resultado.get(0);
        } else {
            return null;
        }                      
       // postgres 
//       SELECT *
//  FROM sirec.mejora m, sirec.obra_proyecto p
//  where m.catpre_codigo='21' and
//  m.obr_codigo= p.obr_codigo;       
    }
    
  public List <ObraProyecto> listarLocales() throws Exception {
        String sql = "SELECT p " +
"  FROM ObraProyecto p, CatalogoDetalle d " +
"  where p.catdetTipoObra=d.catdetCodigo and " +
"  d.catdetCod='L'";
        Query q = em.createQuery(sql);       
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            return q.getResultList();
        }
    }
  
//  SELECT obr_codigo, con_codigo, obr_descripcion, catdet_tipo_obra, obr_num_decreto
//  FROM sirec.obra_proyecto p, sirec.catalogo_detalle d
//  where p.catdet_tipo_obra=d.catdet_codigo and
//  d.catdet_cod='L';
}
