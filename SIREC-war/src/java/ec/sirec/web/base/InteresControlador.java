/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.web.base;

import ec.sirec.ejb.entidades.Interes;
import ec.sirec.ejb.servicios.InteresServicio;
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
 * @author DAVID GUAN
 */
@ManagedBean
@ViewScoped
public class InteresControlador extends BaseControlador {

    private static final Logger LOGGER = Logger.getLogger(InteresControlador.class.getName());

    @EJB
    private InteresServicio interesServicio;

    private Interes interesActual;
    private List<Interes> listaInteres;

    /**
     * Creates a new instance of InteresControlador
     */
    public InteresControlador() {
    }

    @PostConstruct
    public void inicializar() {
        try {
            interesActual = new Interes();
            interesActual.setIntTipo("A");
            listaInteres = new ArrayList<Interes>();
            listaInteres = interesServicio.listarInteres();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void guardarInteres() {
        try {
            if (interesActual.getIntTipo().equals("A")) {
                interesActual.setIntMes(0);
            }
            if (interesServicio.existeInteres(interesActual.getIntAnio(), interesActual.getIntMes())) {
                addWarningMessage("Existe interes para este periodo");
            } else {
                if (interesActual.getIntCodigo() == null) {
                    interesServicio.crearInteres(interesActual);
                    addSuccessMessage("Interes creado correctamente");
                } else {
                    interesServicio.editarInteres(interesActual);
                    addSuccessMessage("Interes editado correctamente");
                }
            }
            inicializar();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void seleccionarItem(Interes inte){
        interesActual=inte;
    }
    public Interes getInteresActual() {
        return interesActual;
    }

    public void setInteresActual(Interes interesActual) {
        this.interesActual = interesActual;
    }

    public List<Interes> getListaInteres() {
        return listaInteres;
    }

    public void setListaInteres(List<Interes> listaInteres) {
        this.listaInteres = listaInteres;
    }
    
    

}
