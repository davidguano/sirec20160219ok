/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.web.impuestos;

import ec.sirec.ejb.entidades.CatalogoDetalle;
import ec.sirec.ejb.entidades.Propietario;
import ec.sirec.ejb.entidades.SegUsuario;
import ec.sirec.ejb.servicios.CatalogoDetalleServicio;
import ec.sirec.ejb.servicios.CementerioReporteServicio;
import ec.sirec.ejb.servicios.PatenteReporteServicio;
import ec.sirec.ejb.servicios.PropietarioServicio;
import ec.sirec.web.base.BaseControlador;
import ec.sirec.web.util.UtilitariosCod;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.ProjectStage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Darwin Aldas
 */
@ManagedBean
@ViewScoped
public class GestionRepCementerioControlador extends BaseControlador {

    @EJB
    private CementerioReporteServicio cementerioReporteServicio;

    @EJB
    private PropietarioServicio propietarioServicio;

    @EJB
    private CatalogoDetalleServicio catalogoDetalleServicio;

    /**
     * Creates a new instance of GestionPatenteControlador
     */
    private static final Logger LOGGER = Logger.getLogger(GestionRepCementerioControlador.class.getName());
    private BigDecimal valorInicial;
    private BigDecimal valorFinal;
    private SegUsuario usuarioActual;
    private Date fechaInicial;
    private Date fechaFinal;
    private List<Object[]> listaReportes;
    private List<Object[]> listaRepSeleccion;
    private List<CatalogoDetalle> listaGenero;
    private CatalogoDetalle catDetGenero;
    String cadenaRegSeleccionados;
    private int seleccionaReporte;
    private int numReporte;
    private String tipoReporte;
    private Propietario propietarioActual;
    private List<Propietario> listaPropietarios;
    private String nombrePropietario;
    private CatalogoDetalle actEconomica;
    private CatalogoDetalle catDetParroquia;
    private List<CatalogoDetalle> listParroquias;
    private List<CatalogoDetalle> listActividadEconomica;
    private CatalogoDetalle catDetTipoPerNicho;
    private List<CatalogoDetalle> listTipoPerNicho;
    private int verResultados;

