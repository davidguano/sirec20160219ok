/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.ejb.servicios;



import ec.sirec.ejb.facade.CementerioFacade;
import ec.sirec.ejb.facade.PatenteArchivoFacade;
import ec.sirec.ejb.facade.PatenteFacade;
import java.math.BigDecimal;
import java.security.Timestamp;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;

/**
 *
 * @author Darwin Aldas
 */
@Stateless
@LocalBean
public class CementerioReporteServicio {

    @EJB
    private CementerioFacade cementerioDao;

    public List<Object[]> listarDatReporte1(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal) throws Exception {
        return cementerioDao.listReporte1(fechaInicial, fechaFinal);
    }

    public List<Object[]> listaDatReporte2(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal, String tipoNicho) throws Exception {
        return cementerioDao.listReporte2(fechaInicial, fechaFinal, tipoNicho);
    }

    public List<Object[]> listaDatReporte3(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal, int codParroquia) throws Exception {
        return cementerioDao.listReporte3(fechaInicial, fechaFinal, codParroquia);
    }

    public List<Object[]> listaDatReporte4(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal, String genero) throws Exception {
        return cementerioDao.listReporte4(fechaInicial, fechaFinal, genero);
    }

    public List<Object[]> listaDatReporte5(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal) throws Exception {
        return cementerioDao.listReporte5(fechaInicial, fechaFinal);
    }

    public List<Object[]> listaDatReporte6(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal, int numAnios) throws Exception {
        return cementerioDao.listReporte6(fechaInicial, fechaFinal, numAnios);
    }

}
