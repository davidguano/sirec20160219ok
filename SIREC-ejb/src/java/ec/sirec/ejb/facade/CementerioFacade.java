/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.ejb.facade;

import ec.sirec.ejb.entidades.Catalogo;
import ec.sirec.ejb.entidades.Cementerio;
import java.math.BigDecimal;
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

    //---------Reporte de deudores mayores a 4 años

    public List<Object[]> listReporte1(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal,String fechActual) throws Exception {
        List<Object[]> lista = new ArrayList<Object[]>();       
        System.out.println("Parametro fecha " + fechActual);
        String sql
                = " select c.cem_codigo as codigo, c.pro_occiso_ci as cedula,c.cem_nombre_occiso as nomOcciso, cd.catdet_texto,case "
                + "when c.cem_genero='M' then 'MASCULINO' "
                + "when c.cem_genero='F' then 'FEMENINO' "
                + "end as genero,case "
                + "when c.cem_estado='I' then 'INHUMADO' "
                + "when c.cem_estado='E' then 'EXHUMADO' "
                + "when c.cem_estado='R' then 'RESERVADO' "
                + "when c.cem_estado='G' then 'GRATUITO' "
                + "end as estadoCuerpo,c.cem_num_papeleta as papeleta, "
                + "c.cem_fecha_fallece as fechaFallece, "
                + "c.cem_representante as representante "
                + ",(DATE_PART('year',:fechaFinal) - DATE_PART('year', c.cem_fecha_fin_contrato)) as aniosMora "
                + " from sirec.cementerio  c,sirec.catalogo_detalle cd "
                + " where c.catdet_parroquia=cd.catdet_codigo "
                + " and c.cem_fecha_registra between :fechaInicial and :fechaFinal ";
        Query q = em.createQuery(sql);
        q.setParameter("fechaInicial", fechaInicial).setParameter("fechaFinal", fechaInicial);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            lista = q.getResultList();
            return lista;
        }
    }

    //---Reporte de occisos por tipo (Adulto, Niño)
    public List<Object[]> listReporte2(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal, String perTpoNicho) throws Exception {
        List<Object[]> lista = new ArrayList<Object[]>();
        String sql = " select c.cem_codigo as codigo, c.pro_occiso_ci as cedula,c.cem_nombre_occiso as nomOcciso, cd.catdet_texto,case "
                + "when c.cem_genero='M' then 'MASCULINO' "
                + "when c.cem_genero='F' then 'FEMENINO' "
                + "end as genero,case "
                + "when c.cem_estado='I' then 'INHUMADO' "
                + "when c.cem_estado='E' then 'EXHUMADO' "
                + "when c.cem_estado='R' then 'RESERVADO' "
                + "when c.cem_estado='G' then 'GRATUITO' "
                + "end as estadoCuerpo,c.cem_num_papeleta as papeleta, "
                + "c.cem_fecha_fallece as fechaFallece, "
                + "c.cem_representante as representante "
                + "from sirec.cementerio  c,sirec.catalogo_detalle cd "
                + "where c.catdet_parroquia=cd.catdet_codigo "
                + " and c.cem_fecha_registra between :fechaInicial and :fechaFinal "
                + "and c.cem_tipo =:parTipoNicho ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("fechaInicial", fechaInicial).setParameter("fechaFinal", fechaFinal).setParameter("parTipoNicho", perTpoNicho);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            lista = q.getResultList();
            return lista;
        }
    }

    //-----Reporte de occisos por parroquia
    public List<Object[]> listReporte3(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal, int codParroquia) throws Exception {
        List<Object[]> lista = new ArrayList<Object[]>();
        String sql
                = " select c.cem_codigo as codigo,c.pro_occiso_ci as cedula,c.cem_nombre_occiso as nomOcciso, cd.catdet_texto,case "
                + "when c.cem_genero='M' then 'MASCULINO' "
                + "when c.cem_genero='F' then 'FEMENINO' "
                + "end as genero,case "
                + "when c.cem_estado='I' then 'INHUMADO' "
                + "when c.cem_estado='E' then 'EXHUMADO' "
                + "when c.cem_estado='R' then 'RESERVADO' "
                + "when c.cem_estado='G' then 'GRATUITO' "
                + "end as estadoCuerpo,c.cem_num_papeleta as papeleta, "
                + "c.cem_fecha_fallece as fechaFallece, "
                + "c.cem_representante as representante "
                + "from sirec.cementerio  c,sirec.catalogo_detalle cd "
                + "where c.catdet_parroquia=cd.catdet_codigo "
                + "and c.cem_fecha_registra between :fechaInicial and :fechaFinal  "
                + "and c.catdet_parroquia=:codParroquia "
                + " ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("fechaInicial", fechaInicial).setParameter("fechaFinal", fechaFinal).setParameter("codParroquia", codParroquia);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            lista = q.getResultList();
            return lista;
        }
    }

    //-----Repòrte de occisos por genero
    public List<Object[]> listReporte4(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal, String genero) throws Exception {
        List<Object[]> lista = new ArrayList<Object[]>();
        String sql
                = " select c.cem_codigo as codigo, c.pro_occiso_ci as cedula,c.cem_nombre_occiso as nomOcciso, cd.catdet_texto,case "
                + "when c.cem_genero='M' then 'MASCULINO' "
                + "when c.cem_genero='F' then 'FEMENINO' "
                + "end as genero,case "
                + "when c.cem_estado='I' then 'INHUMADO' "
                + "when c.cem_estado='E' then 'EXHUMADO' "
                + "when c.cem_estado='R' then 'RESERVADO' "
                + "when c.cem_estado='G' then 'GRATUITO' "
                + "end as estadoCuerpo,c.cem_num_papeleta as papeleta, "
                + "c.cem_fecha_fallece as fechaFallece, "
                + "c.cem_representante as representante "
                + "from sirec.cementerio  c,sirec.catalogo_detalle cd "
                + "where c.catdet_parroquia=cd.catdet_codigo "
                + "and c.cem_fecha_registra between :fechaInicial and :fechaFinal  "
                + "and c.cem_genero =:genero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("fechaInicial", fechaInicial).setParameter("fechaFinal", fechaFinal).setParameter("genero", genero);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            lista = q.getResultList();
            return lista;
        }
    }

    //------Reporte de negocios por propietario
    public List<Object[]> listReporte5(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal, String propietario) throws Exception {
        List<Object[]> lista = new ArrayList<Object[]>();
        String sql
                = " select distinct( pa.pat_codigo) as clavePatente, "
                + "CASE "
                + "WHEN pa.pat_estado ='A' THEN 'ACTIVO' "
                + "WHEN pa.pat_estado ='P' THEN 'PRE-INSCRITO' "
                + "WHEN pa.pat_estado ='I' THEN 'INACTIVO' "
                + "END as estado "
                + ",cp.catpre_cod_nacional||''||cp.catpre_cod_local as catastroPredial,pa.pat_nombre_comercial as nombreComercial,pa.pat_representante_legal, "
                + "p.pro_apellidos||' '||p.pro_nombres as nomContribuente , "
                + "tes.catdet_texto as tipoEstablecimiento, tem.catdet_texto as tipoEmpresa, "
                + "tae.catdet_texto as actividadEconomica, "
                + "pa.pat_inicio_act_eco as inicioActEconomica, "
                + "CASE "
                + "WHEN  pa.pat_artesano_calificado =TRUE THEN 'SI' "
                + "WHEN  pa.pat_artesano_calificado =FALSE THEN 'NO' "
                + "END as artCalificado, "
                + " CASE "
                + "WHEN  pa.pat_obligado_cont =TRUE THEN 'SI' "
                + "WHEN  pa.pat_obligado_cont =FALSE THEN 'NO' "
                + "END as obligadoContabilidad, "
                + "pv.patval_patrimonio as patrimonio, "
                + "pv.patval_anio as anio "
                + "from "
                + "sirec.propietario  p,sirec.propietario_predio pp,sirec.catastro_predial cp, "
                + "sirec.patente pa,sirec.patente_valoracion pv , "
                + "sirec.catalogo_detalle tes,sirec.catalogo_detalle as tem, "
                + "sirec.catalogo_detalle as tlo,sirec.catalogo_detalle as tae, "
                + "sirec.catalogo_detalle cdp,sirec.catalogo "
                + "where p.pro_ci=pp.pro_ci "
                + "and pp.catpre_codigo=cp.catpre_codigo "
                + "and cp.catpre_codigo=pa.catpre_codigo "
                + "and pa.pat_codigo=pv.pat_codigo "
                + " and cp.catdet_parroquia=cdp.catdet_codigo "
                + " and pa.catdet_tipo_est=tes.catdet_codigo "
                + " and pa.catdet_tipo_empresa=tem.catdet_codigo "
                + " and pa.catdet_tipo_local=tlo.catdet_codigo "
                + " and pa.catdet_tipo_act_eco=tae.catdet_codigo "
                + " and pa.pat_fecha_registra between  :fechaInicial   and  :fechaFinal "
                + " and pa.pro_ci_patente =:propietario "
                + " order by 1 ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("fechaInicial", fechaInicial).setParameter("fechaFinal", fechaFinal).setParameter("propietario", propietario);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            lista = q.getResultList();
            return lista;
        }
    }

    //------Reporte de negocios por actividad economica
    public List<Object[]> listReporte6(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal, int actEconomica) throws Exception {
        List<Object[]> lista = new ArrayList<Object[]>();
        String sql
                = " select distinct( pa.pat_codigo) as clavePatente, "
                + "CASE "
                + "WHEN pa.pat_estado ='A' THEN 'ACTIVO' "
                + "WHEN pa.pat_estado ='P' THEN 'PRE-INSCRITO' "
                + "WHEN pa.pat_estado ='I' THEN 'INACTIVO' "
                + "END as estado "
                + ",cp.catpre_cod_nacional||''||cp.catpre_cod_local as catastroPredial,pa.pat_nombre_comercial as nombreComercial,pa.pat_representante_legal, "
                + "p.pro_apellidos||' '||p.pro_nombres as nomContribuente , "
                + "tes.catdet_texto as tipoEstablecimiento, tem.catdet_texto as tipoEmpresa, "
                + "tae.catdet_texto as actividadEconomica, "
                + "pa.pat_inicio_act_eco as inicioActEconomica, "
                + "CASE "
                + "WHEN  pa.pat_artesano_calificado =TRUE THEN 'SI' "
                + "WHEN  pa.pat_artesano_calificado =FALSE THEN 'NO' "
                + "END as artCalificado, "
                + " CASE "
                + "WHEN  pa.pat_obligado_cont =TRUE THEN 'SI' "
                + "WHEN  pa.pat_obligado_cont =FALSE THEN 'NO' "
                + "END as obligadoContabilidad, "
                + "pv.patval_patrimonio as patrimonio, "
                + "pv.patval_anio as anio "
                + "from "
                + "sirec.propietario  p,sirec.propietario_predio pp,sirec.catastro_predial cp, "
                + "sirec.patente pa,sirec.patente_valoracion pv , "
                + "sirec.catalogo_detalle tes,sirec.catalogo_detalle as tem, "
                + "sirec.catalogo_detalle as tlo,sirec.catalogo_detalle as tae, "
                + "sirec.catalogo_detalle cdp,sirec.catalogo "
                + "where p.pro_ci=pp.pro_ci "
                + "and pp.catpre_codigo=cp.catpre_codigo "
                + "and cp.catpre_codigo=pa.catpre_codigo "
                + "and pa.pat_codigo=pv.pat_codigo "
                + " and cp.catdet_parroquia=cdp.catdet_codigo "
                + " and pa.catdet_tipo_est=tes.catdet_codigo "
                + " and pa.catdet_tipo_empresa=tem.catdet_codigo "
                + " and pa.catdet_tipo_local=tlo.catdet_codigo "
                + " and pa.catdet_tipo_act_eco=tae.catdet_codigo "
                + " and pa.pat_fecha_registra between  :fechaInicial   and  :fechaFinal "
                + "and pa.catdet_tipo_act_eco=:actEconomica "
                + " order by 1 ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("fechaInicial", fechaInicial).setParameter("fechaFinal", fechaFinal).setParameter("actEconomica", actEconomica);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            lista = q.getResultList();
            return lista;
        }
    }

    //----Reporte de negocios por artesano calificado-----
    public List<Object[]> listReporte7(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal) throws Exception {
        List<Object[]> lista = new ArrayList<Object[]>();
        String sql
                = " select distinct( pa.pat_codigo) as clavePatente, "
                + "CASE "
                + "WHEN pa.pat_estado ='A' THEN 'ACTIVO' "
                + "WHEN pa.pat_estado ='P' THEN 'PRE-INSCRITO' "
                + "WHEN pa.pat_estado ='I' THEN 'INACTIVO' "
                + "END as estado "
                + ",cp.catpre_cod_nacional||''||cp.catpre_cod_local as catastroPredial,pa.pat_nombre_comercial as nombreComercial,pa.pat_representante_legal, "
                + "p.pro_apellidos||' '||p.pro_nombres as nomContribuente , "
                + "tes.catdet_texto as tipoEstablecimiento, tem.catdet_texto as tipoEmpresa, "
                + "tae.catdet_texto as actividadEconomica, "
                + "pa.pat_inicio_act_eco as inicioActEconomica, "
                + "CASE "
                + "WHEN  pa.pat_artesano_calificado =TRUE THEN 'SI' "
                + "WHEN  pa.pat_artesano_calificado =FALSE THEN 'NO' "
                + "END as artCalificado, "
                + " CASE "
                + "WHEN  pa.pat_obligado_cont =TRUE THEN 'SI' "
                + "WHEN  pa.pat_obligado_cont =FALSE THEN 'NO' "
                + "END as obligadoContabilidad, "
                + "pv.patval_patrimonio as patrimonio, "
                + "pv.patval_anio as anio "
                + "from "
                + "sirec.propietario  p,sirec.propietario_predio pp,sirec.catastro_predial cp, "
                + "sirec.patente pa,sirec.patente_valoracion pv , "
                + "sirec.catalogo_detalle tes,sirec.catalogo_detalle as tem, "
                + "sirec.catalogo_detalle as tlo,sirec.catalogo_detalle as tae, "
                + "sirec.catalogo_detalle cdp,sirec.catalogo "
                + "where p.pro_ci=pp.pro_ci "
                + "and pp.catpre_codigo=cp.catpre_codigo "
                + "and cp.catpre_codigo=pa.catpre_codigo "
                + "and pa.pat_codigo=pv.pat_codigo "
                + " and cp.catdet_parroquia=cdp.catdet_codigo "
                + " and pa.catdet_tipo_est=tes.catdet_codigo "
                + " and pa.catdet_tipo_empresa=tem.catdet_codigo "
                + " and pa.catdet_tipo_local=tlo.catdet_codigo "
                + " and pa.catdet_tipo_act_eco=tae.catdet_codigo "
                + " and pa.pat_fecha_registra between  :fechaInicial   and  :fechaFinal "
                + " and pa.pat_artesano_calificado=true "
                + " order by 1 ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("fechaInicial", fechaInicial).setParameter("fechaFinal", fechaFinal);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            lista = q.getResultList();
            return lista;
        }
    }

}
