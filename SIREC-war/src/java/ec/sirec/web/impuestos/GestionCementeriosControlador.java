/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.web.impuestos;

import ec.sirec.ejb.entidades.CatalogoDetalle;
import ec.sirec.ejb.entidades.CatastroPredial;
import ec.sirec.ejb.entidades.Cementerio;
import ec.sirec.ejb.entidades.CementerioArchivo;
import ec.sirec.ejb.entidades.CementerioHistorialCambios;
import ec.sirec.ejb.entidades.DatoGlobal;
import ec.sirec.ejb.entidades.Patente;
import ec.sirec.ejb.entidades.PatenteArchivo;
import ec.sirec.ejb.entidades.PredioArchivo;
import ec.sirec.ejb.entidades.Propietario;
import ec.sirec.ejb.entidades.SegUsuario;
import ec.sirec.ejb.servicios.CatalogoDetalleServicio;
import ec.sirec.ejb.servicios.CatastroPredialServicio;
import ec.sirec.ejb.servicios.CementerioServicio;
import ec.sirec.ejb.servicios.PatenteArchivoServicio;
import ec.sirec.ejb.servicios.PatenteServicio;
import ec.sirec.ejb.servicios.PropietarioServicio;
import ec.sirec.web.base.BaseControlador;
import ec.sirec.web.util.ParametrosFile;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

/**
 *
 * @author Darwin Aldas
 */
@ManagedBean
@ViewScoped
public class GestionCementeriosControlador extends BaseControlador {

    @EJB
    private PropietarioServicio propietarioServicio;

    @EJB
    private CementerioServicio cementerioServicio;

    @EJB
    private PatenteArchivoServicio patenteArchivoServicio;

    @EJB
    private CatalogoDetalleServicio catalogoDetalleServicio;

    @EJB
    private PatenteServicio patenteServicio;

    private Cementerio cementerioActual;
    private Propietario propietarioActual;
    private CatalogoDetalle catDetGenero;
    private CatalogoDetalle catDetUbicAtaud;
    private CatalogoDetalle catDetEstadoDefun;
    private CatalogoDetalle catDetTipNicho;
    private CatalogoDetalle catDetParroquia;
    private boolean habilitaEditar;
    private Date fechaFallece;
    private Date fehcaFinContrato;
    private DatoGlobal datoGlobalActual;
    private SegUsuario usuarioActual;
    private List<CatalogoDetalle> listGenero;
    private List<CatalogoDetalle> listaUbicAtaud;
    private List<CatalogoDetalle> listEstadoDefun;
    private List<CatalogoDetalle> listTipoNicho;
    private List<ParametrosFile> listaFiles;
    private List<PatenteArchivo> listadoArchivos;
    private List<CatalogoDetalle> listaHorarioFunciona;
    private List<CatalogoDetalle> listaCatDetParroquias;
    private List<Cementerio> listaOccisoInumado;
    private List<Cementerio> listOccisoExumado;
    private List<Cementerio> listaNombreOccisos;
    private String ciRuc;
    private String observacionesCambioHis;
    private int verBuscaCementerio;
    private String numNicho;
    private int resulBusqueda;
    private int busOpcion;
    private int busxNicho;
    private int busxOcciso;
    private int verNicho;
    private int verSuelo;
    /**
     * Creates a new instance of GestionPatenteControlador
     */
    private String numPatente;
    private static final Logger LOGGER = Logger.getLogger(GestionCementeriosControlador.class.getName());

