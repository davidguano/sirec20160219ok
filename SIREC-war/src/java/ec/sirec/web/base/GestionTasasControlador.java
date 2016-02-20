/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.web.base;

import ec.sirec.ejb.entidades.CatalogoDetalle;
import ec.sirec.ejb.entidades.Propietario;
import ec.sirec.ejb.entidades.SegUsuario;
import ec.sirec.ejb.entidades.Servicios;
import ec.sirec.ejb.entidades.Tasa;
import ec.sirec.ejb.servicios.CatalogoDetalleServicio;
import ec.sirec.ejb.servicios.CatastroPredialServicio;
import ec.sirec.ejb.servicios.PropietarioServicio;
import ec.sirec.ejb.servicios.ServiciosServicio;
import ec.sirec.ejb.servicios.TasaServicio;
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

/**
 *
 * @author vespinoza
 */
@ManagedBean
@ViewScoped
public class GestionTasasControlador extends BaseControlador {

    /**
     * Creates a new instance of GestionConceptoControlador
     */
    //LOGGER 
    private static final Logger LOGGER = Logger.getLogger(GestionTasasControlador.class.getName());
    // VARIABLES Y ATRIBUTOS

    private SegUsuario usuarioActual;
    private Tasa tasasActual;
    private String tipoSigno;
    private String opcionBoton;
    
    private String tipoIdentificacion;
    private Propietario propietarioActual;
    private Tasa tasasEmisionActual; 
    private Servicios servicioActual; 
    
     private List<CatalogoDetalle> listaCatalogoDetalleDepartamentos;
     private List<CatalogoDetalle> listaCatalogoDetalleTipoValor;
     private List<Tasa> listaTasas;
      
    @EJB
    private CatalogoDetalleServicio catalogoDetalleServicio;
    @EJB
    private CatastroPredialServicio catastroPredialServicio;
    @EJB
    private TasaServicio tasaServicio;
    @EJB
    private PropietarioServicio propietarioServicio;
    @EJB
    private ServiciosServicio serviciosServicio;

