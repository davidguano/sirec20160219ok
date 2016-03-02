/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.ejb.servicios;

import ec.sirec.ejb.entidades.CatastroPredial;
import ec.sirec.ejb.entidades.CatastroPredialAreas;
import ec.sirec.ejb.entidades.CatastroPredialEdificacion;
import ec.sirec.ejb.entidades.DatoGlobal;
import ec.sirec.ejb.entidades.FittoCorvini;
import ec.sirec.ejb.facade.CatastroPredialAreasFacade;
import ec.sirec.ejb.facade.CatastroPredialEdificacionFacade;
import ec.sirec.ejb.facade.CatastroPredialFacade;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author DAVID GUAN
 */
@Stateless
@LocalBean
public class ValoracionServicio {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    private static final Logger LOGGER = Logger.getLogger(ValoracionServicio.class.getName());

    @EJB
    private CatastroPredialFacade catastroDao;
    @EJB
    private CatastroPredialAreasFacade areasDao;
    @EJB
    private FittoCorviniServicio fcServicio;
    @EJB
    private CatastroPredialEdificacionFacade edificacionDao;
    @EJB
    private DatoGlobalServicio dgServicio;

    public BigDecimal obtenerValoracionTerrenoPorPredio(Integer catpreCodigo) throws Exception {
        BigDecimal vc = BigDecimal.ZERO;
        CatastroPredial cp = catastroDao.buscarPorCampo("CatastroPredial", "catpreCodigo", catpreCodigo);
        if (cp.getCatpreAvaluoTerreno() != null) {
            vc = cp.getCatpreAvaluoTerreno();
        }
        return vc;
    }

    public BigDecimal obtenerValoracionConstruccionPorPredio(Integer catpreCodigo) throws Exception {
        BigDecimal vc = BigDecimal.ZERO;
        try {
            List<CatastroPredialAreas> lstAreas = new ArrayList<CatastroPredialAreas>();
            lstAreas = areasDao.listarPorCampoOrdenada("CatastroPredialAreas", "catpreCodigo.catpreCodigo", catpreCodigo, "catpreareCodigo", "asc");
            if (!lstAreas.isEmpty()) {
                for (CatastroPredialAreas a : lstAreas) {
                    vc = vc.add(obtenerValoracionPorArea(a));
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return vc;
    }

    public BigDecimal obtenerValoracionPorArea(CatastroPredialAreas area) throws Exception {
        BigDecimal vctotal = BigDecimal.ZERO;
        BigDecimal vcxm2 = BigDecimal.ZERO;
        List<CatastroPredialEdificacion> lstEdif = new ArrayList<CatastroPredialEdificacion>();

        lstEdif = edificacionDao.listar23VariablesPorBloquePiso(area.getCatpreCodigo().getCatpreCodigo(), String.valueOf(area.getCatpreareBloque()), String.valueOf(area.getCatprearePiso()));
        if (!lstEdif.isEmpty()) {
            if (lstEdif.size() == 23 && lstEdif.get(1).getCatpreediValor() != null) {
                //objeto 0 tiene la estructura
                //objeto 1 tiene la edad
                //objeto 2 tiene el estado
                //objeto 6 al 22 tiene estructura-acabados-instalaciones, se debe omitir el objeto 20= total 16 variables
                BigDecimal valorNuevoEst = BigDecimal.ZERO;
                BigDecimal valorNuevoAcabado = BigDecimal.ZERO;
                BigDecimal valorNuevoTotal = BigDecimal.ZERO;
                BigDecimal valorDepreciado = BigDecimal.ZERO;
                Integer edadEnPorcVida = 0;
                BigDecimal factorDepreciacion = BigDecimal.ZERO;
                String claseAcabado = "";

                //encontrar valor a nuevo del m2 de estructura
                valorNuevoEst = obtenerValorANuevoPorm2DeDatoGlobal("EST", null, lstEdif.get(0).getCatdetCodigo().getCatdetTexto());

                //encontrar el factor de depreciacion
                double calculoEdad = (lstEdif.get(1).getCatpreediValor() / lstEdif.get(0).getCatdetCodigo().getCatdetValorDecimal()) * 100;
                edadEnPorcVida = (int) Math.round(calculoEdad);
                if (edadEnPorcVida <= 100) {
                    FittoCorvini fc = fcServicio.obtenerValoresClase(edadEnPorcVida);
                    if (lstEdif.get(2).getCatdetCodigo().getCatdetCod().equals("1")) {
                        factorDepreciacion = new BigDecimal(fc.getClase1());
                    } else if (lstEdif.get(2).getCatdetCodigo().getCatdetCod().equals("2")) {
                        factorDepreciacion = new BigDecimal(fc.getClase2());
                    } else if (lstEdif.get(2).getCatdetCodigo().getCatdetCod().equals("3")) {
                        factorDepreciacion = new BigDecimal(fc.getClase3());
                    } else if (lstEdif.get(2).getCatdetCodigo().getCatdetCod().equals("4")) {
                        factorDepreciacion = new BigDecimal(fc.getClase4());
                    } else if (lstEdif.get(2).getCatdetCodigo().getCatdetCod().equals("5")) {
                        factorDepreciacion = new BigDecimal(fc.getClase5());
                    }
                }

                //encontrar clase de acabado
                int n = 0;
                int v = 0;
                for (int i = 6; i < 23; i++) {
                    if (i != 20) {
                        if (lstEdif.get(i).getCatdetCodigo().getCatdetValor() > 0) {
                            n++;
                            v = v + lstEdif.get(i).getCatdetCodigo().getCatdetValor();
                        }

                    }
                }
                if (v < (n * 1.5)) {
                    //de primera
                    claseAcabado = "PRI";
                } else if (v >= (n * 1.5) && v < (n * 2.5)) {
                    //normal
                    claseAcabado = "NOR";
                } else if (v >= (n * 2.5)) {
                    //economico
                    claseAcabado = "ECO";
                }

                //encontrar el valor a nuevo del m2 de acabado
                valorNuevoAcabado = obtenerValorANuevoPorm2DeDatoGlobal("ACA", claseAcabado, lstEdif.get(0).getCatdetCodigo().getCatdetTexto());

                //aplicar la formular
                valorNuevoTotal = valorNuevoEst.add(valorNuevoAcabado);
                valorDepreciado = valorNuevoTotal.multiply(factorDepreciacion).divide(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);

                vcxm2 = valorNuevoTotal.subtract(valorDepreciado);
                System.out.println(valorNuevoEst + "," + valorNuevoAcabado + "," + factorDepreciacion + "," + valorNuevoTotal + "," + valorDepreciado);
            }
        }
        System.out.println(vcxm2 + "," + area.getCatpreareArea());
        vctotal = vcxm2.multiply(new BigDecimal(area.getCatpreareArea())).setScale(2, RoundingMode.HALF_UP);
        return vctotal;
    }

    public BigDecimal obtenerValorANuevoPorm2DeDatoGlobal(String tipo1, String clase, String tipoEst) throws Exception {
        BigDecimal v = BigDecimal.ZERO;
        DatoGlobal dg = new DatoGlobal();
        if (clase == null) {
            dg = dgServicio.obtenerDatoGlobal(tipo1 + "_" + tipoEst);
        } else {
            dg = dgServicio.obtenerDatoGlobal(tipo1 + "_" + clase + "_" + tipoEst);
        }
        if (dg != null) {
            if (dg.getDatgloValor() != null) {
                v = new BigDecimal(dg.getDatgloValor());
            }
        }
        return v;
    }

}
