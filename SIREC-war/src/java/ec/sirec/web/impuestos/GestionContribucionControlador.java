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
import ec.sirec.ejb.entidades.Constructora;
import ec.sirec.ejb.entidades.CpAlcabalaValoracionExtras;
import ec.sirec.ejb.entidades.Mejora;
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
import ec.sirec.ejb.servicios.ConstructoraServicio;
import ec.sirec.ejb.servicios.CpAlcabalaValoracionExtrasServicio;
import ec.sirec.ejb.servicios.CpValoracionExtrasServicio;
import ec.sirec.ejb.servicios.DatoGlobalServicio;
import ec.sirec.ejb.servicios.ObraProyectoServicio;
import ec.sirec.ejb.servicios.PredioArchivoServicio;
import ec.sirec.ejb.servicios.RecaudacionCabServicio;
import ec.sirec.ejb.servicios.RecaudacionDetServicio;
import ec.sirec.web.base.BaseControlador;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author vespinoza
 */
@ManagedBean
@ViewScoped
public class GestionContribucionControlador extends BaseControlador {

    /**
     * Creates a new instance of GestionConceptoControlador
     */
    //LOGGER 
    private static final Logger LOGGER = Logger.getLogger(GestionContribucionControlador.class.getName());
    // VARIABLES Y ATRIBUTOS

//    private SegUsuario usuarioActual;
//    private CatastroPredial catastroPredialActual;
//    private CatastroPredialAlcabalaValoracion catastroPredialAlcabalaValoracion;
//    private CatalogoDetalle catalogoDetalleConcepto;
//
//    private List<CatastroPredial> listaCatastroPredial;
//    private List<CatalogoDetalle> listaCatalogoDetalleConcepto;
//    private CatastroPredialValoracion catastroPredialValoracionActual;
//
//    private List<AdicionalesDeductivos> listaAdicionalesDeductivosDeducciones;
//    private List<String> listaAdicionalesDeductivosDeduccionesSeleccion;
//    private List<AdicionalesDeductivos> listaAdicionalesDeductivosExcenciones;
//    private List<String> listaAdicionalesDeductivosExcencionesSeleccion;
//
//    private AdicionalesDeductivos adicionalesDeductivosActual;
//    private CpAlcabalaValoracionExtras cpAlcabalaValoracionExtrasActual;
//    private StreamedContent archivo;
//    private Propietario propietario;
//    private List<PredioArchivo> listaAlcabalasArchivo;
//    private PredioArchivo predioArchivo;
//    private PropietarioPredio propietarioPredioBusqueda;
//    private int anio;
    
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

    /// OBRA O PROYECTO
    private ObraProyecto obraProyectoActual;
    private List<CatalogoDetalle> listaParroquias;
    private List<CatalogoDetalle> listaEstado;
    private List<CatalogoDetalle> listaEjecucion;
    private List<CatalogoDetalle> listaTipoObra;
    private List<Constructora> listaConstructora;
    private String codEje;
    private String etiquedaEje;
    private List<ObraProyecto> listaObraProyecto;
    private Mejora mejoraActual;
    
    
    //  ASIGNACIOM DE PREDIO
    
    private CatastroPredial catastroPredialActual;
    private PropietarioPredio propietarioPredioBusqueda;

    
    
    @EJB
    private ConstructoraServicio constructoraServicio;
    @EJB
    private ObraProyectoServicio obraProyectoServicio;

