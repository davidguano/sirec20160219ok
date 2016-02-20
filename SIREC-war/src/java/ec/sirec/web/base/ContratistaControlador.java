/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.web.base;

import ec.sirec.ejb.entidades.CatalogoDetalle;
import ec.sirec.ejb.entidades.Constructora;
import ec.sirec.ejb.servicios.ConstructoraServicio;
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
 * @author vespinoza
 */
@ManagedBean
@ViewScoped
public class ContratistaControlador extends BaseControlador {

    private static final Logger LOGGER = Logger.getLogger(ContratistaControlador.class.getName());

    private boolean flagEditar;       
    private Constructora constructoraActual;
    private List<Constructora> listaConstructora;
    @EJB
    private ConstructoraServicio constructoraServicio;

    /**
     * Creates a new instance of ContratistaControlador
     */
    public ContratistaControlador() {
    }

    @PostConstruct
    public void inicializar() {
        try {
            flagEditar = false;                            
            listarConstructora();            
            constructoraActual = new Constructora();
            
            
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public List<CatalogoDetalle> listarCatDetalles(String valor) {
        List<CatalogoDetalle> lstDet = new ArrayList<CatalogoDetalle>();
        try {
            return constructoraServicio.listarCiudadesPorTexto(valor);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return lstDet;
    }
    
    public void validarCedulaRuc() {
        try {
            if (constructoraServicio.esCedulaRucValida(constructoraActual.getConIdentificacion(),flagEditar).equals("valida")) {
                addSuccessMessage("Cedula valida");
            } else {
                addErrorMessage(constructoraServicio.esCedulaRucValida(constructoraActual.getConIdentificacion(),flagEditar));
                constructoraActual.setConIdentificacion(null); 
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }


    
    
    
    
                        
    public void guardarConstructora() {
        try {
            boolean b = false;
            String s = "";
            if (constructoraActual.getConTipoPersona().equals("N")) {
                s = validarDatosPersonaNatural(constructoraActual);
            }
            if (constructoraActual.getConTipoPersona().equals("J")) {
                s = validarDatosPersonaJuridica(constructoraActual);
            }
            if (s.equals("")) {
                b = true;
            } else {
                b = false;
                addErrorMessage(s);
            }

            if (constructoraServicio.esCedulaRucValida(constructoraActual.getConIdentificacion(),flagEditar).equals("valida") && b) {
                if (constructoraServicio.esFechaNacimientoValida(constructoraActual.getConFechaNacimiento())) {
                    if (!flagEditar) {
                        constructoraActual.setUsuIdentificacion(obtenerUsuarioAutenticado()); 
                        constructoraServicio.crearConstructora(constructoraActual); 
                        constructoraActual = new Constructora();
                        addSuccessMessage("Registro creado correctamente");
                    } else {
                        constructoraActual.setUsuIdentificacion(obtenerUsuarioAutenticado()); 
                        constructoraServicio.editarConstructora(constructoraActual);
                        addSuccessMessage("Registro editado correctamente");
                    }
                    listarConstructora();
                } else {
                    addErrorMessage("Fecha no valida");
                }

            } else {
                addErrorMessage(constructoraServicio.esCedulaRucValida(constructoraActual.getConIdentificacion(),flagEditar));
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public String validarDatosPersonaNatural(Constructora prop) {
        String s = "";
        try {
            if (prop.getConIdentificacion().length() != 10) {
                s = "Cedula debe tener 10 digitos";
            }
            if (prop.getConNombres() == null || prop.getConApellidos() == null || prop.getConNombres().equals("") || prop.getConApellidos().equals("")) {
                s = "Nombres y Apellidos obligatorios";
            } else {
                if (prop.getConFechaNacimiento() == null) {
                    s = "Fecha de nacimiento obligatorio";
                } else {
                    if (prop.getCatdetCiudad() == null) {
                        s = "Ciudad es obligatoria";
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return s;
    }

    public String validarDatosPersonaJuridica(Constructora prop) {
        String s = "";
        try {
            if (prop.getConIdentificacion().length() != 13) {
                s = "RUC debe tener 13 digitos";
            }
            if (prop.getConRazonSocial() == null || prop.getConRazonSocial().equals("")) {
                s = "Razon social obligatoria";
            } else {
                if (prop.getCatdetCiudad() == null) {
                    s = "Ciudad es obligatoria";
                }

            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return s;
    }
    
    
    public void listarConstructora() {
        try {
            flagEditar = false;
            listaConstructora = constructoraServicio.listarConstructoraTodos();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
            
    public void seleccionarPropietario(Constructora vcontructora) {
        try {
            constructoraActual = vcontructora;
            flagEditar = true;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void eliminarPropietario(Constructora vcontructora) {
        try {
            addErrorMessage(constructoraServicio.eliminarConstructora(vcontructora)); 
            listarConstructora();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
      

    public Constructora getConstructoraActual() {
        return constructoraActual;
    }

    public void setConstructoraActual(Constructora constructoraActual) {
        this.constructoraActual = constructoraActual;
    }

    public List<Constructora> getListaConstructora() {
        return listaConstructora;
    }

    public void setListaConstructora(List<Constructora> listaConstructora) {
        this.listaConstructora = listaConstructora;
    }

    
}
