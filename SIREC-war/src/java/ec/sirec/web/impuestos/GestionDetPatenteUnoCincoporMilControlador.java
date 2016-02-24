/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.web.impuestos;

import ec.sirec.ejb.entidades.AdicionalesDeductivos;
import ec.sirec.ejb.entidades.CatalogoDetalle;
import ec.sirec.ejb.entidades.DatoGlobal;
import ec.sirec.ejb.entidades.Patente;
import ec.sirec.ejb.entidades.Patente15xmilValoracion;
import ec.sirec.ejb.entidades.Patente15xmilValoracionExtras;
import ec.sirec.ejb.entidades.PatenteValoracion;
import ec.sirec.ejb.servicios.AdicionalesDeductivosServicio;
import ec.sirec.ejb.servicios.CatalogoDetalleServicio;
import ec.sirec.ejb.servicios.PatenteServicio;
import ec.sirec.ejb.servicios.UnoPCinoPorMilServicio;
import ec.sirec.web.base.BaseControlador;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Darwin Aldas
 */
@ManagedBean
@ViewScoped
public class GestionDetPatenteUnoCincoporMilControlador extends BaseControlador {

    @EJB
    private AdicionalesDeductivosServicio adicionalesDeductivosServicio;

    @EJB
    private CatalogoDetalleServicio catalogoDetalleServicio;

    @EJB
    private PatenteServicio patenteServicio;
    @EJB
    private UnoPCinoPorMilServicio unoPCinoPorMilServicio;
    private Patente patenteActual;
    private Patente15xmilValoracion patente15milValActual;
    private Patente15xmilValoracionExtras pat15milValExtrActual;
    DatoGlobal datoGlobalActal;
    private int verPanelDetalleImp;
    private BigDecimal valBaseImponible;
    private BigDecimal valRecargos;
    private BigDecimal valImpuesto15xMil;
    private BigDecimal valTasaProc;
    private BigDecimal valSubTotal;
    private BigDecimal valTotal;
    private BigDecimal valTotAnual;
    private boolean habilitaEdicion;
    private String buscNumPat;
    private String buscAnioPat;
    private int verBuscaPatente;
    private int verCrear;
    String numPatente;
    private CatalogoDetalle catDetAnioBalance;
    private CatalogoDetalle catDetAnioDeclara;
    private List<CatalogoDetalle> listAnios;
    private int verGuardar;
    private int verActualiza;
    ArrayList<String> detaleExoDedMulxMil;
    private int verBotDetDeducciones;
    private int verDetDeducciones;
    private boolean deducciones;
    private boolean existeDedPatente;
    private CatalogoDetalle catDetAnio;
    private static final Logger LOGGER = Logger.getLogger(GestionDetPatenteUnoCincoporMilControlador.class.getName());

