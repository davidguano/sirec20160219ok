/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.sirec.web.converters;

import ec.sirec.ejb.entidades.Cementerio;
import ec.sirec.web.base.BaseControlador;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

/**
 *
 * @author dvaldas
 */
@ManagedBean(name = "cementerioConverter")
@ViewScoped
public class CementerioConverter extends BaseControlador implements Converter {

    @Override
    public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) throws ConverterException {

        if (arg2.trim().equals("") || arg2.equals("null")) {
            return null;
        } else {
            return new Cementerio(Integer.valueOf(arg2));
        }

    }

    @Override
    public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ConverterException {
        if (arg2 == null || arg2.equals("")) {
            return "";
        } else {
            return String.valueOf(((Cementerio) arg2).getCemCodigo());
        }
    }

    @SuppressWarnings("unchecked")
    private Cementerio getObjectFromUIPickListComponent(UIComponent component, String value) {
        final DualListModel<Cementerio> dualList;
        try {
            dualList = (DualListModel<Cementerio>) ((PickList) component).getValue();
            Cementerio concep = getObjectFromList(dualList.getSource(), Integer.valueOf(value));
            if (concep == null) {
                concep = getObjectFromList(dualList.getTarget(), Integer.valueOf(value));
            }
            return concep;
        } catch (ClassCastException cce) {
            throw new ConverterException();
        } catch (NumberFormatException nfe) {
            throw new ConverterException();
        }
    }

    private Cementerio getObjectFromList(final List<?> list, final Integer identifier) {
        for (final Object object : list) {
            final Cementerio estructuraDatos = (Cementerio) object;
            if (estructuraDatos.getCemCodigo().equals(identifier)) {
                return estructuraDatos;
            }
        }
        return null;
    }
}
