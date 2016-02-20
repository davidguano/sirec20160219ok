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
import ec.sirec.ejb.entidades.PatenteValoracion;
import ec.sirec.ejb.entidades.PatenteValoracionExtras;
import ec.sirec.ejb.servicios.AdicionalesDeductivosServicio;
import ec.sirec.ejb.servicios.CatalogoDetalleServicio;
import ec.sirec.ejb.servicios.PatenteServicio;
import ec.sirec.web.base.BaseControlador;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.Buffer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
public class GestionDetPatenteControlador extends BaseControlador {

    @EJB
    private CatalogoDetalleServicio catalogoDetalleServicio;

    @EJB
    private AdicionalesDeductivosServicio adicionalesDeductivosServicio;
    @EJB
    private PatenteServicio patenteServicio;
    private Patente patenteActual;
    private PatenteValoracion patenteValoracionActal;
    private int verPanelDetalleImp;
    private BigDecimal valPatrimonio;
    private BigDecimal valImpPatente;
    private BigDecimal valImpBomberos;
    private BigDecimal valSubTotal;
    private BigDecimal valTotal;
    private BigDecimal valTasaProc;
    private BigDecimal valDeduciones;
    private BigDecimal valBaseImpNegativa;
    private int activaBaseImponible;
    private CatalogoDetalle catDetAnio;
    private List<CatalogoDetalle> listAnios;
    private boolean habilitaEdicion;
    private String numPatente;
    DatoGlobal datoGlobalActual;
    private String buscNumPat;
    private int verBuscaPatente;
    private static final Logger LOGGER = Logger.getLogger(GestionDetPatenteControlador.class.getName());
    private int verguarda;
    private int verActualiza;
    long diferenciaMils;
    long segundos = 0;
    long dias = 0;
    long horas = 0;
    long minutos = 0;
    private int verDetDeducciones;
    private int verBotDetDeducciones;
    ArrayList<String> detaleExoDedMul;

