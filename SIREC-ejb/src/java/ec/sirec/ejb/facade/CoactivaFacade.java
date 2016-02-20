/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.sirec.ejb.facade;

import ec.sirec.ejb.entidades.Coactiva;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author DAVID GUAN
 */
@Stateless
public class CoactivaFacade extends AbstractFacade<Coactiva> {
    @PersistenceContext(unitName = "SIREC-ejbPU")
    private EntityManager em;
    //LOGGER 
    private static final Logger LOGGER = Logger.getLogger(CoactivaFacade.class.getName());

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CoactivaFacade() {
        super(Coactiva.class);
    }
    
    public void actualizarCuentasPorCobrar(Coactiva coa) throws Exception {
        try {
            if (coa!=null) {
                    String sql = "update sirec.cuenta_por_cobrar set cxc_saldo=0, cxc_estado='C'  where cxc_codigo=" + coa.getCxcCodigo().getCxcCodigo();
                    Query q = getEntityManager().createNativeQuery(sql);
                    q.executeUpdate();
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "error en actualizacion", ex);
        }
    }
    
}
