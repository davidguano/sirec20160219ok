/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.ejb.entidades;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Darwin
 */
@Entity
@Table(name = "sirec.cementerio_historial_cambios")
@NamedQueries({
    @NamedQuery(name = "CementerioHistorialCambios.findAll", query = "SELECT c FROM CementerioHistorialCambios c")})
public class CementerioHistorialCambios implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "cam_cem_cod")
    private Integer camCemCod;
    @Column(name = "fecha_modifica")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModifica;
    @Size(max = 2147483647)
    @Column(name = "observacion")
    private String observacion;
    @JoinColumn(name = "usu_identificacion", referencedColumnName = "usu_identificacion")
    @ManyToOne
    private SegUsuario usuIdentificacion;
    @JoinColumn(name = "cem_codigo", referencedColumnName = "cem_codigo")
    @ManyToOne
    private Cementerio cemCodigo;

    public CementerioHistorialCambios() {
    }

    public CementerioHistorialCambios(Integer camCemCod) {
        this.camCemCod = camCemCod;
    }

    public Integer getCamCemCod() {
        return camCemCod;
    }

    public void setCamCemCod(Integer camCemCod) {
        this.camCemCod = camCemCod;
    }

    public Date getFechaModifica() {
        return fechaModifica;
    }

    public void setFechaModifica(Date fechaModifica) {
        this.fechaModifica = fechaModifica;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public SegUsuario getUsuIdentificacion() {
        return usuIdentificacion;
    }

    public void setUsuIdentificacion(SegUsuario usuIdentificacion) {
        this.usuIdentificacion = usuIdentificacion;
    }

    public Cementerio getCemCodigo() {
        return cemCodigo;
    }

    public void setCemCodigo(Cementerio cemCodigo) {
        this.cemCodigo = cemCodigo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (camCemCod != null ? camCemCod.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CementerioHistorialCambios)) {
            return false;
        }
        CementerioHistorialCambios other = (CementerioHistorialCambios) object;
        if ((this.camCemCod == null && other.camCemCod != null) || (this.camCemCod != null && !this.camCemCod.equals(other.camCemCod))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.sirec.ejb.entidades.CementerioHistorialCambios[ camCemCod=" + camCemCod + " ]";
    }
    
}
