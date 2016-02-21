/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.sirec.ejb.entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author DAVID GUAN
 */
@Entity
@Table(name = "sirec.rebaja_desvalorizacion")
@NamedQueries({
    @NamedQuery(name = "RebajaDesvalorizacion.findAll", query = "SELECT r FROM RebajaDesvalorizacion r")})
public class RebajaDesvalorizacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "rebdes_codigo")
    private Integer rebdesCodigo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "anio")
    private int anio;
    @Basic(optional = false)
    @NotNull
    @Column(name = "porcentaje_rebaja")
    private double porcentajeRebaja;

    public RebajaDesvalorizacion() {
    }

    public RebajaDesvalorizacion(Integer rebdesCodigo) {
        this.rebdesCodigo = rebdesCodigo;
    }

    public RebajaDesvalorizacion(Integer rebdesCodigo, int anio, double porcentajeRebaja) {
        this.rebdesCodigo = rebdesCodigo;
        this.anio = anio;
        this.porcentajeRebaja = porcentajeRebaja;
    }

    public Integer getRebdesCodigo() {
        return rebdesCodigo;
    }

    public void setRebdesCodigo(Integer rebdesCodigo) {
        this.rebdesCodigo = rebdesCodigo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public double getPorcentajeRebaja() {
        return porcentajeRebaja;
    }

    public void setPorcentajeRebaja(double porcentajeRebaja) {
        this.porcentajeRebaja = porcentajeRebaja;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rebdesCodigo != null ? rebdesCodigo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RebajaDesvalorizacion)) {
            return false;
        }
        RebajaDesvalorizacion other = (RebajaDesvalorizacion) object;
        if ((this.rebdesCodigo == null && other.rebdesCodigo != null) || (this.rebdesCodigo != null && !this.rebdesCodigo.equals(other.rebdesCodigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.sirec.ejb.entidades.RebajaDesvalorizacion[ rebdesCodigo=" + rebdesCodigo + " ]";
    }
    
}
