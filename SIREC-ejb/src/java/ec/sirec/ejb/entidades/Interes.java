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
import javax.validation.constraints.Size;

/**
 *
 * @author DAVID GUAN
 */
@Entity
@Table(name = "sirec.interes")
@NamedQueries({
    @NamedQuery(name = "Interes.findAll", query = "SELECT i FROM Interes i")})
public class Interes implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "int_codigo")
    private Integer intCodigo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "int_tipo")
    private String intTipo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "int_anio")
    private int intAnio;
    @Basic(optional = false)
    @NotNull
    @Column(name = "int_mes")
    private int intMes;
    @Basic(optional = false)
    @NotNull
    @Column(name = "int_valor")
    private double intValor;

    public Interes() {
    }

    public Interes(Integer intCodigo) {
        this.intCodigo = intCodigo;
    }

    public Interes(Integer intCodigo, String intTipo, int intAnio, int intMes, double intValor) {
        this.intCodigo = intCodigo;
        this.intTipo = intTipo;
        this.intAnio = intAnio;
        this.intMes = intMes;
        this.intValor = intValor;
    }

    public Integer getIntCodigo() {
        return intCodigo;
    }

    public void setIntCodigo(Integer intCodigo) {
        this.intCodigo = intCodigo;
    }

    public String getIntTipo() {
        return intTipo;
    }

    public void setIntTipo(String intTipo) {
        this.intTipo = intTipo;
    }

    public int getIntAnio() {
        return intAnio;
    }

    public void setIntAnio(int intAnio) {
        this.intAnio = intAnio;
    }

    public int getIntMes() {
        return intMes;
    }

    public void setIntMes(int intMes) {
        this.intMes = intMes;
    }

    public double getIntValor() {
        return intValor;
    }

    public void setIntValor(double intValor) {
        this.intValor = intValor;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (intCodigo != null ? intCodigo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Interes)) {
            return false;
        }
        Interes other = (Interes) object;
        if ((this.intCodigo == null && other.intCodigo != null) || (this.intCodigo != null && !this.intCodigo.equals(other.intCodigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.sirec.ejb.entidades.Interes[ intCodigo=" + intCodigo + " ]";
    }
    
}
