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
import ec.sirec.ejb.entidades.CatastroPredialEdificacion;
import ec.sirec.ejb.entidades.CatastroPredialValoracion;
import ec.sirec.ejb.entidades.CpValoracionExtras;
import ec.sirec.ejb.entidades.DatoGlobal;
import ec.sirec.ejb.entidades.FittoCorvini;
import ec.sirec.ejb.entidades.PredioArchivo;
import ec.sirec.ejb.entidades.Propietario;
import ec.sirec.ejb.entidades.PropietarioPredio;
import ec.sirec.ejb.entidades.RecaudacionCab;
import ec.sirec.ejb.entidades.RecaudacionDet;
import ec.sirec.ejb.entidades.SegUsuario;
import ec.sirec.ejb.servicios.AdicionalesDeductivosServicio;
import ec.sirec.ejb.servicios.CatastroPredialServicio;
import ec.sirec.ejb.servicios.CatastroPredialValoracionServicio;
import ec.sirec.ejb.servicios.CpValoracionExtrasServicio;
import ec.sirec.ejb.servicios.CuentaPorCobrarServicio;
import ec.sirec.ejb.servicios.DatoGlobalServicio;
import ec.sirec.ejb.servicios.FittoCorviniServicio;
import ec.sirec.ejb.servicios.PredioArchivoServicio;
import ec.sirec.ejb.servicios.RecaudacionCabServicio;
import ec.sirec.ejb.servicios.RecaudacionDetServicio;
import ec.sirec.ejb.servicios.ValoracionServicio;
import ec.sirec.web.base.BaseControlador;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author vespinoza
 */
@ManagedBean
@ViewScoped
public class GestionImpuestoPredialControlador extends BaseControlador {

    /**
     * Creates a new instance of GestionConceptoControlador
     */
    //LOGGER 
    private static final Logger LOGGER = Logger.getLogger(GestionImpuestoPredialControlador.class.getName());
    // VARIABLES Y ATRIBUTOS

    private List<AdicionalesDeductivos> listaAdicionalesDeductivosRecargos;
    private List<String> listaAdicionalesDeductivosRecargosSeleccion;
    private List<AdicionalesDeductivos> listaAdicionalesDeductivosExoneraciones;
    private List<String> listaAdicionalesDeductivosExoneracionesSeleccion;
    private List<AdicionalesDeductivos> listaAdicionalesDeductivosDeducciones;
    private List<String> listaAdicionalesDeductivosDeduccionesSeleccion;
    private List<PredioArchivo> listaPredioArchivo;
    private List<CatastroPredial> listaCatastroPredialAValoracion;
    private List<CatastroPredial> listaCatastroPredialClavesCatastrales;
    private List<EjecutarValoracion> listaEjecutarValoracion;

    private List<CatastroPredialEdificacion> listaCatastroPredialEdificacion1_1;
    private List<CatastroPredialEdificacion> listaCatastroPredialEdificacion1_2;
    private List<CatastroPredialEdificacion> listaCatastroPredialEdificacion1_3;
    private List<CatastroPredialEdificacion> listaCatastroPredialEdificacion2;
    private List<CatastroPredialEdificacion> listaCatastroPredialEdificacion3;
    private List<CatastroPredialEdificacion> listaCatastroPredialEdificacion4;
    private List<CatalogoDetalle> listaParroquias;
    private List<CatalogoDetalle> listaSectores;

    private PredioArchivo predioArchivo;
    private CatastroPredial catastroPredialActual;
    private CatastroPredial catastroPredialActualEmision;
    private CatastroPredialValoracion catastroPredialValoracionActual;
    private CatastroPredialValoracion catastroPredialValoracionActualEJE;
    private List<CatastroPredialValoracion> catastroPredialValoracionActualEje;
    private SegUsuario usuarioActual;
    private AdicionalesDeductivos adicionalesDeductivosActual;
    private CpValoracionExtras cpValoracionExtrasActual;
    private StreamedContent archivo;
    private BigDecimal totalTotal;
    private FittoCorvini fittoCorvini;
    private RecaudacionCab recaudacioCab;
    private RecaudacionDet recaudacionDet;
    private int anio;
    private int anioPr;
    private CatalogoDetalle catalogoParroquia;
    private CatalogoDetalle catalogoSector;
    private boolean existe;
    private PropietarioPredio propietarioPredioBusqueda;
    private String visibleRebHipotecaria;
    private BigDecimal rebHipotecaria;
    private String visibleBeneficiencia;
    private double valorBeneficiencia;
    private String visibleEntidadPublica;
    private double valorEntidadPublica;

    private EjecutarValoracion ejecutarValoracionAcual;

    // SERVICIOS
    @EJB
    private AdicionalesDeductivosServicio adicionalesDeductivosServicio;
    @EJB
    private PredioArchivoServicio predioArchivoServicio;
    @EJB
    private CatastroPredialServicio catastroPredialServicio;
    @EJB
    private CpValoracionExtrasServicio cpValoracionExtrasServicio;
    @EJB
    private CatastroPredialValoracionServicio catastroPredialValoracionServicio;
    @EJB
    private FittoCorviniServicio fittoCorviniServicio;
    @EJB
    private DatoGlobalServicio datoGlobalServicio;
    @EJB
    private CuentaPorCobrarServicio cxcServicio;
    @EJB
    private ValoracionServicio valoracionServicio;

    private String textoClave;
    private String textoCodigoManzana;
    private String tipoBusqueda;

    public GestionImpuestoPredialControlador() {

    }