    @PostConstruct
    public void inicializar() {
        try {
            
            
             tasasActual= new Tasa();
             tasasEmisionActual = new Tasa(); 
             
             opcionBoton = "N";
             tipoSigno="";
             
             tipoIdentificacion="";
             
             servicioActual = new Servicios(); 
             obtenerUsuario();             
             listarDepartamentos();
             listarTipoValor();
             listarTasas();
             
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    
    public void listarDepartamentos() {
        try {
            listaCatalogoDetalleDepartamentos = new ArrayList<CatalogoDetalle>();
            listaCatalogoDetalleDepartamentos = catalogoDetalleServicio.listarPorNemonicoCatalogo("UNI_ADMIN");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    public void listarTipoValor() {
        try {
            listaCatalogoDetalleTipoValor = new ArrayList<CatalogoDetalle>();
            listaCatalogoDetalleTipoValor = catalogoDetalleServicio.listarPorNemonicoCatalogo("TIP_VAL_TASA");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    public void definirTipo() {
        try {            
            tipoSigno = catastroPredialServicio.cargarObjetoCatalogoDetalle(tasasActual.getCatdetTipoValor().getCatdetCodigo()).getCatdetCod();                        
        } catch (Exception ex) {
            tipoSigno="";
          //  LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public void guardarIngresoTasa() {
        try {
            if(opcionBoton.equals("N")){
             tasaServicio.crearTasa(tasasActual);
             addSuccessMessage("Guardado exitosamente","Guardado exitosamente");     
            }else{
                if(opcionBoton.equals("E")){
                    tasaServicio.editarTasa(tasasActual);
                    
            addSuccessMessage("Se ha modificado el registro","Se ha modificado el registro");                 
            }            
            }                       
            listarTasas();                               
            nuevoIngresoTasa();             
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public void nuevoIngresoTasa() {
        try {                        
            tasasActual = new Tasa();
            tipoSigno = "";
            opcionBoton="N";
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public void listarTasas() {
        try {        
            listaTasas = new ArrayList<Tasa>();
            listaTasas = tasaServicio.listarTasa();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void obtenerUsuario() {
        try { 
        usuarioActual = new SegUsuario();
        usuarioActual = (SegUsuario) getSession().getAttribute("usuario");     
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public void recuperarTasa(Tasa tasa) {
        try {
            opcionBoton="E";
            tasasActual = new Tasa(); 
            tasasActual = tasa;                        
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public void eliminarTasa(Tasa tasa) {
        try {            
            tasaServicio.eliminarTasa(tasa);             
            addSuccessMessage("Registro Eliminado","Registro Eliminado"); 
            listarTasas();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
     public List<Propietario> obtenerPropietarioApellido(String vcedula) {
        List<Propietario> lstPP = new ArrayList<Propietario>();
        try {
            lstPP = propietarioServicio.listarPorCedulaContiene(vcedula);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return lstPP;
    }
     
     public void onItemSelect(SelectEvent event) {
        try {
            Propietario pp = (Propietario) event.getObject();
            propietarioActual = propietarioServicio.buscarPropietario(pp.getProCi());
           
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
     
     public void calcularEmisionTasa() {
        try {
           tasasEmisionActual = tasaServicio.cargarObjetoTasa(tasasEmisionActual.getTasCodigo());
           
           if(tasasEmisionActual.getCatdetDepartamento().getCatdetTexto().equals("CENTRO DE REHABILITACION")){            
               if(tasasEmisionActual.getCatdetTipoValor().getCatdetTexto().equals("Valor Fijo")){            
                servicioActual.setSerDescuento(tasasEmisionActual.getTasValor());             
            }
               servicioActual.setSerSubtotal(servicioActual.getSerValor()); 
            
            }else{           
            if(tasasEmisionActual.getCatdetTipoValor().getCatdetTexto().equals("Valor Fijo")){            
                servicioActual.setSerSubtotal(servicioActual.getSerValor().add(tasasEmisionActual.getTasValor()));             
            }else{
                 if(tasasEmisionActual.getCatdetTipoValor().getCatdetTexto().equals("Porcentaje RBU")){
                    BigDecimal porce = servicioActual.getSerValor().multiply(tasasEmisionActual.getTasValor()).divide(new BigDecimal(100));                                         
                    servicioActual.setSerSubtotal(servicioActual.getSerValor().add(porce));
                 }                            
            }
            servicioActual.setSerDescuento(BigDecimal.ZERO);                         
           }
                                                
            if(tasasEmisionActual.getTasConIva()==true){   
                servicioActual.setSerIva(servicioActual.getSerSubtotal().multiply(new BigDecimal(12)).divide(new BigDecimal(100)));            
            }else{
                servicioActual.setSerIva(BigDecimal.ZERO);
            }
            
            servicioActual.setSerTotal(servicioActual.getSerSubtotal().add(servicioActual.getSerIva()).subtract(servicioActual.getSerDescuento()));
                                   
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
     
     
      public void guardarEmisionTasa() {
        try {
            if(servicioActual.getSerTotal()!=null){
                servicioActual.setProCi(propietarioActual); 
            servicioActual.setTasCodigo(tasasEmisionActual);
            servicioActual.setSerActivo(Boolean.FALSE);              
            serviciosServicio.crearServicios(servicioActual);        
            nuevoEmisionTasa();
                addSuccessMessage("Tasa emitida exitosamente!","Tasa emitida exitosamente!"); 
            }else{
                addErrorMessage("No se ha realizado el calculo!","No se ha realizado el calculo!"); 
            }
            
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
      
      public void nuevoEmisionTasa() {
        try {      
                tipoIdentificacion = "";
                propietarioActual = new Propietario();
                tasasEmisionActual = new Tasa();
                servicioActual = new Servicios();         
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public SegUsuario getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(SegUsuario usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    public Tasa getTasasActual() {
        return tasasActual;
    }

    public void setTasasActual(Tasa tasasActual) {
        this.tasasActual = tasasActual;
    }

    public List<CatalogoDetalle> getListaCatalogoDetalleDepartamentos() {
        return listaCatalogoDetalleDepartamentos;
    }

    public void setListaCatalogoDetalleDepartamentos(List<CatalogoDetalle> listaCatalogoDetalleDepartamentos) {
        this.listaCatalogoDetalleDepartamentos = listaCatalogoDetalleDepartamentos;
    }

    public List<CatalogoDetalle> getListaCatalogoDetalleTipoValor() {
        return listaCatalogoDetalleTipoValor;
    }

    public void setListaCatalogoDetalleTipoValor(List<CatalogoDetalle> listaCatalogoDetalleTipoValor) {
        this.listaCatalogoDetalleTipoValor = listaCatalogoDetalleTipoValor;
    }

    public String getTipoSigno() {
        return tipoSigno;
    }

    public void setTipoSigno(String tipoSigno) {
        this.tipoSigno = tipoSigno;
    }

    public List<Tasa> getListaTasas() {
        return listaTasas;
    }

    public void setListaTasas(List<Tasa> listaTasas) {
        this.listaTasas = listaTasas;
    }

    public String getOpcionBoton() {
        return opcionBoton;
    }

    public void setOpcionBoton(String opcionBoton) {
        this.opcionBoton = opcionBoton;
    }

    public String getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(String tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public Propietario getPropietarioActual() {
        return propietarioActual;
    }

    public void setPropietarioActual(Propietario propietarioActual) {
        this.propietarioActual = propietarioActual;
    }

    public Tasa getTasasEmisionActual() {
        return tasasEmisionActual;
    }

    public void setTasasEmisionActual(Tasa tasasEmisionActual) {
        this.tasasEmisionActual = tasasEmisionActual;
    }

    public Servicios getServicioActual() {
        return servicioActual;
    }

    public void setServicioActual(Servicios servicioActual) {
        this.servicioActual = servicioActual;
    }

    

    

    
    
}