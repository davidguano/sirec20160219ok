/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.web.impuestos;

import ec.sirec.ejb.clases.EjecutarValoracion;
import ec.sirec.ejb.entidades.AdicionalesDeductivos;
import ec.sirec.ejb.entidades.CatalogoDetalle;
import ec.sirec.ejb.entidades.CatastroPredial;
import ec.sirec.ejb.entidades.CatastroPredialAlcabalaValoracion;
import ec.sirec.ejb.entidades.CatastroPredialPlusvaliaValoracion;
import ec.sirec.ejb.entidades.CatastroPredialValoracion;
import ec.sirec.ejb.entidades.CpAlcabalaValoracionExtras;
import ec.sirec.ejb.entidades.ObraProyecto;
import ec.sirec.ejb.entidades.PredioArchivo;
import ec.sirec.ejb.entidades.Propietario;
import ec.sirec.ejb.entidades.PropietarioPredio;
import ec.sirec.ejb.entidades.RecaudacionCab;
import ec.sirec.ejb.entidades.RecaudacionDet;
import ec.sirec.ejb.entidades.SegUsuario;
import ec.sirec.ejb.servicios.AdicionalesDeductivosServicio;
import ec.sirec.ejb.servicios.CatalogoDetalleServicio;
import ec.sirec.ejb.servicios.CatastroPredialAlcabalaValoracionServicio;
import ec.sirec.ejb.servicios.CatastroPredialPlusvaliaValoracionServicio;
import ec.sirec.ejb.servicios.CatastroPredialServicio;
import ec.sirec.ejb.servicios.CatastroPredialValoracionServicio;
import ec.sirec.ejb.servicios.CpAlcabalaValoracionExtrasServicio;
import ec.sirec.ejb.servicios.CpValoracionExtrasServicio;
import ec.sirec.ejb.servicios.DatoGlobalServicio;
import ec.sirec.ejb.servicios.PredioArchivoServicio;
import ec.sirec.ejb.servicios.RecaudacionCabServicio;
import ec.sirec.ejb.servicios.RecaudacionDetServicio;
import ec.sirec.web.base.BaseControlador;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author vespinoza
 */
@ManagedBean
@ViewScoped
public class GestionAlcabalasControlador extends BaseControlador {

    /**
     * Creates a new instance of GestionConceptoControlador
     */
    //LOGGER 
    private static final Logger LOGGER = Logger.getLogger(GestionAlcabalasControlador.class.getName());
    // VARIABLES Y ATRIBUTOS

    private SegUsuario usuarioActual;
    private CatastroPredial catastroPredialActual;
    private CatastroPredialAlcabalaValoracion catastroPredialAlcabalaValoracion;
    private CatalogoDetalle catalogoDetalleConcepto;

    private List<CatastroPredial> listaCatastroPredial;
    private List<CatalogoDetalle> listaCatalogoDetalleConcepto;
    private CatastroPredialValoracion catastroPredialValoracionActual;

    private List<AdicionalesDeductivos> listaAdicionalesDeductivosDeducciones;
    private List<String> listaAdicionalesDeductivosDeduccionesSeleccion;
    private List<AdicionalesDeductivos> listaAdicionalesDeductivosExcenciones;
    private List<String> listaAdicionalesDeductivosExcencionesSeleccion;

    private AdicionalesDeductivos adicionalesDeductivosActual;
    private CpAlcabalaValoracionExtras cpAlcabalaValoracionExtrasActual;
    private StreamedContent archivo;
    private Propietario propietario;
    private List<PredioArchivo> listaAlcabalasArchivo;
    private PredioArchivo predioArchivo;
    private PropietarioPredio propietarioPredioBusqueda;
    private int anio;
    private BigDecimal areaTerreno;
    private BigDecimal areaCons;
    private BigDecimal areaTotal;
    private BigDecimal renumeracion;
    private String visibleRenumeracion;
    
    
    /// ATRIBUTOS PLUSVALIA
    
    private List<CatalogoDetalle> listaTipoDeTarifa;
    private CatastroPredialPlusvaliaValoracion catastroPredialPlusvaliaValoracion;
    
    ////// EMISIÓN DE ALCABALAS y PLUSVALIAS  ////////////
    
    private List<CatastroPredial> listaCatastroPredialTablaValoracion;           
    private List<EjecutarValoracion> listaEjecutarValoracion; 
    private List<EjecutarValoracion> listaCatastroPredialTablaValoracionSeleccion;
    private List<CatalogoDetalle> listAnios;
    private CatalogoDetalle catDetAnio;
    private RecaudacionCab recaudacioCab;
    private RecaudacionDet recaudacionDet;
    private EjecutarValoracion ejecutarValoracionSeleccion; 
     private List<PredioArchivo> listaPlusvaliaArchivo;
    
    @EJB
    private RecaudacionCabServicio recaudacionCabServicio;
    @EJB
    private RecaudacionDetServicio recaudacionDetServicio;
    
    
    // SERVICIOS ALCABALA
    @EJB
    private CatastroPredialServicio catastroPredialServicio;
    @EJB
    private CatalogoDetalleServicio catalogoDetalleServicio;
    @EJB
    private CatastroPredialValoracionServicio catastroPredialValoracionServicio;
    @EJB
    private CatastroPredialAlcabalaValoracionServicio catastroPredialAlcabalaValoracionServicio;
    @EJB
    private CpAlcabalaValoracionExtrasServicio cpAlcabalaValoracionExtrasServicio;
    @EJB
    private AdicionalesDeductivosServicio adicionalesDeductivosServicio;
    @EJB
    private PredioArchivoServicio predioArchivoServicio;
    @EJB
    private DatoGlobalServicio datoGlobalServicio;
    
    /// SERVICIOS PLUSVALIA
    
    @EJB
    private CatastroPredialPlusvaliaValoracionServicio catastroPredialPlusvaliaValoracionServicio;
    
    