    /**
     * Creates a new instance of GestionDetPatenteControlador
     */
    @PostConstruct
    public void inicializar() {
        try {
            detaleExoDedMul = new ArrayList<String>();
            activaBaseImponible = 0;
            verBuscaPatente = 0;
            inicializarValCalcula();
            datoGlobalActual = new DatoGlobal();
            patenteActual = new Patente();
            patenteValoracionActal = new PatenteValoracion();
            verPanelDetalleImp = 0;
            habilitaEdicion = false;
            numPatente = "";
            buscNumPat = "";
            catDetAnio = new CatalogoDetalle();
            verguarda = 0;
            verActualiza = 0;
            verDetDeducciones = 0;
            verBotDetDeducciones = 0;
            listarAnios();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public GestionDetPatenteControlador() {
    }

    public void activaPanelDetalleImpuestos() {
        verPanelDetalleImp = 1;
    }

    public void limpiaPanelDetalleImpuestos() {
        patenteValoracionActal.setPatvalActivos(null);
        patenteValoracionActal.setPatvalPasivos(null);
        patenteValoracionActal.setPatvalPatrimonio(null);
        patenteValoracionActal.setPatvalImpuesto(null);
        patenteValoracionActal.setPatvalDeducciones(null);
        patenteValoracionActal.setPatvalTasaBomb(null);
        patenteValoracionActal.setPatvalSubtotal(null);
        patenteValoracionActal.setPatvalTasaProc(null);
        patenteValoracionActal.setPatvalTotal(null);
        detaleExoDedMul = null;
        verBotDetDeducciones = 0;
        verDetDeducciones = 0;
    }

    public void listarAnios() throws Exception {
        listAnios = catalogoDetalleServicio.listarPorNemonicoCatalogo("ANIOS");
    }

    public void cargarNumPatente() {
        patenteActual = (Patente) this.getSession().getAttribute("patente");
        if (patenteActual == null) {
            numPatente = null;
        } else {
            numPatente = "AE-MPM-" + patenteActual.getPatCodigo();
        }
    }

    public void calcularValorPatrimonio() {
        try {
            detaleExoDedMul = new ArrayList<String>();
            valPatrimonio = patenteValoracionActal.getPatvalActivos().subtract(patenteValoracionActal.getPatvalPasivos());
            valPatrimonio = valPatrimonio.setScale(2, RoundingMode.HALF_UP);
            patenteValoracionActal.setPatvalPatrimonio(valPatrimonio);
            if (valPatrimonio.compareTo(BigDecimal.ZERO) < 0) {
                activaBaseImponible = 1;
            } else {
                activaBaseImponible = 0;
                valBaseImpNegativa = BigDecimal.ZERO;
                calculaImpuestoPatente();
            }

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void calcularValorBaseImponible() {
        try {
            activaBaseImponible = 0;
            valPatrimonio = valBaseImpNegativa;
            valPatrimonio = valPatrimonio.setScale(2, RoundingMode.HALF_UP);
            patenteValoracionActal.setPatvalPatrimonio(valPatrimonio);
            calculaImpuestoPatente();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void calculaImpuestoPatente() {
        BigDecimal impFracBasica = BigDecimal.ZERO;
        BigDecimal impExcede = BigDecimal.ZERO;
        //***************Validacion del impuesto de patente segun tabla 1 Art.18*************
        if (valPatrimonio.doubleValue() >= 0 && valPatrimonio.doubleValue() <= 50000) {
            impFracBasica = BigDecimal.valueOf(10);
            impExcede = BigDecimal.valueOf((0.25 / 100)).multiply(valPatrimonio.subtract(BigDecimal.valueOf(50000)));
            System.err.println("Opcion Rango 1");
        }
        if (valPatrimonio.doubleValue() >= 50000.01 && valPatrimonio.doubleValue() <= 100000) {
            impFracBasica = BigDecimal.valueOf(135);
            impExcede = BigDecimal.valueOf((0.50 / 100)).multiply(valPatrimonio.subtract(BigDecimal.valueOf(100000)));;
            System.err.println("Opcion Rango 2");
        }
        if (valPatrimonio.doubleValue() >= 100000.01 && valPatrimonio.doubleValue() <= 250000) {
            impFracBasica = BigDecimal.valueOf(385);
            impExcede = BigDecimal.valueOf((double) 1 / 100).multiply(valPatrimonio.subtract(BigDecimal.valueOf(250000)));
            System.err.println("Opcion Rango 3");

        }
        if (valPatrimonio.doubleValue() >= 250000.01) {
            impFracBasica = BigDecimal.valueOf(1885);
            impExcede = BigDecimal.valueOf((1.30 / 100)).multiply(valPatrimonio.subtract(BigDecimal.valueOf(250000)));;
            System.err.println("Opcion Rango 4");
        }
        valImpPatente = impFracBasica.add(impExcede);
        valImpPatente = valImpPatente.setScale(2, RoundingMode.HALF_UP);
        System.out.println("Valor impuesto patente total: " + valImpPatente);
        //***************Validacion de impuesto de patente segun tabla 2 Art.18 *************
        if (valPatrimonio.doubleValue() >= 250000.01 && valPatrimonio.doubleValue() <= 500000) {
            if (valImpPatente.doubleValue() >= 2500.00) {
                valImpPatente = BigDecimal.valueOf(2500.00);
                System.out.println("Valor impuesto tabla 2 opt1: " + valImpPatente);
            }
        }
        if (valPatrimonio.doubleValue() >= 500000.01 && valPatrimonio.doubleValue() <= 1000000) {
            if (valImpPatente.doubleValue() >= 5000.00) {
                valImpPatente = BigDecimal.valueOf(5000.00);
                System.out.println("Valor impuesto tabla 2 opt2: " + valImpPatente);
            }
        }
        if (valPatrimonio.doubleValue() >= 1000000.01 && valPatrimonio.doubleValue() <= 5000000) {
            if (valImpPatente.doubleValue() >= 7000.00) {
                valImpPatente = BigDecimal.valueOf(7000.00);
                System.out.println("Valor impuesto tabla 2 opt3: " + valImpPatente);
            }
        }
        if (valPatrimonio.doubleValue() >= 5000000.01 && valPatrimonio.doubleValue() <= 10000000) {
            if (valImpPatente.doubleValue() >= 8000.00) {
                valImpPatente = BigDecimal.valueOf(8000.00);
                System.out.println("Valor impuesto tabla 2 opt4: " + valImpPatente);
            }
        }
        if (valPatrimonio.doubleValue() >= 10000000.01) {
            if (valImpPatente.doubleValue() >= 10000.00) {
                valImpPatente = BigDecimal.valueOf(10000.00);
                System.out.println("Valor impuesto tabla 2 opt5: " + valImpPatente);
            }
        }
        patenteValoracionActal.setPatvalImpuesto(valImpPatente);
        calculaimpBomberos();
    }

    public void calculaimpBomberos() {
        try {
            BigDecimal valCuantia = BigDecimal.ZERO;
            DatoGlobal objDatglobAux = new DatoGlobal();
            objDatglobAux = patenteServicio.cargarObjDatGloPorNombre("Val_cuantia_bomberos");
            valCuantia = BigDecimal.valueOf(Double.parseDouble(objDatglobAux.getDatgloValor()));
            DatoGlobal objDatglobSueldoBasico = new DatoGlobal();
            objDatglobSueldoBasico = patenteServicio.cargarObjDatGloPorNombre("Val_sueldo_basico");
            BigDecimal valSueldoBasico = BigDecimal.valueOf(Double.parseDouble(objDatglobSueldoBasico.getDatgloValor()));
            valImpBomberos = valImpPatente.multiply(valCuantia);
            valImpBomberos = valImpBomberos.setScale(2, RoundingMode.HALF_UP);
            BigDecimal valExedeValSalarioBasico = BigDecimal.valueOf(0.3).multiply(valSueldoBasico);
            System.out.println("Valor imp bomberos: " + valImpBomberos);
            if (valImpBomberos.doubleValue() > valExedeValSalarioBasico.doubleValue()) {
                valImpBomberos = valExedeValSalarioBasico;
            }
            System.out.println("Valor imp bomberos validado: " + valImpBomberos);
            patenteValoracionActal.setPatvalTasaBomb(valImpBomberos);
            valSubTotal = valImpPatente.add(valImpBomberos);
            valSubTotal = valSubTotal.setScale(2, RoundingMode.HALF_UP);
            System.out.println("Valor subtotal: " + valSubTotal);
            patenteValoracionActal.setPatvalSubtotal(valSubTotal);
            calculaValorDeduccion();
            calculaTotal();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }

    }

    public void calculaValorDeduccion() {
        try {
            //------Exoneraciones, deducciones y multas------- 
            BigDecimal valAdDeductivo = BigDecimal.ZERO;
            BigDecimal valTasaProc = BigDecimal.ZERO;
            BigDecimal valEvaTributaria = BigDecimal.ZERO;
            BigDecimal valIncPlazoDeclaracion = BigDecimal.ZERO;
            BigDecimal valObtPatTardia = BigDecimal.ZERO;
            BigDecimal valIncumpleObligado = BigDecimal.ZERO;
            BigDecimal valIncumpleNoObligado = BigDecimal.ZERO;
            BigDecimal valIncumpleSujetosPasivos = BigDecimal.ZERO;
            BigDecimal valSueldoBasico = BigDecimal.ZERO;

            DatoGlobal objDatglobSueldoBasico = new DatoGlobal();
            objDatglobSueldoBasico = patenteServicio.cargarObjDatGloPorNombre("Val_sueldo_basico");
            valSueldoBasico = BigDecimal.valueOf(Double.parseDouble(objDatglobSueldoBasico.getDatgloValor()));

            //-----------------------------Variables----------------------------------------------  
            BigDecimal valPerdidaMitad = BigDecimal.ZERO; //Valor Perdida mitad
            BigDecimal valPerdidaTerceraParte = BigDecimal.ZERO; //Valor Perdida Tercera parte
            BigDecimal valDatFalso = BigDecimal.ZERO; //Valor Falsedad Datos
            BigDecimal valBaseImponible = BigDecimal.ZERO;
            BigDecimal valMultaPlazoDeclaracion = BigDecimal.ZERO;//Valor multa plazo declaración
            PatenteValoracion objPatValoracionAux = new PatenteValoracion();
            objPatValoracionAux = patenteServicio.buscaPatValoracion(patenteActual.getPatCodigo());
            PatenteValoracionExtras objPatValorExtAux = new PatenteValoracionExtras();
            objPatValorExtAux = patenteServicio.buscaPatValExtraPorPatValoracion(objPatValoracionAux.getPatvalCodigo());
//*************************Adicionales Deductivos*********************************
            //if (objPatValorExtAux.getAdidedCodigo().getAdidedCodigo() != 0) { //Suspendido No se ha aclarado el uso de adicionales deductivos
            //AdicionalesDeductivos objAdiDed = new AdicionalesDeductivos();
            //objAdiDed = adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(objPatValorExtAux.getAdidedCodigo().getAdidedCodigo());
            //valAdDeductivo = objAdiDed.getAdidedValorfijo();
            //System.out.println("Valor Adicional deductivo:" + valAdDeductivo);
            //}
//************************Exoneraciones******************************************           
            if (objPatValorExtAux.getPatvalextReduccionMitad() == true) { //Reducción Perdidas a la mitad
                valPerdidaMitad = valImpPatente.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                valPerdidaMitad.setScale(verBuscaPatente, RoundingMode.HALF_UP);
                valImpPatente = valPerdidaMitad;
                detaleExoDedMul.add("Exoneración: Perdidad (mitad) valor Imp.:" + valImpPatente + "\n");
                System.out.println("Exoneración: Perdida mitad impuesto:valimp: " + valImpPatente);
            }
            if (objPatValorExtAux.getPatvalextReduccion3eraparte() == true) {//Reducción`Perdidad tercera parte
                valPerdidaTerceraParte = valImpPatente.divide(BigDecimal.valueOf(3), 2, RoundingMode.UP);
                valPerdidaTerceraParte.setScale(2, RoundingMode.HALF_UP);
                valImpPatente = valPerdidaTerceraParte;
                detaleExoDedMul.add("Exoneración: Perdidad (tercera parte) valor Imp.:" + valImpPatente + "\n");
                System.out.println("Exoneración: Perdida tercera parte impuesto: valimp: " + valImpPatente);
            }
            //Exoneracion Artesano Calificado---------------------------
            if (objPatValorExtAux.getPatvalextExonArtCalificado() == true) {
                valImpPatente = BigDecimal.ZERO;
                detaleExoDedMul.add("Exoneración: Artesano Calificado: valor Imp." + valImpPatente + "\n");
                System.out.println("Exonera impuesto artesano:valimp: " + valImpPatente);
                patenteValoracionActal.setPatvalImpuesto(valImpPatente);
            }
//*****************************Multas*******************************   
            //Incumplimiento de plazo de declaracion de mulas articulo 9 -------------
            if (objPatValorExtAux.getPatvalextNumMesesIncum() != 0) {
                BigDecimal porcIncPlazoDeclara = BigDecimal.ZERO;
                DatoGlobal objDatglobAux = new DatoGlobal();
                objDatglobAux = patenteServicio.cargarObjDatGloPorNombre("Val_porc_inc_noti_obligado");
                porcIncPlazoDeclara = BigDecimal.valueOf(Double.parseDouble(objDatglobAux.getDatgloValor()));
                valIncPlazoDeclaracion = porcIncPlazoDeclara.multiply(valImpPatente).multiply(BigDecimal.valueOf(objPatValorExtAux.getPatvalextNumMesesIncum())); //<--Valor a deducir-->
                detaleExoDedMul.add("Multas: Incumple plazo declaración (Num meses): valor" + valIncPlazoDeclaracion + "\n");
                System.out.println("Multas:Incumple plazo de declaración de patentes (Num meses):" + valIncPlazoDeclaracion);
            }
            //if (verificaPatentePrimeraVez() == true) {
            //int diasRetraso = evaluaPatenteTardia();
            // if (diasRetraso > 30) {
            //  BigDecimal porcValImpTardio = BigDecimal.ZERO;
            // DatoGlobal objDatglobAux = new DatoGlobal();
            // objDatglobAux = patenteServicio.cargarObjDatGloPorNombre("Val_obtiene_pat_tardiamente");
            // porcValImpTardio = BigDecimal.valueOf(Double.parseDouble(objDatglobAux.getDatgloValor()));
            // valObtPatTardia = porcValImpTardio.multiply(valSueldoBasico);
            // detaleExoDedMul.append("Multas:Incumple declaración patente tardia (primera vez):" + valIncumpleObligado + "\n");
            // System.out.println("Multas:Incumple declaración patente tardia:" + valIncumpleObligado);
            // }
            //}
            //Incumplimiento de notificacion------------------------------
            if (objPatValorExtAux.getPatvalextObligado() == true) { //obligado
                BigDecimal porcIncObligado = BigDecimal.ZERO;
                DatoGlobal objDatglobAux = new DatoGlobal();
                objDatglobAux = patenteServicio.cargarObjDatGloPorNombre("Val_porc_inc_noti_obligado");
                porcIncObligado = BigDecimal.valueOf(Double.parseDouble(objDatglobAux.getDatgloValor()));
                valIncumpleObligado = porcIncObligado.multiply(valSueldoBasico);
                valIncumpleObligado.setScale(2, RoundingMode.HALF_UP); //<--Valor a deducir-->
                detaleExoDedMul.add("Multas:Incumple notificacion (Obligado a llevar cont.) valor:" + valIncumpleObligado + "\n");
                System.out.println("Multas:Incumple notificacion obligado" + valIncumpleObligado);
            }
            if (objPatValorExtAux.getPatvalextNoObligado() == true) {//No obligado
                BigDecimal porcIncNoObligado = BigDecimal.ZERO;
                DatoGlobal objDatglobAux = new DatoGlobal();
                objDatglobAux = patenteServicio.cargarObjDatGloPorNombre("Val_porc_inc_noti_no_obligado");
                porcIncNoObligado = BigDecimal.valueOf(Double.parseDouble(objDatglobAux.getDatgloValor()));
                valIncumpleNoObligado = porcIncNoObligado.multiply(valSueldoBasico);
                valIncumpleNoObligado.setScale(2, RoundingMode.HALF_UP); //<--Valor a deducir-->
                detaleExoDedMul.add("Multas:Incumple notificacion (No Obligado a llevar cont.) valor:" + valIncumpleNoObligado + "\n");
                System.out.println("Multas:Incumple notificacion no obligado" + valIncumpleNoObligado);
            }
            //Incumplimiento sujetos pasivos exentos de pago----------------
            if (objPatValorExtAux.getPatvalextIncumPlazoDecla() == true) {//No obligado
                BigDecimal porcIncSujetosPasivos = BigDecimal.ZERO;
                DatoGlobal objDatglobAux = new DatoGlobal();
                objDatglobAux = patenteServicio.cargarObjDatGloPorNombre("Val_porc_inc_sujetos_pasivos");
                porcIncSujetosPasivos = BigDecimal.valueOf(Double.parseDouble(objDatglobAux.getDatgloValor()));
                valIncumpleSujetosPasivos = porcIncSujetosPasivos.multiply(valSueldoBasico);
                valIncumpleSujetosPasivos.setScale(2, RoundingMode.HALF_UP); //<--Valor a deducir-->
                detaleExoDedMul.add("Multas:Incumplimiento (Plazo de declaración de patentes sujetos pasivos extentos de pago) valor:" + valIncumpleSujetosPasivos + "\n");
                System.out.println("Multas:Incumplimiento (Plazo de declaración de patentes sujetos pasivos extentos de pago) valor:" + valIncumpleSujetosPasivos);
            }

            //Incumplimiento plazo de declaración de patentes------------
            //if (objPatValorExtAux.getPatvalextNumMesesIncum() != 0) { //Duplicado
            // BigDecimal porcentajeImp = BigDecimal.ZERO;
            // DatoGlobal objDatglobAux = new DatoGlobal();
            //objDatglobAux = patenteServicio.cargarObjDatGloPorNombre("Val_porc_incumple_declaracion");
            //porcentajeImp = BigDecimal.valueOf(Double.parseDouble(objDatglobAux.getDatgloValor()));
            // valMultaPlazoDeclaracion = (valImpPatente.multiply(porcentajeImp)).multiply(BigDecimal.valueOf(objPatValorExtAux.getPatvalextNumMesesIncum()));
            // valMultaPlazoDeclaracion.setScale(2, RoundingMode.HALF_UP);
            //System.out.println("Multas:Incumple plazo declaracion:valor" + valMultaPlazoDeclaracion);
            //}
            //Falsedad de datos------------------------------------------
            if (objPatValorExtAux.getPatentePorcDatosfalsos() != 0) {
                BigDecimal valPorcentajeDatosFalos = BigDecimal.valueOf(objPatValorExtAux.getPatentePorcDatosfalsos().doubleValue()).divide(BigDecimal.valueOf(100));
                valDatFalso = valSueldoBasico.multiply(valPorcentajeDatosFalos);
                valDatFalso.setScale(2, RoundingMode.HALF_UP);//<--Valor a deducir-->
                detaleExoDedMul.add("Multas:Falsedad de datos: valor: " + valDatFalso + "\n");
                System.out.println("Multas:Falsedad de datos: valor: " + valDatFalso);
            }
            //Evasion Tributaria------------------------------------------
            if (!objPatValorExtAux.getPatenteEvasionTributaria().equals("")) {
                switch (Integer.parseInt(objPatValorExtAux.getPatenteEvasionTributaria())) {
                    case 1:
                        valEvaTributaria = valImpPatente;
                        break;
                    case 2:
                        valEvaTributaria = valImpPatente.multiply(BigDecimal.valueOf(2));
                        break;
                    case 3:
                        valEvaTributaria = valImpPatente.multiply(BigDecimal.valueOf(3));
                        break;
                }
                valEvaTributaria.setScale(2, RoundingMode.HALF_UP);//<--Valor a deducir-->
                detaleExoDedMul.add("Multas:Evación tributaria Tipo 1 : valor: " + valEvaTributaria + "\n");
                System.out.println("Multas:Evación tributaria Tipo 1 : valor: " + valEvaTributaria);
            }
            valDeduciones = valMultaPlazoDeclaracion.add(valDatFalso).add(valTasaProc).add(valEvaTributaria).add(valIncumpleObligado).add(valObtPatTardia).add(valIncumpleNoObligado).add(valIncumpleSujetosPasivos);
            System.out.println("Valor total deducciones:" + valDeduciones);
            patenteValoracionActal.setPatvalDeducciones(valDeduciones);
            objPatValorExtAux = new PatenteValoracionExtras();
            objPatValoracionAux = new PatenteValoracion();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public void verPanelDetalleDeducciones() {
        verDetDeducciones = 1;
    }

    public int evaluaPatenteTardia() {
        int diasTardeFecha;
        int diasIniciaFinMes;
        diasTardeFecha = retornaDiasTardiosObtencionPatente((Timestamp) patenteActual.getPatInicioActEco());
        diasIniciaFinMes = retornaNumDiaFecIniActiFinMes();
        int totDiasTranscurridos = diasTardeFecha - diasIniciaFinMes;
        return totDiasTranscurridos;
    }

    public int retornaDiasTardiosObtencionPatente(Timestamp fechaMenor) {
        java.util.Date today = new java.util.Date();
        java.sql.Timestamp fechaMayor = new java.sql.Timestamp(today.getTime());
        diferenciaMils = fechaMayor.getTime() - fechaMenor.getTime();
        // obtenemos los segundos
        segundos = diferenciaMils / 1000;
        // obtener los dias
        dias = segundos / (3600 * 24);
        segundos = segundos % (3600 * 24);
        // obtenemos las horas
        horas = segundos / 3600;
        // restamos las horas para continuar con minutos
        segundos -= horas * 3600;
        // igual que el paso anterior
        minutos = segundos / 60;
        segundos -= minutos * 60;
        int tiempoDias = Integer.valueOf(String.valueOf(dias));
        return tiempoDias;
    }

    public boolean verificaPatentePrimeraVez() {
        boolean patPrimeraVez = false;
        try {
            java.util.Date date = new Date();
            Calendar calendarioIniActi = Calendar.getInstance();
            calendarioIniActi.setTime(date);
            int anio = calendarioIniActi.get(Calendar.YEAR);
            patPrimeraVez = patenteServicio.buscaPatPrimeraVez(patenteActual.getPatCodigo(), patenteActual.getCatpreCodigo().getCatpreCodigo(), anio);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return patPrimeraVez;
    }

    public int retornaNumDiaFecIniActiFinMes() {
        Calendar calendarioIniActi = Calendar.getInstance();
        calendarioIniActi.setTime(patenteActual.getPatInicioActEco());
        int numeroMes = calendarioIniActi.get(Calendar.MONTH);
        Calendar calendarioDiaInicio = Calendar.getInstance();
        Calendar calendarioDiaFin = Calendar.getInstance();
        Calendar gc = new GregorianCalendar();
        gc.set(Calendar.MONTH, numeroMes);
        gc.set(Calendar.DAY_OF_MONTH, 1);
        Date monthStart = gc.getTime();
        calendarioDiaInicio.setTime(monthStart);
        int diaInicio = calendarioDiaInicio.get(calendarioDiaInicio.DAY_OF_MONTH);
        gc.add(Calendar.MONTH, numeroMes);
        gc.add(Calendar.DAY_OF_MONTH, -1);
        Date monthEnd = gc.getTime();
        calendarioDiaFin.setTime(monthEnd);
        int diaFin = calendarioDiaFin.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("Calcula mes inicia fecha : " + format.format(monthStart));
        System.out.println("Calcula mes  fin  fecha : " + format.format(monthEnd));
        int diferenciaDias = diaFin - diaInicio;
        System.out.println("Dia inicial: " + diaInicio);
        System.out.println("Dia Final: " + diaFin);
        System.out.println("Diferencia entre meses:" + diferenciaDias);
        return diferenciaDias;
    }

    public void calculaTotal() {
        try {
            //Tasa de procesamiento-------------------------------
            DatoGlobal objDatglobAuxTp = new DatoGlobal();
            objDatglobAuxTp = patenteServicio.cargarObjDatGloPorNombre("Val_tasa_procesamiento");
            valTasaProc = BigDecimal.valueOf(Double.parseDouble(objDatglobAuxTp.getDatgloValor()));
            patenteValoracionActal.setPatvalTasaProc(valTasaProc);
            //---------------------------------------------------- 
            valTotal = valSubTotal.subtract(valDeduciones).add(valTasaProc);
            valTotal.setScale(2, RoundingMode.HALF_UP);
            patenteValoracionActal.setPatvalTotal(valTotal);
            verBotDetDeducciones = 1;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public void guardaPatenteValoracion() {
        try {
            CatalogoDetalle objCatDetAux = new CatalogoDetalle();
            objCatDetAux = catalogoDetalleServicio.buscarPorCodigoCatDet(catDetAnio.getCatdetCodigo());
            patenteValoracionActal.setPatvalAnio(Integer.parseInt(objCatDetAux.getCatdetTexto()));
            patenteValoracionActal.setPatCodigo(patenteActual);
            patenteServicio.editarPatenteValoracion(patenteValoracionActal);
            addSuccessMessage("Guardado Exitosamente", "Patente Valoración Guardado");
            patenteValoracionActal = new PatenteValoracion();
            inicializar();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public void buscarPatente() {
        try {
            verBuscaPatente = 1;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public void cagarPatenteActual() {
        verBotDetDeducciones = 0;
        verDetDeducciones = 0;
        try {
            patenteActual = patenteServicio.cargarObjPatente(Integer.parseInt(buscNumPat));
            if (patenteActual == null) {
                numPatente = null;
                patenteValoracionActal = new PatenteValoracion();
                verPanelDetalleImp = 0;
            } else {
                if (cargarExistePatValoracion()) {
                    patenteValoracionActal = patenteServicio.buscaPatValoracion(patenteActual.getPatCodigo());
                    System.out.println("Si encontro el objeto");
                    numPatente = generaNumPatente(); //"AE-MPM-" + patenteActual.getPatCodigo();
                    CatalogoDetalle objCatDetAux = new CatalogoDetalle();
                    objCatDetAux = catalogoDetalleServicio.buscarPoCatdetTexCatdetCod(patenteValoracionActal.getPatvalAnio() + "", "A" + patenteValoracionActal.getPatvalAnio());
                    catDetAnio = objCatDetAux;
                    verguarda = 0;
                    verActualiza = 1;
                } else {
                    System.out.println("No encontro el objeto");
                    numPatente = generaNumPatente();//"AE-MPM-" + patenteActual.getPatCodigo();
                    patenteValoracionActal = new PatenteValoracion();
                    verguarda = 1;
                    verActualiza = 0;

                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
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

    public boolean cargarExistePatValoracion() {
        boolean patValoracion = false;
        try {
            PatenteValoracion objPatValoracion = new PatenteValoracion();
            objPatValoracion = patenteServicio.buscaPatValoracion(patenteActual.getPatCodigo());
            if (objPatValoracion == null) {
                patValoracion = false;
            } else {
                patValoracion = true;
            }

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return patValoracion;
    }

    public void inicializarValCalcula() {
        valImpBomberos = null;
        valImpPatente = null;
        valSubTotal = null;
        valDeduciones = null;
        valTotal = null;
        valTasaProc = null;
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

    public PatenteValoracion getPatenteValoracionActal() {
        return patenteValoracionActal;
    }

    public void setPatenteValoracionActal(PatenteValoracion patenteValoracionActal) {
        this.patenteValoracionActal = patenteValoracionActal;
    }

    public BigDecimal getValPatrimonio() {
        return valPatrimonio;
    }

    public void setValPatrimonio(BigDecimal valPatrimonio) {
        this.valPatrimonio = valPatrimonio;
    }

    public BigDecimal getValImpPatente() {
        return valImpPatente;
    }

    public void setValImpPatente(BigDecimal valImpPatente) {
        this.valImpPatente = valImpPatente;
    }

    public BigDecimal getValImpBomberos() {
        return valImpBomberos;
    }

    public void setValImpBomberos(BigDecimal valImpBomberos) {
        this.valImpBomberos = valImpBomberos;
    }

    public String getNumPatente() {
        return numPatente;
    }

    public void setNumPatente(String numPatente) {
        this.numPatente = numPatente;
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

    public CatalogoDetalle getCatDetAnio() {
        return catDetAnio;
    }

    public void setCatDetAnio(CatalogoDetalle catDetAnio) {
        this.catDetAnio = catDetAnio;
    }

    public List<CatalogoDetalle> getListAnios() {
        return listAnios;
    }

    public void setListAnios(List<CatalogoDetalle> listAnios) {
        this.listAnios = listAnios;
    }

    public int getVerguarda() {
        return verguarda;
    }

    public void setVerguarda(int verguarda) {
        this.verguarda = verguarda;
    }

    public int getVerActualiza() {
        return verActualiza;
    }

    public void setVerActualiza(int verActualiza) {
        this.verActualiza = verActualiza;
    }

    public int getActivaBaseImponible() {
        return activaBaseImponible;
    }

    public void setActivaBaseImponible(int activaBaseImponible) {
        this.activaBaseImponible = activaBaseImponible;
    }

    public BigDecimal getValBaseImpNegativa() {
        return valBaseImpNegativa;
    }

    public void setValBaseImpNegativa(BigDecimal valBaseImpNegativa) {
        this.valBaseImpNegativa = valBaseImpNegativa;
    }

    public ArrayList<String> getDetaleExoDedMul() {
        return detaleExoDedMul;
    }

    public void setDetaleExoDedMul(ArrayList<String> detaleExoDedMul) {
        this.detaleExoDedMul = detaleExoDedMul;
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

}
