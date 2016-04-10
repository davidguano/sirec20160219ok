/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.ejb.servicios;

import ec.sirec.ejb.facade.PatenteFacade;
import java.math.BigDecimal;
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
public class PatenteReporteServicio {

    @EJB
    private PatenteFacade patenteDao;

    public List<Object[]> listarDatReporte1(BigDecimal valInicial, BigDecimal valFinal) throws Exception {
        return patenteDao.listReporte1(valInicial, valFinal);
    }

    public List<Object[]> listaDatReporte2(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal) throws Exception {
        return patenteDao.listReporte2(fechaInicial, fechaFinal);
    }

    public List<Object[]> listaDatReporte3(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal, int codParroquia) throws Exception {
        return patenteDao.listReporte3(fechaInicial, fechaFinal, codParroquia);
    }

    public List<Object[]> listaDatReporte4(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal, int codSector) throws Exception {
        return patenteDao.listReporte4(fechaInicial, fechaFinal, codSector);
    }

    public List<Object[]> listaDatReporte5(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal, String propietario) throws Exception {
        return patenteDao.listReporte5(fechaInicial, fechaFinal, propietario);
    }

    public List<Object[]> listaDatReporte6(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal, int actEco) throws Exception {
        return patenteDao.listReporte6(fechaInicial, fechaFinal, actEco);
    }

    public List<Object[]> listaDatReporte7(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal) throws Exception {
        return patenteDao.listReporte7(fechaInicial, fechaFinal);
    }

    public List<Object[]> listaDatReporte8(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal) throws Exception {
        return patenteDao.listReporte8(fechaInicial, fechaFinal);
    }

    public List<Object[]> listaDatReporte9(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal) throws Exception {
        return patenteDao.listReporte9(fechaInicial, fechaFinal);
    }

    public List<Object[]> listaDatReporte10(java.sql.Timestamp fechaInicial, java.sql.Timestamp fechaFinal, String desActEconomica) throws Exception {
        return patenteDao.listReporte10(fechaInicial, fechaFinal, desActEconomica);
    }
}
