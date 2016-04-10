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
    public List<Object[]> listReporte1(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal) throws Exception {
        List<Object[]> lista = new ArrayList<Object[]>();

        Date fecHoy = new Date();
        System.out.println("Parametro fecha " + fecHoy);
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
                + ",(DATE_PART('year',CAST(:fechaHoy AS DATE)) - DATE_PART('year', c.cem_fecha_fin_contrato)) as aniosMora "
                + " from sirec.cementerio  c,sirec.catalogo_detalle cd "
                + " where c.catdet_parroquia=cd.catdet_codigo "
                + " and c.cem_fecha_registra between :fechaInicial and :fechaFinal ";
        Query q = em.createNativeQuery(sql);
          q.setParameter("fechaInicial", fechaInicial).setParameter("fechaFinal", fechaFinal).setParameter("fechaHoy", fecHoy);
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

    //------Reporte de todos los cancelados
    public List<Object[]> listReporte5(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal) throws Exception {
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
                + "and c.cem_fecha_fin_contrato is not null ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("fechaInicial", fechaInicial).setParameter("fechaFinal", fechaFinal);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            lista = q.getResultList();
            return lista;
        }
    }

    //------Reporte de tiempo de mora por año (Seleccionar años 1, 2, 3)
    public List<Object[]> listReporte6(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal, int numAnios) throws Exception {
        List<Object[]> lista = new ArrayList<Object[]>();
        Date fecHoy = new Date();
        System.out.println("Parametro fecha " + fecHoy);
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
                + ",(DATE_PART('year',CAST(:fechaHoy AS DATE)) - DATE_PART('year', c.cem_fecha_fin_contrato)) as aniosMora "
                + " from sirec.cementerio  c,sirec.catalogo_detalle cd "
                + " where c.catdet_parroquia=cd.catdet_codigo "
                + " and (DATE_PART('year',CAST(:fechaHoy AS DATE)) - DATE_PART('year', c.cem_fecha_fin_contrato))=:numAnios "
                + " and c.cem_fecha_registra between :fechaInicial and :fechaFinal ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("fechaInicial", fechaInicial).setParameter("fechaFinal", fechaFinal).setParameter("numAnios", numAnios).setParameter("fechaHoy", fecHoy);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            lista = q.getResultList();
            return lista;
        }
    }

}
