package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Min
 */
public class Min extends RangelessDecimalProperty {

    public Min(GeoNumeric numeric) {
        super("Minimum.short", numeric);
    }

    @Override
    public Double getValue() {
        return getElement().getIntervalMin();
    }

    @Override
    public void setValue(Double value) {
        GeoNumeric numeric = getElement();
        numeric.setIntervalMin(value);
        numeric.getApp().setPropertiesOccured();
    }
}