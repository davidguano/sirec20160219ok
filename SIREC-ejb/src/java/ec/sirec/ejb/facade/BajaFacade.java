/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.ejb.facade;

import ec.sirec.ejb.entidades.Baja;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author DAVID GUAN
 */
@Stateless
public class BajaFacade extends AbstractFacade<Baja> {

    @PersistenceContext(unitName = "SIREC-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BajaFacade() {
        super(Baja.class);
    }

    public String obtenerNombreBajaPorTipo(String tipoImp, String tipoBaja) throws Exception{
        String t1 = "";
        if (tipoImp.equals("PR")) {
            t1 = "CAT";
        } else if (tipoImp.equals("PA")) {
            t1 = "PAT";
        } else if (tipoImp.equals("PM")) {
            t1 = "MIL";
        }
        String t2 = "";
        if (tipoBaja.equals("3E")) {
            t2 = "3ED";
        } else if (tipoBaja.equals("OF")) {
            t2 = "OFI";
        } else if (tipoBaja.equals("PR")) {
            t2 = "PRE ";
        }

        String sql = "select max(bajNombreArchivo) from Baja b where b.recdetCodigo.recdetTipo=:tipoImp and "
                + "b.bajTipo=:tipoBaja";
        Query q = em.createQuery(sql);
        q.setParameter("tipoImp", tipoImp).setParameter("tipoBaja", tipoBaja);
        if (q.getResultList().isEmpty() || q.getResultList().get(0) == null) {
            return t1 + "_" + t2 + "_000001";
        } else {
            String rs = String.valueOf(q.getSingleResult());
            Long sec = Long.valueOf(rs.substring(8, 14));
            if ((sec.intValue()+1) < 10) {
                return t1 + "_" + t2 + "_00000" + (sec.intValue()+1);
            } else {
                if (sec.intValue() < 100) {
                    return t1 + "_" + t2 + "_0000" + (sec.intValue()+1);
                } else {
                    if (sec.intValue() < 1000) {
                        return t1 + "_" + t2 + "_000" + (sec.intValue()+1);
                    } else {
                        if (sec.intValue() < 10000) {
                            return t1 + "_" + t2 + "_00" + (sec.intValue()+1);
                        } else {
                            if (sec.intValue() < 100000) {
                                return t1 + "_" + t2 + "_0" + (sec.intValue()+1);
                            } else {
                                if (sec.intValue() < 1000000) {
                                    return t1 + "_" + t2 + "_" + (sec.intValue()+1);
                                } else{
                                    return null;
                                }
                            }
                        }
                    }
                }
            }

        }
    }

}