    @PostConstruct
    public void inicializar() {
        try {
            catastroPredialValoracionActualEJE = new CatastroPredialValoracion();
            existe = false;
            listarCatalogosDetalle();
            obtenerUsuario();
            catastroPredialActual = new CatastroPredial();
            catastroPredialActualEmision = new CatastroPredial();
            listaPredioArchivo = new ArrayList<PredioArchivo>();
            visibleRebHipotecaria = "";
            // rebHipotecaria = new BigDecimal(BigInteger.ZERO).setScale(2, RoundingMode.HALF_UP);
            visibleBeneficiencia = "";
            // valorBeneficiencia = 0.00;
            visibleEntidadPublica = "";
            //valorEntidadPublica = 0.00;
            tipoBusqueda = "";
            anio = 0;
            anioPr = 0;
            listarParroquias();
            //listarSectores();

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void buscarValoracion() {
        try {

            listaAdicionalesDeductivosRecargosSeleccion = new ArrayList<String>();
            listaAdicionalesDeductivosDeduccionesSeleccion = new ArrayList<String>();
            listaAdicionalesDeductivosExoneracionesSeleccion = new ArrayList<String>();

            catastroPredialValoracionActual = new CatastroPredialValoracion();
            catastroPredialValoracionActual = catastroPredialValoracionServicio.existeCatastroValoracion(catastroPredialActual, anio);
            if (catastroPredialValoracionActual == null) {

                catastroPredialValoracionActual = new CatastroPredialValoracion();
                catastroPredialValoracionActual.setCatpreCodigo(catastroPredialActual);
                catastroPredialValoracionActual.setCatprevalAnio(anio);
                catastroPredialValoracionServicio.crearCatastroPredialValoracion(catastroPredialValoracionActual);
            } else {
                //listaAdicionalesDeductivosRecargosSeleccion = new ArrayList<String>();
                List<AdicionalesDeductivos> listaR_PR = adicionalesDeductivosServicio.recuperarAdicionesDeductivos(catastroPredialValoracionActual, "R", "PR");
                for (int i = 0; i < listaR_PR.size(); i++) {
                    listaAdicionalesDeductivosRecargosSeleccion.add(listaR_PR.get(i).getAdidedCodigo() + "");
                }

                //listaAdicionalesDeductivosDeduccionesSeleccion = new ArrayList<String>();
                List<AdicionalesDeductivos> listaD_PR = adicionalesDeductivosServicio.recuperarAdicionesDeductivos(catastroPredialValoracionActual, "D", "PR");
                for (int i = 0; i < listaD_PR.size(); i++) {
                    listaAdicionalesDeductivosDeduccionesSeleccion.add(listaD_PR.get(i).getAdidedCodigo() + "");
                }

                //listaAdicionalesDeductivosExoneracionesSeleccion = new ArrayList<String>();
                List<AdicionalesDeductivos> listaE_PR = adicionalesDeductivosServicio.recuperarAdicionesDeductivos(catastroPredialValoracionActual, "E", "PR");
                for (int i = 0; i < listaE_PR.size(); i++) {
                    listaAdicionalesDeductivosExoneracionesSeleccion.add(listaE_PR.get(i).getAdidedCodigo() + "");
                }

                muestraRejabaHipotecaria();
                muestraBeneficienciaEntPublica();

            }
            System.out.println("xsss:  " + catastroPredialValoracionActual.getCatprevalCodigo());

            listarArchivos();

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void listarTodasComboClaves() {
        try {
            listaCatastroPredialClavesCatastrales = new ArrayList<CatastroPredial>();
            if (textoClave.length() >= 8) {

                listaCatastroPredialClavesCatastrales = catastroPredialServicio.listarCatastrosPorClaveContieneContiene(textoClave);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void listarParroquias() {
        try {
            listaParroquias = catastroPredialServicio.listaCatParroquias();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void listarSectores() {
        try {

            CatalogoDetalle parroquia = catastroPredialServicio.cargarObjetoCatalogoDetalle(catalogoParroquia.getCatdetCodigo());
            listaSectores = catastroPredialServicio.listaCatSectores(parroquia.getCatdetCod());

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void obtenerUsuario() {
        usuarioActual = new SegUsuario();
        usuarioActual = (SegUsuario) getSession().getAttribute("usuario");
        //System.out.println(usuarioActual.getUsuIdentificacion());         
    }

    public void listarCatalogosDetalle() {
        try {
            listaAdicionalesDeductivosRecargos = new ArrayList<AdicionalesDeductivos>();
            listaAdicionalesDeductivosRecargos = adicionalesDeductivosServicio.listarAdicionesDeductivosTipo("R", "PR");
            listaAdicionalesDeductivosExoneraciones = new ArrayList<AdicionalesDeductivos>();
            listaAdicionalesDeductivosExoneraciones = adicionalesDeductivosServicio.listarAdicionesDeductivosTipo("E", "PR");
            listaAdicionalesDeductivosDeducciones = new ArrayList<AdicionalesDeductivos>();
            listaAdicionalesDeductivosDeducciones = adicionalesDeductivosServicio.listarAdicionesDeductivosTipo("D", "PR");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void listarArchivos() {
        try {
            if (catastroPredialActual != null) {
                if (catastroPredialActual.getCatpreCodigo() != null) {
                    listaPredioArchivo = new ArrayList<PredioArchivo>();
                    // listaPredioArchivo = predioArchivoServicio.listarArchivos(usuarioActual);
                    listaPredioArchivo = predioArchivoServicio.listarArchivosXImpuesto(catastroPredialActual, "PR", anio);
                }
            } else {
                listaPredioArchivo = new ArrayList<PredioArchivo>();
                addWarningMessage("Eliga la clave Catastral!");

            }
            System.out.println("s:  " + listaPredioArchivo.size());

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void eliminarArchivo(PredioArchivo archivo) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            predioArchivoServicio.eliminarPredioArchivo(archivo);
            context.addMessage(null, new FacesMessage("Mensaje:", "Se Elimino el Archivo  " + archivo.getPrearcNombre()));
            listarArchivos();

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void handleFileUpload(FileUploadEvent event) {

        try {

            if (catastroPredialActual.getCatpreCodigo() != null) {

                if (anio != 0) {

                    predioArchivo = new PredioArchivo();
                    predioArchivo.setPrearcNombre(event.getFile().getFileName());
                    predioArchivo.setCatpreCodigo(catastroPredialActual);
                    predioArchivo.setPrearcData(event.getFile().getContents());
                    predioArchivo.setPrearcTipo("PR");
                    predioArchivo.setUsuIdentificacion(usuarioActual);
                    predioArchivo.setUltaccDetalle("");
                    predioArchivo.setUltaccMarcatiempo(new Date());
                    predioArchivo.setPrearcAnio(anio);

                    predioArchivoServicio.crearPredioArchivo(predioArchivo);

                    FacesMessage msg = new FacesMessage("El documento ", event.getFile().getFileName() + " ha sido cargado satisfactoriamente.");
                    FacesContext.getCurrentInstance().addMessage(null, msg);

                    listarArchivos();
                } else {
                    addErrorMessage("Seleccione Clave Catastral!!!");
                }
            } else {
                addErrorMessage("Seleccione Año");
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    //Preparamos archivo para descarga
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

    public void guardarAdicionalesDeductivos() {
        try {
            adicionalesDeductivosActual = new AdicionalesDeductivos();
            if (catastroPredialActual.getCatpreCodigo() != null || catastroPredialActual != null) {
                try {
                    if (catastroPredialValoracionActual.isCatprevalActivo() == null || catastroPredialValoracionActual.isCatprevalActivo() == false) {
                        if (listaPredioArchivo.size() > 0) {
                            cpValoracionExtrasServicio.eliminarCpValoracionExtrar(catastroPredialValoracionActual);
                            for (int i = 0; i < listaAdicionalesDeductivosRecargosSeleccion.size(); i++) {
                                cpValoracionExtrasActual = new CpValoracionExtras();
                                adicionalesDeductivosActual = adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(Integer.parseInt(listaAdicionalesDeductivosRecargosSeleccion.get(i)));
                                //catastroPredialValoracionActual = catastroPredialValoracionServicio.buscarPorCatastroPredial(catastroPredialActual);
                                cpValoracionExtrasActual.setCatprevalCodigo(catastroPredialValoracionActual);
                                cpValoracionExtrasActual.setAdidedCodigo(adicionalesDeductivosActual);
                                cpValoracionExtrasServicio.crearCpValoracionExtras(cpValoracionExtrasActual);
                            }

                            for (int i = 0; i < listaAdicionalesDeductivosExoneracionesSeleccion.size(); i++) {
                                cpValoracionExtrasActual = new CpValoracionExtras();
                                adicionalesDeductivosActual = adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(Integer.parseInt(listaAdicionalesDeductivosExoneracionesSeleccion.get(i)));
                                //catastroPredialValoracionActual = catastroPredialValoracionServicio.buscarPorCatastroPredial(catastroPredialActual);
                                cpValoracionExtrasActual.setCatprevalCodigo(catastroPredialValoracionActual);
                                cpValoracionExtrasActual.setAdidedCodigo(adicionalesDeductivosActual);
                                //System.out.println("dd: "+ adicionalesDeductivosActual.getAdidedNemonico());                                                        
                                if (adicionalesDeductivosActual.getAdidedNemonico().equals("E_IBE")) {
                                    cpValoracionExtrasActual.setCpvalextPorcentajeAdicional(valorBeneficiencia);
                                }
                                if (adicionalesDeductivosActual.getAdidedNemonico().equals("E_PDP")) {
                                    cpValoracionExtrasActual.setCpvalextPorcentajeAdicional(valorEntidadPublica);
                                }
                                cpValoracionExtrasServicio.crearCpValoracionExtras(cpValoracionExtrasActual);
                            }

                            for (int i = 0; i < listaAdicionalesDeductivosDeduccionesSeleccion.size(); i++) {
                                cpValoracionExtrasActual = new CpValoracionExtras();
                                adicionalesDeductivosActual = adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(Integer.parseInt(listaAdicionalesDeductivosDeduccionesSeleccion.get(i)));
                                //catastroPredialValoracionActual = catastroPredialValoracionServicio.buscarPorCatastroPredial(catastroPredialActual);
                                cpValoracionExtrasActual.setCatprevalCodigo(catastroPredialValoracionActual);
                                cpValoracionExtrasActual.setAdidedCodigo(adicionalesDeductivosActual);
                                if (adicionalesDeductivosActual.getAdidedNemonico().equals("D_RHI")) {
                                    cpValoracionExtrasActual.setCpvalextValorAdicional(rebHipotecaria);
                                }
                                cpValoracionExtrasServicio.crearCpValoracionExtras(cpValoracionExtrasActual);
                            }

                            addSuccessMessage("Guardado Exitosamente!");

                            limpiarCatastro();
                            limpiarDedRecExo();
                            listarArchivos();
                        } else {
                            addSuccessMessage("No existen documentos cargados!");
                        }

                    } else {

                        addSuccessMessage("No Se pueden guardar Cambios, Ya fue emitida!");

                    }

                } catch (NullPointerException exNull) {
                    // LOGGER.log(Level.SEVERE, null, exNull);
                    addSuccessMessage("No existen documentos cargados!");
                }
            } else {
                addErrorMessage("Seleccione Clave Catastral o no existen catastro predial Valoracion!");
            }
        } catch (Exception ex) {
            addErrorMessage("Seleccione los campos");
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void buscarClaveCatastral() {
        try {
            catastroPredialActual = catastroPredialServicio.cargarObjetoCatastro(catastroPredialActual.getCatpreCodigo());
        } catch (Exception ex) {
            catastroPredialActual = new CatastroPredial();
            // System.out.println("alma de hombre");            
            //LOGGER.log(Level.SEVERE, null, ex);         
        }
    }

    public void limpiarCatastro() {
        try {
            propietarioPredioBusqueda = new PropietarioPredio();
            catastroPredialActual = new CatastroPredial();

        } catch (Exception ex) {

            // System.out.println("alma de hombre");            
            //LOGGER.log(Level.SEVERE, null, ex);         
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

    public List<CatastroPredial> obtenerCatastroXCalve(String clave) {
        List<CatastroPredial> lstPP = new ArrayList<CatastroPredial>();
        try {
            lstPP = catastroPredialServicio.listarCatastrosPorClaveContieneContiene(clave);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return lstPP;
    }

    public void limpiarDedRecExo() {
        try {

            anio = 0;
            listaAdicionalesDeductivosRecargosSeleccion = new ArrayList<String>();
            listaAdicionalesDeductivosDeduccionesSeleccion = new ArrayList<String>();
            listaAdicionalesDeductivosExoneracionesSeleccion = new ArrayList<String>();
            visibleRebHipotecaria = "";
            rebHipotecaria = new BigDecimal(BigInteger.ZERO).setScale(2, RoundingMode.HALF_UP);
            visibleBeneficiencia = "";
            valorBeneficiencia = 0.00;
            visibleEntidadPublica = "";
            valorEntidadPublica = 0.00;

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void muestraRejabaHipotecaria() {
        try {
            visibleRebHipotecaria = "";
            rebHipotecaria = BigDecimal.ZERO;
            for (int i = 0; i < listaAdicionalesDeductivosDeduccionesSeleccion.size(); i++) {
                adicionalesDeductivosActual = adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(Integer.parseInt(listaAdicionalesDeductivosDeduccionesSeleccion.get(i)));

                if (adicionalesDeductivosActual.getAdidedNemonico().equals("D_RHI")) {
                    visibleRebHipotecaria = adicionalesDeductivosActual.getAdidedNemonico();
                    rebHipotecaria = cpValoracionExtrasServicio.obtenerValorAdicionalRHipotecaria(catastroPredialValoracionActual, adicionalesDeductivosActual).getCpvalextValorAdicional();
                    if (rebHipotecaria == null) {
                        rebHipotecaria = BigDecimal.ZERO;
                    }
                    i = listaAdicionalesDeductivosDeduccionesSeleccion.size();
                }
            }

        } catch (Exception ex) {
            //LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void muestraBeneficienciaEntPublica() {
        try {
            visibleBeneficiencia = "";
            visibleEntidadPublica = "";

            for (int i = 0; i < listaAdicionalesDeductivosExoneracionesSeleccion.size(); i++) {
                adicionalesDeductivosActual = adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(Integer.parseInt(listaAdicionalesDeductivosExoneracionesSeleccion.get(i)));

                if (adicionalesDeductivosActual.getAdidedNemonico().equals("E_IBE")) {
                    visibleBeneficiencia = adicionalesDeductivosActual.getAdidedNemonico();
                    valorBeneficiencia = cpValoracionExtrasServicio.obtenerValorAdicionalRHipotecaria(catastroPredialValoracionActual, adicionalesDeductivosActual).getCpvalextPorcentajeAdicional();
                }
            }

            for (int i = 0; i < listaAdicionalesDeductivosExoneracionesSeleccion.size(); i++) {
                adicionalesDeductivosActual = adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(Integer.parseInt(listaAdicionalesDeductivosExoneracionesSeleccion.get(i)));

                if (adicionalesDeductivosActual.getAdidedNemonico().equals("E_PDP")) {
                    visibleEntidadPublica = adicionalesDeductivosActual.getAdidedNemonico();
                    valorEntidadPublica = cpValoracionExtrasServicio.obtenerValorAdicionalRHipotecaria(catastroPredialValoracionActual, adicionalesDeductivosActual).getCpvalextPorcentajeAdicional();
                }
            }

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void onItemSelect(SelectEvent event) {
        try {
            PropietarioPredio pp = (PropietarioPredio) event.getObject();
            pp = catastroPredialServicio.buscarPropietarioPredioPorCodigo(pp.getPropreCodigo());
            //catastroPredialActual = iraCatastroDesdeBusqueda(pp.getCatpreCodigo());
            catastroPredialActual = catastroPredialServicio.cargarObjetoCatastro(pp.getCatpreCodigo().getCatpreCodigo());
            limpiarDedRecExo();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void onItemSelectClave(SelectEvent event) {
        try {
            CatastroPredial pp = (CatastroPredial) event.getObject();

            propietarioPredioBusqueda = new PropietarioPredio();
            propietarioPredioBusqueda = catastroPredialServicio.buscarPropietarioPredioPorCatastro(pp.getCatpreCodigo());
            catastroPredialActual = catastroPredialServicio.cargarObjetoCatastro(pp.getCatpreCodigo());
            limpiarDedRecExo();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////

    public void ejecutarValoracion() {
        try {
            totalTotal = new BigDecimal(0);
            listaEjecutarValoracion = new ArrayList<EjecutarValoracion>();
            catastroPredialValoracionActualEje = new ArrayList<CatastroPredialValoracion>();

            if (tipoBusqueda.equals("C")) {
                catastroPredialServicio.crearCatastroXCasatroVal(catastroPredialActualEmision, anioPr);
                catastroPredialValoracionActualEje = catastroPredialValoracionServicio.buscarValoracionXClaveCatastral(catastroPredialActualEmision, anioPr);
            } else if (tipoBusqueda.equals("T")) {
                catastroPredialServicio.crearCatastroXTodo(anioPr);
                catastroPredialValoracionActualEje = catastroPredialValoracionServicio.listarCatastroAnioTodo(anioPr);
            } else if (tipoBusqueda.equals("P")) {
                catastroPredialServicio.crearCatastroXParroquiaVal(catalogoParroquia, anioPr);
                catastroPredialValoracionActualEje = catastroPredialValoracionServicio.listarCatastroXParroquia(catalogoParroquia, anioPr);
            } else if (tipoBusqueda.equals("S")) {
                catastroPredialServicio.crearCatastroXSectorVal(catalogoSector, anioPr);
                catastroPredialValoracionActualEje = catastroPredialValoracionServicio.listarCatastroXSector(catalogoSector, anioPr);
            }else if (tipoBusqueda.equals("M")) {
                if(textoCodigoManzana.length()==13){
                catastroPredialServicio.crearCatastroXManzanaVal(textoCodigoManzana, anioPr);
                catastroPredialValoracionActualEje = catastroPredialValoracionServicio.listarCatastroXManzana(textoCodigoManzana, anioPr);
                }
            }

        

        for (int i = 0; i < catastroPredialValoracionActualEje.size(); i++) {
            catastroPredialValoracionActualEJE = catastroPredialValoracionActualEje.get(i);
            if (catastroPredialValoracionActualEJE.isCatprevalActivo() == null || catastroPredialValoracionActualEJE.isCatprevalActivo() == false) {
                valoracionConstruccion2(catastroPredialValoracionActualEJE.getCatpreCodigo());
            }

        }

        for (int i = 0; i < catastroPredialValoracionActualEje.size(); i++) {

//                CatastroPredial CP = listaCatastroPredialAValoracion.get(i);
//                CatastroPredialValoracion CPV = catastroPredialValoracionServicio.buscarPorCatastroPredial(CP); 
            CatastroPredialValoracion CPV = catastroPredialValoracionActualEje.get(i);
            CatastroPredial CP = catastroPredialServicio.cargarObjetoCatastro(CPV.getCatpreCodigo().getCatpreCodigo());
            //CatastroPredial CP = CPV.getCatpreCodigo();

            EjecutarValoracion eVal = new EjecutarValoracion();

            eVal.setCatastroPredial(CP);
            eVal.setCatpreCodigo(CP.getCatpreCodigo());
            eVal.setCatpreClaveCatastal(CP.getCatpreCodNacional() + CP.getCatpreCodLocal());
            eVal.setProCi(catastroPredialServicio.obtenerPropietarioPrincipalPredio(CP.getCatpreCodigo()));
            
            //propietario = catastroPredialServicio.obtenerPropietarioPrincipalPredio(pp.getCatpreCodigo());
            if (CP.getCatpreAreaTotal() != null) {
                eVal.setCatpreAreaTotal(CP.getCatpreAreaTotal());
            } else {
                eVal.setCatpreAreaTotal(0.0);
            }
            if (CP.getCatpreAreaTotalCons() != null) {
                eVal.setCatpreAreaTotalCons(CP.getCatpreAreaTotalCons());
            } else {
                eVal.setCatpreAreaTotalCons(0.0);
            }

            if (CPV != null) {
                if (CPV.getCatprevalAvaluoEdif() == null) {
                    CPV.setCatprevalAvaluoEdif(BigDecimal.ZERO);
                }
                if (CPV.getCatprevalAvaluoTerr() == null) {
                    CPV.setCatprevalAvaluoTerr(BigDecimal.ZERO);
                }
                if (CPV.getCatprevalValorPropieda() == null) {
                    CPV.setCatprevalValorPropieda(BigDecimal.ZERO);
                }
                if (CPV.getCatprevalBaseImponible() == null) {
                    CPV.setCatprevalBaseImponible(BigDecimal.ZERO);
                }
                if (CPV.getCatprevalImpuesto() == null) {
                    CPV.setCatprevalImpuesto(BigDecimal.ZERO);
                }
                if (CPV.getCatprevalBomberos() == null) {
                    CPV.setCatprevalBomberos(BigDecimal.ZERO);
                }
                if (CPV.getCatprevalSolarNoedificado() == null) {
                    CPV.setCatprevalSolarNoedificado(BigDecimal.ZERO);
                }
                if (CPV.getCatprevalTasaAdm() == null) {
                    CPV.setCatprevalTasaAdm(BigDecimal.ZERO);
                }
                eVal.setCatastroPredialValoracion(CPV);
            } else {
                CPV = new CatastroPredialValoracion();
                CPV.setCatprevalAvaluoEdif(BigDecimal.ZERO);
                CPV.setCatprevalAvaluoTerr(BigDecimal.ZERO);
                CPV.setCatprevalValorPropieda(BigDecimal.ZERO);
                CPV.setCatprevalBaseImponible(BigDecimal.ZERO);
                CPV.setCatprevalImpuesto(BigDecimal.ZERO);
                CPV.setCatprevalBomberos(BigDecimal.ZERO);
                CPV.setCatprevalSolarNoedificado(BigDecimal.ZERO);
                CPV.setCatprevalTasaAdm(BigDecimal.ZERO);
                eVal.setCatastroPredialValoracion(CPV);
            }

            fijarCpValoracionExtrasDeducciones(CPV);
            fijarCpValoracionExtrasRecargos(CPV);
            fijarCpValoracionExtrasExoneraciones(CPV);

            // sumatoria de los RECARGOS , excepto Solar No edificado nemonico: R_INE 
            if (cpValoracionExtrasServicio.obteneValorTipoAdicional(CP.getCatpreCodigo(), CPV.getCatprevalCodigo(), "PR", "R") != null) {
                eVal.setTotalRecargos(cpValoracionExtrasServicio.obteneValorTipoAdicional(CP.getCatpreCodigo(), CPV.getCatprevalCodigo(), "PR", "R"));
            } else {
                eVal.setTotalRecargos(BigDecimal.ZERO);
            }

            if (cpValoracionExtrasServicio.obteneValorTipoAdicional(CP.getCatpreCodigo(), CPV.getCatprevalCodigo(), "PR", "D") != null) {

                BigDecimal totalDec = cpValoracionExtrasServicio.obteneValorTipoAdicional(CP.getCatpreCodigo(), CPV.getCatprevalCodigo(), "PR", "D");
                BigDecimal Prop50 = CPV.getCatprevalValorPropieda().multiply(new BigDecimal(50)).divide(new BigDecimal(100));

                if (totalDec.compareTo(Prop50) == -1) {
                    eVal.setTotalDeduciones(totalDec);
                } else {
                    eVal.setTotalDeduciones(BigDecimal.ZERO);
                }

            } else {
                eVal.setTotalDeduciones(BigDecimal.ZERO);
            }
            if (cpValoracionExtrasServicio.obteneValorTipoAdicional(CP.getCatpreCodigo(), CPV.getCatprevalCodigo(), "PR", "E") != null) {
                eVal.setTotalExoneracion(cpValoracionExtrasServicio.obteneValorTipoAdicional(CP.getCatpreCodigo(), CPV.getCatprevalCodigo(), "PR", "E"));
            } else {
                eVal.setTotalExoneracion(BigDecimal.ZERO);
            }

            BigDecimal TOTAL = eVal.getCatastroPredialValoracion().getCatprevalImpuesto().add(eVal.getCatastroPredialValoracion().getCatprevalBomberos())
                    .add(eVal.getCatastroPredialValoracion().getCatprevalSolarNoedificado())
                    .add(eVal.getCatastroPredialValoracion().getCatprevalTasaAdm()).add(eVal.getTotalRecargos()).subtract(eVal.getTotalDeduciones())
                    .subtract(eVal.getTotalExoneracion()).setScale(2, RoundingMode.HALF_UP);

            if (TOTAL.signum() == -1) {
                TOTAL = BigDecimal.ZERO;
            }

            eVal.setTotalRegistro(TOTAL);

            listaEjecutarValoracion.add(eVal);
            totalTotal = totalTotal.add(eVal.getTotalRegistro().setScale(2, RoundingMode.HALF_UP));
        }
    }
    catch (Exception ex

    
        ) {
            LOGGER.log(Level.SEVERE, null, ex);
    }

}

/*
 public void valoracionConstruccion(CatastroPredial catastroPredialActual) {
 try {                           
 catastroPredialActual = catastroPredialServicio.cargarObjetoCatastro(catastroPredialActual.getCatpreCodigo());   
             
 listaCatastroPredialEdificacion1_1 = new ArrayList<CatastroPredialEdificacion>();                      
 listaCatastroPredialEdificacion1_1 = catastroPredialServicio.listarEdificacionesGrupo1_1(catastroPredialActual,"1","1");
           
 List <Double>  vidaUtil = new ArrayList<Double>();
          
 for (int i = 0; i < listaCatastroPredialEdificacion1_1.size(); i++) {
 CatastroPredialEdificacion CPEdif = listaCatastroPredialEdificacion1_1.get(i);
 try {
 //  System.out.println("valor: " + CPEdif.getCatdetCodigo().getCatdetValor());
 vidaUtil.add(CPEdif.getCatdetCodigo().getCatdetValorDecimal());
 } catch (NullPointerException nex) {
 vidaUtil.add(Double.valueOf("0"));
 // LOGGER.log(Level.SEVERE, null, nex);
 }
 }
          
 listaCatastroPredialEdificacion1_2 = new ArrayList<CatastroPredialEdificacion>();                      
 listaCatastroPredialEdificacion1_2 = catastroPredialServicio.listarEdificacionesGrupo1_SubGrupo2(catastroPredialActual);           
 List <Integer> Edad = new ArrayList<Integer>();  
 for (int i = 0; i < listaCatastroPredialEdificacion1_2.size(); i++) {
 CatastroPredialEdificacion CPEdif = listaCatastroPredialEdificacion1_2.get(i);
 try {
 // System.out.println("valor: " + CPEdif.getCatdetCodigo().getCatdetValor());
 Edad.add(CPEdif.getCatpreediValor());
 } catch (NullPointerException nex) {
 Edad.add(0);
 // LOGGER.log(Level.SEVERE, null, nex);
 }
 }
          
 listaCatastroPredialEdificacion1_3 = new ArrayList<CatastroPredialEdificacion>();                      
 listaCatastroPredialEdificacion1_3 = catastroPredialServicio.listarEdificacionesGrupo1_SubGrupo3(catastroPredialActual);           
 List <String> estadoCons = new ArrayList<String>();  
 for (int i = 0; i < listaCatastroPredialEdificacion1_3.size(); i++) {
 CatastroPredialEdificacion CPEdif = listaCatastroPredialEdificacion1_3.get(i);
 try {
 // System.out.println("valor: " + CPEdif.getCatdetCodigo().getCatdetValor());
 estadoCons.add(CPEdif.getCatdetCodigo().getCatdetTexto());
 } catch (NullPointerException nex) {
 estadoCons.add("");
 // LOGGER.log(Level.SEVERE, null, nex);
 }
 }

 // List<Integer> FactorDepreciacion = new ArrayList<Integer>();
 List<Double> FD = new ArrayList<Double>();
 for (int i = 0; i < vidaUtil.size(); i++) {
                 
 //                 System.out.println("vidaUtil.get(i) "+vidaUtil.get(i));  
 //                 System.out.println("catastroPredialActual "+catastroPredialActual); 
                 
 Double vid = vidaUtil.get(i);
 if(vid==null){
 vid=Double.valueOf("0");
 }
                 
 Integer edad = Edad.get(i);
 if(edad==null){
 edad=0;
 }
                 
 int factoresInt = (int) Math.round(((double) edad / (double) vid) * 100);                
 System.out.println("factoresInt: "+factoresInt);                 
 fittoCorvini = new FittoCorvini();
 fittoCorvini = fittoCorviniServicio.obtenerValoresClase(factoresInt);
 if(fittoCorvini!=null){                 
 if (estadoCons.get(i).equals("ESTABLE")) {
 FD.add(fittoCorvini.getClase1()/100);
 } else {
 if (estadoCons.get(i).equals("A REPARAR")) {
 FD.add(fittoCorvini.getClase3()/100);
 } else {
 if (estadoCons.get(i).equals("OBSOLETO")) {
 FD.add(fittoCorvini.getClase5()/100);
 }
 }
 }                 
 }else{
 FD.add(Double.valueOf("0"));                  
 }                                    
 }
          
 for (int i = 0; i < FD.size(); i++) {   
 System.out.println(" DF: "+ FD.get(i));                                
 }                              
          
 // GRUPO 2
 listaCatastroPredialEdificacion2 = new ArrayList<CatastroPredialEdificacion>();                      
 listaCatastroPredialEdificacion2 = catastroPredialServicio.listarEdificacionesGrupo2(catastroPredialActual);          
 List <Double> Grupo2 = new ArrayList<Double>();
 List <Double> Grupo2AUX = new ArrayList<Double>();
 int inicio2 = 0;
 int fin2 = vidaUtil.size();
 for (int i = 0; i < listaCatastroPredialEdificacion2.size(); i++) {
 CatastroPredialEdificacion CPEdif = listaCatastroPredialEdificacion2.get(i);                 
 if(inicio2==fin2){
 inicio2=0;
 }                 
 if(inicio2<fin2){                     
 if(i<fin2){
 Double valor2 = CPEdif.getCatdetCodigo().getCatdetValorDecimal();
 if(valor2==null){
 valor2=Double.valueOf("0");
 }
 Grupo2.add(inicio2, valor2); 
 Grupo2AUX.add(inicio2, valor2);                           
 // System.out.println("Grupo2.get(inicio2): "+ Grupo2.get(inicio2));                          
 }else{ 
 Double valor2 = CPEdif.getCatdetCodigo().getCatdetValorDecimal();
 if(valor2==null){
 valor2=Double.valueOf("0");
 }
                         
 Grupo2.set(inicio2, valor2+ Grupo2AUX.get(inicio2));                          
 //System.out.println(Grupo2.get(inicio2)+" = "+ CPEdif.getCatdetCodigo().getCatdetValor() +" + "+ Grupo2AUX.get(inicio2));                             
 Grupo2AUX.set(inicio2, Grupo2.get(inicio2));                                                                           
 //System.out.println("total: "+ Grupo2AUX.get(inicio2));                         
 }                     
 inicio2++;
 }                
 }
 // GRUPO 3          
 listaCatastroPredialEdificacion3 = new ArrayList<CatastroPredialEdificacion>();                      
 listaCatastroPredialEdificacion3 = catastroPredialServicio.listarEdificacionesGrupo3(catastroPredialActual);          
 List <Double> Grupo3 = new ArrayList<Double>();
 List <Double> Grupo3AUX = new ArrayList<Double>();
 int inicio3 = 0;
 int fin3 = vidaUtil.size();
 for (int i = 0; i < listaCatastroPredialEdificacion3.size(); i++) {
 CatastroPredialEdificacion CPEdif = listaCatastroPredialEdificacion3.get(i);                 
 if(inicio3==fin3){
 inicio3=0;
 }                 
 if(inicio3<fin3){                     
 if(i<fin3){
 Double valor3 = CPEdif.getCatdetCodigo().getCatdetValorDecimal();
 if(valor3==null){
 valor3=Double.valueOf("0");
 }
 Grupo3.add(inicio3, valor3); 
 Grupo3AUX.add(inicio3, valor3);                                                                           
 }else{
 Double valor3 = CPEdif.getCatdetCodigo().getCatdetValorDecimal();
 if(valor3==null){
 valor3=Double.valueOf("0");
 }
                         
 Grupo3.set(inicio3, valor3 + Grupo3AUX.get(inicio3));                                               
 Grupo3AUX.set(inicio3, Grupo3.get(inicio3));                                                                                                             
 }                     
 inicio3++;
 }                
 }
          
 listaCatastroPredialEdificacion4 = new ArrayList<CatastroPredialEdificacion>();                      
 listaCatastroPredialEdificacion4 = catastroPredialServicio.listarEdificacionesGrupo4(catastroPredialActual);          
 List <Double> Grupo4 = new ArrayList<Double>();
 List <Double> Grupo4AUX = new ArrayList<Double>();
 int inicio4 = 0;
 int fin4 = vidaUtil.size();
 for (int i = 0; i < listaCatastroPredialEdificacion4.size(); i++) {
 CatastroPredialEdificacion CPEdif = listaCatastroPredialEdificacion4.get(i);                 
 if(inicio4==fin4){
 inicio4=0;
 }                 
 if(inicio4<fin4){                     
 if (i < fin4) {
 try {
 Double valor4 = CPEdif.getCatdetCodigo().getCatdetValorDecimal();
 if(valor4==null){
 valor4=Double.valueOf("0");
 }
                             
 Grupo4.add(inicio4, valor4);
 Grupo4AUX.add(inicio4, valor4);
 } catch (NullPointerException ex) {
 Grupo4.add(inicio4, Double.valueOf("0"));
 Grupo4AUX.add(inicio4, Double.valueOf("0"));
 }
 }else{
 try{
 Double valor4 = CPEdif.getCatdetCodigo().getCatdetValorDecimal();
 if(valor4==null){
 valor4=Double.valueOf("0");
 }   
                             
 Grupo4.set(inicio4, valor4 + Grupo4AUX.get(inicio4));                                               
 Grupo4AUX.set(inicio4, Grupo4.get(inicio4));  
 } catch (NullPointerException ex) {
 Grupo4.set(inicio4, 0 + Grupo4AUX.get(inicio4));                                               
 Grupo4AUX.set(inicio4, Grupo4.get(inicio4));  
 }
 }                     
 inicio4++;
 }                
 }
          
 List <Double> VN = new ArrayList<Double>();                                         
 for (int i = 0; i < Grupo2.size(); i++) {   
 VN.add(Grupo2.get(i)+Grupo3.get(i)+Grupo4.get(i));                                   
 }
          
 BigDecimal valorAvaluoConstruccion = BigDecimal.ZERO;
 BigDecimal valorAvaluoTerrero = new BigDecimal(10000);
          
 for (int i = 0; i < vidaUtil.size(); i++) {
 Double AreaTotalCons = catastroPredialActual.getCatpreAreaTotalCons();
 if(AreaTotalCons==null){
 AreaTotalCons = Double.valueOf("0");
 }
 Double FDvalor = FD.get(i);
 if(FDvalor==null){
 FDvalor = Double.valueOf("0");
 }
              
 double d = (VN.get(i)-(VN.get(i)*FDvalor))*AreaTotalCons;   
 //             System.out.println(" "+ VN.get(i)+" - "+VN.get(i) +" * "+ FD.get(i)+") *"+catastroPredialActual.getCatpreAreaTotalCons());           
 //             System.out.println("d: "+ d); 
 valorAvaluoConstruccion = valorAvaluoConstruccion.add(new BigDecimal((VN.get(i)-(VN.get(i)*FDvalor))*AreaTotalCons)) ;                                                    
 }                              
          
 catastroPredialValoracionActualEJE.setCatpreCodigo(catastroPredialActual);
 catastroPredialValoracionActualEJE.setCatprevalAvaluoEdif(valorAvaluoConstruccion.setScale(2, RoundingMode.HALF_UP)); 
 catastroPredialValoracionActualEJE.setCatprevalAvaluoTerr(valorAvaluoTerrero.setScale(2, RoundingMode.HALF_UP)); 
 catastroPredialValoracionActualEJE.setCatprevalAvaluoTot(valorAvaluoConstruccion.add(valorAvaluoTerrero).setScale(2, RoundingMode.HALF_UP)); 
 catastroPredialValoracionActualEJE.setCatprevalValorPropieda(valorAvaluoConstruccion.add(valorAvaluoTerrero).setScale(2, RoundingMode.HALF_UP));   
          
 calcularBaseImponible(catastroPredialActual);
          
 catastroPredialValoracionActualEJE.setCatprevalImpuesto(catastroPredialValoracionActualEJE.getCatprevalBaseImponible().multiply(new BigDecimal(datoGlobalServicio.obtenerDatoGlobal("Banda_Impositiva_Urbana").getDatgloValor()).divide(new BigDecimal(100))).setScale(2, RoundingMode.HALF_UP));  
 catastroPredialValoracionActualEJE.setCatprevalBomberos(catastroPredialValoracionActualEJE.getCatprevalBaseImponible().multiply(new BigDecimal(datoGlobalServicio.obtenerDatoGlobal("Bomberos").getDatgloValor()).divide(new BigDecimal(100))).setScale(2, RoundingMode.HALF_UP));  
          
          
 // catastroPredialValoracionActual.setCatprevalSolarNoedificado(catastroPredialValoracionActual.getCatprevalBaseImponible().multiply(new BigDecimal(datoGlobalServicio.obtenerDatoGlobal("Solar_No_Edif").getDatgloValor()).divide(new BigDecimal(100))).setScale(2, RoundingMode.HALF_UP));  
 solarNoEdif();
          
 catastroPredialValoracionActualEJE.setCatprevalTasaAdm(new BigDecimal(datoGlobalServicio.obtenerDatoGlobal("Tasa_Administrativa").getDatgloValor()).setScale(2, RoundingMode.HALF_UP));  
 catastroPredialValoracionActualEJE.setCatprevalAnio(anioPr);
          
 catastroPredialValoracionServicio.editarCatastroPredialValoracion(catastroPredialValoracionActualEJE);
          
 System.out.println("valorConstruccionT: "+ valorAvaluoConstruccion);
 } catch (Exception ex) {
 LOGGER.log(Level.SEVERE, null, ex);
 }
 }*/
public void valoracionConstruccion2(CatastroPredial catastroPredialActual) {
        try {
            catastroPredialActual = catastroPredialServicio.cargarObjetoCatastro(catastroPredialActual.getCatpreCodigo());

            BigDecimal valorAvaluoConstruccion = valoracionServicio.obtenerValoracionConstruccionPorPredio(catastroPredialActual.getCatpreCodigo());
            BigDecimal valorAvaluoTerrero = valoracionServicio.obtenerValoracionTerrenoPorPredio(catastroPredialActual.getCatpreCodigo());

            catastroPredialValoracionActualEJE.setCatpreCodigo(catastroPredialActual);
            catastroPredialValoracionActualEJE.setCatprevalAvaluoEdif(valorAvaluoConstruccion.setScale(2, RoundingMode.HALF_UP));
            catastroPredialValoracionActualEJE.setCatprevalAvaluoTerr(valorAvaluoTerrero.setScale(2, RoundingMode.HALF_UP));
            catastroPredialValoracionActualEJE.setCatprevalAvaluoTot(valorAvaluoConstruccion.add(valorAvaluoTerrero).setScale(2, RoundingMode.HALF_UP));
            catastroPredialValoracionActualEJE.setCatprevalValorPropieda(valorAvaluoConstruccion.add(valorAvaluoTerrero).setScale(2, RoundingMode.HALF_UP));

            calcularBaseImponible(catastroPredialActual);

            catastroPredialValoracionActualEJE.setCatprevalImpuesto(catastroPredialValoracionActualEJE.getCatprevalBaseImponible().multiply(new BigDecimal(datoGlobalServicio.obtenerDatoGlobal("Banda_Impositiva_Urbana").getDatgloValor()).divide(new BigDecimal(100))).setScale(2, RoundingMode.HALF_UP));
            catastroPredialValoracionActualEJE.setCatprevalBomberos(catastroPredialValoracionActualEJE.getCatprevalBaseImponible().multiply(new BigDecimal(datoGlobalServicio.obtenerDatoGlobal("Bomberos").getDatgloValor()).divide(new BigDecimal(100))).setScale(2, RoundingMode.HALF_UP));

            // catastroPredialValoracionActual.setCatprevalSolarNoedificado(catastroPredialValoracionActual.getCatprevalBaseImponible().multiply(new BigDecimal(datoGlobalServicio.obtenerDatoGlobal("Solar_No_Edif").getDatgloValor()).divide(new BigDecimal(100))).setScale(2, RoundingMode.HALF_UP));  
            solarNoEdif();

            catastroPredialValoracionActualEJE.setCatprevalTasaAdm(new BigDecimal(datoGlobalServicio.obtenerDatoGlobal("Tasa_Administrativa").getDatgloValor()).setScale(2, RoundingMode.HALF_UP));
            catastroPredialValoracionActualEJE.setCatprevalAnio(anioPr);

            catastroPredialValoracionServicio.editarCatastroPredialValoracion(catastroPredialValoracionActualEJE);

            System.out.println("valorConstruccionT: " + valorAvaluoConstruccion);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void fijarCpValoracionExtrasDeducciones(CatastroPredialValoracion CPV) {
        try {
//            CpValoracionExtras cpR1 = cpValoracionExtrasServicio.buscarValoresRecargos(CPV, "D_RHI"); 
//            if(cpR1!=null){                                                                                                 
//                   cpR1.setCpvalextBase(CPV.getCatprevalValorPropieda());
//                   cpR1.setCpvalextValor(CPV.getCatprevalValorPropieda().subtract(cpR1.getCpvalextValorAdicional()));
//                   cpValoracionExtrasServicio.editarCpValoracionExtras(cpR1);
//            }

            CpValoracionExtras cpEx1 = cpValoracionExtrasServicio.buscarValoresRecargos(CPV, "D_R3E");
            if (cpEx1 != null) {
                BigDecimal totalAvaluoxPro = BigDecimal.ZERO;
                List<CatastroPredial> catastroPropietario = catastroPredialServicio.listarCatastroPorCedulaPropietario(catastroPredialServicio.obtenerPropietarioPrincipalPredio(catastroPredialActualEmision.getCatpreCodigo()).getProCi());
                for (int i = 0; i < catastroPropietario.size(); i++) {
                    CatastroPredialValoracion CPVxPro = catastroPredialServicio.obtenerValoracionPredio(catastroPropietario.get(i));
                    System.out.println("ko: " + CPVxPro.getCatprevalAvaluoTot());
                    if (CPVxPro.getCatprevalAvaluoTot() != null) {
                        totalAvaluoxPro = totalAvaluoxPro.add(CPVxPro.getCatprevalAvaluoTot());
                    }
                }
                System.out.println("total suma Prop: " + totalAvaluoxPro);
                // si suma es mayor
                if (totalAvaluoxPro.compareTo(new BigDecimal(datoGlobalServicio.obtenerDatoGlobal("500RBU").getDatgloValor())) == 1) {
                    cpEx1.setCpvalextBase(CPV.getCatprevalValorPropieda());
                    cpEx1.setCpvalextValor(CPV.getCatprevalImpuesto().multiply(new BigDecimal(0)).divide(new BigDecimal(100)));
                } else {
                    // si es menor
                    if (totalAvaluoxPro.compareTo(new BigDecimal(datoGlobalServicio.obtenerDatoGlobal("500RBU").getDatgloValor())) == -1) {
                        cpEx1.setCpvalextBase(CPV.getCatprevalImpuesto());
                        cpEx1.setCpvalextValor(CPV.getCatprevalImpuesto().multiply(new BigDecimal(100)).divide(new BigDecimal(100)));
                    }
                }
                cpValoracionExtrasServicio.editarCpValoracionExtras(cpEx1);

            }

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

    }

    public void fijarCpValoracionExtrasRecargos(CatastroPredialValoracion CPV) {
        try {
            // solar no edificado, se calcula anteriormente            
            CpValoracionExtras cpR1 = cpValoracionExtrasServicio.buscarValoresRecargos(CPV, "R_INZ");
            if (cpR1 != null) {
                cpR1.setCpvalextBase(CPV.getCatprevalAvaluoTerr());
                cpR1.setCpvalextValor(CPV.getCatprevalAvaluoTerr().multiply(new BigDecimal(adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(cpR1.getAdidedCodigo().getAdidedCodigo()).getAdidedPorcentaje())));
                cpValoracionExtrasServicio.editarCpValoracionExtras(cpR1);
            }

            CpValoracionExtras cpR2 = cpValoracionExtrasServicio.buscarValoresRecargos(CPV, "R_IOB");
            if (cpR2 != null) {
                cpR2.setCpvalextBase(CPV.getCatprevalAvaluoTerr());
                double porcen = adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(cpR2.getAdidedCodigo().getAdidedCodigo()).getAdidedPorcentaje();
                cpR2.setCpvalextValor(CPV.getCatprevalAvaluoTerr().multiply(new BigDecimal(porcen)));
                cpValoracionExtrasServicio.editarCpValoracionExtras(cpR2);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void fijarCpValoracionExtrasExoneraciones(CatastroPredialValoracion CPV) {
        try {

            // 1
            CpValoracionExtras cpEx1 = cpValoracionExtrasServicio.buscarValoresRecargos(CPV, "E_PUA");
            if (cpEx1 != null) {

                BigDecimal rbu25 = new BigDecimal(9150);
                if (CPV.getCatprevalValorPropieda().compareTo(rbu25) == -1) {
                    cpEx1.setCpvalextBase(CPV.getCatprevalImpuesto());
                    double porcen = adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(cpEx1.getAdidedCodigo().getAdidedCodigo()).getAdidedPorcentaje();
                    cpEx1.setCpvalextValor(CPV.getCatprevalImpuesto().multiply(new BigDecimal(porcen)).divide(new BigDecimal(100)));
                    cpValoracionExtrasServicio.editarCpValoracionExtras(cpEx1);
                } else {
                    cpEx1.setCpvalextValor(CPV.getCatprevalImpuesto().multiply(new BigDecimal(0)).divide(new BigDecimal(100)));
                    cpValoracionExtrasServicio.editarCpValoracionExtras(cpEx1);
                }
            }
            // 2 
            CpValoracionExtras cpR2 = cpValoracionExtrasServicio.buscarValoresRecargos(CPV, "E_PES");
            if (cpR2 != null) {
                cpR2.setCpvalextBase(CPV.getCatprevalImpuesto());
                double porcen = adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(cpR2.getAdidedCodigo().getAdidedCodigo()).getAdidedPorcentaje();
                cpR2.setCpvalextValor(CPV.getCatprevalImpuesto().multiply(new BigDecimal(porcen)).divide(new BigDecimal(100)));
                cpValoracionExtrasServicio.editarCpValoracionExtras(cpR2);
            }
            // 3
            CpValoracionExtras cpEx3 = cpValoracionExtrasServicio.buscarValoresRecargos(CPV, "E_IBE");
            if (cpEx3 != null) {
                cpEx3.setCpvalextBase(CPV.getCatprevalImpuesto());
                double porcen = adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(cpEx3.getAdidedCodigo().getAdidedCodigo()).getAdidedPorcentaje();
                if (porcen == cpEx3.getCpvalextPorcentajeAdicional()) {
                    cpEx3.setCpvalextValor(CPV.getCatprevalImpuesto().multiply(new BigDecimal(porcen)).divide(new BigDecimal(100)));
                } else {
                    porcen = porcen - cpEx3.getCpvalextPorcentajeAdicional();
                    cpEx3.setCpvalextValor(CPV.getCatprevalImpuesto().multiply(new BigDecimal(porcen)).divide(new BigDecimal(100)));

                }
                cpValoracionExtrasServicio.editarCpValoracionExtras(cpEx3);
            }
            // 4
            CpValoracionExtras cpEx4 = cpValoracionExtrasServicio.buscarValoresRecargos(CPV, "E_ORG");
            if (cpEx4 != null) {
                cpEx4.setCpvalextBase(CPV.getCatprevalImpuesto());
                double porcen = adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(cpEx4.getAdidedCodigo().getAdidedCodigo()).getAdidedPorcentaje();
                cpEx4.setCpvalextValor(CPV.getCatprevalImpuesto().multiply(new BigDecimal(porcen)).divide(new BigDecimal(100)));
                cpValoracionExtrasServicio.editarCpValoracionExtras(cpEx4);
            }

            // 5
            CpValoracionExtras cpEx5 = cpValoracionExtrasServicio.buscarValoresRecargos(CPV, "E_PDP");
            if (cpEx5 != null) {
                cpEx5.setCpvalextBase(CPV.getCatprevalImpuesto());
                double porcen = adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(cpEx5.getAdidedCodigo().getAdidedCodigo()).getAdidedPorcentaje();
                if (porcen == cpEx5.getCpvalextPorcentajeAdicional()) {
                    cpEx5.setCpvalextValor(CPV.getCatprevalImpuesto().multiply(new BigDecimal(porcen)).divide(new BigDecimal(100)));
                } else {
                    porcen = porcen - cpEx5.getCpvalextPorcentajeAdicional();
                    cpEx5.setCpvalextValor(CPV.getCatprevalImpuesto().multiply(new BigDecimal(porcen)).divide(new BigDecimal(100)));
                }
                cpValoracionExtrasServicio.editarCpValoracionExtras(cpEx5);
            }

            // 6
            CpValoracionExtras cpEx6 = cpValoracionExtrasServicio.buscarValoresRecargos(CPV, "E_PAF");
            if (cpEx6 != null) {
                cpEx6.setCpvalextBase(CPV.getCatprevalImpuesto());
                double porcen = adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(cpEx6.getAdidedCodigo().getAdidedCodigo()).getAdidedPorcentaje();
                cpEx6.setCpvalextValor(CPV.getCatprevalImpuesto().multiply(new BigDecimal(porcen)).divide(new BigDecimal(100)));
                cpValoracionExtrasServicio.editarCpValoracionExtras(cpEx6);
            }

            // 7
            CpValoracionExtras cpEx7 = cpValoracionExtrasServicio.buscarValoresRecargos(CPV, "E_PIE");
            if (cpEx7 != null) {
                cpEx7.setCpvalextBase(CPV.getCatprevalImpuesto());
                double porcen = adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(cpEx7.getAdidedCodigo().getAdidedCodigo()).getAdidedPorcentaje();
                cpEx7.setCpvalextValor(CPV.getCatprevalImpuesto().multiply(new BigDecimal(porcen)).divide(new BigDecimal(100)));
                cpValoracionExtrasServicio.editarCpValoracionExtras(cpEx7);
            }

            // 8
            CpValoracionExtras cpEx8 = cpValoracionExtrasServicio.buscarValoresRecargos(CPV, "E_EVH");
            if (cpEx8 != null) {
                cpEx8.setCpvalextBase(CPV.getCatprevalImpuesto());
                double porcen = adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(cpEx8.getAdidedCodigo().getAdidedCodigo()).getAdidedPorcentaje();
                cpEx8.setCpvalextValor(CPV.getCatprevalImpuesto().multiply(new BigDecimal(porcen)).divide(new BigDecimal(100)));
                cpValoracionExtrasServicio.editarCpValoracionExtras(cpEx8);
            }
            // 9
            CpValoracionExtras cpEx9 = cpValoracionExtrasServicio.buscarValoresRecargos(CPV, "E_CVN");
            if (cpEx9 != null) {
                cpEx9.setCpvalextBase(CPV.getCatprevalImpuesto());
                double porcen = adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(cpEx9.getAdidedCodigo().getAdidedCodigo()).getAdidedPorcentaje();
                cpEx9.setCpvalextValor(CPV.getCatprevalImpuesto().multiply(new BigDecimal(porcen)).divide(new BigDecimal(100)));
                cpValoracionExtrasServicio.editarCpValoracionExtras(cpEx9);
            }
            // 10
            CpValoracionExtras cpEx10 = cpValoracionExtrasServicio.buscarValoresRecargos(CPV, "E_FFI");
            if (cpEx10 != null) {
                cpEx10.setCpvalextBase(CPV.getCatprevalImpuesto());
                double porcen = adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(cpEx10.getAdidedCodigo().getAdidedCodigo()).getAdidedPorcentaje();
                cpEx10.setCpvalextValor(CPV.getCatprevalImpuesto().multiply(new BigDecimal(porcen)).divide(new BigDecimal(100)));
                cpValoracionExtrasServicio.editarCpValoracionExtras(cpEx10);
            }

            // 10
            CpValoracionExtras cpEx11 = cpValoracionExtrasServicio.buscarValoresRecargos(CPV, "E_ERH");
            if (cpEx11 != null) {
                cpEx11.setCpvalextBase(CPV.getCatprevalImpuesto());
                double porcen = adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(cpEx11.getAdidedCodigo().getAdidedCodigo()).getAdidedPorcentaje();
                cpEx11.setCpvalextValor(CPV.getCatprevalImpuesto().multiply(new BigDecimal(porcen)).divide(new BigDecimal(100)));
                cpValoracionExtrasServicio.editarCpValoracionExtras(cpEx11);
            }

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

    }

    public void preEmisionValoracion() {
        try {

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

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

    public void emision() {
        try {
            for (int i = 0; i < listaEjecutarValoracion.size(); i++) {

                EjecutarValoracion eje = listaEjecutarValoracion.get(i);

                cxcServicio.crearCxcPorImpPredial(eje.getCatastroPredialValoracion());
                eje.getCatastroPredialValoracion().setCatprevalActivo(true);
                catastroPredialValoracionServicio.editarCatastroPredialValoracion(eje.getCatastroPredialValoracion());

                //addSuccessMessage("Emisión Realizada"); 
            }

//             FacesContext fc = FacesContext.getCurrentInstance();  
//        fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New message has been added successfully", null));
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void solarNoEdif() {
        try {
            CpValoracionExtras cpR2 = cpValoracionExtrasServicio.buscarValoresRecargos(catastroPredialValoracionActualEJE, "R_INE");
            if (cpR2 != null) {
                cpR2.setCpvalextBase(catastroPredialValoracionActualEJE.getCatprevalValorPropieda());
                double porcen = adicionalesDeductivosServicio.buscarAdicionesDeductivosXCodigo(cpR2.getAdidedCodigo().getAdidedCodigo()).getAdidedPorcentaje();
                cpR2.setCpvalextValor(catastroPredialValoracionActualEJE.getCatprevalValorPropieda().multiply(new BigDecimal(porcen).divide(new BigDecimal(100))).setScale(2, RoundingMode.HALF_UP));
                cpValoracionExtrasServicio.editarCpValoracionExtras(cpR2);
                catastroPredialValoracionActualEJE.setCatprevalSolarNoedificado(cpR2.getCpvalextValor());
            } else {
                catastroPredialValoracionActualEJE.setCatprevalSolarNoedificado(new BigDecimal(0).setScale(2, RoundingMode.HALF_UP));
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void calcularBaseImponible(CatastroPredial catastroPredialActual) {

        try {
//           List<CpValoracionExtras> adicDeduc = cpValoracionExtrasServicio.listarCpValoracionExtrasXCatPreVal(catastroPredialValoracionActualEJE);
//           boolean terceraEdad=false;
//           for (int i = 0; i < adicDeduc.size(); i++) {                
//            CpValoracionExtras VE = adicDeduc.get(i);
//             if (VE.getAdidedCodigo().getAdidedNemonico().equals("D_R3E")){
//             terceraEdad=true;
//             i=adicDeduc.size();
//             }else{
//             terceraEdad=false;
//             }
//               System.out.println("ds>: "+VE.getAdidedCodigo().getAdidedNemonico());
//           }                                                       
            //if(terceraEdad==true){

               //catastroPredialServicio.obtenerPropietarioPrincipalPredio(catastroPredialActual.getCatpreCodigo()).getProCi();
//               BigDecimal totalAvaluoxPro = BigDecimal.ZERO;
//               List<CatastroPredial> catastroPropietario = catastroPredialServicio.listarCatastroPorCedulaPropietario(catastroPredialServicio.obtenerPropietarioPrincipalPredio(catastroPredialActual.getCatpreCodigo()).getProCi());
//               for (int i = 0; i < catastroPropietario.size(); i++) {
//                   CatastroPredialValoracion CPVxPro = catastroPredialServicio.obtenerValoracionPredio(catastroPropietario.get(i));
//                   System.out.println("ko: " + CPVxPro.getCatprevalAvaluoTot());
//                   if (CPVxPro.getCatprevalAvaluoTot() != null) {
//                       totalAvaluoxPro = totalAvaluoxPro.add(CPVxPro.getCatprevalAvaluoTot());
//                   }
////               }
//               if(totalAvaluoxPro.compareTo(new BigDecimal(datoGlobalServicio.obtenerDatoGlobal("500RBU").getDatgloValor()))==1){
//                     catastroPredialValoracionActualEJE.setCatprevalBaseImponible(totalAvaluoxPro.subtract(new BigDecimal(datoGlobalServicio.obtenerDatoGlobal("500RBU").getDatgloValor())).setScale(2, RoundingMode.HALF_UP));               
//               }else{
//                   if(totalAvaluoxPro.compareTo(new BigDecimal(datoGlobalServicio.obtenerDatoGlobal("500RBU").getDatgloValor()))==-1){                   
//                       catastroPredialValoracionActualEJE.setCatprevalBaseImponible(totalAvaluoxPro.setScale(2, RoundingMode.HALF_UP));                               
//               }               
//               }
            //    System.out.println("total: " + totalAvaluoxPro);
//               
//           }else{
//               catastroPredialValoracionActualEJE.setCatprevalBaseImponible(catastroPredialValoracionActualEJE.getCatprevalAvaluoTot().setScale(2, RoundingMode.HALF_UP));                
//           }
            CpValoracionExtras cpD1 = cpValoracionExtrasServicio.buscarValoresRecargos(catastroPredialValoracionActualEJE, "D_RHI");

            System.out.println("hi: sf ");

            if (cpD1 != null) {

                System.out.println("hi: " + catastroPredialValoracionActualEJE.getCatprevalValorPropieda().subtract(cpD1.getCpvalextValorAdicional()));
                catastroPredialValoracionActualEJE.setCatprevalBaseImponible(catastroPredialValoracionActualEJE.getCatprevalValorPropieda().subtract(cpD1.getCpvalextValorAdicional()));
            } else {
                catastroPredialValoracionActualEJE.setCatprevalBaseImponible(catastroPredialValoracionActualEJE.getCatprevalValorPropieda());

            }

            CpValoracionExtras cpEx1 = cpValoracionExtrasServicio.buscarValoresRecargos(catastroPredialValoracionActualEJE, "D_R3E");
            if (cpEx1 != null) {
                BigDecimal totalAvaluoxPro = BigDecimal.ZERO;
                List<CatastroPredial> catastroPropietario = catastroPredialServicio.listarCatastroPorCedulaPropietario(catastroPredialServicio.obtenerPropietarioPrincipalPredio(catastroPredialActualEmision.getCatpreCodigo()).getProCi());
                for (int i = 0; i < catastroPropietario.size(); i++) {
                    CatastroPredialValoracion CPVxPro = catastroPredialServicio.obtenerValoracionPredio(catastroPropietario.get(i));
                    System.out.println("ko: " + CPVxPro.getCatprevalAvaluoTot());
                    if (CPVxPro.getCatprevalAvaluoTot() != null) {
                        totalAvaluoxPro = totalAvaluoxPro.add(CPVxPro.getCatprevalAvaluoTot());
                    }
                }
                System.out.println("total suma Prop: " + totalAvaluoxPro);
                // si suma es mayor
                if (totalAvaluoxPro.compareTo(new BigDecimal(datoGlobalServicio.obtenerDatoGlobal("500RBU").getDatgloValor())) == 1) {
                    catastroPredialValoracionActualEJE.setCatprevalBaseImponible(totalAvaluoxPro.subtract(new BigDecimal(datoGlobalServicio.obtenerDatoGlobal("500RBU").getDatgloValor())).setScale(2, RoundingMode.HALF_UP));
                } else {
                    if (totalAvaluoxPro.compareTo(new BigDecimal(datoGlobalServicio.obtenerDatoGlobal("500RBU").getDatgloValor())) == -1) {
                        catastroPredialValoracionActualEJE.setCatprevalBaseImponible(catastroPredialValoracionActualEJE.getCatprevalBaseImponible().setScale(2, RoundingMode.HALF_UP));
                    }
                }

            } else {
                //if(cpD1==null){ 
                catastroPredialValoracionActualEJE.setCatprevalBaseImponible(catastroPredialValoracionActualEJE.getCatprevalBaseImponible().setScale(2, RoundingMode.HALF_UP));
                //}

            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    // METODOS
    public List<AdicionalesDeductivos> getListaAdicionalesDeductivosRecargos() {
        return listaAdicionalesDeductivosRecargos;
    }

    public void setListaAdicionalesDeductivosRecargos(
            List<AdicionalesDeductivos> listaAdicionalesDeductivosRecargos) {
        this.listaAdicionalesDeductivosRecargos = listaAdicionalesDeductivosRecargos;
    }

    public List<String> getListaAdicionalesDeductivosRecargosSeleccion() {
        return listaAdicionalesDeductivosRecargosSeleccion;
    }

    public void setListaAdicionalesDeductivosRecargosSeleccion(List<String> listaAdicionalesDeductivosRecargosSeleccion) {
        this.listaAdicionalesDeductivosRecargosSeleccion = listaAdicionalesDeductivosRecargosSeleccion;
    }

    public List<AdicionalesDeductivos> getListaAdicionalesDeductivosExoneraciones() {
        return listaAdicionalesDeductivosExoneraciones;
    }

    public void setListaAdicionalesDeductivosExoneraciones(List<AdicionalesDeductivos> listaAdicionalesDeductivosExoneraciones) {
        this.listaAdicionalesDeductivosExoneraciones = listaAdicionalesDeductivosExoneraciones;
    }

    public List<String> getListaAdicionalesDeductivosExoneracionesSeleccion() {
        return listaAdicionalesDeductivosExoneracionesSeleccion;
    }

    public void setListaAdicionalesDeductivosExoneracionesSeleccion(List<String> listaAdicionalesDeductivosExoneracionesSeleccion) {
        this.listaAdicionalesDeductivosExoneracionesSeleccion = listaAdicionalesDeductivosExoneracionesSeleccion;
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

    public List<PredioArchivo> getListaPredioArchivo() {
        return listaPredioArchivo;
    }

    public void setListaPredioArchivo(List<PredioArchivo> listaPredioArchivo) {
        this.listaPredioArchivo = listaPredioArchivo;
    }

    public StreamedContent getArchivo() {
        return archivo;
    }

    public void setArchivo(StreamedContent archivo) {
        this.archivo = archivo;
    }

   

    public CatastroPredial getCatastroPredialActual() {
        return catastroPredialActual;
    }

    public void setCatastroPredialActual(CatastroPredial catastroPredialActual) {
        this.catastroPredialActual = catastroPredialActual;
    }

    public List<CatastroPredial> getListaCatastroPredialAValoracion() {
        return listaCatastroPredialAValoracion;
    }

    public void setListaCatastroPredialAValoracion(List<CatastroPredial> listaCatastroPredialAValoracion) {
        this.listaCatastroPredialAValoracion = listaCatastroPredialAValoracion;
    }

    public List<CatastroPredial> getListaCatastroPredialClavesCatastrales() {
        return listaCatastroPredialClavesCatastrales;
    }

    public void setListaCatastroPredialClavesCatastrales(List<CatastroPredial> listaCatastroPredialClavesCatastrales) {
        this.listaCatastroPredialClavesCatastrales = listaCatastroPredialClavesCatastrales;
    }


    public List<EjecutarValoracion> getListaEjecutarValoracion() {
        return listaEjecutarValoracion;
    }

    public void setListaEjecutarValoracion(List<EjecutarValoracion> listaEjecutarValoracion) {
        this.listaEjecutarValoracion = listaEjecutarValoracion;
    }

    public BigDecimal getTotalTotal() {
        return totalTotal;
    }

    public void setTotalTotal(BigDecimal TotalTotal) {
        this.totalTotal = TotalTotal;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public List<CatalogoDetalle> getListaParroquias() {
        return listaParroquias;
    }

    public void setListaParroquias(List<CatalogoDetalle> listaParroquias) {
        this.listaParroquias = listaParroquias;
    }

    public CatalogoDetalle getCatalogoParroquia() {
        return catalogoParroquia;
    }

    public void setCatalogoParroquia(CatalogoDetalle catalogoParroquia) {
        this.catalogoParroquia = catalogoParroquia;
    }

    public List<CatalogoDetalle> getListaSectores() {
        return listaSectores;
    }

    public void setListaSectores(List<CatalogoDetalle> listaSectores) {
        this.listaSectores = listaSectores;
    }

    public CatalogoDetalle getCatalogoSector() {
        return catalogoSector;
    }

    public void setCatalogoSector(CatalogoDetalle catalogoSector) {
        this.catalogoSector = catalogoSector;
    }

    public int getAnioPr() {
        return anioPr;
    }

    public void setAnioPr(int anioPr) {
        this.anioPr = anioPr;
    }

    public PropietarioPredio getPropietarioPredioBusqueda() {
        return propietarioPredioBusqueda;
    }

    public void setPropietarioPredioBusqueda(PropietarioPredio propietarioPredioBusqueda) {
        this.propietarioPredioBusqueda = propietarioPredioBusqueda;
    }

    public boolean isExiste() {
        return existe;
    }

    public void setExiste(boolean existe) {
        this.existe = existe;
    }

    public String getVisibleRebHipotecaria() {
        return visibleRebHipotecaria;
    }

    public void setVisibleRebHipotecaria(String visibleRebHipotecaria) {
        this.visibleRebHipotecaria = visibleRebHipotecaria;
    }

    public BigDecimal getRebHipotecaria() {
        return rebHipotecaria;
    }

    public void setRebHipotecaria(BigDecimal rebHipotecaria) {
        this.rebHipotecaria = rebHipotecaria;
    }

    public CatastroPredialValoracion getCatastroPredialValoracionActualEJE() {
        return catastroPredialValoracionActualEJE;
    }

    public void setCatastroPredialValoracionActualEJE(CatastroPredialValoracion catastroPredialValoracionActualEJE) {
        this.catastroPredialValoracionActualEJE = catastroPredialValoracionActualEJE;
    }

    public String getVisibleBeneficiencia() {
        return visibleBeneficiencia;
    }

    public void setVisibleBeneficiencia(String visibleBeneficiencia) {
        this.visibleBeneficiencia = visibleBeneficiencia;
    }

    public double getValorBeneficiencia() {
        return valorBeneficiencia;
    }

    public void setValorBeneficiencia(double valorBeneficiencia) {
        this.valorBeneficiencia = valorBeneficiencia;
    }

    public String getVisibleEntidadPublica() {
        return visibleEntidadPublica;
    }

    public void setVisibleEntidadPublica(String visibleEntidadPublica) {
        this.visibleEntidadPublica = visibleEntidadPublica;
    }

    public double getValorEntidadPublica() {
        return valorEntidadPublica;
    }

    public void setValorEntidadPublica(double valorEntidadPublica) {
        this.valorEntidadPublica = valorEntidadPublica;
    }

    public CatastroPredial getCatastroPredialActualEmision() {
        return catastroPredialActualEmision;
    }

    public void setCatastroPredialActualEmision(CatastroPredial catastroPredialActualEmision) {
        this.catastroPredialActualEmision = catastroPredialActualEmision;
    }

    public String getTextoClave() {
        return textoClave;
    }

    public void setTextoClave(String textoClave) {
        this.textoClave = textoClave;
    }

    public String getTextoCodigoManzana() {
        return textoCodigoManzana;
    }

    public void setTextoCodigoManzana(String textoCodigoManzana) {
        this.textoCodigoManzana = textoCodigoManzana;
    }

    public String getTipoBusqueda() {
        return tipoBusqueda;
    }

    public void setTipoBusqueda(String tipoBusqueda) {
        this.tipoBusqueda = tipoBusqueda;
    }
    
    
}