    @PostConstruct
    public void inicializar() {
        try {
            verResultados = 0;
            catDetTipoPerNicho = new CatalogoDetalle();
            listTipoPerNicho = new ArrayList<CatalogoDetalle>();
            nombrePropietario = "";
            actEconomica = new CatalogoDetalle();
            listActividadEconomica = new ArrayList<CatalogoDetalle>();
            listaPropietarios = new ArrayList<Propietario>();
            catDetGenero = new CatalogoDetalle();
            listaGenero = new ArrayList<CatalogoDetalle>();
            tipoReporte = null;
            listaReportes = new ArrayList<Object[]>();
            listaRepSeleccion = new ArrayList<Object[]>();
            cadenaRegSeleccionados = "";
            seleccionaReporte = 0;
            numReporte = 0;
            propietarioActual = new Propietario();
            catDetParroquia = new CatalogoDetalle();
            listParroquias = new ArrayList<CatalogoDetalle>();
            listarSectores();
            listarParroquias();
            listarTipoPersonaNicho();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void listarSectores() throws Exception {
        listaGenero = catalogoDetalleServicio.listarPorNemonicoCatalogo("GENERO_SX");
    }

    public void listarParroquias() throws Exception {
        listParroquias = catalogoDetalleServicio.listarPorNemonicoCatalogo("PARROQUIAS");
    }

    public void listarTipoPersonaNicho() throws Exception {
        listTipoPerNicho = catalogoDetalleServicio.listarPorNemonicoCatalogo("TIPO_NICHO");
    }

    public String retornaFecha() {
        String fechaSistema = "";
        Calendar fecha = Calendar.getInstance();
        int anio = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH) + 1;
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        int hora = fecha.get(Calendar.HOUR_OF_DAY);
        int minuto = fecha.get(Calendar.MINUTE);
        //int segundo = fecha.get(Calendar.SECOND);
        String mesLetras = "";
        switch (mes) {
            case 1:
                mesLetras = "Enero";
                break;
            case 2:
                mesLetras = "Febrero";
                break;
            case 3:
                mesLetras = "Marzo";
                break;
            case 4:
                mesLetras = "Abril";
                break;
            case 5:
                mesLetras = "Mayo";
                break;
            case 6:
                mesLetras = "Junio";
                break;
            case 7:
                mesLetras = "Julio";
                break;
            case 8:
                mesLetras = "Agosto";
                break;
            case 9:
                mesLetras = "Septiembre";
                break;
            case 10:
                mesLetras = "Octubre";
                break;
            case 11:
                mesLetras = "Noviembre";
                break;
            case 12:
                mesLetras = "Diciembre";
                break;
        }
        fechaSistema = "Fecha de emisión: " + dia + " de " + mesLetras + " del " + anio + " a las: " + hora + ":" + minuto;
        return fechaSistema;
    }

    public void buscaSelecciona() {
        verResultados = 0;
        listaReportes = new ArrayList<Object[]>();
        listaRepSeleccion = new ArrayList<Object[]>();
        switch (seleccionaReporte) {
            case 1:
                numReporte = 1;
                break;
            case 2:
                numReporte = 2;
                break;
            case 3:
                numReporte = 3;
                break;
            case 4:
                numReporte = 4;
                break;
            case 5:
                numReporte = 5;
                break;
            case 6:
                numReporte = 6;
                break;
            case 7:
                numReporte = 7;
                break;
//            case 8:
//                numReporte = 8;
//                break;
//            case 9:
//                numReporte = 9;
//                break;
//            case 10:
//                numReporte = 10;
//                break;

        }
    }

    public GestionRepCementerioControlador() {
    }

    public void cargaObjetosBitacora() {
        try {

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
//Reporte de negocios por rango de patrimonio 

    public void listarDatosReporte1() {
        try {
            Timestamp fec1 = new Timestamp(fechaInicial.getTime());
            Timestamp fec2 = new Timestamp(fechaFinal.getTime());
            String fechActual = "2016-01-01 11:01:28".toLowerCase();
            listaReportes = cementerioReporteServicio.listarDatReporte1(fec1, fec2, fechActual);
            if (listaReportes == null) {
                verResultados = 1;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
//Reporte de occisos por tipo de nicho

    public void listarDatosReporte2() {
        try {
            Timestamp fec1 = new Timestamp(fechaInicial.getTime());
            Timestamp fec2 = new Timestamp(fechaFinal.getTime());
            CatalogoDetalle objCat = new CatalogoDetalle();
            objCat = catalogoDetalleServicio.buscarPorCodigoCatDet(catDetTipoPerNicho.getCatdetCodigo());
            listaReportes = cementerioReporteServicio.listaDatReporte2(fec1, fec2, objCat.getCatdetCod());
            if (listaReportes == null) {
                verResultados = 1;
            } else {
                verResultados = 0;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
//Reporte de occisos por parroquia

    public void listarDatosReporte3() {
        try {
            System.out.println("Reporte de negocios por parroquia");
            Timestamp fec1 = new Timestamp(fechaInicial.getTime());
            Timestamp fec2 = new Timestamp(fechaFinal.getTime());
            listaReportes = cementerioReporteServicio.listaDatReporte3(fec1, fec2, catDetParroquia.getCatdetCodigo());
            if (listaReportes == null) {
                System.out.println("Ver vacio" + verResultados);
                verResultados = 1;
            } else {
                verResultados = 0;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
//Reporte de negocios por sector

    public void listarDatosReporte4() {
        try {
            Timestamp fec1 = new Timestamp(fechaInicial.getTime());
            Timestamp fec2 = new Timestamp(fechaFinal.getTime());
            CatalogoDetalle objCat = new CatalogoDetalle();
            objCat = catalogoDetalleServicio.buscarPorCodigoCatDet(catDetGenero.getCatdetCodigo());
            listaReportes = cementerioReporteServicio.listaDatReporte4(fec1, fec2, objCat.getCatdetCod());
            if (listaReportes == null) {
                verResultados = 1;
            } else {
                verResultados = 0;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
//Reporte denegocios por propietario

    public void listarDatosReporte5() {
        try {
            System.out.println("Reporte denegocios por propietario");
            Timestamp fec1 = new Timestamp(fechaInicial.getTime());
            Timestamp fec2 = new Timestamp(fechaFinal.getTime());
            listaReportes = cementerioReporteServicio.listaDatReporte5(fec1, fec2, propietarioActual.getProCi());
            if (listaReportes == null) {
                verResultados = 1;
            } else {
                verResultados = 0;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
//Reporte de negocios por actividad economica

    public void listarDatosReporte6() {
        System.out.println("Reporte de negocios por actividad economica");
        try {
            Timestamp fec1 = new Timestamp(fechaInicial.getTime());
            Timestamp fec2 = new Timestamp(fechaFinal.getTime());
            listaReportes = cementerioReporteServicio.listaDatReporte6(fec1, fec2, actEconomica.getCatdetCodigo());
            if (listaReportes == null) {
                verResultados = 1;
            } else {
                verResultados = 0;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
//Reporte d negocios por artesano calificado

    public void listarDatosReporte7() {
        try {
            System.out.println("Reporte d negocios por artesano calificado");
            Timestamp fec1 = new Timestamp(fechaInicial.getTime());
            Timestamp fec2 = new Timestamp(fechaFinal.getTime());
            listaReportes = cementerioReporteServicio.listaDatReporte7(fec1, fec2);
            if (listaReportes == null) {
                verResultados = 1;
            } else {
                verResultados = 0;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public List<Propietario> sugiereCedulaRuc(String cedulaNumero) {
        List<Propietario> listNombresProp = new ArrayList<Propietario>();
        try {
            listaPropietarios = propietarioServicio.listarPropietariosPorCedula(cedulaNumero);
            for (Propietario propietario : listaPropietarios) {
                listNombresProp.add(propietario);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listNombresProp;
    }

    public void onItemSelectPropietarioOcciso(SelectEvent event) throws Exception {
        Propietario objProp = (Propietario) event.getObject();
        propietarioActual = propietarioServicio.buscarPropietarioPorCedula(objProp.getProCi());
        nombrePropietario = propietarioActual.getProApellidos() + " " + propietarioActual.getProNombres();
    }

    public void ejecutaReporteSeleccionado() throws Exception {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        Map<String, String> params = externalContext.getRequestParameterMap();
        tipoReporte = params.get("paramTipoReporte");
        System.out.println("Parametro Enviado" + tipoReporte);
        switch (seleccionaReporte) {
            case 1:
                numReporte = 1;
                reporteDeudaMayor4Anios();
                break;
            case 2:
                numReporte = 2;
                reporteOccisoPertipoNicho();
                break;
            case 3:
                numReporte = 3;
                System.out.println("Reporte3");
                reporteOccisoParroquia();
                break;
            case 4:
                numReporte = 4;
                System.out.println("Reporte4");
                reporteOccisoGenero();
                break;
            case 5:
                numReporte = 5;
                System.out.println("Reporte5");
                // reporteNegPropietario();
                break;
            case 6:
                numReporte = 6;
                System.out.println("Reporte6");
                //  reporteNegActEconomica();
                break;

        }
    }

    public String reporteDeudaMayor4Anios() throws Exception {
        if (cargarRgistroSeleccionado().equals("")) {
            addErrorMessage("Debe seleccionar  al menos un registro", "Debe seleccionar al menos un registro");
        } else {
            //Conexion con local datasource
            usuarioActual = new SegUsuario();
            usuarioActual = (SegUsuario) this.getSession().getAttribute("usuario");
            UtilitariosCod util = new UtilitariosCod();
            Connection conexion = util.getConexion();
            byte[] fichero = null;
            JasperReport jasperReport = null;
            JasperPrint jasperPrint = new JasperPrint(); //Excel
            Map parameters = new HashMap();
            try {
                FacesContext context = FacesContext.getCurrentInstance();
                HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
                ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
                session.removeAttribute("reporteInforme");
                Timestamp fec1 = new Timestamp(fechaInicial.getTime());
                Timestamp fec2 = new Timestamp(fechaFinal.getTime());
                System.out.println("Fecha inical" + fec1);
                System.out.println("Fecha final" + fec2);
                parameters.put("usuario_genera", usuarioActual.getUsuNombres() + " " + usuarioActual.getUsuApellidos());
                parameters.put("fecha_genera", retornaFecha());
                parameters.put("logo_gad", servletContext.getRealPath("/imagenes/icons/gadPedroMoncayo.jpg"));
                parameters.put("rango_parametros", cargarRgistroSeleccionado());
                jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/patentes/rptOccisoDeuMas4Anios.jasper"));
                if (tipoReporte.equals("PDF")) {
                    fichero = JasperRunManager.runReportToPdf(jasperReport, parameters, conexion);
                    session.setAttribute("reporteInforme", fichero);
                    usuarioActual = new SegUsuario();
                }
                if (tipoReporte.equals("XLS")) {
                    jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conexion);
                    JRXlsExporter exporterXLS = new JRXlsExporter();
                    ByteArrayOutputStream xlsReport = new ByteArrayOutputStream();
                    exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
                    exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, xlsReport);
                    exporterXLS.exportReport();
                    fichero = xlsReport.toByteArray();
                    session.setAttribute("reporteInformeXls", fichero);
                    usuarioActual = new SegUsuario();
                }

            } catch (JRException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, null, e);
            } finally {
                if (conexion != null) {
                    conexion.close();
                }
            }
        }

        return null;
    }

    public String reporteOccisoParroquia() throws Exception {
        String nombreParroquia = "";
        CatalogoDetalle objCatDetAux = new CatalogoDetalle();
        objCatDetAux = catalogoDetalleServicio.buscarPorCodigoCatDet(catDetParroquia.getCatdetCodigo());
        nombreParroquia = objCatDetAux.getCatdetTexto();
        if (cargarRgistroSeleccionado().equals("")) {
            addErrorMessage("Debe seleccionar  al menos un registro", "Debe seleccionar al menos un registro");
        } else {
            //Conexion con local datasource
            Timestamp fec1 = new Timestamp(fechaInicial.getTime());
            Timestamp fec2 = new Timestamp(fechaFinal.getTime());
            System.out.println("Fecha inical" + fec1);
            System.out.println("Fecha final" + fec2);
            System.out.println("Parroquias" + catDetParroquia.getCatdetCodigo());
            usuarioActual = new SegUsuario();
            usuarioActual = (SegUsuario) this.getSession().getAttribute("usuario");
            UtilitariosCod util = new UtilitariosCod();
            Connection conexion = util.getConexion();
            byte[] fichero = null;
            JasperReport jasperReport = null;
            JasperPrint jasperPrint = new JasperPrint(); //Excel
            Map parameters = new HashMap();
            try {
                FacesContext context = FacesContext.getCurrentInstance();
                HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
                ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
                session.removeAttribute("reporteInforme");
                parameters.put("fecha_inicial", fec1);
                parameters.put("fecha_final", fec2);
                parameters.put("parroquia", catDetParroquia.getCatdetCodigo());
                parameters.put("nomParroquia", nombreParroquia);
                parameters.put("usuario_genera", usuarioActual.getUsuNombres() + " " + usuarioActual.getUsuApellidos());
                parameters.put("fecha_genera", retornaFecha());
                parameters.put("logo_gad", servletContext.getRealPath("/imagenes/icons/gadPedroMoncayo.jpg"));
                parameters.put("rango_parametros", cargarRgistroSeleccionado());
                jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/cementerios/rptOccisoPorParroquia.jasper"));
                if (tipoReporte.equals("PDF")) {
                    fichero = JasperRunManager.runReportToPdf(jasperReport, parameters, conexion);
                    session.setAttribute("reporteInforme", fichero);
                    usuarioActual = new SegUsuario();
                }
                if (tipoReporte.equals("XLS")) {
                    jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conexion);
                    JRXlsExporter exporterXLS = new JRXlsExporter();
                    ByteArrayOutputStream xlsReport = new ByteArrayOutputStream();
                    exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
                    exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, xlsReport);
                    exporterXLS.exportReport();
                    fichero = xlsReport.toByteArray();
                    session.setAttribute("reporteInformeXls", fichero);
                    usuarioActual = new SegUsuario();
                }

            } catch (JRException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, null, e);
            } finally {
                if (conexion != null) {
                    conexion.close();
                }
            }
        }

        return null;
    }

    public String reporteOccisoGenero() throws Exception {
        String nombreGenero = "";
        CatalogoDetalle objCatDetAux = new CatalogoDetalle();
        objCatDetAux = catalogoDetalleServicio.buscarPorCodigoCatDet(catDetGenero.getCatdetCodigo());
        nombreGenero = objCatDetAux.getCatdetTexto();
        if (cargarRgistroSeleccionado().equals("")) {
            addErrorMessage("Debe seleccionar  al menos un registro", "Debe seleccionar al menos un registro");
        } else {
            Timestamp fec1 = new Timestamp(fechaInicial.getTime());
            Timestamp fec2 = new Timestamp(fechaFinal.getTime());
            //Conexion con local datasource
            usuarioActual = new SegUsuario();
            usuarioActual = (SegUsuario) this.getSession().getAttribute("usuario");
            UtilitariosCod util = new UtilitariosCod();
            Connection conexion = util.getConexion();
            byte[] fichero = null;
            JasperReport jasperReport = null;
            JasperPrint jasperPrint = new JasperPrint(); //Excel
            Map parameters = new HashMap();
            try {
                FacesContext context = FacesContext.getCurrentInstance();
                HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
                ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
                session.removeAttribute("reporteInforme");
                parameters.put("fecha_inicial", fec1);
                parameters.put("fecha_final", fec2);
                parameters.put("tipoGenero", objCatDetAux.getCatdetCod());
                parameters.put("nomGenero", nombreGenero);
                parameters.put("usuario_genera", usuarioActual.getUsuNombres() + " " + usuarioActual.getUsuApellidos());
                parameters.put("fecha_genera", retornaFecha());
                parameters.put("logo_gad", servletContext.getRealPath("/imagenes/icons/gadPedroMoncayo.jpg"));
                parameters.put("rango_parametros", cargarRgistroSeleccionado());
                jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/cementerios/rptOccisoPorGenero.jasper"));
                if (tipoReporte.equals("PDF")) {
                    fichero = JasperRunManager.runReportToPdf(jasperReport, parameters, conexion);
                    session.setAttribute("reporteInforme", fichero);
                    usuarioActual = new SegUsuario();
                }
                if (tipoReporte.equals("XLS")) {
                    jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conexion);
                    JRXlsExporter exporterXLS = new JRXlsExporter();
                    ByteArrayOutputStream xlsReport = new ByteArrayOutputStream();
                    exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
                    exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, xlsReport);
                    exporterXLS.exportReport();
                    fichero = xlsReport.toByteArray();
                    session.setAttribute("reporteInformeXls", fichero);
                    usuarioActual = new SegUsuario();
                }

            } catch (JRException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, null, e);
            } finally {
                if (conexion != null) {
                    conexion.close();
                }
            }
        }

        return null;
    }

//    public String reporteNegPropietario() throws Exception {
//        if (cargarRgistroSeleccionado().equals("")) {
//            addErrorMessage("Debe seleccionar  al menos un registro", "Debe seleccionar al menos un registro");
//        } else {
//            Timestamp fec1 = new Timestamp(fechaInicial.getTime());
//            Timestamp fec2 = new Timestamp(fechaFinal.getTime());
//            //Conexion con local datasource
//            usuarioActual = new SegUsuario();
//            usuarioActual = (SegUsuario) this.getSession().getAttribute("usuario");
//            UtilitariosCod util = new UtilitariosCod();
//            Connection conexion = util.getConexion();
//            byte[] fichero = null;
//            JasperReport jasperReport = null;
//            JasperPrint jasperPrint = new JasperPrint(); //Excel
//            Map parameters = new HashMap();
//            try {
//                FacesContext context = FacesContext.getCurrentInstance();
//                HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
//                ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
//                session.removeAttribute("reporteInforme");
//                parameters.put("fecha_inicial", fec1);
//                parameters.put("fecha_final", fec2);
//                parameters.put("propietario", propietarioActual.getProCi());
//                parameters.put("usuario_genera", usuarioActual.getUsuNombres() + " " + usuarioActual.getUsuApellidos());
//                parameters.put("nomPropietario", nombrePropietario);
//                parameters.put("fecha_genera", retornaFecha());
//                parameters.put("logo_gad", servletContext.getRealPath("/imagenes/icons/gadPedroMoncayo.jpg"));
//                parameters.put("rango_parametros", cargarRgistroSeleccionado());
//                jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/patentes/rptNegocioPorPropietario.jasper"));
//                if (tipoReporte.equals("PDF")) {
//                    fichero = JasperRunManager.runReportToPdf(jasperReport, parameters, conexion);
//                    session.setAttribute("reporteInforme", fichero);
//                    usuarioActual = new SegUsuario();
//                }
//                if (tipoReporte.equals("XLS")) {
//                    jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conexion);
//                    JRXlsExporter exporterXLS = new JRXlsExporter();
//                    ByteArrayOutputStream xlsReport = new ByteArrayOutputStream();
//                    exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
//                    exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, xlsReport);
//                    exporterXLS.exportReport();
//                    fichero = xlsReport.toByteArray();
//                    session.setAttribute("reporteInformeXls", fichero);
//                    usuarioActual = new SegUsuario();
//                }
//
//            } catch (JRException ex) {
//                LOGGER.log(Level.SEVERE, null, ex);
//            } catch (Exception e) {
//                LOGGER.log(Level.SEVERE, null, e);
//            } finally {
//                if (conexion != null) {
//                    conexion.close();
//                }
//            }
//        }
//
//        return null;
//    }
//    public String reporteNegActEconomica() throws Exception {
//        String nombreActEconomica = "";
//        CatalogoDetalle objCatDetAux = new CatalogoDetalle();
//        objCatDetAux = catalogoDetalleServicio.buscarPorCodigoCatDet(actEconomica.getCatdetCodigo());
//        nombreActEconomica = objCatDetAux.getCatdetTexto();
//        if (cargarRgistroSeleccionado().equals("")) {
//            addErrorMessage("Debe seleccionar  al menos un registro", "Debe seleccionar al menos un registro");
//        } else {
//            Timestamp fec1 = new Timestamp(fechaInicial.getTime());
//            Timestamp fec2 = new Timestamp(fechaFinal.getTime());
//            //Conexion con local datasource
//            usuarioActual = new SegUsuario();
//            usuarioActual = (SegUsuario) this.getSession().getAttribute("usuario");
//            UtilitariosCod util = new UtilitariosCod();
//            Connection conexion = util.getConexion();
//            byte[] fichero = null;
//            JasperReport jasperReport = null;
//            JasperPrint jasperPrint = new JasperPrint(); //Excel
//            Map parameters = new HashMap();
//            try {
//                FacesContext context = FacesContext.getCurrentInstance();
//                HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
//                ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
//                session.removeAttribute("reporteInforme");
//                parameters.put("fecha_inicial", fec1);
//                parameters.put("fecha_final", fec2);
//                parameters.put("actEconomica", actEconomica.getCatdetCodigo());
//                parameters.put("nomActividad", nombreActEconomica);
//                parameters.put("usuario_genera", usuarioActual.getUsuNombres() + " " + usuarioActual.getUsuApellidos());
//                parameters.put("fecha_genera", retornaFecha());
//                parameters.put("logo_gad", servletContext.getRealPath("/imagenes/icons/gadPedroMoncayo.jpg"));
//                parameters.put("rango_parametros", cargarRgistroSeleccionado());
//                jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/patentes/rptNegocioPorActEco.jasper"));
//                if (tipoReporte.equals("PDF")) {
//                    fichero = JasperRunManager.runReportToPdf(jasperReport, parameters, conexion);
//                    session.setAttribute("reporteInforme", fichero);
//                    usuarioActual = new SegUsuario();
//                }
//                if (tipoReporte.equals("XLS")) {
//                    jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conexion);
//                    JRXlsExporter exporterXLS = new JRXlsExporter();
//                    ByteArrayOutputStream xlsReport = new ByteArrayOutputStream();
//                    exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
//                    exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, xlsReport);
//                    exporterXLS.exportReport();
//                    fichero = xlsReport.toByteArray();
//                    session.setAttribute("reporteInformeXls", fichero);
//                    usuarioActual = new SegUsuario();
//                }
//
//            } catch (JRException ex) {
//                LOGGER.log(Level.SEVERE, null, ex);
//            } catch (Exception e) {
//                LOGGER.log(Level.SEVERE, null, e);
//            } finally {
//                if (conexion != null) {
//                    conexion.close();
//                }
//            }
//        }
//
//        return null;
//    }
//    public String reporteNegArtCalificado() throws Exception {
//        if (cargarRgistroSeleccionado().equals("")) {
//            addErrorMessage("Debe seleccionar  al menos un registro", "Debe seleccionar al menos un registro");
//        } else {
//            Timestamp fec1 = new Timestamp(fechaInicial.getTime());
//            Timestamp fec2 = new Timestamp(fechaFinal.getTime());
//            //Conexion con local datasource
//            usuarioActual = new SegUsuario();
//            usuarioActual = (SegUsuario) this.getSession().getAttribute("usuario");
//            UtilitariosCod util = new UtilitariosCod();
//            Connection conexion = util.getConexion();
//            byte[] fichero = null;
//            JasperReport jasperReport = null;
//            JasperPrint jasperPrint = new JasperPrint(); //Excel
//            Map parameters = new HashMap();
//            try {
//                FacesContext context = FacesContext.getCurrentInstance();
//                HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
//                ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
//                session.removeAttribute("reporteInforme");
//                parameters.put("fecha_inicial", fec1);
//                parameters.put("fecha_final", fec2);
//                parameters.put("usuario_genera", usuarioActual.getUsuNombres() + " " + usuarioActual.getUsuApellidos());
//                parameters.put("fecha_genera", retornaFecha());
//                parameters.put("logo_gad", servletContext.getRealPath("/imagenes/icons/gadPedroMoncayo.jpg"));
//                parameters.put("rango_parametros", cargarRgistroSeleccionado());
//                jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/patentes/rptNegocioPorArtCalificado.jasper"));
//                if (tipoReporte.equals("PDF")) {
//                    fichero = JasperRunManager.runReportToPdf(jasperReport, parameters, conexion);
//                    session.setAttribute("reporteInforme", fichero);
//                    usuarioActual = new SegUsuario();
//                }
//                if (tipoReporte.equals("XLS")) {
//                    jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conexion);
//                    JRXlsExporter exporterXLS = new JRXlsExporter();
//                    ByteArrayOutputStream xlsReport = new ByteArrayOutputStream();
//                    exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
//                    exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, xlsReport);
//                    exporterXLS.exportReport();
//                    fichero = xlsReport.toByteArray();
//                    session.setAttribute("reporteInformeXls", fichero);
//                    usuarioActual = new SegUsuario();
//                }
//
//            } catch (JRException ex) {
//                LOGGER.log(Level.SEVERE, null, ex);
//            } catch (Exception e) {
//                LOGGER.log(Level.SEVERE, null, e);
//            } finally {
//                if (conexion != null) {
//                    conexion.close();
//                }
//            }
//        }
//
//        return null;
//    }
//    public String reporteNegLLevaCont() throws Exception {
//        if (cargarRgistroSeleccionado().equals("")) {
//            addErrorMessage("Debe seleccionar  al menos un registro", "Debe seleccionar al menos un registro");
//        } else {
//            Timestamp fec1 = new Timestamp(fechaInicial.getTime());
//            Timestamp fec2 = new Timestamp(fechaFinal.getTime());
//            //Conexion con local datasource
//            usuarioActual = new SegUsuario();
//            usuarioActual = (SegUsuario) this.getSession().getAttribute("usuario");
//            UtilitariosCod util = new UtilitariosCod();
//            Connection conexion = util.getConexion();
//            byte[] fichero = null;
//            JasperReport jasperReport = null;
//            JasperPrint jasperPrint = new JasperPrint(); //Excel
//            Map parameters = new HashMap();
//            try {
//                FacesContext context = FacesContext.getCurrentInstance();
//                HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
//                ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
//                session.removeAttribute("reporteInforme");
//                parameters.put("fecha_inicial", fec1);
//                parameters.put("fecha_final", fec2);
//                parameters.put("usuario_genera", usuarioActual.getUsuNombres() + " " + usuarioActual.getUsuApellidos());
//                parameters.put("fecha_genera", retornaFecha());
//                parameters.put("logo_gad", servletContext.getRealPath("/imagenes/icons/gadPedroMoncayo.jpg"));
//                parameters.put("rango_parametros", cargarRgistroSeleccionado());
//                jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/patentes/rptNegocioPorLlevaConta.jasper"));
//                if (tipoReporte.equals("PDF")) {
//                    fichero = JasperRunManager.runReportToPdf(jasperReport, parameters, conexion);
//                    session.setAttribute("reporteInforme", fichero);
//                    usuarioActual = new SegUsuario();
//                }
//                if (tipoReporte.equals("XLS")) {
//                    jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conexion);
//                    JRXlsExporter exporterXLS = new JRXlsExporter();
//                    ByteArrayOutputStream xlsReport = new ByteArrayOutputStream();
//                    exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
//                    exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, xlsReport);
//                    exporterXLS.exportReport();
//                    fichero = xlsReport.toByteArray();
//                    session.setAttribute("reporteInformeXls", fichero);
//                    usuarioActual = new SegUsuario();
//                }
//
//            } catch (JRException ex) {
//                LOGGER.log(Level.SEVERE, null, ex);
//            } catch (Exception e) {
//                LOGGER.log(Level.SEVERE, null, e);
//            } finally {
//                if (conexion != null) {
//                    conexion.close();
//                }
//            }
//        }
//
//        return null;
//    }
//    public String reporteNegNoLlevaCont() throws Exception {
//        if (cargarRgistroSeleccionado().equals("")) {
//            addErrorMessage("Debe seleccionar  al menos un registro", "Debe seleccionar al menos un registro");
//        } else {
//            Timestamp fec1 = new Timestamp(fechaInicial.getTime());
//            Timestamp fec2 = new Timestamp(fechaFinal.getTime());
//            //Conexion con local datasource
//            usuarioActual = new SegUsuario();
//            usuarioActual = (SegUsuario) this.getSession().getAttribute("usuario");
//            UtilitariosCod util = new UtilitariosCod();
//            Connection conexion = util.getConexion();
//            byte[] fichero = null;
//            JasperReport jasperReport = null;
//            JasperPrint jasperPrint = new JasperPrint(); //Excel
//            Map parameters = new HashMap();
//            try {
//                FacesContext context = FacesContext.getCurrentInstance();
//                HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
//                ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
//                session.removeAttribute("reporteInforme");
//                parameters.put("fecha_inicial", fec1);
//                parameters.put("fecha_final", fec2);
//                parameters.put("usuario_genera", usuarioActual.getUsuNombres() + " " + usuarioActual.getUsuApellidos());
//                parameters.put("fecha_genera", retornaFecha());
//                parameters.put("logo_gad", servletContext.getRealPath("/imagenes/icons/gadPedroMoncayo.jpg"));
//                parameters.put("rango_parametros", cargarRgistroSeleccionado());
//                jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/patentes/rptNegocioPorNoLlevaConta.jasper"));
//                if (tipoReporte.equals("PDF")) {
//                    fichero = JasperRunManager.runReportToPdf(jasperReport, parameters, conexion);
//                    session.setAttribute("reporteInforme", fichero);
//                    usuarioActual = new SegUsuario();
//                }
//                if (tipoReporte.equals("XLS")) {
//                    jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conexion);
//                    JRXlsExporter exporterXLS = new JRXlsExporter();
//                    ByteArrayOutputStream xlsReport = new ByteArrayOutputStream();
//                    exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
//                    exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, xlsReport);
//                    exporterXLS.exportReport();
//                    fichero = xlsReport.toByteArray();
//                    session.setAttribute("reporteInformeXls", fichero);
//                    usuarioActual = new SegUsuario();
//                }
//
//            } catch (JRException ex) {
//                LOGGER.log(Level.SEVERE, null, ex);
//            } catch (Exception e) {
//                LOGGER.log(Level.SEVERE, null, e);
//            } finally {
//                if (conexion != null) {
//                    conexion.close();
//                }
//            }
//        }
//
//        return null;
//    }
    public String cargarRgistroSeleccionado() {
        String codigos = "";
        for (int i = 0; i <= listaRepSeleccion.size() - 1; i++) {
            Object[] xcs = listaRepSeleccion.get(i);
            codigos = codigos + xcs[0];
            if (i < listaRepSeleccion.size() - 1) {
                codigos = codigos + ",";
            }
        }
        System.out.println("codigos: " + codigos);
        return codigos;
    }

    public String reporteOccisoPertipoNicho() throws Exception {
        String nombrePerTipoNicho = "";
        CatalogoDetalle objCatDetAux = new CatalogoDetalle();
        objCatDetAux = catalogoDetalleServicio.buscarPorCodigoCatDet(catDetTipoPerNicho.getCatdetCodigo());
        nombrePerTipoNicho = objCatDetAux.getCatdetTexto();
        if (cargarRgistroSeleccionado().equals("")) {
            addErrorMessage("Debe seleccionar  al menos un registro", "Debe seleccionar al menos un registro");
        } else {
            //Conexion con local datasource
            Timestamp fec1 = new Timestamp(fechaInicial.getTime());
            Timestamp fec2 = new Timestamp(fechaFinal.getTime());
            System.out.println("Fecha inical" + fec1);
            System.out.println("Fecha final" + fec2);
//            System.out.println("Parroquias" + catDetParroquia.getCatdetCodigo());
            usuarioActual = new SegUsuario();
            usuarioActual = (SegUsuario) this.getSession().getAttribute("usuario");
            UtilitariosCod util = new UtilitariosCod();
            Connection conexion = util.getConexion();
            byte[] fichero = null;
            JasperReport jasperReport = null;
            JasperPrint jasperPrint = new JasperPrint(); //Excel
            Map parameters = new HashMap();
            try {
                FacesContext context = FacesContext.getCurrentInstance();
                HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
                ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
                session.removeAttribute("reporteInforme");
                parameters.put("fecha_inicial", fec1);
                parameters.put("fecha_final", fec2);
                parameters.put("tipoNicho", objCatDetAux.getCatdetCod());
                parameters.put("nomNicho", nombrePerTipoNicho);
                parameters.put("usuario_genera", usuarioActual.getUsuNombres() + " " + usuarioActual.getUsuApellidos());
                parameters.put("fecha_genera", retornaFecha());
                parameters.put("logo_gad", servletContext.getRealPath("/imagenes/icons/gadPedroMoncayo.jpg"));
                parameters.put("rango_parametros", cargarRgistroSeleccionado());
                jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/cementerios/rptOccisoPorTipoNicho.jasper"));
                if (tipoReporte.equals("PDF")) {
                    fichero = JasperRunManager.runReportToPdf(jasperReport, parameters, conexion);
                    session.setAttribute("reporteInforme", fichero);
                    usuarioActual = new SegUsuario();
                }
                if (tipoReporte.equals("XLS")) {
                    jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conexion);
                    JRXlsExporter exporterXLS = new JRXlsExporter();
                    ByteArrayOutputStream xlsReport = new ByteArrayOutputStream();
                    exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
                    exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, xlsReport);
                    exporterXLS.exportReport();
                    fichero = xlsReport.toByteArray();
                    session.setAttribute("reporteInformeXls", fichero);
                    usuarioActual = new SegUsuario();
                }

            } catch (JRException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, null, e);
            } finally {
                if (conexion != null) {
                    conexion.close();
                }
            }
        }

        return null;
    }

    public BigDecimal getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(BigDecimal valorInicial) {
        this.valorInicial = valorInicial;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public List<Object[]> getListaReportes() {
        return listaReportes;
    }

    public void setListaReportes(List<Object[]> listaReportes) {
        this.listaReportes = listaReportes;
    }

    public List<Object[]> getListaRepSeleccion() {
        return listaRepSeleccion;
    }

    public void setListaRepSeleccion(List<Object[]> listaRepSeleccion) {
        this.listaRepSeleccion = listaRepSeleccion;
    }

    public int getSeleccionaReporte() {
        return seleccionaReporte;
    }

    public void setSeleccionaReporte(int seleccionaReporte) {
        this.seleccionaReporte = seleccionaReporte;
    }

    public int getNumReporte() {
        return numReporte;
    }

    public void setNumReporte(int numReporte) {
        this.numReporte = numReporte;
    }

    public String getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public List<CatalogoDetalle> getListaGenero() {
        return listaGenero;
    }

    public void setListaGenero(List<CatalogoDetalle> listaGenero) {
        this.listaGenero = listaGenero;
    }

    public CatalogoDetalle getCatDetGenero() {
        return catDetGenero;
    }

    public void setCatDetGenero(CatalogoDetalle catDetGenero) {
        this.catDetGenero = catDetGenero;
    }

    public Propietario getPropietarioActual() {
        return propietarioActual;
    }

    public void setPropietarioActual(Propietario propietarioActual) {
        this.propietarioActual = propietarioActual;
    }

    public String getNombrePropietario() {
        return nombrePropietario;
    }

    public void setNombrePropietario(String nombrePropietario) {
        this.nombrePropietario = nombrePropietario;
    }

    public CatalogoDetalle getActEconomica() {
        return actEconomica;
    }

    public void setActEconomica(CatalogoDetalle actEconomica) {
        this.actEconomica = actEconomica;
    }

    public List<CatalogoDetalle> getListActividadEconomica() {
        return listActividadEconomica;
    }

    public void setListActividadEconomica(List<CatalogoDetalle> listActividadEconomica) {
        this.listActividadEconomica = listActividadEconomica;
    }

    public CatalogoDetalle getCatDetTipoPerNicho() {
        return catDetTipoPerNicho;
    }

    public void setCatDetTipoPerNicho(CatalogoDetalle catDetTipoPerNicho) {
        this.catDetTipoPerNicho = catDetTipoPerNicho;
    }

    public List<CatalogoDetalle> getListTipoPerNicho() {
        return listTipoPerNicho;
    }

    public void setListTipoPerNicho(List<CatalogoDetalle> listTipoPerNicho) {
        this.listTipoPerNicho = listTipoPerNicho;
    }

    public int getVerResultados() {
        return verResultados;
    }

    public void setVerResultados(int verResultados) {
        this.verResultados = verResultados;
    }

    public CatalogoDetalle getCatDetParroquia() {
        return catDetParroquia;
    }

    public void setCatDetParroquia(CatalogoDetalle catDetParroquia) {
        this.catDetParroquia = catDetParroquia;
    }

    public List<CatalogoDetalle> getListParroquias() {
        return listParroquias;
    }

    public void setListParroquias(List<CatalogoDetalle> listParroquias) {
        this.listParroquias = listParroquias;
    }

}