    @PostConstruct
    public void inicializar() {
        try {

            obraProyectoActual = new ObraProyecto();
            listaParroquias = new ArrayList<CatalogoDetalle>();
            listaEstado = new ArrayList<CatalogoDetalle>();
            listaEjecucion = new ArrayList<CatalogoDetalle>();
            listaTipoObra = new ArrayList<CatalogoDetalle>();
            listaConstructora = new ArrayList<Constructora>();
            codEje = "";
            etiquedaEje = "";

            obraProyectoActual.setObrViales(BigDecimal.ZERO);
            obraProyectoActual.setObrAcerasBordillos(BigDecimal.ZERO);
            obraProyectoActual.setObrServicio(BigDecimal.ZERO);
            obraProyectoActual.setObrInfUrbana(BigDecimal.ZERO);
            obraProyectoActual.setObrDesecacionRellenos(BigDecimal.ZERO);
            obraProyectoActual.setObrTotal(BigDecimal.ZERO);

            listarParroquias();
            listarEstados();
            listarEjecucion();
            listarObraProyectos();
            listarTipoObra();
            
            inicializar2();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void inicializar2() {
        try {
            catastroPredialActual = new CatastroPredial();
            mejoraActual = new Mejora();
            
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
//    public void obtenerUsuario() {
//        usuarioActual = new SegUsuario();
//        usuarioActual = (SegUsuario) getSession().getAttribute("usuario");
//        //System.out.println(usuarioActual.getUsuIdentificacion());         
//    }

    public GestionContribucionControlador() {
    }

    public void listarParroquias() {
        try {
            listaParroquias = catastroPredialServicio.listaCatParroquias();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void listarEstados() {
        try {
            listaEstado = catastroPredialServicio.listaCatEstadoObr();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void listarEjecucion() {
        try {
            listaEjecucion = catastroPredialServicio.listaCatEjecicion();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public void listarTipoObra() {
        try {
            listaTipoObra = catastroPredialServicio.listaCatTipoObra();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void listarContratistas() {

        try {
            codEje = catastroPredialServicio.cargarObjetoCatalogoDetalle(obraProyectoActual.getCatdetEjecucion().getCatdetCodigo()).getCatdetCod();
            if (codEje.equals("E")) {
                etiquedaEje = "Empresa pública:";
            } else {
                if (codEje.equals("C")) {
                    etiquedaEje = "Contratista:";
                }
            }
            listaConstructora = constructoraServicio.listarConstructoraXTipo(codEje);

            System.out.println("tama " + listaConstructora.size());

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void sumarDistribucion() {
        try {
            obraProyectoActual.setObrTotal(obraProyectoActual.getObrViales().add(obraProyectoActual.getObrAcerasBordillos()).
                    add(obraProyectoActual.getObrServicio()).add(obraProyectoActual.getObrInfUrbana()).add(obraProyectoActual.getObrDesecacionRellenos()));
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void guardarObraProyecto() {
        try {
            String mensaje = obraProyectoServicio.crearObraProyecto(obraProyectoActual);
            addSuccessMessage(mensaje, mensaje);

            inicializar();
            listarObraProyectos();

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void listarObraProyectos() {
        try {
            listaObraProyecto = new ArrayList<ObraProyecto>();
            listaObraProyecto = obraProyectoServicio.listarObrasProyectos();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public void determinar(ObraProyecto obraProyectoActual1) {
        try {
            obraProyectoActual=obraProyectoActual1;            
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public void aceptarDeterminacion() {
        try {
            obraProyectoServicio.editarObraProyecto(obraProyectoActual);
            listarObraProyectos();
            inicializar();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    ////////////////////////////////////  ASIGNACION DE PREDIOS 
    
     public List<CatastroPredial> obtenerCatastroXCalve(String clave) {
        List<CatastroPredial> lstPP = new ArrayList<CatastroPredial>();
        try {
            lstPP = catastroPredialServicio.listarCatastrosPorClaveContieneContiene(clave);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return lstPP;
    }
     
     public void onItemSelectClave(SelectEvent event) {
        try {
            CatastroPredial pp = (CatastroPredial) event.getObject();            
             propietarioPredioBusqueda = new PropietarioPredio();
             propietarioPredioBusqueda = catastroPredialServicio.buscarPropietarioPredioPorCatastro(pp.getCatpreCodigo());            
            catastroPredialActual = catastroPredialServicio.cargarObjetoCatastro(pp.getCatpreCodigo());            
             //limpiar();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
     public void guardarAsignacionObra() {
        try {
//            String mensaje = obraProyectoServicio.crearObraProyecto(obraProyectoActual);
//            addSuccessMessage(mensaje, mensaje);
//
//            inicializar();
//            listarObraProyectos();

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public ObraProyecto getObraProyectoActual() {
        return obraProyectoActual;
    }

    public void setObraProyectoActual(ObraProyecto obraProyectoActual) {
        this.obraProyectoActual = obraProyectoActual;
    }
    
 public List<CatalogoDetalle> getListaParroquias() {
        return listaParroquias;
    }

    public void setListaParroquias(List<CatalogoDetalle> listaParroquias) {
        this.listaParroquias = listaParroquias;
    }

    public List<CatalogoDetalle> getListaEstado() {
        return listaEstado;
    }

    public void setListaEstado(List<CatalogoDetalle> listaEstado) {
        this.listaEstado = listaEstado;
    }

    public List<CatalogoDetalle> getListaEjecucion() {
        return listaEjecucion;
    }

    public void setListaEjecucion(List<CatalogoDetalle> listaEjecucion) {
        this.listaEjecucion = listaEjecucion;
    }

    public List<Constructora> getListaConstructora() {
        return listaConstructora;
    }

    public void setListaConstructora(List<Constructora> listaConstructora) {
        this.listaConstructora = listaConstructora;
    }

    public String getCodEje() {
        return codEje;
    }

    public void setCodEje(String codEje) {
        this.codEje = codEje;
    }

    public String getEtiquedaEje() {
        return etiquedaEje;
    }

    public void setEtiquedaEje(String etiquedaEje) {
        this.etiquedaEje = etiquedaEje;
    }

    public List<ObraProyecto> getListaObraProyecto() {
        return listaObraProyecto;
    }

    public void setListaObraProyecto(List<ObraProyecto> listaObraProyecto) {
        this.listaObraProyecto = listaObraProyecto;
    }

    public List<CatalogoDetalle> getListaTipoObra() {
        return listaTipoObra;
    }

    public void setListaTipoObra(List<CatalogoDetalle> listaTipoObra) {
        this.listaTipoObra = listaTipoObra;
    }        
 
    public CatastroPredial getCatastroPredialActual() {
        return catastroPredialActual;
    }

    public void setCatastroPredialActual(CatastroPredial catastroPredialActual) {
        this.catastroPredialActual = catastroPredialActual;
    }
    
     public PropietarioPredio getPropietarioPredioBusqueda() {
        return propietarioPredioBusqueda;
    }

    public void setPropietarioPredioBusqueda(PropietarioPredio propietarioPredioBusqueda) {
        this.propietarioPredioBusqueda = propietarioPredioBusqueda;
    }

    public Mejora getMejoraActual() {
        return mejoraActual;
    }

    public void setMejoraActual(Mejora mejoraActual) {
        this.mejoraActual = mejoraActual;
    }
    
    
}