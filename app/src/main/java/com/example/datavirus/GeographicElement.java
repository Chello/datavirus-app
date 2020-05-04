package com.example.datavirus;

import androidx.annotation.Nullable;

public class GeographicElement implements Comparable<GeographicElement> {
    //TODO write doc
    protected String denominazione;
    protected DPCData.GeoField geoField;

    public String getDenominazione() {
        return denominazione;
    }

    public DPCData.GeoField getGeoField() {
        return geoField;
    }

    public GeographicElement(String denominazione, DPCData.GeoField geoField) {
        this.denominazione = denominazione;
        this.geoField = geoField;
    }

    public GeographicElement(DPCData.GeoField geoField) {
        this.geoField = geoField;
        this.denominazione = "Nazionale";//res.getString(R.string.national_poll);
    }

    @Override
    public int compareTo(GeographicElement o) {
        return this.denominazione.compareTo(o.denominazione);
    }
}