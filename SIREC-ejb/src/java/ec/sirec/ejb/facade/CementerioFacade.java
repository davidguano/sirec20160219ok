/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.sirec.ejb.facade;

import ec.sirec.ejb.entidades.Catalogo;
import ec.sirec.ejb.entidades.Cementerio;
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
public class CementerioFacade extends AbstractFacade<Cementerio> {
    @PersistenceContext(unitName = "SIREC-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CementerioFacade() {
        super(Cementerio.class);
    }
    public List<Cementerio> listarOccisoPorNombre(String nombre) throws Exception {
     
        String sql = "select c from Cementerio c "
                + " where c.cemNombreOcciso like :nombre ";
        Query q = em.createQuery(sql);
        q.setParameter("nombre", "%" + nombre + "%");
        q.setMaxResults(5);
               return q.getResultList();
    }
    
}
