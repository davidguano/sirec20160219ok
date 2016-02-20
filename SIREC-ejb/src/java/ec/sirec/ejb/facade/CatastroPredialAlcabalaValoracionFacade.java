/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.sirec.ejb.facade;

import ec.sirec.ejb.entidades.CatastroPredialAlcabalaValoracion;
import java.util.ArrayList;
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
public class CatastroPredialAlcabalaValoracionFacade extends AbstractFacade<CatastroPredialAlcabalaValoracion> {
    @PersistenceContext(unitName = "SIREC-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CatastroPredialAlcabalaValoracionFacade() {
        super(CatastroPredialAlcabalaValoracion.class);
    }
    
    
    
    public List<Object[]> listaAlcabalasEmitidas(int anio) {
        List<Object[]> lista = new ArrayList<Object[]>();                                               
        String sql = " SELECT a.catprealcval_codigo,\n" +
"concat(cp.catpre_cod_nacional, cp.catpre_cod_local) clave_catastral,\n" +
"	a.catprealcval_anio,\n" +
"	(SELECT cd.catdet_texto parroquia from sirec.catalogo_detalle cd\n" +
"	where cd.catdet_codigo=cp.catdet_parroquia),\n" +
"	(SELECT cd.catdet_texto sector from sirec.catalogo_detalle cd\n" +
"	where cd.catdet_codigo=cp.catdet_sector),\n" +
"	(select pro.pro_direccion direccion from sirec.propietario pro, sirec.propietario_predio pp\n" +
"	where pro.pro_ci=pp.pro_ci and pp.catpre_codigo=cp.catpre_codigo),\n" +
"	(select concat(pro.pro_apellidos,' ',pro.pro_nombres) nombre_propietario from sirec.propietario pro, sirec.propietario_predio pp\n" +
"	where pro.pro_ci=pp.pro_ci and pp.catpre_codigo=cp.catpre_codigo),\n" +
"	(select pro.pro_ci ruc_ci from sirec.propietario pro, sirec.propietario_predio pp\n" +
"	where pro.pro_ci=pp.pro_ci and pp.catpre_codigo=cp.catpre_codigo),\n" +
"	a.catprealcval_comprador,\n" +
"	cp.catpre_area_total,\n" +
"	cp.catpre_area_total_cons,\n" +
"	(cp.catpre_area_total+cp.catpre_area_total_cons) total,\n" +
"	(select v.catpreval_avaluo_tot avaluo_total from sirec.catastro_predial_valoracion v\n" +
"	where v.catpre_codigo=cp.catpre_codigo and a.catprealcval_anio=v.catpreval_anio),\n" +
"	a.catprealcval_precioventa,\n" +
"	(SELECT cd.catdet_texto concepto from sirec.catalogo_detalle cd\n" +
"	where cd.catdet_codigo=a.catdet_concepto),\n" +
"	a.catprealcval_baseimp,\n" +
"  a.catprealcval_impuesto,\n" +
"  a.catprealcval_consejo_prov,\n" +
"  a.catprealcval_tasa_proc,\n" +
"  a.catprealcval_total,\n" +
"  a.catprealcval_observaciones\n" +
"  FROM sirec.catastro_predial_alcabala_valoracion a, sirec.catastro_predial cp\n" +
"WHERE 	a.catprealcval_activo=true and\n" +
"a.catpre_codigo=cp.catpre_codigo and\n" +
"a.catprealcval_anio=:anio\n" +
"order by a.catprealcval_anio, nombre_propietario asc";
        Query q = getEntityManager().createNativeQuery(sql);
        q.setParameter("anio", anio);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            System.out.println(sql);
            lista = q.getResultList();
            return lista;
        }
    }
}
