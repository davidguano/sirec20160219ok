/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.sirec.ejb.facade;

import ec.sirec.ejb.entidades.CatastroPredial;
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
public class CatastroPredialFacade extends AbstractFacade<CatastroPredial> {
    @PersistenceContext(unitName = "SIREC-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CatastroPredialFacade() {
        super(CatastroPredial.class);
    }
    public List<CatastroPredial> listarPorClaveCatastralContiene(String vvalor1) throws Exception {
        String sql = "select e from CatastroPredial e where CONCAT(e.catpreCodNacional,e.catpreCodLocal) like :vvalor1 order by e.catpreCodigo asc";
        Query q = getEntityManager().createQuery(sql);
        q.setParameter("vvalor1", "%"+vvalor1+"%");
        return q.getResultList();

    }
     public List<CatastroPredial> listarPorClaveManzana13Digitos(String vvalor1) throws Exception {
        String sql = "select e from CatastroPredial e where CONCAT(e.catpreCodNacional,e.catpreCodLocal) like :vvalor1 order by e.catpreCodigo asc";
        Query q = getEntityManager().createQuery(sql);
        q.setParameter("vvalor1", vvalor1+"%");
        return q.getResultList();

    }
     
     public List<CatastroPredial> listarPorAlcabalasPlusvaliasExistentes(Integer anio) throws Exception {
        String sql = "select distinct p from CatastroPredial p, CatastroPredialAlcabalaValoracion al,"
                + " CatastroPredialPlusvaliaValoracion pl "
                + " where (p.catpreCodigo=al.catpreCodigo or "
                + " p.catpreCodigo=pl.catpreCodigo) and "
                + " al.catprealcvalAnio=:anio and pl.catprepluvalAnio=:anio order by p.catpreCodigo asc";
        Query q = getEntityManager().createQuery(sql);
        q.setParameter("anio", anio);
        return q.getResultList();

    }
     
// SELECT distinct p.catpre_codigo
// FROM sirec.catastro_predial p, sirec.catastro_predial_alcabala_valoracion al, sirec.catastro_predial_plusvalia_valoracion pl
// where (p.catpre_codigo=al.catpre_codigo or
// p.catpre_codigo=pl.catpre_codigo) and 
// al.catprealcval_anio=2016 and pl.catprepluval_anio=2016 group by p.catpre_codigo;        
     
}