    @PostConstruct
    public void inicializar() {
        try {
            verNicho = 0;
            verSuelo = 0;
            cementerioActual = new Cementerio();
            propietarioActual = new Propietario();
            numPatente = generaNumPatente();
            catDetGenero = new CatalogoDetalle();
            catDetUbicAtaud = new CatalogoDetalle();
            catDetEstadoDefun = new CatalogoDetalle();
            catDetTipNicho = new CatalogoDetalle();
            catDetParroquia = new CatalogoDetalle();
            propietarioActual = new Propietario();
            habilitaEditar = false;
            listaFiles = new ArrayList<ParametrosFile>();
            ciRuc = "";
            observacionesCambioHis = "";
            listaNombreOccisos = new ArrayList<Cementerio>();
            verBuscaCementerio = 0;
            numNicho = "";
            resulBusqueda = 0;
            busOpcion = 0;
            busxNicho = 0;
            busxOcciso = 0;
            listarGeneroSexo();
            listarUbicacionAtaud();
            listarTipoNicho();
            listarEstadoDefuncion();
            listarHorarioFuncionamiento();
            listarParroquias();
            listarOccisoInhumado();
            listarOccisoExumado();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public GestionCementeriosControlador() {
    }

    public void cargaObjetosBitacora() {
        try {
            datoGlobalActual = new DatoGlobal();
            usuarioActual = new SegUsuario();
            datoGlobalActual = patenteServicio.cargarObjDatGloPorNombre("Msj_cementerio_In");
            usuarioActual = (SegUsuario) this.getSession().getAttribute("usuario");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void limpiarObjetosBitacora() {
        datoGlobalActual = new DatoGlobal();
        usuarioActual = new SegUsuario();
    }

    public void guardarCementerios() {
        System.out.println("Datos" + ciRuc);
        try {
            if (habilitaEditar == false) {
                if (existeRuc()) {
                    cargaObjetosBitacora();
                    CatalogoDetalle objCatDet = new CatalogoDetalle();
                    cementerioActual.setProCi(propietarioActual);
                    cementerioActual.setCatdetParroquia(catDetParroquia);
                    objCatDet = catalogoDetalleServicio.buscarPorCodigoCatDet(catDetGenero.getCatdetCodigo());
                    cementerioActual.setCemGenero(objCatDet.getCatdetCod());
                    objCatDet = catalogoDetalleServicio.buscarPorCodigoCatDet(catDetUbicAtaud.getCatdetCodigo());
                    cementerioActual.setCemUbicacion(objCatDet.getCatdetCod());
                    objCatDet = catalogoDetalleServicio.buscarPorCodigoCatDet(catDetEstadoDefun.getCatdetCodigo());
                    cementerioActual.setCemEstado(objCatDet.getCatdetCod());
                    objCatDet = catalogoDetalleServicio.buscarPorCodigoCatDet(catDetTipNicho.getCatdetCodigo());
                    cementerioActual.setCemTipo(objCatDet.getCatdetCod());
                    cementerioActual.setCemFechaFallece(fechaFallece);
                    cementerioActual.setCemFechaFinContrato(fehcaFinContrato);
                    cementerioActual.setUsuIdentificacion(usuarioActual);
                    cementerioActual.setUltaccDetalle(datoGlobalActual.getDatgloValor());
                    cementerioActual.setUltaccMarcatiempo(java.util.Calendar.getInstance().getTime());
                    cementerioServicio.crearCementerio(cementerioActual);
                    if (!listaFiles.isEmpty()) {
                        guardarArchivos();
                    }
                    addSuccessMessage("Guardado Exitosamente");
                    cementerioActual = new Cementerio();
                    limpiarObjetosBitacora();
                    inicializar();
                } else {
                    addWarningMessage("Cédula/Ruc no existe en la base de datos");
                }
            } else {
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public void buscaSelecciona() {
        switch (busOpcion) {
            case 1:
                busxNicho = 1;
                busxOcciso = 0;
                break;
            case 2:
                busxNicho = 0;
                busxOcciso = 1;
                break;
        }
    }

    public void activaNichoSuelo() {
        CatalogoDetalle objCatDetAux = new CatalogoDetalle();
        try {
            objCatDetAux = catalogoDetalleServicio.buscarPoCatdetTexCatdetCod("NICHO", "N");
            if (catDetUbicAtaud.getCatdetCodigo() == objCatDetAux.getCatdetCodigo()) {
                verNicho = 1;
                verSuelo = 0;
            } else {
                verSuelo = 1;
                verNicho = 0;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

    }

    public void guardarCementerioHistorial() {
        try {
            //---actualizo el estado de inhumado a exhumado
//            cementerioActual.setCemEstado("E");
//            cementerioServicio.editarCementerio(cementerioActual);
            //---guardo historial
            cargaObjetosBitacora();
            CementerioHistorialCambios objCemHis = new CementerioHistorialCambios();
            objCemHis.setCemCodigo(cementerioActual);
            objCemHis.setObservacion(observacionesCambioHis);
            objCemHis.setFechaModifica(java.util.Calendar.getInstance().getTime());
            objCemHis.setUsuIdentificacion(usuarioActual);
            if (!listaFiles.isEmpty()) {
                guardarArchivos();
            }
            cementerioServicio.crearCementerioHistorial(objCemHis);
            addSuccessMessage("Guardado Exitosamente");
            listarOccisoExumado();
            listarOccisoInhumado();
            limpiarObjetosBitacora();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public List<Cementerio> sugiereNombreOcciso(String nombre) {
        cementerioActual.setCemNombreOcciso(nombre);
        List<Cementerio> listNombres = new ArrayList<Cementerio>();
        try {
            listaNombreOccisos = cementerioServicio.listarOccisosPorNombre(cementerioActual.getCemNombreOcciso());
            for (Cementerio cementerio : listaNombreOccisos) {
                listNombres.add(cementerio);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listNombres;
    }

    public void onItemSelectCementerio(SelectEvent event) throws Exception {
        Cementerio objCem = (Cementerio) event.getObject();
        cementerioActual = cementerioServicio.buscarCementerioPorId(objCem.getCemCodigo());
        if (cementerioActual == null) {
            resulBusqueda = 1;
        }
    }

    public void buscaNichoParroquia() {
        try {
            cementerioActual = cementerioServicio.buscarPorParroquiaNumNicho(catDetParroquia.getCatdetCodigo(), numNicho);
            if (cementerioActual == null) {
                resulBusqueda = 1;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public boolean validaNumeroNichoxParroquia() {
        System.err.println("Ingresa a la validacion de nicho");
        boolean existeNicho = true;
        Cementerio objCemAux = new Cementerio();
        try {
            objCemAux = cementerioServicio.buscarPorParroquiaNumNicho(catDetParroquia.getCatdetCodigo(), cementerioActual.getCemNumNicho());
            if (objCemAux != null) {
                existeNicho = false;
                addErrorMessage("Nº de nicho ingresado existente en parroquia:" + catDetParroquia.getCatdetTexto());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
        return existeNicho;
    }

    public void buscarCementerio() {
        try {
            System.out.println("Entra al metodo buscar");
            verBuscaCementerio = 1;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public boolean existeRuc() {
        boolean existe = false;
        try {
            propietarioActual = propietarioServicio.buscarPropietario(ciRuc);
            if (propietarioActual != null) {
                existe = true;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return existe;
    }

    public void onTabChange(TabChangeEvent event) {
//        if (event.getTab().getId().equals("patDet")) {
//            GestionDetPatenteControlador objGesDetControlador = new GestionDetPatenteControlador();
//            objGesDetControlador.inicializar();
//        }
//        if (event.getTab().getId().equals("exoDedMulPat")) {
//            GestionExoDedMulPatenteControlador objGesExoDedMulPat = new GestionExoDedMulPatenteControlador();
//            objGesExoDedMulPat.inicializar();
//        }
    }

    public void guardarArchivos() {
        Iterator<ParametrosFile> itera = listaFiles.iterator();
        try {

            while (itera.hasNext()) {
                ParametrosFile elemento = itera.next();
                CementerioArchivo cemArchivo = new CementerioArchivo();
                cemArchivo.setCemCodigo(cementerioActual);
                cemArchivo.setCemarcNombre(elemento.getName());
                cemArchivo.setCemarcData(elemento.getData());
                cemArchivo.setCemarcTipo("CA"); //Archivo de Cementerios
                cemArchivo.setUsuIdentificacion(usuarioActual.getUsuIdentificacion());
                cemArchivo.setUltaccDetalle(datoGlobalActual.getDatgloValor());
                cemArchivo.setUltaccMarcaTiempo(java.util.Calendar.getInstance().getTime());
                patenteArchivoServicio.guardarCementerioArchivo(cemArchivo);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public String generaNumPatente() { //Genera numero de patente aleatorio
        String numeroPatente = "";
        try {
            Patente objPatente = new Patente();
            objPatente = patenteServicio.cargarMaxObjPatente();
            int valorRetornado = objPatente.getPatCodigo() + 1;
            StringBuffer numSecuencial = new StringBuffer(valorRetornado + "");
            int valRequerido = 6;
            int valRetorno = numSecuencial.length();
            int valNecesita = valRequerido - valRetorno;
            StringBuffer sb = new StringBuffer(valNecesita);
            for (int i = 0; i < valNecesita; i++) {
                sb.append("0");
            }
            numeroPatente = "AE-MPM-" + sb.toString() + valorRetornado;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return numeroPatente;
    }

    public void listarGeneroSexo() throws Exception {
        listGenero = catalogoDetalleServicio.listarPorNemonicoCatalogo("GENERO_SX");
    }

    public void listarOccisoInhumado() throws Exception {
        listaOccisoInumado = cementerioServicio.listarOccisoInhumado();
    }

    public void listarOccisoExumado() throws Exception {
        listOccisoExumado = cementerioServicio.listarOccisoExhumado();
    }

    public void listarUbicacionAtaud() throws Exception {
        listaUbicAtaud = catalogoDetalleServicio.listarPorNemonicoCatalogo("UBIC_ATAUD");
    }

    public void listarEstadoDefuncion() throws Exception {
        listEstadoDefun = catalogoDetalleServicio.listarPorNemonicoCatalogo("DEFUN_ESTADO");
    }

    public void listarParroquias() throws Exception {
        listaCatDetParroquias = catalogoDetalleServicio.listarPorNemonicoCatalogo("PARROQUIAS");
    }

    public void listarTipoNicho() throws Exception {
        listTipoNicho = catalogoDetalleServicio.listarPorNemonicoCatalogo("TIPO_NICHO");
    }

    public void listarHorarioFuncionamiento() throws Exception {
        listaHorarioFunciona = catalogoDetalleServicio.listarPorNemonicoCatalogo("HOR_FUNCIONA");
    }
//-----Carga de archivos

    public void handleFileUpload(FileUploadEvent event) throws Exception {
        try {
            InputStream is = event.getFile().getInputstream();
            ParametrosFile archivo = new ParametrosFile();
            archivo.setLength(event.getFile().getSize());
            archivo.setName(event.getFile().getFileName());
            archivo.setData(event.getFile().getContents());
            listaFiles.add(archivo);
            addSuccessMessage("Archivo Cargado", "");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void confirmaEliminarArchivo(ParametrosFile archivo) {
        try {
            listaFiles.remove(archivo);
            addSuccessMessage("Archivo Eliminado");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
            addWarningMessage("No se puede eliminar el regitro");
        }
    }

    public void recuperarCementerioCampos(Cementerio cementerio) {
        try {
            cementerioActual = cementerio;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public void confirmaEliminarPatArchivo(PatenteArchivo file) {
        try {
//            cementerioServicio.eliminarArchivo(file);
            addSuccessMessage("Registro Eliminado");
//            listadoArchivos = patenteArchivoServicio.listarArchivoPorPatente(cementerioActual);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
            addWarningMessage("No se puede eliminar el regitro");
        }
    }

    public String getNumPatente() {
        return numPatente;
    }

    public void setNumPatente(String numPatente) {
        this.numPatente = numPatente;
    }

    public Propietario getPropietarioActual() {
        return propietarioActual;
    }

    public void setPropietarioActual(Propietario propietarioActual) {
        this.propietarioActual = propietarioActual;
    }

    public CatalogoDetalle getCatDetGenero() {
        return catDetGenero;
    }

    public void setCatDetGenero(CatalogoDetalle catDetGenero) {
        this.catDetGenero = catDetGenero;
    }

    public List<CatalogoDetalle> getListGenero() {
        return listGenero;
    }

    public void setListGenero(List<CatalogoDetalle> listGenero) {
        this.listGenero = listGenero;
    }

    public CatalogoDetalle getCatDetUbicAtaud() {
        return catDetUbicAtaud;
    }

    public void setCatDetUbicAtaud(CatalogoDetalle catDetUbicAtaud) {
        this.catDetUbicAtaud = catDetUbicAtaud;
    }

    public List<CatalogoDetalle> getListaUbicAtaud() {
        return listaUbicAtaud;
    }

    public void setListaUbicAtaud(List<CatalogoDetalle> listaUbicAtaud) {
        this.listaUbicAtaud = listaUbicAtaud;
    }

    public CatalogoDetalle getCatDetEstadoDefun() {
        return catDetEstadoDefun;
    }

    public void setCatDetEstadoDefun(CatalogoDetalle catDetEstadoDefun) {
        this.catDetEstadoDefun = catDetEstadoDefun;
    }

    public List<CatalogoDetalle> getListEstadoDefun() {
        return listEstadoDefun;
    }

    public void setListEstadoDefun(List<CatalogoDetalle> listEstadoDefun) {
        this.listEstadoDefun = listEstadoDefun;
    }

    public CatalogoDetalle getCatDetTipNicho() {
        return catDetTipNicho;
    }

    public void setCatDetTipNicho(CatalogoDetalle catDetTipNicho) {
        this.catDetTipNicho = catDetTipNicho;
    }

    public List<CatalogoDetalle> getListTipoNicho() {
        return listTipoNicho;
    }

    public void setListTipoNicho(List<CatalogoDetalle> listTipoNicho) {
        this.listTipoNicho = listTipoNicho;
    }

    public Cementerio getCementerioActual() {
        return cementerioActual;
    }

    public void setCementerioActual(Cementerio cementerioActual) {
        this.cementerioActual = cementerioActual;
    }

    public Date getFechaFallece() {
        return fechaFallece;
    }

    public void setFechaFallece(Date fechaFallece) {
        this.fechaFallece = fechaFallece;
    }

    public Date getFehcaFinContrato() {
        return fehcaFinContrato;
    }

    public void setFehcaFinContrato(Date fehcaFinContrato) {
        this.fehcaFinContrato = fehcaFinContrato;
    }

    public List<ParametrosFile> getListaFiles() {
        return listaFiles;
    }

    public void setListaFiles(List<ParametrosFile> listaFiles) {
        this.listaFiles = listaFiles;
    }

    public List<PatenteArchivo> getListadoArchivos() {
        return listadoArchivos;
    }

    public void setListadoArchivos(List<PatenteArchivo> listadoArchivos) {
        this.listadoArchivos = listadoArchivos;
    }

    public List<CatalogoDetalle> getListaHorarioFunciona() {
        return listaHorarioFunciona;
    }

    public void setListaHorarioFunciona(List<CatalogoDetalle> listaHorarioFunciona) {
        this.listaHorarioFunciona = listaHorarioFunciona;
    }

    public CatalogoDetalle getCatDetParroquia() {
        return catDetParroquia;
    }

    public void setCatDetParroquia(CatalogoDetalle catDetParroquia) {
        this.catDetParroquia = catDetParroquia;
    }

    public List<CatalogoDetalle> getListaCatDetParroquias() {
        return listaCatDetParroquias;
    }

    public void setListaCatDetParroquias(List<CatalogoDetalle> listaCatDetParroquias) {
        this.listaCatDetParroquias = listaCatDetParroquias;
    }

    public String getCiRuc() {
        return ciRuc;
    }

    public void setCiRuc(String ciRuc) {
        this.ciRuc = ciRuc;
    }

    public List<Cementerio> getListaOccisoInumado() {
        return listaOccisoInumado;
    }

    public void setListaOccisoInumado(List<Cementerio> listaOccisoInumado) {
        this.listaOccisoInumado = listaOccisoInumado;
    }

    public List<Cementerio> getListOccisoExumado() {
        return listOccisoExumado;
    }

    public void setListOccisoExumado(List<Cementerio> listOccisoExumado) {
        this.listOccisoExumado = listOccisoExumado;
    }

    public String getObservacionesCambioHis() {
        return observacionesCambioHis;
    }

    public void setObservacionesCambioHis(String observacionesCambioHis) {
        this.observacionesCambioHis = observacionesCambioHis;
    }

    public int getVerBuscaCementerio() {
        return verBuscaCementerio;
    }

    public void setVerBuscaCementerio(int verBuscaCementerio) {
        this.verBuscaCementerio = verBuscaCementerio;
    }

    public String getNumNicho() {
        return numNicho;
    }

    public void setNumNicho(String numNicho) {
        this.numNicho = numNicho;
    }

    public int getResulBusqueda() {
        return resulBusqueda;
    }

    public void setResulBusqueda(int resulBusqueda) {
        this.resulBusqueda = resulBusqueda;
    }

    public int getBusOpcion() {
        return busOpcion;
    }

    public void setBusOpcion(int busOpcion) {
        this.busOpcion = busOpcion;
    }

    public int getBusxNicho() {
        return busxNicho;
    }

    public void setBusxNicho(int busxNicho) {
        this.busxNicho = busxNicho;
    }

    public int getBusxOcciso() {
        return busxOcciso;
    }

    public void setBusxOcciso(int busxOcciso) {
        this.busxOcciso = busxOcciso;
    }

    public int getVerNicho() {
        return verNicho;
    }

    public void setVerNicho(int verNicho) {
        this.verNicho = verNicho;
    }

    public int getVerSuelo() {
        return verSuelo;
    }

    public void setVerSuelo(int verSuelo) {
        this.verSuelo = verSuelo;
    }

}
