/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.sirec.ejb.facade;

import ec.sirec.ejb.entidades.CpAlcabalaValoracionExtras;
import java.math.BigDecimal;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author dguano
 */
@Stateless
public class CpAlcabalaValoracionExtrasFacade extends AbstractFacade<CpAlcabalaValoracionExtras> {
    @PersistenceContext(unitName = "SIREC-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CpAlcabalaValoracionExtrasFacade() {
        super(CpAlcabalaValoracionExtras.class);
    }
    
    
    public BigDecimal obteneValorTipoAdicionalAlcabala(Object vvalor1, Object vvalor2, Object vvalor3) throws Exception {
        String sql = " select sum(e.cpalcvalextValor) "
                + " from CpAlcabalaValoracionExtras e, AdicionalesDeductivos d, CatastroPredialAlcabalaValoracion a "
                + " where a.catprealcvalCodigo=:vvalor1 and "
                + " a.catprealcvalCodigo=e.catprealcvalCodigo and "          
                + " d.adidedCodigo=e.adidedCodigo and "          
                + " d.adidedTipoImpuesto=:vvalor2 and "                        
                + " d.adidedTipo=:vvalor3";                        
        Query q = getEntityManager().createQuery(sql);
        q.setParameter("vvalor1", vvalor1);  // codigo alcabala
        q.setParameter("vvalor2", vvalor2);  //  tipo imp
        q.setParameter("vvalor3", vvalor3);   // tipo de adicional
        return (BigDecimal)q.getSingleResult();
    } 
    
//    SELECT  sum(e.cpalcvalext_valor)
//FROM sirec.cp_alcabala_valoracion_extras e, sirec.adicionales_deductivos d, sirec.catastro_predial_alcabala_valoracion a
//where a.catprealcval_codigo=14 and 
//a.catprealcval_codigo=e.catprealcval_codigo and
//d.adided_codigo=e.adided_codigo and
//d.adided_tipo_impuesto='AL' and
//d.adided_tipo='D'
}
