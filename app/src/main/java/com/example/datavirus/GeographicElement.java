package com.example.datavirus;

public class GeographicElement {
    //TODO write doc
    private String denominazione;
    private DPCData.GeoField geoField;

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
}