/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.web.impuestos;

import ec.sirec.ejb.entidades.CatalogoDetalle;
import ec.sirec.ejb.entidades.CatastroPredial;
import ec.sirec.ejb.entidades.SegUsuario;
import ec.sirec.ejb.servicios.CatalogoDetalleServicio;
import ec.sirec.ejb.servicios.CatastroPredialAlcabalaValoracionServicio;
import ec.sirec.ejb.servicios.CatastroPredialPlusvaliaValoracionServicio;
import ec.sirec.ejb.servicios.CatastroPredialServicio;
import ec.sirec.web.base.BaseControlador;
import ec.sirec.web.util.UtilitariosCod;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 *
 * @author vespinoza
 */
@ManagedBean
@ViewScoped
public class GestionRepAlcabalasEmitidasControlador extends BaseControlador {

    
    private static final Logger LOGGER = Logger.getLogger(GestionRepAlcabalasEmitidasControlador.class.getName());
    private SegUsuario usuarioActual;
    private Date fechaActual;
    private String fechaActualString;
    private String criterioPL;
    private String criterioAL;

     private List<CatalogoDetalle> listAnios;
     private CatalogoDetalle catDetAnio;
     private CatalogoDetalle catDetAnioPL;
     private CatalogoDetalle catDetTipoTarifa;
     private CatalogoDetalle catDetParroquia;
     private CatalogoDetalle catDetSectores;
     private CatalogoDetalle catDetParroquiaAL;
     private CatalogoDetalle catDetSectoresAL;
     private CatastroPredial catastroPredialReporte;
     private CatastroPredial catastroPredialReporteAL;
     private CatalogoDetalle catDetConcepto;
     
      private List<CatalogoDetalle> listaTipoDeTarifa;
      private List<CatalogoDetalle> listaParroquias;
       private List<CatalogoDetalle> listaSectores;
       private List<CatalogoDetalle> listaSectoresAL;
        private List<CatastroPredial> listaCatastroPredialClavesCatastrales;
         private List<CatalogoDetalle> listaCatalogoDetalleConcepto;
         
      
      private List<Object[]> listaAlcabalasEmitidas;
      private List<Object[]> listaAlcabalasEmitidasSeleccion;
      private List<Object[]> listaPlusvaliaEmitidas;
      private List<Object[]> listaPlusvaliaEmitidasSeleccion;
     
