/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.web.impuestos;

import ec.sirec.ejb.entidades.SegUsuario;
import ec.sirec.ejb.servicios.PatenteReporteServicio;
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

/**
 *
 * @author Darwin Aldas
 */
@ManagedBean
@ViewScoped
public class GestionRepPatenteControlador extends BaseControlador {

    @EJB
    private PatenteReporteServicio patenteReporteServicio;

    /**
     * Creates a new instance of GestionPatenteControlador
     */
    private static final Logger LOGGER = Logger.getLogger(GestionRepPatenteControlador.class.getName());
    private BigDecimal valorInicial;
    private BigDecimal valorFinal;
    private SegUsuario usuarioActual;
    private Date fechaActual;
    private Date fechaInicial;
    private Date fechaFinal;
    private List<Object[]> listaReportes;
    private List<Object[]> listaRepSeleccion;
    String cadenaRegSeleccionados;
    private int seleccionaReporte;
    private int numReporte;
    private String tipoReporte;

    @PostConstruct
    public void inicializar() {
        try {
            tipoReporte = null;
            fechaActual = new Date();
            listaReportes = new ArrayList<Object[]>();
            listaRepSeleccion = new ArrayList<Object[]>();
            cadenaRegSeleccionados = "";
            seleccionaReporte = 0;
            numReporte = 0;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
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
        listaReportes = new ArrayList<Object[]>();
        listaRepSeleccion = new ArrayList<Object[]>();
        switch (seleccionaReporte) {
            case 1:
                numReporte = 1;
                break;
            case 2:
                numReporte = 2;
                break;
        }
    }

    public GestionRepPatenteControlador() {
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
            listaReportes = patenteReporteServicio.listarDatReporte1(valorInicial, valorFinal);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
//Reporte de emision inicial

    public void listarDatosReporte2() {
        try {
            Calendar fecha1 = Calendar.getInstance();
            fechaActual = fecha1.getTime();
            Timestamp fec1 = new Timestamp(fechaInicial.getTime());
            Timestamp fec2 = new Timestamp(fechaFinal.getTime());
            listaReportes = patenteReporteServicio.listaDatReporte2(fec1, fec2);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public String reporteNegRangoPatrimonio() throws Exception {
        if (cargarRgistroSeleccionado().equals("")) {
            addErrorMessage("Debe seleccionar  al menos un registro", "Debe seleccionar al menos un registro");
        } else {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            Map<String, String> params = externalContext.getRequestParameterMap();
            tipoReporte = params.get("paramTipoReporte");
            System.out.println("Parametro Enviado" + tipoReporte);
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
                parameters.put("valor_inicial", valorInicial);
                parameters.put("valor_final", valorFinal);
                parameters.put("usuario_genera", usuarioActual.getUsuNombres() + " " + usuarioActual.getUsuApellidos());
                parameters.put("fecha_genera", retornaFecha());
                parameters.put("logo_gad", servletContext.getRealPath("/imagenes/icons/gadPedroMoncayo.jpg"));
                parameters.put("rango_parametros", cargarRgistroSeleccionado());
                jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/patentes/rptNegocioPorRangoPatrimonio.jasper"));
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

    public String reporteEmisionPatentes() throws Exception {
        Calendar fecha1 = Calendar.getInstance();
        fechaActual = fecha1.getTime();
        //Conexion con local datasource
        usuarioActual = new SegUsuario();
        usuarioActual = (SegUsuario) this.getSession().getAttribute("usuario");
        Timestamp fec1 = new Timestamp(fechaInicial.getTime());
        Timestamp fec2 = new Timestamp(fechaFinal.getTime());
        UtilitariosCod util = new UtilitariosCod();
        Connection conexion = util.getConexion();
        byte[] fichero = null;
        JasperReport jasperReport = null;
        Map parameters = new HashMap();
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
            ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
            session.removeAttribute("reporteInforme");
            parameters.put("fecha_inicial", fec1);
            parameters.put("fecha_final", fec2);
            parameters.put("usuario_genera", usuarioActual.getUsuNombres() + " " + usuarioActual.getUsuApellidos());
            parameters.put("fecha_genera", fechaActual.getDay() + "-" + fechaActual.getMonth() + "-" + fechaActual.getYear());
            parameters.put("logo_gad", servletContext.getRealPath("/imagenes/icons/gadPedroMoncayo.jpg"));
            parameters.put("rango_parametros", cargarRgistroSeleccionado());
            jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/patentes/rptEmisionInicial.jasper"));
            fichero = JasperRunManager.runReportToPdf(jasperReport, parameters, conexion);
            session.setAttribute("reporteInforme", fichero);
            usuarioActual = new SegUsuario();
        } catch (JRException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        } finally {
            if (conexion != null) {
                conexion.close();
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
}
