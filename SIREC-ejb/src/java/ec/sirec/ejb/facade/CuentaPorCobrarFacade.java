/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.sirec.ejb.facade;

import ec.sirec.ejb.entidades.CuentaPorCobrar;
import java.math.BigDecimal;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author DAVID GUAN
 */
@Stateless
public class CuentaPorCobrarFacade extends AbstractFacade<CuentaPorCobrar> {
    @PersistenceContext(unitName = "SIREC-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CuentaPorCobrarFacade() {
        super(CuentaPorCobrar.class);
    }
    public BigDecimal obtenerTotalDeduccionesPorValoracionPredial(Integer catprevalCodigo) throws Exception {
        String sql = " select sum(e.cpvalextValor) from CpValoracionExtras e, AdicionalesDeductivos ad where e.adidedCodigo=ad and ad.adidedTipo='D' and e.catprevalCodigo.catprevalCodigo=:catprevalCodigo ";                        
        Query q = getEntityManager().createQuery(sql);
        q.setParameter("catprevalCodigo", catprevalCodigo);
        BigDecimal b=new BigDecimal("0");
        if(q.getSingleResult()!=null){
            b = (BigDecimal)q.getSingleResult();
        } 
        return b;
    }
    public BigDecimal obtenerTotalExencionesPorValoracionPredial(Integer catprevalCodigo) throws Exception {
        String sql = " select sum(e.cpvalextValor) from CpValoracionExtras e, AdicionalesDeductivos ad where e.adidedCodigo=ad and ad.adidedTipo='E' and e.catprevalCodigo.catprevalCodigo=:catprevalCodigo ";                        
        Query q = getEntityManager().createQuery(sql);
        q.setParameter("catprevalCodigo", catprevalCodigo);
        BigDecimal b=new BigDecimal("0");
        if(q.getSingleResult()!=null){
            b = (BigDecimal)q.getSingleResult();
        } 
        return b;
    }
    public BigDecimal obtenerTotalRecargosPorValoracionPredial(Integer catprevalCodigo) throws Exception {
        String sql = " select sum(e.cpvalextValor) from CpValoracionExtras e, AdicionalesDeductivos ad where e.adidedCodigo=ad and ad.adidedTipo='R' and ad.adidedCodigo not in (98) and  e.catprevalCodigo.catprevalCodigo=:catprevalCodigo ";                        
        Query q = getEntityManager().createQuery(sql);
        q.setParameter("catprevalCodigo", catprevalCodigo);
        BigDecimal b=new BigDecimal("0");
        if(q.getSingleResult()!=null){
            b = (BigDecimal)q.getSingleResult();
        } 
        return b;
    }
}
