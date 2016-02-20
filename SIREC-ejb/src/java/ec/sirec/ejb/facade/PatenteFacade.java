/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.ejb.facade;

import ec.sirec.ejb.entidades.Patente;
import java.math.BigDecimal;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
public class PatenteFacade extends AbstractFacade<Patente> {

    @PersistenceContext(unitName = "SIREC-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PatenteFacade() {
        super(Patente.class);
    }

    public Patente retornaNumSecuencial() throws Exception {
        String sql = "select max(c) from Patente c ";
        Query q = em.createQuery(sql);
        return (Patente) q.getResultList().get(0);
    }

    public List<Object[]> listaDatosEmisionAnioPatente(int codPatente, int anio) {
        List<Object[]> lista = new ArrayList<Object[]>();
        String sql = " select distinct( pa.pat_codigo) as clavePatente,p.pro_apellidos||' '||p.pro_nombres as nomContribuente ,"
                + " p.pro_ci as identificacion,p.pro_direccion as direccion,cdp.catdet_texto as parroquia,pv.patval_anio as año,pv.patval_patrimonio as patrimonio, "
                + " pv.patval_impuesto as impuestoPatente,pv.patval_tasa_bomb as tasaBomberos,pv.patval_tasa_proc as tasaProcesamiento,"
                + " pv.patval_total as totalPatente,pvmil.pat15val_base_imponible as baseImponible,pvmil.pat15val_impuesto as impuestoxMil,pvmil.pat15val_tasa_proc as tasaProxMil,"
                + " pvmil.pat15val_total as totalValxMil "
                + " from "
                + " sirec.propietario  p,sirec.propietario_predio pp,sirec.catastro_predial cp,"
                + " sirec.patente pa,sirec.patente_valoracion pv,sirec.patente_15xmil_valoracion pvmil,"
                + " sirec.catalogo_detalle cdp"
                + " where p.pro_ci=pp.pro_ci"
                + " and pp.catpre_codigo=cp.catpre_codigo"
                + " and cp.catpre_codigo=pa.catpre_codigo"
                + " and pa.pat_codigo=pv.pat_codigo"
                + " and pa.pat_codigo=pvmil.pat_codigo"
                + " and cp.catdet_parroquia=cdp.catdet_codigo "
                + " and pa.pat_codigo=:codPatente "
                + " and pv.patval_anio=:anio "
                + "union "
                + " select distinct( pa.pat_codigo) as clavePatente,p.pro_apellidos||' '||p.pro_nombres as nomContribuente ,  "
                + "p.pro_ci as identificacion,p.pro_direccion as direccion,cdp.catdet_texto as parroquia,pv.patval_anio as año,pv.patval_patrimonio as patrimonio,  "
                + " pv.patval_impuesto as impuestoPatente,pv.patval_tasa_bomb as tasaBomberos,pv.patval_tasa_proc as tasaProcesamiento, pv.patval_total as totalPatente "
                + ",0 as baseImponible,0 as impuestoxMil,0 as tasaProxMil, "
                + " 0 as totalValxMil  "
                + " from  sirec.propietario  p,sirec.propietario_predio pp,sirec.catastro_predial cp, sirec.patente pa, "
                + " sirec.patente_valoracion pv, sirec.catalogo_detalle cdp "
                + "  where p.pro_ci=pp.pro_ci and pp.catpre_codigo=cp.catpre_codigo "
                + "  and cp.catpre_codigo=pa.catpre_codigo "
                + "   and pa.pat_codigo=pv.pat_codigo  "
                + "   and cp.catdet_parroquia=cdp.catdet_codigo "
                + "and pa.pat_codigo not in (select pat_codigo from sirec.patente_15xmil_valoracion ) "
                + "  and pa.pat_codigo=:codPatente "
                + "    and pv.patval_anio=:anio";

        Query q = getEntityManager().createNativeQuery(sql);
        q.setParameter("codPatente", codPatente).setParameter("anio", anio);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            System.out.println(sql);
            lista = q.getResultList();
            return lista;
        }
    }

    public List<Object[]> listaDatosEmisionAnioParroquia(int anio, int parroquia) {
        List<Object[]> lista = new ArrayList<Object[]>();
        String sql = " select distinct( pa.pat_codigo) as clavePatente,p.pro_apellidos||' '||p.pro_nombres as nomContribuente ,"
                + " p.pro_ci as identificacion,p.pro_direccion as direccion,cdp.catdet_texto as parroquia,pv.patval_anio as año,pv.patval_patrimonio as patrimonio, "
                + " pv.patval_impuesto as impuestoPatente,pv.patval_tasa_bomb as tasaBomberos,pv.patval_tasa_proc as tasaProcesamiento,"
                + " pv.patval_total as totalPatente,pvmil.pat15val_base_imponible as baseImponible,pvmil.pat15val_impuesto as impuestoxMil,pvmil.pat15val_tasa_proc as tasaProxMil,"
                + " pvmil.pat15val_total as totalValxMil "
                + " from "
                + " sirec.propietario  p,sirec.propietario_predio pp,sirec.catastro_predial cp,"
                + " sirec.patente pa,sirec.patente_valoracion pv,sirec.patente_15xmil_valoracion pvmil,"
                + " sirec.catalogo_detalle cdp"
                + " where p.pro_ci=pp.pro_ci"
                + " and pp.catpre_codigo=cp.catpre_codigo"
                + " and cp.catpre_codigo=pa.catpre_codigo"
                + " and pa.pat_codigo=pv.pat_codigo"
                + " and pa.pat_codigo=pvmil.pat_codigo"
                + " and cp.catdet_parroquia=cdp.catdet_codigo "
                + " and cp.catdet_parroquia=:parroquia "
                + " and pv.patval_anio=:anio "
                + "union "
                + "select distinct( pa.pat_codigo) as clavePatente,p.pro_apellidos||' '||p.pro_nombres as nomContribuente ,  "
                + "p.pro_ci as identificacion,p.pro_direccion as direccion,cdp.catdet_texto as parroquia,pv.patval_anio as año,pv.patval_patrimonio as patrimonio,  "
                + " pv.patval_impuesto as impuestoPatente,pv.patval_tasa_bomb as tasaBomberos,pv.patval_tasa_proc as tasaProcesamiento, pv.patval_total as totalPatente "
                + ",0 as baseImponible,0 as impuestoxMil,0 as tasaProxMil, "
                + " 0 as totalValxMil  "
                + " from  sirec.propietario  p,sirec.propietario_predio pp,sirec.catastro_predial cp, sirec.patente pa, "
                + " sirec.patente_valoracion pv, sirec.catalogo_detalle cdp "
                + "  where p.pro_ci=pp.pro_ci and pp.catpre_codigo=cp.catpre_codigo "
                + "  and cp.catpre_codigo=pa.catpre_codigo "
                + " and cp.catdet_parroquia=cdp.catdet_codigo "
                + "   and pa.pat_codigo=pv.pat_codigo "
                + "  and pa.pat_codigo not in (select pat_codigo from sirec.patente_15xmil_valoracion ) "
                + "   and cp.catdet_parroquia=:parroquia  "
                + "   and pv.patval_anio=:anio";

        Query q = getEntityManager().createNativeQuery(sql);
        q.setParameter("parroquia", parroquia).setParameter("anio", anio);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            System.out.println(sql);
            lista = q.getResultList();
            return lista;
        }
    }

    public List<Object[]> listaDatosEmisionAnioGlobal(int anio) {
        List<Object[]> lista = new ArrayList<Object[]>();
        String sql = " select distinct( pa.pat_codigo) as clavePatente,p.pro_apellidos||' '||p.pro_nombres as nomContribuente ,"
                + " p.pro_ci as identificacion,p.pro_direccion as direccion,cdp.catdet_texto as parroquia,pv.patval_anio as año,pv.patval_patrimonio as patrimonio, "
                + " pv.patval_impuesto as impuestoPatente,pv.patval_tasa_bomb as tasaBomberos,pv.patval_tasa_proc as tasaProcesamiento,"
                + " pv.patval_total as totalPatente,pvmil.pat15val_base_imponible as baseImponible,pvmil.pat15val_impuesto as impuestoxMil,pvmil.pat15val_tasa_proc as tasaProxMil,"
                + " pvmil.pat15val_total as totalValxMil "
                + " from "
                + " sirec.propietario  p,sirec.propietario_predio pp,sirec.catastro_predial cp,"
                + " sirec.patente pa,sirec.patente_valoracion pv,sirec.patente_15xmil_valoracion pvmil,"
                + " sirec.catalogo_detalle cdp"
                + " where p.pro_ci=pp.pro_ci"
                + " and pp.catpre_codigo=cp.catpre_codigo"
                + " and cp.catpre_codigo=pa.catpre_codigo"
                + " and pa.pat_codigo=pv.pat_codigo"
                + " and pa.pat_codigo=pvmil.pat_codigo"
                + " and cp.catdet_parroquia=cdp.catdet_codigo "
                + " and pv.patval_anio=:anio "
                + "union "
                + "select distinct( pa.pat_codigo) as clavePatente,p.pro_apellidos||' '||p.pro_nombres as nomContribuente ,  "
                + "p.pro_ci as identificacion,p.pro_direccion as direccion,cdp.catdet_texto as parroquia,pv.patval_anio as año,pv.patval_patrimonio as patrimonio,  "
                + " pv.patval_impuesto as impuestoPatente,pv.patval_tasa_bomb as tasaBomberos,pv.patval_tasa_proc as tasaProcesamiento, pv.patval_total as totalPatente "
                + ",0 as baseImponible,0 as impuestoxMil,0 as tasaProxMil, "
                + " 0 as totalValxMil  "
                + " from  sirec.propietario  p,sirec.propietario_predio pp,sirec.catastro_predial cp, sirec.patente pa, "
                + " sirec.patente_valoracion pv, sirec.catalogo_detalle cdp "
                + "  where p.pro_ci=pp.pro_ci and pp.catpre_codigo=cp.catpre_codigo "
                + "  and cp.catpre_codigo=pa.catpre_codigo "
                + "   and pa.pat_codigo=pv.pat_codigo  "
                + "   and cp.catdet_parroquia=cdp.catdet_codigo  "
                + "   and pa.pat_codigo not in (select pat_codigo from sirec.patente_15xmil_valoracion ) "
                + "    and pv.patval_anio=:anio "
                + "    order by 1";

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

    public Patente buscaPatentePorRucActEcon(String cedula, int actEconomica) throws Exception {
        String sql = "select p from Patente p ,Propietario pr, "
                + " PropietarioPredio pp ,CatastroPredial cp"
                + " where pr.proCi=pp.proCi"
                + " and pp.catpreCodigo=cp.catpreCodigo"
                + " and cp.catpreCodigo=cp.catpreCodigo"
                + " and pr.proCi=:cedula "
                + " and  p.catdetTipoActEco.catdetCodigo=:actEco";
        Query q = em.createQuery(sql);
        q.setParameter("cedula", cedula).setParameter("actEco", actEconomica);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            return (Patente) q.getResultList().get(0);
        }
    }

    public boolean buscaPatentePrimeraVez(int codPatValoracion, int catasPredial, int anio) {
        String sql = " select pa.pat_codigo as codigoPatente from sirec.patente pa,sirec.patente_valoracion pv "
                + "where pa.pat_codigo=pv.pat_codigo "
                + "and pv.patval_anio=:anio "
                + "and pa.catpre_codigo=:catPredial "
                + "and pv.pat_codigo=:patValoracion ";
        Query q = getEntityManager().createNativeQuery(sql);
        q.setParameter("patValoracion", codPatValoracion).setParameter("catPredial", catasPredial).setParameter("anio", anio);
        if (q.getResultList().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    //*****************************Listado para reportes*****************************
    //Reporte 1:Negocios por rango de patrimonio
    public List<Object[]> listReporte1(BigDecimal valorInicial, BigDecimal valorFinal) throws Exception {
        List<Object[]> lista = new ArrayList<Object[]>();
        String sql = " select distinct( pa.pat_codigo) as clavePatente, "
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
                + "pv.patval_patrimonio as patrimonio "
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
                + " and pv.patval_patrimonio between  :valorInicial  and  :valorFinal order by 1 ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("valorInicial", valorInicial).setParameter("valorFinal", valorFinal);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            System.out.println(sql);
            lista = q.getResultList();
            return lista;
        }
    }

    public List<Object[]> listReporte2(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal) throws Exception {
        List<Object[]> lista = new ArrayList<Object[]>();
        String sql = " select distinct( pa.pat_codigo) as clavePatente,p.pro_apellidos||' '||p.pro_nombres as nomContribuente , "
                + "p.pro_ci as identificacion,p.pro_direccion as direccion,cdp.catdet_texto as parroquia,pv.patval_anio as año,pv.patval_patrimonio as patrimonio, "
                + "pv.patval_impuesto as impuestoPatente,pv.patval_tasa_bomb as tasaBomberos,pv.patval_tasa_proc as tasaProcesamiento, "
                + "pv.patval_total as totalPatente,pvmil.pat15val_base_imponible as baseImponible,pvmil.pat15val_impuesto as impuestoxMil,pvmil.pat15val_tasa_proc as tasaProxMil, "
                + "pvmil.pat15val_total as totalValxMil "
                + "from "
                + "sirec.propietario  p,sirec.propietario_predio pp,sirec.catastro_predial cp, "
                + "sirec.patente pa,sirec.patente_valoracion pv,sirec.patente_15xmil_valoracion pvmil, "
                + "sirec.catalogo_detalle cdp "
                + "where p.pro_ci=pp.pro_ci "
                + "and pp.catpre_codigo=cp.catpre_codigo "
                + "and cp.catpre_codigo=pa.catpre_codigo "
                + "and pa.pat_codigo=pv.pat_codigo "
                + "and pa.pat_codigo=pvmil.pat_codigo "
                + "and cp.catdet_parroquia=cdp.catdet_codigo "
                + "and pa.ultacc_marcatiempo between  :fechaInicial and  :fechaFinal "
                + "order by 1,pv.patval_anio ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("fechaInicial", fechaInicial).setParameter("fechaFinal", fechaFinal);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            System.out.println(sql);
            lista = q.getResultList();
            return lista;
        }
    }
}
