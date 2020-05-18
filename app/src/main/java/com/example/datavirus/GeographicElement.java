package com.example.datavirus;

/**
 * Class that represent a geographic element with its type, as GeoField
 */
public class GeographicElement implements Comparable<GeographicElement> {

    protected String denominazione;
    protected DPCData.GeoField geoField;

    public String getDenominazione() {
        return denominazione;
    }

    public DPCData.GeoField getGeoField() {
        return geoField;
    }

    /**
     * Constructor. Requires its geographic name (denominazione) and its GeoField
     * @param denominazione the province/region name
     * @param geoField the GeoField
     */
    public GeographicElement(String denominazione, DPCData.GeoField geoField) {
        this.denominazione = denominazione;
        this.geoField = geoField;
    }

    /**
     * Constructor. Creates the object as Nazionale.
     */
    public GeographicElement() {
        this.geoField = DPCData.GeoField.NAZIONALE;
        this.denominazione = "Nazionale";//res.getString(R.string.national_poll);
    }

    @Override
    public int compareTo(GeographicElement o) {
        return this.denominazione.compareTo(o.denominazione);
    }
}