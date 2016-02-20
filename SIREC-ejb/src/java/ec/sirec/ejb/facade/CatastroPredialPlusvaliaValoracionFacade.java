/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.sirec.ejb.facade;

import ec.sirec.ejb.entidades.CatastroPredialPlusvaliaValoracion;
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
public class CatastroPredialPlusvaliaValoracionFacade extends AbstractFacade<CatastroPredialPlusvaliaValoracion> {
    @PersistenceContext(unitName = "SIREC-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CatastroPredialPlusvaliaValoracionFacade() {
        super(CatastroPredialPlusvaliaValoracion.class);
    }
    
    public List<Object[]> listaPlusvaliaEmitidas(int anio) {
        List<Object[]> lista = new ArrayList<Object[]>();                                               
        String sql = " SELECT a.catprepluval_codigo, "+
       "concat(cp.catpre_cod_nacional, cp.catpre_cod_local) clave_catastral,\n" +
"	a.catprepluval_anio,\n" +
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
" 	(SELECT cd.catdet_texto tipo_tarifa from sirec.catalogo_detalle cd\n" +
"	where cd.catdet_codigo=a.catdet_tipo_tarifa),\n" +
"	a.catprepluval_fecha_ultescr,\n" +
"       a.catprepluval_precioventa,\n" +
"       a.catprepluval_precioventa_ant,\n" +
"       a.catprepluval_dif_bruta,\n" +
"       a.catprepluval_valor_contrmej,\n" +
"       a.catprepluval_dif_neta,\n" +
"       a.catprepluval_anios_transf,\n" +
"       a.catprepluval_anios_transf_val,\n" +
"       a.catprepluval_dif_final,\n" +
"       a.catprepluval_porc_rebaja,\n" +
"       a.catprepluval_valor_rebaja,\n" +
"       a.catprepluval_baseimp,\n" +
"       a.catprepluval_tasaproc,\n" +
"       a.catprepluval_impuesto\n" +
"  FROM sirec.catastro_predial_plusvalia_valoracion a, sirec.catastro_predial cp\n" +
"WHERE a.catprepluval_activo=true and\n" +
"a.catpre_codigo=cp.catpre_codigo and\n" +
"a.catprepluval_anio=:anio\n" +
"order by a.catprepluval_anio, nombre_propietario asc";
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
    
     public List<Object[]> listaPlusvaliaXTipoTarifa(Object tipoTarifa) {
        List<Object[]> lista = new ArrayList<Object[]>();                                               
        String sql = " SELECT a.catprepluval_codigo, "+
      " concat(cp.catpre_cod_nacional, cp.catpre_cod_local) clave_catastral,\n" +
"	a.catprepluval_anio,\n" +
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
" 	(SELECT cd.catdet_texto tipo_tarifa from sirec.catalogo_detalle cd\n" +
"	where cd.catdet_codigo=a.catdet_tipo_tarifa),\n" +
"	a.catprepluval_fecha_ultescr,\n" +
"       a.catprepluval_precioventa,\n" +
"       a.catprepluval_precioventa_ant,\n" +
"       a.catprepluval_dif_bruta,\n" +
"       a.catprepluval_valor_contrmej,\n" +
"       a.catprepluval_dif_neta,\n" +
"       a.catprepluval_anios_transf,\n" +
"       a.catprepluval_anios_transf_val,\n" +
"       a.catprepluval_dif_final,\n" +
"       a.catprepluval_porc_rebaja,\n" +
"       a.catprepluval_valor_rebaja,\n" +
"       a.catprepluval_baseimp,\n" +
"       a.catprepluval_tasaproc,\n" +
"       a.catprepluval_impuesto\n" +
"  FROM sirec.catastro_predial_plusvalia_valoracion a, sirec.catastro_predial cp\n" +
"WHERE 	a.catprepluval_activo=true and\n" +
"a.catpre_codigo=cp.catpre_codigo and\n" +
"a.catdet_tipo_tarifa=:tipoTarifa\n" +
"order by a.catprepluval_anio, nombre_propietario, a.catdet_tipo_tarifa asc";
        Query q = getEntityManager().createNativeQuery(sql);
        q.setParameter("tipoTarifa", tipoTarifa);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            System.out.println(sql);
            lista = q.getResultList();
            return lista;
        }
    }
}