    /**
     * Creates a new instance of GestionDetPatenteControlador
     */
    @PostConstruct
    public void inicializar() {
        try {
            catDetAnio = new CatalogoDetalle();
            verCrear = 0;
            existeDedPatente = false;
            deducciones = false;
            detaleExoDedMulxMil = new ArrayList<String>();
            verGuardar = 0;
            verActualiza = 0;
            numPatente = "";
            verBuscaPatente = 0;
            buscNumPat = "";
            buscAnioPat = "";
            datoGlobalActal = new DatoGlobal();
            patenteActual = new Patente();
            patente15milValActual = new Patente15xmilValoracion();
            verPanelDetalleImp = 0;
            habilitaEdicion = false;
            catDetAnioBalance = new CatalogoDetalle();
            catDetAnioDeclara = new CatalogoDetalle();
            verBotDetDeducciones = 0;
            verDetDeducciones = 0;
            listarAnios();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public GestionDetPatenteUnoCincoporMilControlador() {
    }

    public void buscarPatente() {
        try {
            inicializar();
            verBuscaPatente = 1;
            verCrear = 1;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public void crearPatente() {
        try {
            inicializar();
            verBuscaPatente = 1;
            verCrear = 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public void cargarNumPatente() {
        patenteActual = (Patente) this.getSession().getAttribute("patente");
        if (patenteActual == null) {
            numPatente = null;
        } else {
            numPatente = "AE-MPM-" + patenteActual.getPatCodigo();
        }
    }

    public void cagarPatenteActual() {
        detaleExoDedMulxMil = null;
        verBotDetDeducciones = 0;
        verDetDeducciones = 0;
        deducciones = false;
        try {
            patenteActual = patenteServicio.cargarObjPatente(Integer.parseInt(buscNumPat));
            if (patenteActual == null) {
                numPatente = null;
                patente15milValActual = new Patente15xmilValoracion();
                verPanelDetalleImp = 0;
            } else {
                if (cargarExistePat15porMilValoracion()) {
                    // patente15milValActual = unoPCinoPorMilServicio.buscaPatValoracion15xMilPorAnio(patenteActual.getPatCodigo(), Integer.parseInt(buscAnioPat));
                    System.out.println("Si encontro el objeto");
                    numPatente = generaNumPatente(); //"AE-MPM-" + patenteActual.getPatCodigo();
                    CatalogoDetalle objCatDetAux = new CatalogoDetalle();
                    objCatDetAux = catalogoDetalleServicio.buscarPoCatdetTexCatdetCod(patente15milValActual.getPat15valAnioDecla() + "", "A" + patente15milValActual.getPat15valAnioDecla());
                    catDetAnioDeclara = objCatDetAux;
                    objCatDetAux = catalogoDetalleServicio.buscarPoCatdetTexCatdetCod(patente15milValActual.getPat15valAnioBalance() + "", "A" + patente15milValActual.getPat15valAnioBalance());
                    catDetAnioBalance = objCatDetAux;
                    //Verifica si tiene deducciones

                } else {
                    System.out.println("No encontro el objeto");
                    numPatente = generaNumPatente(); //"AE-MPM-" + patenteActual.getPatCodigo();
                    patente15milValActual = new Patente15xmilValoracion();
                    verGuardar = 1;
                    verActualiza = 0;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public void listarAnios() throws Exception {
        listAnios = catalogoDetalleServicio.listarPorNemonicoCatalogo("ANIOS");
    }

    public String generaNumPatente() { //Genera numero de patente aleatorio
        String numeroPatente = "";
        try {
            int valorRetornado = patenteActual.getPatCodigo();
            StringBuffer numSecuencial = new StringBuffer(valorRetornado + "");
            int valRequerido = 6;
            int valRetorno = numSecuencial.length();
            int valNecesita = valRequerido - valRetorno;
            StringBuffer sb = new StringBuffer(valNecesita);
            for (int i = 0; i < valNecesita; i++) {
                sb.append("0");
            }
            numeroPatente = "AE-MPM-" + sb.toString() + valorRetornado;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return numeroPatente;
    }

    public void limpiaPanelDetalleImpuestos() {
        patente15milValActual.setPat15valIngresoAnual(null);
        patente15milValActual.setPat15valNumSucursales(null);
        patente15milValActual.setPat15valActivo(null);
        patente15milValActual.setPat15valActivos(null);
        patente15milValActual.setPat15valPasivosCorriente(null);
        patente15milValActual.setPat15valPasivosConting(null);
        patente15milValActual.setPat15valOtrasDeducciones(null);
        patente15milValActual.setPat15valBaseImponible(null);
        patente15milValActual.setPat15valImpuesto(null);
        patente15milValActual.setPat15valRecargos(null);
        patente15milValActual.setPat15valTasaProc(null);
        patente15milValActual.setPat15valSubtotal(null);
        patente15milValActual.setPat15valTotal(null);
        verBotDetDeducciones = 0;
        verDetDeducciones = 0;
        detaleExoDedMulxMil = null;
    }

    public boolean cargarExistePat15porMilValoracion() {
        boolean pa15PorMilValoracion = false;
        //  Patente15xmilValoracion objPat15PorMilValoracion = new Patente15xmilValoracion();
        try {
            if (verCrear == 1) {
                patente15milValActual = unoPCinoPorMilServicio.buscaPatValoracion15xMilPorAnio(patenteActual.getPatCodigo(), Integer.parseInt(buscAnioPat));

            } else {
                CatalogoDetalle objCatDetAux = new CatalogoDetalle();
                objCatDetAux = catalogoDetalleServicio.buscarPorCodigoCatDet(catDetAnioDeclara.getCatdetCodigo());
                patente15milValActual = unoPCinoPorMilServicio.buscaPatValoracion15xMilPorAnio(patenteActual.getPatCodigo(), Integer.parseInt(objCatDetAux.getCatdetTexto()));
            }

            if (patente15milValActual == null) {
                pa15PorMilValoracion = false;
                verGuardar = 1;
                verActualiza = 0;
            } else {
                verGuardar = 0;
                verActualiza = 1;
                pa15PorMilValoracion = true;
                Patente15xmilValoracionExtras objPatValEx = new Patente15xmilValoracionExtras();
                objPatValEx = unoPCinoPorMilServicio.buscaPatVal15xMilExtraPorPatValoracion(patente15milValActual.getPat15valCodigo());
                if (objPatValEx != null) {
                    deducciones = true;
                    existeDedPatente = true;
                } else {
                    deducciones = false;
                    existeDedPatente = false;
                }
            }

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return pa15PorMilValoracion;
    }

    public void activaPanelDetalleImpuestos() {
        verPanelDetalleImp = 1;
    }

    public void calcularBaseImponible() {
        verBotDetDeducciones = 0;
        verDetDeducciones = 0;
        try {
            detaleExoDedMulxMil = new ArrayList<String>();
            valBaseImponible = patente15milValActual.getPat15valActivos().subtract(patente15milValActual.getPat15valPasivosCorriente()).subtract(patente15milValActual.getPat15valPasivosConting()).subtract(patente15milValActual.getPat15valOtrasDeducciones());
            valBaseImponible.setScale(2, RoundingMode.HALF_UP);
            patente15milValActual.setPat15valBaseImponible(valBaseImponible);
//            datoGlobalActal = unoPCinoPorMilServicio.buscaMensajeTransaccion("Val_tasa_procesamiento");
            valTasaProc = BigDecimal.ZERO;
            patente15milValActual.setPat15valTasaProc(valTasaProc);
            calculaValorImpuesto15xMil();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }

    }

    public void calculaValorImpuesto15xMil() {
        valImpuesto15xMil = (valBaseImponible.multiply(BigDecimal.valueOf(1.5))).divide(BigDecimal.valueOf(1000));
        valImpuesto15xMil.setScale(2, RoundingMode.HALF_UP);
        patente15milValActual.setPat15valImpuesto(valImpuesto15xMil);

        if (existeDedPatente) {
            calculaDeducciones();
        } else {
            valRecargos = BigDecimal.ZERO;
            patente15milValActual.setPat15valRecargos(valRecargos);

        }
        calculaTotalSubtotal();
    }

    public void calculaDeducciones() {
        try {

            Patente15xmilValoracion objPat15MilAux = new Patente15xmilValoracion();
            objPat15MilAux = unoPCinoPorMilServicio.buscaPatValoracion15xMil(patenteActual.getPatCodigo());
            Patente15xmilValoracionExtras objPat15MilExtAux = new Patente15xmilValoracionExtras();
            objPat15MilExtAux = unoPCinoPorMilServicio.buscaPatVal15xMilExtraPorPatValoracion(objPat15MilAux.getPat15valCodigo());
//**************************************EXONERACIONES************************************************           
            //--Exoneracion entidad publica
            if (objPat15MilExtAux.getPat15valextEntiPub() == true) {
                valImpuesto15xMil = BigDecimal.ZERO;
                patente15milValActual.setPat15valImpuesto(valImpuesto15xMil);
                detaleExoDedMulxMil.add("Exoneración: Entidad Publica valor impuesto:" + valImpuesto15xMil + "\n");
            }
            //--Exoneracion Fundaciones
            if (objPat15MilExtAux.getPat15valextFunBenEdu() == true) {
                valImpuesto15xMil = BigDecimal.ZERO;
                patente15milValActual.setPat15valImpuesto(valImpuesto15xMil);
                detaleExoDedMulxMil.add("Exoneración: Fundacines valor impuesto:" + valImpuesto15xMil + "\n");
            }
            //--Exoneracion Artesano
            if (objPat15MilExtAux.getPat15valextLeyFomArtes() == true) {
                valImpuesto15xMil = BigDecimal.ZERO;
                detaleExoDedMulxMil.add("Exoneración: Artesano Calificado valor impuesto:" + valImpuesto15xMil + "\n");
                patente15milValActual.setPat15valImpuesto(valImpuesto15xMil);
            }
            //--Exoneracion Agro Industria
            if (objPat15MilExtAux.getPat15valextActAgro() == true) {
                valImpuesto15xMil = BigDecimal.ZERO;
                detaleExoDedMulxMil.add("Exoneración: Agro Industrias valor impuesto:" + valImpuesto15xMil + "\n");
                patente15milValActual.setPat15valImpuesto(valImpuesto15xMil);
            }
            //--Exoneracion coop
            if (objPat15MilExtAux.getPat15valextCoop() == true) {
                valImpuesto15xMil = BigDecimal.ZERO;
                detaleExoDedMulxMil.add("Exoneración: Cooperaciones  valor impuesto:" + valImpuesto15xMil + "\n");
                patente15milValActual.setPat15valImpuesto(valImpuesto15xMil);
            }
            //--Exoneracion Multi Nacional
            if (objPat15MilExtAux.getPat15valextMultiNac() == true) {
                valImpuesto15xMil = BigDecimal.ZERO;
                detaleExoDedMulxMil.add("Exoneración: Multi Nacional valor impuesto:" + valImpuesto15xMil + "\n");
                patente15milValActual.setPat15valImpuesto(valImpuesto15xMil);
            }

//*******************************MULTAS******************************************************
            BigDecimal valDatFalso = BigDecimal.ZERO; //Valor Falsedad Datos
            BigDecimal valMultaPlazoDeclaracion = BigDecimal.ZERO;//Valor multa plazo declaración
            BigDecimal valMultaProcesoLiquidacion = BigDecimal.ZERO;//Multa a pagar por no comunicar el proceso de liquidacion
            //Incumplimiento plazo de declaración de patentes------------
            if (objPat15MilExtAux.getPat15valNumMesesIncum() != 0) {
                BigDecimal porcentajeImp = BigDecimal.ZERO;
                DatoGlobal objDatglobAux = new DatoGlobal();
                objDatglobAux = patenteServicio.cargarObjDatGloPorNombre("Val_porc_incumple_declaracion15PorMil");
                porcentajeImp = BigDecimal.valueOf(Double.parseDouble(objDatglobAux.getDatgloValor()));
                valMultaPlazoDeclaracion = (valImpuesto15xMil.multiply(porcentajeImp)).multiply(BigDecimal.valueOf(objPat15MilExtAux.getPat15valNumMesesIncum()));
                detaleExoDedMulxMil.add("Multas 1.5 por mil: Incumple plazo declaracion:valor" + valMultaPlazoDeclaracion + "\n");
                System.out.println("Multas 1.5 por mil:Incumple plazo declaracion:valor" + valMultaPlazoDeclaracion);
            }
            //Falsedad de datos------------------------------------------
            if (objPat15MilExtAux.getPat15valEvaluaDatFalsos() != 0) {
                BigDecimal valPorcentajeDatosFalos = BigDecimal.valueOf(objPat15MilExtAux.getPat15valEvaluaDatFalsos().doubleValue()).divide(BigDecimal.valueOf(100));
                DatoGlobal objDatglobAux = new DatoGlobal();
                objDatglobAux = patenteServicio.cargarObjDatGloPorNombre("Val_sueldo_basico");
                BigDecimal valSueldoBasico = BigDecimal.valueOf(Double.parseDouble(objDatglobAux.getDatgloValor()));
                valDatFalso = valSueldoBasico.multiply(valPorcentajeDatosFalos);
                detaleExoDedMulxMil.add("Multas 1.5 por mil: Falsedad de datos: valor: " + valDatFalso + "\n");
                System.out.println("Multas 1.5 por mil:Falsedad de datos: valor: " + valDatFalso);
            }
            //La no justificacion de las empresas en proceso de liquidación---------------
            if (objPat15MilExtAux.getPat15valProcesoLiquidacion() != 0) {
                DatoGlobal objDatglobAux = new DatoGlobal();
                objDatglobAux = patenteServicio.cargarObjDatGloPorNombre("Val_recargo_mensual_emp_proceso_liquidacion");
                BigDecimal valPagoMensual = BigDecimal.valueOf(Double.parseDouble(objDatglobAux.getDatgloValor()));
                valMultaProcesoLiquidacion = valPagoMensual.multiply(BigDecimal.valueOf(objPat15MilExtAux.getPat15valEvaluaDatFalsos()));
                detaleExoDedMulxMil.add("Multas 1.5 por mil: Multa Proceso liquidación: valor: " + valMultaProcesoLiquidacion + "\n");
                System.out.println("Multas 1.5 por mil:Multa Proceso liquidación: valor: " + valMultaProcesoLiquidacion);
            }
            //Base imponible---------------------------------
            //-----------------------------------------------
            valRecargos = valMultaPlazoDeclaracion.add(valDatFalso).add(valMultaProcesoLiquidacion);
            valRecargos.setScale(2, RoundingMode.HALF_UP);
            System.out.println("Valor de los recargos" + valRecargos);
            patente15milValActual.setPat15valRecargos(valRecargos);
            objPat15MilAux = new Patente15xmilValoracion();
            objPat15MilExtAux = new Patente15xmilValoracionExtras();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }

    }

    public void calculaTotalSubtotal() {
        valSubTotal = valTasaProc.add(valImpuesto15xMil);
        valSubTotal.setScale(2, RoundingMode.HALF_UP);
        System.out.println("Valor del subtotal" + valSubTotal);
        patente15milValActual.setPat15valSubtotal(valSubTotal);
        valTotal = valSubTotal.add(valRecargos);
        valTotal.setScale(2, RoundingMode.HALF_UP);
        System.out.println("Valor del total" + valTotal);
        patente15milValActual.setPat15valTotal(valTotal);
        verBotDetDeducciones = 1;
    }

    public void verPanelDetalleDeducciones() {
        verDetDeducciones = 1;
    }

    public void inicializarValores() {
        valBaseImponible = null;
        valRecargos = null;
        valImpuesto15xMil = null;
        valTasaProc = null;
        valSubTotal = null;
        valTotal = null;
        valTotAnual = null;
    }

    public void guardaPatenteDet15xMil() {
        try {
            if (verExistePateneAnioxMil() == false) {
                CatalogoDetalle objCatDetAux = new CatalogoDetalle();
                objCatDetAux = catalogoDetalleServicio.buscarPorCodigoCatDet(catDetAnioBalance.getCatdetCodigo());
                patente15milValActual.setPat15valAnioBalance(Integer.parseInt(objCatDetAux.getCatdetTexto()));
                objCatDetAux = new CatalogoDetalle();
                objCatDetAux = catalogoDetalleServicio.buscarPorCodigoCatDet(catDetAnioDeclara.getCatdetCodigo());
                patente15milValActual.setPat15valAnioDecla(Integer.parseInt(objCatDetAux.getCatdetTexto()));
                patente15milValActual.setPatCodigo(patenteActual);
                unoPCinoPorMilServicio.crearPatenteValoracion15xMil(patente15milValActual);
                guardaPatExoDedMul15xMilValorCero();
                unoPCinoPorMilServicio.editarPatenteValoracion15xMil(patente15milValActual);
                addSuccessMessage("Guardado Exitosamente", "Patente 1.5 Mil Valoración Guardado");
                patente15milValActual = new Patente15xmilValoracion();
                inicializar();
            } else {
                addSuccessMessage("Ya esta determinada la patente 1.5 x mil para este anio", "Ya esta determinada la patente 1.5 x mil para este anio");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public boolean verExistePateneAnioxMil() {
        boolean existePatAnio = false;
        try {
            CatalogoDetalle objCatDetAnio = new CatalogoDetalle();
            objCatDetAnio = catalogoDetalleServicio.buscarPorCodigoCatDet(catDetAnioDeclara.getCatdetCodigo());
            Patente15xmilValoracion objPatValExiste = new Patente15xmilValoracion();
            objPatValExiste = unoPCinoPorMilServicio.buscaPatValoracion15xMilPorAnio(patenteActual.getPatCodigo(), Integer.parseInt(objCatDetAnio.getCatdetTexto()));
            if (objPatValExiste == null) {
                existePatAnio = false;
            } else {
                existePatAnio = true;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
        return existePatAnio;
    }

    public void actualizaPatenteDet15xMil() {
        try {
            if (habilitaEdicion == false) {
                CatalogoDetalle objCatDetAux = new CatalogoDetalle();
                objCatDetAux = catalogoDetalleServicio.buscarPorCodigoCatDet(catDetAnioBalance.getCatdetCodigo());
                patente15milValActual.setPat15valAnioBalance(Integer.parseInt(objCatDetAux.getCatdetTexto()));
                objCatDetAux = new CatalogoDetalle();
                objCatDetAux = catalogoDetalleServicio.buscarPorCodigoCatDet(catDetAnioDeclara.getCatdetCodigo());
                patente15milValActual.setPat15valAnioDecla(Integer.parseInt(objCatDetAux.getCatdetTexto()));
                patente15milValActual.setPatCodigo(patenteActual);
                unoPCinoPorMilServicio.editarPatenteValoracion15xMil(patente15milValActual);
                addSuccessMessage("Guardado Exitosamente", "Patente 1.5 Mil Valoración Guardado");
                patente15milValActual = new Patente15xmilValoracion();
                inicializar();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public void guardaPatenteDet15xMilValorCero() {
        try {
            patente15milValActual.setPatCodigo(patenteActual);
            CatalogoDetalle objCatDetAux = new CatalogoDetalle();
            objCatDetAux = catalogoDetalleServicio.buscarPorCodigoCatDet(catDetAnioBalance.getCatdetCodigo());
            patente15milValActual.setPat15valAnioBalance(Integer.parseInt(objCatDetAux.getCatdetTexto()));
            objCatDetAux = new CatalogoDetalle();
            objCatDetAux = catalogoDetalleServicio.buscarPorCodigoCatDet(catDetAnioDeclara.getCatdetCodigo());
            patente15milValActual.setPat15valAnioDecla(Integer.parseInt(objCatDetAux.getCatdetTexto()));
            patente15milValActual.setPat15valNumSucursales(0);
            // patente15milValActual.setPat15valAnioBalance(0);
            patente15milValActual.setPat15valIngresoAnual(BigDecimal.ZERO);
            patente15milValActual.setPat15valActivos(BigDecimal.ZERO);
            patente15milValActual.setPat15valPasivosCorriente(BigDecimal.ZERO);
            patente15milValActual.setPat15valPasivosConting(BigDecimal.ZERO);
            patente15milValActual.setPat15valOtrasDeducciones(BigDecimal.ZERO);
            patente15milValActual.setPat15valBaseImponible(BigDecimal.ZERO);
            patente15milValActual.setPat15valTasaProc(BigDecimal.ZERO);
            patente15milValActual.setPat15valImpuesto(BigDecimal.ZERO);
            patente15milValActual.setPat15valSubtotal(BigDecimal.ZERO);
            patente15milValActual.setPat15valRecargos(BigDecimal.ZERO);
            patente15milValActual.setPat15valTotal(BigDecimal.ZERO);
            patente15milValActual.setPat15valActivo(false);
            unoPCinoPorMilServicio.crearPatenteValoracion15xMil(patente15milValActual);
            guardaPatExoDedMul15xMilValorCero();
            addSuccessMessage("Guardado Exitosamente", "Patente 1.5 Mil Valoración Guardado");
            inicializar();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public void guardaPatExoDedMul15xMilValorCero() {
        try {
            Patente15xmilValoracionExtras objPat15Extra = new Patente15xmilValoracionExtras();
            objPat15Extra.setPat15valCodigo(patente15milValActual);
            AdicionalesDeductivos objAdiDec = new AdicionalesDeductivos();
            objAdiDec = adicionalesDeductivosServicio.buscarAdicionesDeductivosXNemonico("ADIDED_PAT");
            objPat15Extra.setAdidedCodigo(objAdiDec);
            objPat15Extra.setPat15valextBase(BigDecimal.ZERO);
            objPat15Extra.setPat15valextValor(BigDecimal.ZERO);
            objPat15Extra.setPat15valextEntiPub(false);
            objPat15Extra.setPat15valextFunBenEdu(false);
            objPat15Extra.setPat15valextLeyFomArtes(false);
            objPat15Extra.setPat15valextActAgro(false);
            objPat15Extra.setPat15valextCoop(false);
            objPat15Extra.setPat15valextMultiNac(false);
            objPat15Extra.setPat15valNumMesesIncum(0);
            objPat15Extra.setPat15valEvaluaDatFalsos(0);
            objPat15Extra.setPat15valProcesoLiquidacion(0);
            CatalogoDetalle objCatDetAux = new CatalogoDetalle();
            objCatDetAux = catalogoDetalleServicio.buscarPorCodigoCatDet(catDetAnioDeclara.getCatdetCodigo());
            objPat15Extra.setPat15valAnio(Integer.parseInt(objCatDetAux.getCatdetTexto()));
            unoPCinoPorMilServicio.crearPatenteValoracion15xMilExtra(objPat15Extra);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public int getVerPanelDetalleImp() {
        return verPanelDetalleImp;
    }

    public void setVerPanelDetalleImp(int verPanelDetalleImp) {
        this.verPanelDetalleImp = verPanelDetalleImp;
    }

    public Patente getPatenteActual() {
        return patenteActual;
    }

    public void setPatenteActual(Patente patenteActual) {
        this.patenteActual = patenteActual;
    }

    public BigDecimal getValBaseImponible() {
        return valBaseImponible;
    }

    public void setValBaseImponible(BigDecimal valBaseImponible) {
        this.valBaseImponible = valBaseImponible;
    }

    public BigDecimal getValImpuesto15xMil() {
        return valImpuesto15xMil;
    }

    public void setValImpuesto15xMil(BigDecimal valImpuesto15xMil) {
        this.valImpuesto15xMil = valImpuesto15xMil;
    }

    public BigDecimal getValTotal() {
        return valTotal;
    }

    public void setValTotal(BigDecimal valTotal) {
        this.valTotal = valTotal;
    }

    public BigDecimal getValSubTotal() {
        return valSubTotal;
    }

    public void setValSubTotal(BigDecimal valSubTotal) {
        this.valSubTotal = valSubTotal;
    }

    public Patente15xmilValoracion getPatente15milValActual() {
        return patente15milValActual;
    }

    public void setPatente15milValActual(Patente15xmilValoracion patente15milValActual) {
        this.patente15milValActual = patente15milValActual;
    }

    public String getBuscNumPat() {
        return buscNumPat;
    }

    public void setBuscNumPat(String buscNumPat) {
        this.buscNumPat = buscNumPat;
    }

    public int getVerBuscaPatente() {
        return verBuscaPatente;
    }

    public void setVerBuscaPatente(int verBuscaPatente) {
        this.verBuscaPatente = verBuscaPatente;
    }

    public String getNumPatente() {
        return numPatente;
    }

    public void setNumPatente(String numPatente) {
        this.numPatente = numPatente;
    }

    public BigDecimal getValTasaProc() {
        return valTasaProc;
    }

    public void setValTasaProc(BigDecimal valTasaProc) {
        this.valTasaProc = valTasaProc;
    }

    public BigDecimal getValRecargos() {
        return valRecargos;
    }

    public void setValRecargos(BigDecimal valRecargos) {
        this.valRecargos = valRecargos;
    }

    public CatalogoDetalle getCatDetAnioBalance() {
        return catDetAnioBalance;
    }

    public void setCatDetAnioBalance(CatalogoDetalle catDetAnioBalance) {
        this.catDetAnioBalance = catDetAnioBalance;
    }

    public CatalogoDetalle getCatDetAnioDeclara() {
        return catDetAnioDeclara;
    }

    public void setCatDetAnioDeclara(CatalogoDetalle catDetAnioDeclara) {
        this.catDetAnioDeclara = catDetAnioDeclara;
    }

    public List<CatalogoDetalle> getListAnios() {
        return listAnios;
    }

    public void setListAnios(List<CatalogoDetalle> listAnios) {
        this.listAnios = listAnios;
    }

    public int getVerGuardar() {
        return verGuardar;
    }

    public void setVerGuardar(int verGuardar) {
        this.verGuardar = verGuardar;
    }

    public int getVerActualiza() {
        return verActualiza;
    }

    public void setVerActualiza(int verActualiza) {
        this.verActualiza = verActualiza;
    }

    public ArrayList<String> getDetaleExoDedMulxMil() {
        return detaleExoDedMulxMil;
    }

    public void setDetaleExoDedMulxMil(ArrayList<String> detaleExoDedMulxMil) {
        this.detaleExoDedMulxMil = detaleExoDedMulxMil;
    }

    public int getVerBotDetDeducciones() {
        return verBotDetDeducciones;
    }

    public void setVerBotDetDeducciones(int verBotDetDeducciones) {
        this.verBotDetDeducciones = verBotDetDeducciones;
    }

    public int getVerDetDeducciones() {
        return verDetDeducciones;
    }

    public void setVerDetDeducciones(int verDetDeducciones) {
        this.verDetDeducciones = verDetDeducciones;
    }

    public boolean isDeducciones() {
        return deducciones;
    }

    public boolean isExisteDedPatente() {
        return existeDedPatente;
    }

    public void setExisteDedPatente(boolean existeDedPatente) {
        this.existeDedPatente = existeDedPatente;
    }

    public String getBuscAnioPat() {
        return buscAnioPat;
    }

    public void setBuscAnioPat(String buscAnioPat) {
        this.buscAnioPat = buscAnioPat;
    }

    public int getVerCrear() {
        return verCrear;
    }

    public void setVerCrear(int verCrear) {
        this.verCrear = verCrear;
    }

    public CatalogoDetalle getCatDetAnio() {
        return catDetAnio;
    }

    public void setCatDetAnio(CatalogoDetalle catDetAnio) {
        this.catDetAnio = catDetAnio;
    }

}