    @PostConstruct
    public void inicializar() {
        try {
            catastroPredialAlcabalaValoracion = new CatastroPredialAlcabalaValoracion();
            catastroPredialActual = new CatastroPredial();
            catalogoDetalleConcepto = new CatalogoDetalle();
            listaAlcabalasArchivo = new ArrayList<PredioArchivo>();
           
            predioArchivo = new PredioArchivo();
            anio = 0;
            
            areaTerreno = new BigDecimal(BigInteger.ZERO);
            areaCons = new BigDecimal(BigInteger.ZERO);
            areaTotal = new BigDecimal(BigInteger.ZERO);
            renumeracion =  new BigDecimal(BigInteger.ZERO).setScale(2, RoundingMode.HALF_UP);
            visibleRenumeracion = "";
            
            obtenerUsuario();
            listarConceptos();
            listarCatalogosDetalle();
            
            
            // INICIALIZAR PLUSVALIA
            catastroPredialPlusvaliaValoracion = new CatastroPredialPlusvaliaValoracion();
             listaPlusvaliaArchivo = new ArrayList<PredioArchivo>();
            
            listarTipoTarifa();
            
            // EMISION ALCABALA PLUSVALIA          
            
            ejecutarValoracionSeleccion = new EjecutarValoracion(); 
            listAnios = new  ArrayList<CatalogoDetalle>();
            catDetAnio= new CatalogoDetalle();
            listaCatastroPredialTablaValoracionSeleccion = new ArrayList<EjecutarValoracion>();
            listarAnios();
            
            //ejecutarValoracion();

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void obtenerUsuario() {
        usuarioActual = new SegUsuario();
        usuarioActual = (SegUsuario) getSession().getAttribute("usuario");
        //System.out.println(usuarioActual.getUsuIdentificacion());         
    }

    public GestionAlcabalasControlador() {
    }

    public void listarCatastroPredial() {
        try {
            listaCatastroPredial = new ArrayList<CatastroPredial>();
            listaCatastroPredial = catastroPredialServicio.listarClaveCatastral();

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

    public void obtenerCamposCatPredial() {
        try {
            
            visibleRenumeracion = "";
            
            catastroPredialActual = catastroPredialServicio.cargarObjetoCatastro(catastroPredialActual.getCatpreCodigo());                                    
            if(catastroPredialActual.getCatpreAreaTotal()==null){
                areaTerreno = new BigDecimal(BigInteger.ZERO).setScale(2, RoundingMode.HALF_UP);           
            }else{
               areaTerreno = new BigDecimal(catastroPredialActual.getCatpreAreaTotal()).setScale(2, RoundingMode.HALF_UP);
            }
            
             if(catastroPredialActual.getCatpreAreaTotalCons()==null){
                 areaCons = new BigDecimal(BigInteger.ZERO).setScale(2, RoundingMode.HALF_UP);              
             }else{
                 areaCons = new BigDecimal(catastroPredialActual.getCatpreAreaTotalCons()).setScale(2, RoundingMode.HALF_UP);
             }
                                               
            
            propietario = new Propietario();
            propietario = catastroPredialServicio.obtenerPropietarioPrincipalPredio(catastroPredialActual.getCatpreCodigo());
            System.out.println("s: " + propietario.getProCi());
            System.out.println("m: " + propietario.getProNombres());

            catastroPredialValoracionActual = new CatastroPredialValoracion();
            catastroPredialValoracionActual = catastroPredialValoracionServicio.existeCatastroValoracion(catastroPredialActual, anio); 
            
           listaAdicionalesDeductivosDeduccionesSeleccion = new ArrayList<String>();           
           listaAdicionalesDeductivosExcencionesSeleccion = new ArrayList<String>();           
                        
            catastroPredialAlcabalaValoracion = catastroPredialServicio.buscarAlcabalaPorCatastroPredialAnio(catastroPredialActual,anio);
            if(catastroPredialAlcabalaValoracion==null){
                
                catastroPredialAlcabalaValoracion = new CatastroPredialAlcabalaValoracion();                
                catastroPredialAlcabalaValoracion.setCatprealcvalAnio(anio); 
            }else{
            
                
                 List<AdicionalesDeductivos> listaD_AL = adicionalesDeductivosServicio.recuperarAdicionalesDeductivosAlcabala(catastroPredialAlcabalaValoracion, "AL", "D");
                for (int i = 0; i < listaD_AL.size(); i++) {                     
                     listaAdicionalesDeductivosDeduccionesSeleccion.add(listaD_AL.get(i).getAdidedCodigo()+"");
                }
                                
                 List<AdicionalesDeductivos> listaE_AL = adicionalesDeductivosServicio.recuperarAdicionalesDeductivosAlcabala(catastroPredialAlcabalaValoracion, "AL", "E");                                                   
                for (int i = 0; i < listaE_AL.size(); i++) {                     
                     listaAdicionalesDeductivosExcencionesSeleccion.add(listaE_AL.get(i).getAdidedCodigo()+"");
                }
                
                muestraRenumeracion();
                // buscar  adiocnales deductivos.
            
            }
            
            
            listarArchivos();
            listarArchivosPL();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            
        }
    }

    public BigDecimal valorMayorBaseImponible() {
        BigDecimal baseImponible = new BigDecimal(BigInteger.ONE);
        try {
            if (catastroPredialValoracionActual.getCatprevalAvaluoTot().compareTo(catastroPredialAlcabalaValoracion.getCatprealcvalPrecioventa()) == -1) {
                baseImponible = catastroPredialAlcabalaValoracion.getCatprealcvalPrecioventa();
            } else {
                baseImponible = catastroPredialValoracionActual.getCatprevalAvaluoTot();
            }
        catastroPredialPlusvaliaValoracion.setCatprepluvalPrecioventa(baseImponible); 
        
            System.out.println("");
            
      } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return baseImponible;
    }
    
    public void calcularDeterminacion() {
        try {

            BigDecimal baseImponible = valorMayorBaseImponible();
//            if (catastroPredialValoracionActual.getCatprevalAvaluoTerr().compareTo(catastroPredialAlcabalaValoracion.getCatprealcvalPrecioventa()) == -1) {
//                baseImponible = catastroPredialAlcabalaValoracion.getCatprealcvalPrecioventa();
//            } else {
//                baseImponible = catastroPredialValoracionActual.getCatprevalAvaluoTerr();
//            }
            BigDecimal impuesto = baseImponible.multiply(new BigDecimal(1)).divide(new BigDecimal(100));
            BigDecimal conProv = baseImponible.multiply(new BigDecimal(0.01)).divide(new BigDecimal(100));
           //conProv = conProv.setScale(2, RoundingMode.CEILING);

            catastroPredialAlcabalaValoracion.setCatprealcvalBaseimp(baseImponible.setScale(2, RoundingMode.HALF_UP));
            catastroPredialAlcabalaValoracion.setCatprealcvalImpuesto(impuesto.setScale(2, RoundingMode.HALF_UP));
            catastroPredialAlcabalaValoracion.setCatprealcvalConsejoProv(conProv.setScale(2, RoundingMode.HALF_UP));
            catastroPredialAlcabalaValoracion.setCatprealcvalTasaProc(new BigDecimal(2).setScale(2, RoundingMode.HALF_UP));
            catastroPredialAlcabalaValoracion.setCatprealcvalTotal(impuesto.add(conProv).add(catastroPredialAlcabalaValoracion.getCatprealcvalTasaProc()).setScale(2, RoundingMode.HALF_UP));

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void guardarAlcabala() {
        try {
            catastroPredialAlcabalaValoracion.setCatpreCodigo(catastroPredialActual);
            catastroPredialAlcabalaValoracion.setCatprealcvalActivo(false); 
            catastroPredialAlcabalaValoracionServicio.crearCatastroPredialAlcabalaValoracion(catastroPredialAlcabalaValoracion);
            addSuccessMessage("Guardado Exitosamente!","Guardado Exitosamente!");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void listarCatalogosDetalle() {
        try {
            listaAdicionalesDeductivosDeducciones = new ArrayList<AdicionalesDeductivos>();
            listaAdicionalesDeductivosDeducciones = adicionalesDeductivosServicio.listarAdicionesDeductivosTipo("D", "AL");
            listaAdicionalesDeductivosExcenciones = new ArrayList<AdicionalesDeductivos>();
            listaAdicionalesDeductivosExcenciones = adicionalesDeductivosServicio.listarAdicionesDeductivosTipo("E", "AL");

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void listarArchivos() {
        try {
            if (catastroPredialActual != null) {
                listaAlcabalasArchivo = new ArrayList<PredioArchivo>();
                listaAlcabalasArchivo = predioArchivoServicio.listarArchivosXImpuesto(catastroPredialActual, "AL",anio);
            } else {
                listaAlcabalasArchivo = new ArrayList<PredioArchivo>();
                addWarningMessage("Eliga la clave Catastral!");

            }
        } catch (Exception ex) {
            addWarningMessage("Eliga la clave Catastral!");
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
     public void listarArchivosPL() {
        try {
            if (catastroPredialActual != null) {
                listaPlusvaliaArchivo = new ArrayList<PredioArchivo>();
                listaPlusvaliaArchivo = predioArchivoServicio.listarArchivosXImpuesto(catastroPredialActual, "PL",anio);
            } else {
                listaPlusvaliaArchivo = new ArrayList<PredioArchivo>();
                addWarningMessage("Eliga la clave Catastral!");

            }
        } catch (Exception ex) {
            addWarningMessage("Eliga la clave Catastral!");
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void eliminarArchivo(PredioArchivo archivo) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            predioArchivoServicio.eliminarPredioArchivo(archivo);
            //context.addMessage(null, new FacesMessage("Mensaje:", "Se Elimino el Archivo  " + archivo.getPrearcNombre()));
            addSuccessMessage("Se eliminó el Archivo " + archivo.getPrearcNombre(),"Se eliminó el Archivo " + archivo.getPrearcNombre());            
            listarArchivos();

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public void eliminarArchivoPL(PredioArchivo archivo) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            predioArchivoServicio.eliminarPredioArchivo(archivo);
            addSuccessMessage("Se eliminó el Archivo " + archivo.getPrearcNombre(),"Se eliminó el Archivo " + archivo.getPrearcNombre());            
            listarArchivosPL();

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            predioArchivo = new PredioArchivo();
            predioArchivo.setPrearcNombre(event.getFile().getFileName());
            predioArchivo.setCatpreCodigo(catastroPredialActual);
            predioArchivo.setPrearcData(event.getFile().getContents());
            predioArchivo.setPrearcTipo("AL");
            predioArchivo.setPrearcAnio(anio); 
            predioArchivo.setUsuIdentificacion(usuarioActual);
            predioArchivo.setUltaccDetalle("Documento justificativo de la deducción o exención - Alcabala");
            predioArchivo.setUltaccMarcatiempo(new Date());
            predioArchivoServicio.crearPredioArchivo(predioArchivo);
            addSuccessMessage("El documento "+ event.getFile().getFileName() + " ha sido cargado satisfactoriamente.","El documento "+ event.getFile().getFileName() + " ha sido cargado satisfactoriamente.");             
            listarArchivos();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public void handleFileUploadPL(FileUploadEvent event) {
        try {
            predioArchivo = new PredioArchivo();
            predioArchivo.setPrearcNombre(event.getFile().getFileName());
            predioArchivo.setCatpreCodigo(catastroPredialActual);
            predioArchivo.setPrearcData(event.getFile().getContents());
            predioArchivo.setPrearcTipo("PL");
            predioArchivo.setPrearcAnio(anio); 
            predioArchivo.setUsuIdentificacion(usuarioActual);
            predioArchivo.setUltaccDetalle("Documento justificativo de la determinacion y deduciones - Plusvalía");
            predioArchivo.setUltaccMarcatiempo(new Date());
            predioArchivoServicio.crearPredioArchivo(predioArchivo);
            addSuccessMessage("El documento "+ event.getFile().getFileName() + " ha sido cargado satisfactoriamente.","El documento "+ event.getFile().getFileName() + " ha sido cargado satisfactoriamente.");             
            listarArchivosPL();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

  
    public void descargarArchivo(PredioArchivo patArchivoActual) {
        predioArchivo = patArchivoActual;
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("datoArchivo", patArchivoActual.getPrearcData());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("nombreArchivo", patArchivoActual.getPrearcNombre());
    }

     //Se descarga archivo por medio de  Servlet
    public String download() {
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.addHeader("Content-disposition", "attachment; filename=\"" + "c://subido" + predioArchivo.getPrearcNombre() + "\"");
        try {
            ServletOutputStream os = response.getOutputStream();
            os.write(predioArchivo.getPrearcData());
            os.flush();
            os.close();
            FacesContext.getCurrentInstance().responseComplete();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
        return null;
    }
    
    
    public void calularAdicionalesAlcabalas(AdicionalesDeductivos adicionalesDeductivosActual1) {
        try {                    
                                    
            if(adicionalesDeductivosActual1.getAdidedNemonico().equals("E_R10")){
                
                // capital quemado
                BigDecimal capital = new BigDecimal("3550");
                BigDecimal RBU10  = renumeracion.multiply(new BigDecimal("10")); 
                double porcentaje=0.0;
                if(capital.compareTo(RBU10)==-1){
                    porcentaje = 100.0;                    
                }else{
                    if(capital.compareTo(RBU10)==0 || capital.compareTo(RBU10)==1){
                          porcentaje = 50; 
                    }
                }
                
            cpAlcabalaValoracionExtrasActual.setCpalcvalextBase(catastroPredialAlcabalaValoracion.getCatprealcvalTotal());
            cpAlcabalaValoracionExtrasActual.setCpalcvalextValor(catastroPredialAlcabalaValoracion.getCatprealcvalTotal().multiply(new BigDecimal(porcentaje)).divide(new BigDecimal(100)));           
                
            }else{
                cpAlcabalaValoracionExtrasActual.setCpalcvalextBase(catastroPredialAlcabalaValoracion.getCatprealcvalTotal());
            cpAlcabalaValoracionExtrasActual.setCpalcvalextValor(catastroPredialAlcabalaValoracion.getCatprealcvalTotal().multiply(new BigDecimal(adicionalesDeductivosActual1.getAdidedPorcentaje())).divide(new BigDecimal(100)));           
            }
            
            
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public void guardarDeduccionesExcenciones() {
        try {

            adicionalesDeductivosActual = new AdicionalesDeductivos();
            //catastroPredialValoracionActual = new CatastroPredialValoracion();

            if (catastroPredialActual != null) {
                try {
                    if (listaAlcabalasArchivo.size() > 0) {

                        if(visibleRenumeracion.equals("E_R10") && renumeracion.compareTo(BigDecimal.ZERO)==0){
                        
                            addErrorMessage("Se necesita un valor: Remuneración","Se necesita un valor: Remuneración"); 
                        
                        }else{
                        
                        //catastroPredialAlcabalaValoracion = catastroPredialServicio.buscarAlcabalaPorCatastroPredial(catastroPredialActual);
                        if (catastroPredialAlcabalaValoracion != null) {
                            cpAlcabalaValoracionExtrasServicio.eliminarCpValoracionExtrar(catastroPredialAlcabalaValoracion);
                            
                            for (int i = 0; i < listaAdicionalesDeductivosDeduccionesSeleccion.size(); i++) {
                                cpAlcabalaValoracionExtrasActual = new CpAlcabalaValoracionExtras();
                                adicionalesDeductivosActual = adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(Integer.parseInt(listaAdicionalesDeductivosDeduccionesSeleccion.get(i)));
                                cpAlcabalaValoracionExtrasActual.setCatprealcvalCodigo(catastroPredialAlcabalaValoracion);
                                cpAlcabalaValoracionExtrasActual.setAdidedCodigo(adicionalesDeductivosActual);                                
                                calularAdicionalesAlcabalas(adicionalesDeductivosActual);                           
                                cpAlcabalaValoracionExtrasServicio.crearCpAlcabalaValoracionExtras(cpAlcabalaValoracionExtrasActual);
                            }

                            for (int i = 0; i < listaAdicionalesDeductivosExcencionesSeleccion.size(); i++) {
                                cpAlcabalaValoracionExtrasActual = new CpAlcabalaValoracionExtras();
                                adicionalesDeductivosActual = adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(Integer.parseInt(listaAdicionalesDeductivosExcencionesSeleccion.get(i)));
                                cpAlcabalaValoracionExtrasActual.setCatprealcvalCodigo(catastroPredialAlcabalaValoracion);
                                cpAlcabalaValoracionExtrasActual.setAdidedCodigo(adicionalesDeductivosActual);
                                 calularAdicionalesAlcabalas(adicionalesDeductivosActual); 
                                cpAlcabalaValoracionExtrasServicio.crearCpAlcabalaValoracionExtras(cpAlcabalaValoracionExtrasActual);
                            }

                            addSuccessMessage("Guardado Exitosamente!","Guardado Exitosamente!");

                        } else {
                            addErrorMessage("No existe Determinación del Alcabala","No existe Determinación del Alcabala");

                        }
                        
                        
                        } // fin remuneracion 
                        

                    } else {
                        addSuccessMessage("No se han cargado documentos!","No se han cargado documentos!");
                    }
                } catch (NullPointerException exNull) {
                    // LOGGER.log(Level.SEVERE, null, exNull);
                    addSuccessMessage("No se han cargado documentos!","No se han cargado documentos!");
//              FacesMessage msg = new FacesMessage("No se han cargado documentos!");
//        FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            } else {
                addErrorMessage("No existe Clave Catastral","No existe Clave Catastral");
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
     public List<PropietarioPredio> obtenerPropietarioPredioPorApellidoProp(String vapellido) {
        List<PropietarioPredio> lstPP = new ArrayList<PropietarioPredio>();
        try {
            lstPP = catastroPredialServicio.listarPropietariosPredioPorApellidoPropContiene(vapellido);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return lstPP;
    }
     
       public void onItemSelect(SelectEvent event) {
        try {
            PropietarioPredio pp = (PropietarioPredio) event.getObject();
            pp = catastroPredialServicio.buscarPropietarioPredioPorCodigo(pp.getPropreCodigo());            
            catastroPredialActual = catastroPredialServicio.cargarObjetoCatastro(pp.getCatpreCodigo().getCatpreCodigo());                                               
            
            
            
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    //////////////////////////////////// METODOS PLUSVALIA  ////////////////////////////////////
    
    
    
    public void listarTipoTarifa() {        
        try {
           listaTipoDeTarifa = new ArrayList<CatalogoDetalle>();
           listaTipoDeTarifa = catastroPredialServicio.listarTipoDeTarifa();            
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
     public void calularDiferenciaBruta() {        
        try {
            catastroPredialPlusvaliaValoracion.setCatprepluvalDifBruta(catastroPredialPlusvaliaValoracion.getCatprepluvalPrecioventa().subtract(catastroPredialPlusvaliaValoracion.getCatprepluvalPrecioventaAnt()).setScale(2, RoundingMode.HALF_UP));                       
            BigDecimal valorMejora;            
             ObraProyecto obraProyecto = catastroPredialServicio.buscarMejoraXCatastro(catastroPredialActual);            
             if(obraProyecto.getObrTotal()==null){
                 valorMejora = BigDecimal.ZERO;  
             }else{
                 valorMejora = obraProyecto.getObrTotal();
             }
            catastroPredialPlusvaliaValoracion.setCatprepluvalValorContrmej(valorMejora.setScale(2, RoundingMode.HALF_UP));             
            calularDiferenciaNeta();
            
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public void calularDiferenciaNeta() {        
        try {
            catastroPredialPlusvaliaValoracion.setCatprepluvalDifNeta(catastroPredialPlusvaliaValoracion.getCatprepluvalDifBruta().subtract(catastroPredialPlusvaliaValoracion.getCatprepluvalValorContrmej()).setScale(2, RoundingMode.HALF_UP));                                                  
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public void calularValorAniosTranDominio() {        
        try {                                  
            catastroPredialPlusvaliaValoracion.setCatprepluvalAniosTransfVal(new BigDecimal(catastroPredialPlusvaliaValoracion.getCatprepluvalAniosTransf()).multiply(catastroPredialPlusvaliaValoracion.getCatprepluvalDifNeta().multiply(new BigDecimal(0.05))).setScale(2, RoundingMode.HALF_UP));            
            catastroPredialPlusvaliaValoracion.setCatprepluvalDifFinal(catastroPredialPlusvaliaValoracion.getCatprepluvalDifNeta().subtract(catastroPredialPlusvaliaValoracion.getCatprepluvalAniosTransfVal()).setScale(2, RoundingMode.HALF_UP));                         
            calularRebajaDesvalorizacionBaseImpImpuesto();
            
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
public void calularRebajaDesvalorizacionBaseImpImpuesto() {        
        try {                                  
            // valor quemado porcentaje rebaja
            catastroPredialPlusvaliaValoracion.setCatprepluvalPorcRebaja(0); 
            catastroPredialPlusvaliaValoracion.setCatprepluvalValorRebaja(new BigDecimal(catastroPredialPlusvaliaValoracion.getCatprepluvalPorcRebaja()).multiply(catastroPredialPlusvaliaValoracion.getCatprepluvalDifFinal()).setScale(2, RoundingMode.HALF_UP));
            catastroPredialPlusvaliaValoracion.setCatprepluvalBaseimp(catastroPredialPlusvaliaValoracion.getCatprepluvalDifFinal().subtract(catastroPredialPlusvaliaValoracion.getCatprepluvalValorRebaja()).setScale(2, RoundingMode.HALF_UP));                                                           
//            System.out.println("ss: "+catastroPredialServicio.cargarObjetoCatalogoDetalle(catastroPredialPlusvaliaValoracion.getCatdetTipoTarifa().getCatdetCodigo()).getCatdetValorDecimal()); 
//            System.out.println("ssbaseImp: "+catastroPredialPlusvaliaValoracion.getCatprepluvalBaseimp()); 
            catastroPredialPlusvaliaValoracion.setCatprepluvalImpuesto(new BigDecimal(catastroPredialServicio.cargarObjetoCatalogoDetalle(catastroPredialPlusvaliaValoracion.getCatdetTipoTarifa().getCatdetCodigo()).getCatdetValorDecimal()).multiply(catastroPredialPlusvaliaValoracion.getCatprepluvalBaseimp()).setScale(2, RoundingMode.HALF_UP));            
            catastroPredialPlusvaliaValoracion.setCatprepluvalTasaproc(new BigDecimal(datoGlobalServicio.obtenerDatoGlobal("Val_tasa_procesamiento").getDatgloValor()).setScale(2, RoundingMode.HALF_UP));
            
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public void guardarPlusvalia() {
        try {
          if (listaPlusvaliaArchivo.size() > 0) {
          catastroPredialPlusvaliaValoracion.setCatpreCodigo(catastroPredialActual); 
            catastroPredialPlusvaliaValoracion.setCatprepluvalActivo(false); 
            catastroPredialPlusvaliaValoracion.setCatprepluvalAnio(anio);
            catastroPredialPlusvaliaValoracionServicio.crearCatastroPredialPlusvaliaValoracion(catastroPredialPlusvaliaValoracion);           
            addSuccessMessage("Guardado Exitosamente!","Guardado Exitosamente!");
         } else {
            addErrorMessage("No se han cargado documentos!","No se han cargado documentos!");             
           }                                    
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public void muestraRenumeracion() {
        try {
            visibleRenumeracion = "";
            for (int i = 0; i < listaAdicionalesDeductivosExcencionesSeleccion.size(); i++) {
                adicionalesDeductivosActual = adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(Integer.parseInt(listaAdicionalesDeductivosExcencionesSeleccion.get(i)));

                if (adicionalesDeductivosActual.getAdidedNemonico().equals("E_R10")) {
                    visibleRenumeracion = adicionalesDeductivosActual.getAdidedNemonico();
                    i = listaAdicionalesDeductivosExcencionesSeleccion.size();
                }                               
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
     //////////////////////////////////// METODOS EMISIÓN DE ALCABALAS y PLUSVALIAS ////////////////////////////////////
    
    public void ejecutarValoracion() {
        try {
            
            listaEjecutarValoracion = new ArrayList<EjecutarValoracion>();
            listaCatastroPredialTablaValoracion = new ArrayList<CatastroPredial>();                                                
            listaCatastroPredialTablaValoracion = catastroPredialServicio.listarClaveCatastral();                           
             catDetAnio = catastroPredialServicio.cargarObjetoCatalogoDetalle(catDetAnio.getCatdetCodigo());
            // System.out.println("fsfsfsf "+ catDetAnio.getCatdetValor());
                        
            for (int i = 0; i < listaCatastroPredialTablaValoracion.size(); i++) {                                           
                EjecutarValoracion eVal = new EjecutarValoracion();
                CatastroPredial CP = listaCatastroPredialTablaValoracion.get(i);
                eVal.setCatastroPredial(CP);
                eVal.setAnio(catDetAnio.getCatdetValor());
                eVal.setCatpreCodigo(CP.getCatpreCodigo());
                eVal.setCatpreClaveCatastal(CP.getCatpreCodNacional() + CP.getCatpreCodLocal());
                eVal.setProCi(catastroPredialServicio.obtenerPropietarioPrincipalPredio(CP.getCatpreCodigo())); 
                
                CatastroPredialAlcabalaValoracion ALCA = catastroPredialServicio.buscarAlcabalaPorCatastroPredialAnio(CP, catDetAnio.getCatdetValor());                                                                                                               
                if (ALCA != null) {
                    if (ALCA.getCatprealcvalTotal() == null) {
                        ALCA.setCatprealcvalTotal(BigDecimal.ZERO);
                    } else {

                        if (cpAlcabalaValoracionExtrasServicio.obteneValorTipoAdicionalAlcabala(ALCA.getCatprealcvalCodigo(), "AL", "D") != null) {
                            eVal.setTotalAlcabalaDeducciones(cpAlcabalaValoracionExtrasServicio.obteneValorTipoAdicionalAlcabala(ALCA.getCatprealcvalCodigo(), "AL", "D"));
                        } else {
                            eVal.setTotalAlcabalaDeducciones(BigDecimal.ZERO);
                        }
                        
                        if (cpAlcabalaValoracionExtrasServicio.obteneValorTipoAdicionalAlcabala(ALCA.getCatprealcvalCodigo(), "AL", "E") != null) {
                            eVal.setTotalAlcabalaExenciones(cpAlcabalaValoracionExtrasServicio.obteneValorTipoAdicionalAlcabala(ALCA.getCatprealcvalCodigo(), "AL", "E"));
                        } else {
                            eVal.setTotalAlcabalaExenciones(BigDecimal.ZERO);
                        }
                        
                        BigDecimal totalFinal = ALCA.getCatprealcvalTotal().subtract(eVal.getTotalAlcabalaDeducciones()).subtract(eVal.getTotalAlcabalaExenciones()); 
                        if(totalFinal.signum()==-1){
                            totalFinal = BigDecimal.ZERO;
                        }
                        ALCA.setCatprealcvalTotalDedEx(totalFinal); 
                        catastroPredialAlcabalaValoracionServicio.editarCatastroPredialAlcabalaValoracion(ALCA);
                        eVal.setTotalAlcabalaFinal(totalFinal);                         
                    }
                                                                               
                }else{
                    ALCA = new CatastroPredialAlcabalaValoracion();
                    ALCA.setCatprealcvalTotal(BigDecimal.ZERO);                                        
                }
                
                
                
                eVal.setCatastroPredialAlcabalaValoracion(ALCA); 
                  
                CatastroPredialPlusvaliaValoracion PLUS = catastroPredialServicio.buscarPlusvaliaPorCatastroPredialAnio(CP,catDetAnio.getCatdetValor());                                                                 
                if(PLUS!=null){                    
                    if(PLUS.getCatprepluvalImpuesto()==null){
                        PLUS.setCatprepluvalImpuesto(BigDecimal.ZERO);
                    }                                                                              
                }else{
                    PLUS = new CatastroPredialPlusvaliaValoracion();
                    PLUS.setCatprepluvalImpuesto(BigDecimal.ZERO);                                
                }
                eVal.setCatastroPredialPlusvaliaValoracion(PLUS);                                                                                 
                listaEjecutarValoracion.add(eVal); 
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

    }
    
    
    public void emision() {
        try {
            
             System.out.println("nnnnnnnnnnnnnnnnn "+listaCatastroPredialTablaValoracionSeleccion.size());              
            // for (int i = 0; i < listaCatastroPredialTablaValoracionSeleccion.size(); i++) {                                                   
                 recaudacioCab = new RecaudacionCab();
                 EjecutarValoracion eje = ejecutarValoracionSeleccion;
                 
                 System.out.println("nnnnnnnnnnnnnnnnn "+eje.getCatastroPredialAlcabalaValoracion().getCatpreCodigo());
                 
                 if(eje.getCatastroPredialAlcabalaValoracion().getCatprealcvalCodigo()!=null){
                     
                 CatastroPredialAlcabalaValoracion catastroPredialAlcabalaValoracion1 = catastroPredialAlcabalaValoracionServicio.buscarCatastroPredialAlcabalaValoracion(eje.getCatastroPredialAlcabalaValoracion().getCatprealcvalCodigo());
                   
                 if(catastroPredialAlcabalaValoracion1.isCatprealcvalActivo()==false){
                 recaudacioCab.setProCi(eje.getProCi()); 
                 recaudacioCab.setRecFecha(new Date());                                     
                 recaudacioCab.setRecTotal(eje.getCatastroPredialAlcabalaValoracion().getCatprealcvalTotal());
                 recaudacioCab.setRecEstado("A");
                 recaudacioCab.setUsuIdentificacion(usuarioActual);                  
                 recaudacionCabServicio.crearRecaudacionCab(recaudacioCab);
                                                   
                 recaudacionDet = new RecaudacionDet();
                 recaudacionDet.setRecCodigo(recaudacioCab); 
                 recaudacionDet.setRecdetTipo("AL"); 
                 recaudacionDet.setRecdetReferencia(eje.getCatastroPredialAlcabalaValoracion().getCatpreCodigo().getCatpreCodNacional()+eje.getCatastroPredialAlcabalaValoracion().getCatpreCodigo().getCatpreCodLocal());
                 recaudacionDet.setRecdetValor(eje.getCatastroPredialAlcabalaValoracion().getCatprealcvalTotal());              
                 recaudacionDetServicio.crearRecaudacionDet(recaudacionDet);
                 
                  catastroPredialAlcabalaValoracion1.setCatprealcvalActivo(true);  
                 catastroPredialAlcabalaValoracionServicio.editarCatastroPredialAlcabalaValoracion(catastroPredialAlcabalaValoracion1);                 
                 addSuccessMessage("Emisión Realizada","Emisión Realizada");
                 }else{                 
                  addSuccessMessage("Alcabala ya fue emitida","Alcabala ya fue emitida");                     
                 } 
                 }
                 
                 if(eje.getCatastroPredialPlusvaliaValoracion().getCatprepluvalCodigo()!=null){
                     
                 CatastroPredialPlusvaliaValoracion CatastroPredialPlusvaliaValoracion1 = catastroPredialPlusvaliaValoracionServicio.buscarCatastroPredialPlusvaliaValoracion(eje.getCatastroPredialPlusvaliaValoracion().getCatprepluvalCodigo());    
                 
                  if(CatastroPredialPlusvaliaValoracion1.isCatprepluvalActivo()==false){
                      
                 recaudacioCab = new RecaudacionCab();                
                 recaudacioCab.setProCi(eje.getProCi()); 
                 recaudacioCab.setRecFecha(new Date()); 
                 recaudacioCab.setRecTotal(eje.getCatastroPredialPlusvaliaValoracion().getCatprepluvalImpuesto());
                 recaudacioCab.setRecEstado("A");
                 recaudacioCab.setUsuIdentificacion(usuarioActual);                  
                 recaudacionCabServicio.crearRecaudacionCab(recaudacioCab);
                                                   
                 recaudacionDet = new RecaudacionDet();
                 recaudacionDet.setRecCodigo(recaudacioCab); 
                 recaudacionDet.setRecdetTipo("PL"); 
                 recaudacionDet.setRecdetReferencia(eje.getCatastroPredialPlusvaliaValoracion().getCatpreCodigo().getCatpreCodNacional()+eje.getCatastroPredialPlusvaliaValoracion().getCatpreCodigo().getCatpreCodLocal());
                 recaudacionDet.setRecdetValor(eje.getCatastroPredialPlusvaliaValoracion().getCatprepluvalImpuesto());
                 recaudacionDetServicio.crearRecaudacionDet(recaudacionDet);  
                 
                 CatastroPredialPlusvaliaValoracion1.setCatprepluvalActivo(true);
                 catastroPredialPlusvaliaValoracionServicio.editarCatastroPredialPlusvaliaValoracion(CatastroPredialPlusvaliaValoracion1);
                 addSuccessMessage("Emisión Realizada","Emisión Realizada");
                  }else{
                  
                   addSuccessMessage("Plusvalia ya fue emitida","Plusvalia ya fue emitida");   
                  }                                  
                  }                                                                     
                 
            // }
                                    
        } catch (Exception ex) {
                      
            LOGGER.log(Level.SEVERE, null, ex);         
        }
    }
    
    
    
    public void listarAnios() throws Exception {
        listAnios = catalogoDetalleServicio.listarPorNemonicoCatalogo("ANIOS");
    }
    
    public void postProcessXLS(Object document) throws IOException {

        XSSFWorkbook wb = (XSSFWorkbook) document;
        XSSFSheet hoja = wb.getSheetAt(0);
        CellStyle style = wb.createCellStyle();
        style.setFillPattern(CellStyle.NO_FILL);
        org.apache.poi.ss.usermodel.Font font = wb.createFont();
        font.setFontName("Times Roman");
        font.setBoldweight(org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        /**
         * ** ConfiguraciÃ³n del estilo de la celda header de la tabla. *****
         */
        CellStyle styleHeaderTable = wb.createCellStyle();
        styleHeaderTable.setFillPattern(CellStyle.NO_FILL);

        org.apache.poi.ss.usermodel.Font fontHeaderTable = wb.createFont();
        fontHeaderTable.setFontName("Times Roman");
        fontHeaderTable.setBoldweight(org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD);
        fontHeaderTable.setColor(IndexedColors.BLACK.getIndex());
        styleHeaderTable.setFont(fontHeaderTable);
        Sheet sheet = wb.getSheetAt(0);
        sheet.autoSizeColumn((short) 0); //ajusta el ancho de la primera columna
        sheet.autoSizeColumn((short) 1);
        sheet.autoSizeColumn((short) 2);
        for (int i = 0; i < 20; i++) {
            hoja.autoSizeColumn((short) i);
        }
    }
    
    public void resetearFitrosTabla(String id) {
        DataTable table = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(id);
        table.reset();
    }

    public void onRowSelect(SelectEvent event) {   
        
        EjecutarValoracion d = (EjecutarValoracion) event.getObject();
        ejecutarValoracionSeleccion=d;        
        //System.out.println("fff"+ d.getCatastroPredialAlcabalaValoracion().getCatprealcvalTotal());        
        
    }
    ////////////////////////////////////// METODOS SET Y GET ALCABALA  ////////////////////////////////////
    
    public StreamedContent getArchivo() {
        return archivo;
    }

    public void setArchivo(StreamedContent archivo) {
        this.archivo = archivo;
    }

    public List<CatastroPredial> getListaCatastroPredial() {
        return listaCatastroPredial;
    }

    public void setListaCatastroPredial(List<CatastroPredial> listaCatastroPredial) {
        this.listaCatastroPredial = listaCatastroPredial;
    }

    public CatastroPredial getCatastroPredialActual() {
        return catastroPredialActual;
    }

    public void setCatastroPredialActual(CatastroPredial catastroPredialActual) {
        this.catastroPredialActual = catastroPredialActual;
    }

    public CatastroPredialAlcabalaValoracion getCatastroPredialAlcabalaValoracion() {
        return catastroPredialAlcabalaValoracion;
    }

    public void setCatastroPredialAlcabalaValoracion(CatastroPredialAlcabalaValoracion catastroPredialAlcabalaValoracion) {
        this.catastroPredialAlcabalaValoracion = catastroPredialAlcabalaValoracion;
    }

    public List<CatalogoDetalle> getListaCatalogoDetalleConcepto() {
        return listaCatalogoDetalleConcepto;
    }

    public void setListaCatalogoDetalleConcepto(List<CatalogoDetalle> listaCatalogoDetalleConcepto) {
        this.listaCatalogoDetalleConcepto = listaCatalogoDetalleConcepto;
    }

    public CatalogoDetalle getCatalogoDetalleConcepto() {
        return catalogoDetalleConcepto;
    }

    public void setCatalogoDetalleConcepto(CatalogoDetalle catalogoDetalleConcepto) {
        this.catalogoDetalleConcepto = catalogoDetalleConcepto;
    }

    public CatastroPredialValoracion getCatastroPredialValoracionActual() {
        return catastroPredialValoracionActual;
    }

    public void setCatastroPredialValoracionActual(CatastroPredialValoracion catastroPredialValoracionActual) {
        this.catastroPredialValoracionActual = catastroPredialValoracionActual;
    }

    public List<AdicionalesDeductivos> getListaAdicionalesDeductivosDeducciones() {
        return listaAdicionalesDeductivosDeducciones;
    }

    public void setListaAdicionalesDeductivosDeducciones(List<AdicionalesDeductivos> listaAdicionalesDeductivosDeducciones) {
        this.listaAdicionalesDeductivosDeducciones = listaAdicionalesDeductivosDeducciones;
    }

    public List<String> getListaAdicionalesDeductivosDeduccionesSeleccion() {
        return listaAdicionalesDeductivosDeduccionesSeleccion;
    }

    public void setListaAdicionalesDeductivosDeduccionesSeleccion(List<String> listaAdicionalesDeductivosDeduccionesSeleccion) {
        this.listaAdicionalesDeductivosDeduccionesSeleccion = listaAdicionalesDeductivosDeduccionesSeleccion;
    }

    public List<AdicionalesDeductivos> getListaAdicionalesDeductivosExcenciones() {
        return listaAdicionalesDeductivosExcenciones;
    }

    public void setListaAdicionalesDeductivosExcenciones(List<AdicionalesDeductivos> listaAdicionalesDeductivosExcenciones) {
        this.listaAdicionalesDeductivosExcenciones = listaAdicionalesDeductivosExcenciones;
    }

    public List<String> getListaAdicionalesDeductivosExcencionesSeleccion() {
        return listaAdicionalesDeductivosExcencionesSeleccion;
    }

    public void setListaAdicionalesDeductivosExcencionesSeleccion(List<String> listaAdicionalesDeductivosExcencionesSeleccion) {
        this.listaAdicionalesDeductivosExcencionesSeleccion = listaAdicionalesDeductivosExcencionesSeleccion;
    }

    public Propietario getPropietario() {
        return propietario;
    }

    public void setPropietario(Propietario propietario) {
        this.propietario = propietario;
    }

    public List<PredioArchivo> getListaAlcabalasArchivo() {
        return listaAlcabalasArchivo;
    }

    public void setListaAlcabalasArchivo(List<PredioArchivo> listaAlcabalasArchivo) {
        this.listaAlcabalasArchivo = listaAlcabalasArchivo;
    }
    
     ////////////////////////////////////// METODOS SET Y GET PLUSVALIA  ////////////////////////////////////

    public List<CatalogoDetalle> getListaTipoDeTarifa() {
        return listaTipoDeTarifa;
    }

    public void setListaTipoDeTarifa(List<CatalogoDetalle> listaTipoDeTarifa) {
        this.listaTipoDeTarifa = listaTipoDeTarifa;
    }

    public CatastroPredialPlusvaliaValoracion getCatastroPredialPlusvaliaValoracion() {
        return catastroPredialPlusvaliaValoracion;
    }

    public void setCatastroPredialPlusvaliaValoracion(CatastroPredialPlusvaliaValoracion catastroPredialPlusvaliaValoracion) {
        this.catastroPredialPlusvaliaValoracion = catastroPredialPlusvaliaValoracion;
    }
    
    ////////////////////////////////////// METODOS SET Y GET - EMISIÓN DE ALCABALAS y PLUSVALIAS ////////////////////////////////////  ////////////////////////////////////

    public List<EjecutarValoracion> getListaEjecutarValoracion() {
        return listaEjecutarValoracion;
    }

    public void setListaEjecutarValoracion(List<EjecutarValoracion> listaEjecutarValoracion) {
        this.listaEjecutarValoracion = listaEjecutarValoracion;
    }

//    public EjecutarValoracion[] getListaEjecutarValoracion() {
//        return listaEjecutarValoracion;
//    }
//
//    public void setListaEjecutarValoracion(EjecutarValoracion[] listaEjecutarValoracion) {
//        this.listaEjecutarValoracion = listaEjecutarValoracion;
//    }

    
    
    public List<CatastroPredial> getListaCatastroPredialTablaValoracion() {
        return listaCatastroPredialTablaValoracion;
    }

    public void setListaCatastroPredialTablaValoracion(List<CatastroPredial> listaCatastroPredialTablaValoracion) {
        this.listaCatastroPredialTablaValoracion = listaCatastroPredialTablaValoracion;
    }


    public List<EjecutarValoracion> getListaCatastroPredialTablaValoracionSeleccion() {
        return listaCatastroPredialTablaValoracionSeleccion;
    }

    public void setListaCatastroPredialTablaValoracionSeleccion(List<EjecutarValoracion> listaCatastroPredialTablaValoracionSeleccion) {
        this.listaCatastroPredialTablaValoracionSeleccion = listaCatastroPredialTablaValoracionSeleccion;
    }

     public PropietarioPredio getPropietarioPredioBusqueda() {
        return propietarioPredioBusqueda;
    }

    public void setPropietarioPredioBusqueda(PropietarioPredio propietarioPredioBusqueda) {
        this.propietarioPredioBusqueda = propietarioPredioBusqueda;
    }
    
    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public List<CatalogoDetalle> getListAnios() {
        return listAnios;
    }

    public void setListAnios(List<CatalogoDetalle> listAnios) {
        this.listAnios = listAnios;
    }

    public CatalogoDetalle getCatDetAnio() {
        return catDetAnio;
    }

    public void setCatDetAnio(CatalogoDetalle catDetAnio) {
        this.catDetAnio = catDetAnio;
    }

    public EjecutarValoracion getEjecutarValoracionSeleccion() {
        return ejecutarValoracionSeleccion;
    }

    public void setEjecutarValoracionSeleccion(EjecutarValoracion ejecutarValoracionSeleccion) {
        this.ejecutarValoracionSeleccion = ejecutarValoracionSeleccion;
    }

    public BigDecimal getAreaTerreno() {
        return areaTerreno;
    }

    public void setAreaTerreno(BigDecimal areaTerreno) {
        this.areaTerreno = areaTerreno;
    }

    public BigDecimal getAreaCons() {
        return areaCons;
    }

    public void setAreaCons(BigDecimal areaCons) {
        this.areaCons = areaCons;
    }

    public BigDecimal getAreaTotal() {
        return areaTotal;
    }

    public void setAreaTotal(BigDecimal areaTotal) {
        this.areaTotal = areaTotal;
    }

    public BigDecimal getRenumeracion() {
        return renumeracion;
    }

    public void setRenumeracion(BigDecimal renumeracion) {
        this.renumeracion = renumeracion;
    }

    public String getVisibleRenumeracion() {
        return visibleRenumeracion;
    }

    public void setVisibleRenumeracion(String visibleRenumeracion) {
        this.visibleRenumeracion = visibleRenumeracion;
    }

    public List<PredioArchivo> getListaPlusvaliaArchivo() {
        return listaPlusvaliaArchivo;
    }

    public void setListaPlusvaliaArchivo(List<PredioArchivo> listaPlusvaliaArchivo) {
        this.listaPlusvaliaArchivo = listaPlusvaliaArchivo;
    }
    
    
}