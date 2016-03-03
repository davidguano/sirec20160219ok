/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.ejb.facade;

import ec.sirec.ejb.entidades.CatastroPredialAlcabalaValoracion;
import ec.sirec.ejb.entidades.CatastroPredialPlusvaliaValoracion;
import ec.sirec.ejb.entidades.CatastroPredialValoracion;
import ec.sirec.ejb.entidades.CuentaPorCobrar;
import ec.sirec.ejb.entidades.Patente15xmilValoracion;
import ec.sirec.ejb.entidades.PatenteValoracion;
import ec.sirec.ejb.entidades.RecaudacionDet;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author vespinoza
 */
@Stateless
public class RecaudacionDetFacade extends AbstractFacade<RecaudacionDet> {

    //LOGGER 
    private static final Logger LOGGER = Logger.getLogger(RecaudacionDetFacade.class.getName());
    @PersistenceContext(unitName = "SIREC-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RecaudacionDetFacade() {
        super(RecaudacionDet.class);
    }

    public List<RecaudacionDet> listaDetallesARecaudarPorCiAnio(String vci, Integer vAnio) throws Exception {
        List<Object[]> resultado = new ArrayList<Object[]>();
        List<RecaudacionDet> lstDets = new ArrayList<RecaudacionDet>();
        try {
            String sql1 = "select cxc_tipo, cxc_referencia,cxc_valor_total, cxc_cod_ref, cxc_codigo , cxc_anio, cxc_referencia2 from sirec.cuenta_por_cobrar \n"
                    + " where cxc_estado='P' and pro_ci='" + vci + "' ";
            if (vAnio != null && vAnio > 0) {
                sql1 = sql1 + " and cxc_anio=" + vAnio;
            }
            Query q = getEntityManager().createNativeQuery(sql1);
            System.out.println(sql1);
            resultado = q.getResultList();
            if (!resultado.isEmpty()) {
                for (Object[] obj : resultado) {
                    RecaudacionDet det = new RecaudacionDet();
                    det.setRecdetTipo(obj[0].toString());
                    det.setRecdetReferencia(obj[1].toString());
                    det.setRecdetValorInicial((BigDecimal) obj[2]);
                    det.setRecdetCodref((Integer) obj[3]);
                    det.setRecdetPorDesc(obtenerPorcentajeDescuentoCxc(obj[0].toString(), vci, vAnio));
                    det.setRecdetValor((det.getRecdetValorInicial().multiply(new BigDecimal(det.getRecdetPorDesc())).divide(new BigDecimal("100"))).add(det.getRecdetValorInicial()));
                    det.setCxcCodigo(new CuentaPorCobrar((Integer) obj[4]));
                    det.setAnio((Integer) obj[5]);
                    det.setUbicacion(obj[6].toString());
                    lstDets.add(det);
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "error en generacion de detalles", ex);
        }
        return lstDets;
    }

    public void actualizarCuentasPorCobrar(List<RecaudacionDet> lstDets) throws Exception {
        try {
            if (!lstDets.isEmpty()) {
                for (RecaudacionDet det : lstDets) {
                    if (det.getRecdetTipo().equals("CE")) {
                        String sql = "update sirec.cuenta_por_cobrar set cxc_saldo=0, cxc_estado='R'  where cxc_tipo=:tipo and cxc_cod_ref=" + det.getRecdetCodref();
                        Query q = getEntityManager().createNativeQuery(sql);
                        q.setParameter("tipo", det.getRecdetTipo());
                        q.executeUpdate();
                    } else {
                        String sql = "update sirec.cuenta_por_cobrar set cxc_saldo=0, cxc_estado='R', cxc_valor_total="+det.getRecdetValor().toString()+"  where cxc_tipo=:tipo and cxc_cod_ref=" + det.getRecdetCodref();
                        Query q = getEntityManager().createNativeQuery(sql);
                        q.setParameter("tipo", det.getRecdetTipo());
                        q.executeUpdate();
                    }

                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "error en actualizacion", ex);
        }
    }

    public int obtenerPorcentajeDescuentoCxc(String tipo, String vci, Integer vAnio) throws Exception {
        int p = 0;
        Calendar fechaActual = java.util.Calendar.getInstance();
        int mes = fechaActual.get(Calendar.MONTH) + 1;
        int dia = fechaActual.get(Calendar.DAY_OF_MONTH);
        if (tipo.equals("PR")) {
            if (mes == 1) {
                if (dia <= 15) {
                    p = -10;
                } else {
                    p = -9;
                }
            } else if (mes == 2) {
                if (dia <= 15) {
                    p = -8;
                } else {
                    p = -7;
                }
            } else if (mes == 3) {
                if (dia <= 15) {
                    p = -6;
                } else {
                    p = -5;
                }
            } else if (mes == 4) {
                if (dia <= 15) {
                    p = -4;
                } else {
                    p = -3;
                }
            } else if (mes == 5) {
                if (dia <= 15) {
                    p = -3;
                } else {
                    p = -2;
                }
            } else if (mes == 6) {
                if (dia <= 15) {
                    p = -2;
                } else {
                    p = -1;
                }
            } else if (mes >= 7) {
                p = 10;
            }
        } else if (tipo.equals("PA")) {

        }
        return p;
    }
    
    public CatastroPredialValoracion buscarCatastroPredialValoracionPorCodigo(Integer vcod) throws Exception {
        String sql = "select e from CatastroPredialValoracion e where e.catprevalCodigo=:vcod";
        Query q = getEntityManager().createQuery(sql);
        q.setParameter("vcod", vcod);
        List<CatastroPredialValoracion> resultado = q.getResultList();
        if (resultado.size() > 0) {
            return (CatastroPredialValoracion) resultado.get(0);
        } else {
            return null;
        }
    }
    public PatenteValoracion buscarPatenteValoracionPorCodigo(Integer vcod) throws Exception {
        String sql = "select e from PatenteValoracion e where e.patvalCodigo=:vcod";
        Query q = getEntityManager().createQuery(sql);
        q.setParameter("vcod", vcod);
        List<PatenteValoracion> resultado = q.getResultList();
        if (resultado.size() > 0) {
            return (PatenteValoracion) resultado.get(0);
        } else {
            return null;
        }
    }
    
    public Patente15xmilValoracion buscarPatente15x1000ValoracionPorCodigo(Integer vcod) throws Exception {
        String sql = "select e from Patente15xmilValoracion e where e.pat15valCodigo=:vcod";
        Query q = getEntityManager().createQuery(sql);
        q.setParameter("vcod", vcod);
        List<Patente15xmilValoracion> resultado = q.getResultList();
        if (resultado.size() > 0) {
            return (Patente15xmilValoracion) resultado.get(0);
        } else {
            return null;
        }
    }
    
    public CatastroPredialPlusvaliaValoracion buscarPlusvaliaValoracionPorCodigo(Integer vcod) throws Exception {
        String sql = "select e from CatastroPredialPlusvaliaValoracion e where e.catprepluvalCodigo=:vcod";
        Query q = getEntityManager().createQuery(sql);
        q.setParameter("vcod", vcod);
        List<CatastroPredialPlusvaliaValoracion> resultado = q.getResultList();
        if (resultado.size() > 0) {
            return (CatastroPredialPlusvaliaValoracion) resultado.get(0);
        } else {
            return null;
        }
    }
    
    public CatastroPredialAlcabalaValoracion buscarAlcabalaValoracionPorCodigo(Integer vcod) throws Exception {
        String sql = "select e from CatastroPredialAlcabalaValoracion e where e.catprealcvalCodigo=:vcod";
        Query q = getEntityManager().createQuery(sql);
        q.setParameter("vcod", vcod);
        List<CatastroPredialAlcabalaValoracion> resultado = q.getResultList();
        if (resultado.size() > 0) {
            return (CatastroPredialAlcabalaValoracion) resultado.get(0);
        } else {
            return null;
        }
    }

}
