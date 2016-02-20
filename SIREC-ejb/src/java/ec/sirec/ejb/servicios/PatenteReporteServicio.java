/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.ejb.servicios;

import ec.sirec.ejb.entidades.Cementerio;
import ec.sirec.ejb.entidades.CementerioArchivo;
import ec.sirec.ejb.entidades.Patente;
import ec.sirec.ejb.entidades.PatenteArchivo;
import ec.sirec.ejb.facade.CementerioArchivoFacade;
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
public class PatenteReporteServicio {

    @EJB
    private PatenteFacade patenteDao;

    public List<Object[]> listarDatReporte1(BigDecimal valInicial, BigDecimal valFinal) throws Exception {
        return patenteDao.listReporte1(valInicial, valFinal);
    }
    public List<Object[]> listaDatReporte2(java.sql.Timestamp fechaInicial,java.sql.Timestamp fechaFinal) throws Exception{
    return patenteDao.listReporte2(fechaInicial, fechaFinal);
    }
   }