     @EJB
    private CatalogoDetalleServicio catalogoDetalleServicio;
    @EJB
    private CatastroPredialServicio catastroPredialServicio;
    @EJB
    private CatastroPredialAlcabalaValoracionServicio catastroPredialAlcabalaValoracionServicio;
    @EJB
    private CatastroPredialPlusvaliaValoracionServicio catastroPredialPlusvaliaValoracionServicio;
    
    
    @PostConstruct
    public void inicializar() {
        try {
            fechaActual = new Date();
            catDetAnio= new CatalogoDetalle(); 
            catDetAnioPL= new CatalogoDetalle(); 
            catDetTipoTarifa = new CatalogoDetalle();
            catDetParroquia = new CatalogoDetalle();
            catDetConcepto = new CatalogoDetalle();
            catDetParroquiaAL = new CatalogoDetalle();
            catDetSectoresAL = new CatalogoDetalle();
             listaAlcabalasEmitidas = new ArrayList<Object[]>();
             listaAlcabalasEmitidasSeleccion = new ArrayList<Object[]>();
             listaPlusvaliaEmitidas = new ArrayList<Object[]>();
             listaPlusvaliaEmitidasSeleccion = new ArrayList<Object[]>();
            criterioPL ="";
            criterioAL ="";
            
            
            
             catastroPredialReporte = new CatastroPredial();
             catastroPredialReporteAL = new CatastroPredial();
             
            listarTipoTarifa();            
            listarAnios();
            listarParroquia();
            listarTodasComboClaves();
            listarConceptos();
              
            obtenerFechaCadena();
            
            
            
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public GestionRepAlcabalasEmitidasControlador() {
    }

    public void obtenerFechaCadena() {        
        try {            
             SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");          
             fechaActualString = sdf.format(fechaActual);                        
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }        
    }
    
    public void listarTipoTarifa() {        
        try {
           listaTipoDeTarifa = new ArrayList<CatalogoDetalle>();
           listaTipoDeTarifa = catastroPredialServicio.listarTipoDeTarifa();            
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public void listarParroquia() {        
        try {
           listaParroquias = new ArrayList<CatalogoDetalle>();
           listaParroquias =  catastroPredialServicio.listaCatParroquias();    
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
     public void listarSectores() {
        try {            
           CatalogoDetalle parroquia = catastroPredialServicio.cargarObjetoCatalogoDetalle(catDetParroquia.getCatdetCodigo());                     
            listaSectores = catastroPredialServicio.listaCatSectores(parroquia.getCatdetCod());                                    
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
     
     public void listarSectoresAL() {
        try {            
           CatalogoDetalle parroquia = catastroPredialServicio.cargarObjetoCatalogoDetalle(catDetParroquiaAL.getCatdetCodigo());                     
            listaSectoresAL = catastroPredialServicio.listaCatSectores(parroquia.getCatdetCod());                                    
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public void listarAnios() throws Exception {
        listAnios = catalogoDetalleServicio.listarPorNemonicoCatalogo("ANIOS");
    }
    
    public void listarTodasComboClaves() {

        try {
            listaCatastroPredialClavesCatastrales = catastroPredialServicio.listarClaveCatastral();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public void listarConceptos() {
        try {

            listaCatalogoDetalleConcepto = new ArrayList<CatalogoDetalle>();
            listaCatalogoDetalleConcepto = catalogoDetalleServicio.listarPorNemonicoCatalogo("CONCEPTOS");

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public String obtenerCodigos(List<Object[]> listaSeleccion) {        
        String codigos="";
        try {                  
         for (int i = 0; i <= listaSeleccion.size()-1; i++) {             
             Object[]  xcs = listaSeleccion.get(i);    
             codigos = codigos + xcs[0];
                    if (i < listaSeleccion.size() - 1) {
                        codigos = codigos + ",";
                    }              
         } 
         System.out.println("codigos: " +codigos);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return codigos;
    }
    
    public String reporteAlcabalaEmitidas() throws Exception {                                    
        
        if(listaAlcabalasEmitidasSeleccion.size()>0){
            
            //Conexion con local datasource
        usuarioActual = new SegUsuario();
        usuarioActual = (SegUsuario) this.getSession().getAttribute("usuario");        
            catDetAnio = catastroPredialServicio.cargarObjetoCatalogoDetalle(catDetAnio.getCatdetCodigo());           
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
            parameters.put("usuario_genera", usuarioActual.getUsuNombres() + " " + usuarioActual.getUsuApellidos());
            parameters.put("fecha_genera", fechaActualString);
            parameters.put("codigos", obtenerCodigos(listaAlcabalasEmitidasSeleccion));            
            parameters.put("logo_gad", servletContext.getRealPath("/imagenes/icons/gadPedroMoncayo.jpg"));   
                                                                              
            if (criterioAL.equals("T")) {               
                jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/impuestos/alcabala_concepto.jasper"));
            } else {
                if (criterioAL.equals("A")) {
                    jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/impuestos/alcabala_emitida.jasper"));
                } else {
                    if (criterioAL.equals("P")) {
                        jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/impuestos/alcabala_parroquia.jasper"));
                    } else {
                        if (criterioAL.equals("S")) {
                            jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/impuestos/alcabala_sector.jasper"));
                        }else{
                        if (criterioAL.equals("C")) {
                            jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/impuestos/alcabala_predio.jasper"));
                        }
                        }
                    }
                }
            }
            
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
            
        }else{
        
              addSuccessMessage("Seleccione registros","Seleccione registros");
        
        }
        
        
        return null;
    }
    
    public String reportePlusvaliaEmitidas() throws Exception {
        //Conexion con local datasource
        
        if(listaPlusvaliaEmitidasSeleccion.size()>0){
        
            usuarioActual = new SegUsuario();
        usuarioActual = (SegUsuario) this.getSession().getAttribute("usuario");                   
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
            parameters.put("usuario_genera", usuarioActual.getUsuNombres() + " " + usuarioActual.getUsuApellidos());
            parameters.put("fecha_genera", fechaActualString);            
            parameters.put("logo_gad", servletContext.getRealPath("/imagenes/icons/gadPedroMoncayo.jpg"));
            parameters.put("codigos", obtenerCodigos(listaPlusvaliaEmitidasSeleccion)); 
            if (criterioPL.equals("F")) {               
                jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/impuestos/plusvalia_tipo_tarifa.jasper"));
            } else {
                if (criterioPL.equals("A")) {
                    jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/impuestos/plusvalia_emitida.jasper"));
                } else {
                    if (criterioPL.equals("P")) {
                        jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/impuestos/plusvalia_parroquia.jasper"));
                    } else {
                        if (criterioPL.equals("S")) {
                            jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/impuestos/plusvalia_sector.jasper"));
                        }else{
                        if (criterioPL.equals("C")) {
                            jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/impuestos/plusvalia_predio.jasper"));
                        }
                        }
                    }
                }
            }
            
            
             //   jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/impuestos/plusvalia_emitida.jasper"));
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
            
        }else{
        
            addSuccessMessage("Seleccione registros","Seleccione registros");
            
        }
        
        
        return null;
    }
    
    
    
    public String reportePlusvaliaXTipoTarifa() throws Exception {
        //Conexion con local datasource
        usuarioActual = new SegUsuario();
        usuarioActual = (SegUsuario) this.getSession().getAttribute("usuario");
        
           // catDetAnio = catastroPredialServicio.cargarObjetoCatalogoDetalle(catDetTipoTarifa.getCatdetCodigo());
           
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
            parameters.put("usuario_genera", usuarioActual.getUsuNombres() + " " + usuarioActual.getUsuApellidos());
            parameters.put("fecha_genera", fechaActualString);
            parameters.put("tipo_tarifa", catDetTipoTarifa.getCatdetCodigo());            
            parameters.put("logo_gad", servletContext.getRealPath("/imagenes/icons/gadPedroMoncayo.jpg"));            
                jasperReport = (JasperReport) JRLoader.loadObject(servletContext.getRealPath("/reportes/impuestos/plusvalia_tipo_tarifa.jasper"));
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
    
    
     public void listarAlcabalaEmitidas() throws Exception {
         try {         
               
             if (criterioAL.equals("A")) {                 
             catDetAnio = catastroPredialServicio.cargarObjetoCatalogoDetalle(catDetAnio.getCatdetCodigo());        
            listaAlcabalasEmitidas = catastroPredialAlcabalaValoracionServicio.listarAlcabalaEmitidaXAño(Integer.parseInt(catDetAnio.getCatdetTexto()));                
             } else {
                 if (criterioAL.equals("T")) {
                     listaAlcabalasEmitidas = catastroPredialAlcabalaValoracionServicio.listarAlcabalaEmitidaXConcepto(catDetConcepto);
                 } else {
                     if (criterioAL.equals("P")) {
                        listaAlcabalasEmitidas = catastroPredialAlcabalaValoracionServicio.listarAlcabalaEmitidaXParroquia(catDetParroquiaAL);
                     } else {
                         if (criterioAL.equals("S")) {
                          listaAlcabalasEmitidas = catastroPredialAlcabalaValoracionServicio.listarAlcabalaEmitidaXSector(catDetSectoresAL);
                         } else {
                             if (criterioAL.equals("C")) {
                              listaAlcabalasEmitidas = catastroPredialAlcabalaValoracionServicio.listarAlcabalaEmitidaXClaveCatastral(catastroPredialReporteAL);
                             }
                         }
                     }
                 }
             }
                                    
         }catch(Exception ex){
          LOGGER.log(Level.SEVERE, null, ex);
         }
     }
            
    
     public void listarPlusvaliaEmitidas() throws Exception {
         try {
             if (criterioPL.equals("A")) {
                 catDetAnioPL = catastroPredialServicio.cargarObjetoCatalogoDetalle(catDetAnioPL.getCatdetCodigo());
                 listaPlusvaliaEmitidas = catastroPredialPlusvaliaValoracionServicio.listarPlusvaliaEmitidaXAño(Integer.parseInt(catDetAnioPL.getCatdetTexto()));
             } else {
                 if (criterioPL.equals("F")) {
                     listaPlusvaliaEmitidas = catastroPredialPlusvaliaValoracionServicio.listarPlusvaliaEmitidaXTipoTarifa(catDetTipoTarifa);
                 } else {
                     if (criterioPL.equals("P")) {
                         listaPlusvaliaEmitidas = catastroPredialPlusvaliaValoracionServicio.listarPlusvaliaEmitidaXParroquia(catDetParroquia);
                     } else {
                         if (criterioPL.equals("S")) {
                             listaPlusvaliaEmitidas = catastroPredialPlusvaliaValoracionServicio.listarPlusvaliaEmitidaXSector(catDetSectores);
                         } else {
                             if (criterioPL.equals("C")) {
                                 listaPlusvaliaEmitidas = catastroPredialPlusvaliaValoracionServicio.listarPlusvaliaEmitidaXClaveCatastral(catastroPredialReporte);
                             }
                         }
                     }
                 }
             }
                            
             
         }catch(Exception ex){
          LOGGER.log(Level.SEVERE, null, ex);
         }
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

    public CatalogoDetalle getCatDetAnioPL() {
        return catDetAnioPL;
    }

    public void setCatDetAnioPL(CatalogoDetalle catDetAnioPL) {
        this.catDetAnioPL = catDetAnioPL;
    }

    public List<CatalogoDetalle> getListaTipoDeTarifa() {
        return listaTipoDeTarifa;
    }

    public void setListaTipoDeTarifa(List<CatalogoDetalle> listaTipoDeTarifa) {
        this.listaTipoDeTarifa = listaTipoDeTarifa;
    }

    public CatalogoDetalle getCatDetTipoTarifa() {
        return catDetTipoTarifa;
    }

    public void setCatDetTipoTarifa(CatalogoDetalle catDetTipoTarifa) {
        this.catDetTipoTarifa = catDetTipoTarifa;
    }

    public List<Object[]> getListaAlcabalasEmitidas() {
        return listaAlcabalasEmitidas;
    }

    public void setListaAlcabalasEmitidas(List<Object[]> listaAlcabalasEmitidas) {
        this.listaAlcabalasEmitidas = listaAlcabalasEmitidas;
    }

    public List<Object[]> getListaAlcabalasEmitidasSeleccion() {
        return listaAlcabalasEmitidasSeleccion;
    }

    public void setListaAlcabalasEmitidasSeleccion(List<Object[]> listaAlcabalasEmitidasSeleccion) {
        this.listaAlcabalasEmitidasSeleccion = listaAlcabalasEmitidasSeleccion;
    }

    public List<Object[]> getListaPlusvaliaEmitidas() {
        return listaPlusvaliaEmitidas;
    }

    public void setListaPlusvaliaEmitidas(List<Object[]> listaPlusvaliaEmitidas) {
        this.listaPlusvaliaEmitidas = listaPlusvaliaEmitidas;
    }

    public List<Object[]> getListaPlusvaliaEmitidasSeleccion() {
        return listaPlusvaliaEmitidasSeleccion;
    }

    public void setListaPlusvaliaEmitidasSeleccion(List<Object[]> listaPlusvaliaEmitidasSeleccion) {
        this.listaPlusvaliaEmitidasSeleccion = listaPlusvaliaEmitidasSeleccion;
    }

    public String getCriterioPL() {
        return criterioPL;
    }

    public void setCriterioPL(String criterioPL) {
        this.criterioPL = criterioPL;
    }

    public CatalogoDetalle getCatDetParroquia() {
        return catDetParroquia;
    }

    public void setCatDetParroquia(CatalogoDetalle catDetParroquia) {
        this.catDetParroquia = catDetParroquia;
    }

    public List<CatalogoDetalle> getListaParroquias() {
        return listaParroquias;
    }

    public void setListaParroquias(List<CatalogoDetalle> listaParroquias) {
        this.listaParroquias = listaParroquias;
    }

    public List<CatalogoDetalle> getListaSectores() {
        return listaSectores;
    }

    public void setListaSectores(List<CatalogoDetalle> listaSectores) {
        this.listaSectores = listaSectores;
    }

    public CatalogoDetalle getCatDetSectores() {
        return catDetSectores;
    }

    public void setCatDetSectores(CatalogoDetalle catDetSectores) {
        this.catDetSectores = catDetSectores;
    }

    public CatastroPredial getCatastroPredialReporte() {
        return catastroPredialReporte;
    }

    public void setCatastroPredialReporte(CatastroPredial catastroPredialReporte) {
        this.catastroPredialReporte = catastroPredialReporte;
    }

    public List<CatastroPredial> getListaCatastroPredialClavesCatastrales() {
        return listaCatastroPredialClavesCatastrales;
    }

    public void setListaCatastroPredialClavesCatastrales(List<CatastroPredial> listaCatastroPredialClavesCatastrales) {
        this.listaCatastroPredialClavesCatastrales = listaCatastroPredialClavesCatastrales;
    }

    public String getCriterioAL() {
        return criterioAL;
    }

    public void setCriterioAL(String criterioAL) {
        this.criterioAL = criterioAL;
    }

    public CatalogoDetalle getCatDetConcepto() {
        return catDetConcepto;
    }

    public void setCatDetConcepto(CatalogoDetalle catDetConcepto) {
        this.catDetConcepto = catDetConcepto;
    }

    public List<CatalogoDetalle> getListaCatalogoDetalleConcepto() {
        return listaCatalogoDetalleConcepto;
    }

    public void setListaCatalogoDetalleConcepto(List<CatalogoDetalle> listaCatalogoDetalleConcepto) {
        this.listaCatalogoDetalleConcepto = listaCatalogoDetalleConcepto;
    }

    public CatastroPredial getCatastroPredialReporteAL() {
        return catastroPredialReporteAL;
    }

    public void setCatastroPredialReporteAL(CatastroPredial catastroPredialReporteAL) {
        this.catastroPredialReporteAL = catastroPredialReporteAL;
    }

    public CatalogoDetalle getCatDetParroquiaAL() {
        return catDetParroquiaAL;
    }

    public void setCatDetParroquiaAL(CatalogoDetalle catDetParroquiaAL) {
        this.catDetParroquiaAL = catDetParroquiaAL;
    }

    public CatalogoDetalle getCatDetSectoresAL() {
        return catDetSectoresAL;
    }

    public void setCatDetSectoresAL(CatalogoDetalle catDetSectoresAL) {
        this.catDetSectoresAL = catDetSectoresAL;
    }

    public List<CatalogoDetalle> getListaSectoresAL() {
        return listaSectoresAL;
    }

    public void setListaSectoresAL(List<CatalogoDetalle> listaSectoresAL) {
        this.listaSectoresAL = listaSectoresAL;
    }
       
}
