/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.ejb.facade;

import ec.sirec.ejb.entidades.RebajaDesvalorizacion;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author new
 */
@Stateless
public class RebajaDesvalorizacionFacade extends AbstractFacade<RebajaDesvalorizacion> {
    @PersistenceContext(unitName = "SIREC-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RebajaDesvalorizacionFacade() {
        super(RebajaDesvalorizacion.class);
    }
    
}
