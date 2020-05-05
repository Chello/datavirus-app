package com.example.datavirus;

/**
 * Contains also a field (like "casi_totali" or "dimessi_guariti")
 */
public class FieldGeographicElement extends GeographicElement {

    private String field;

    public FieldGeographicElement(GeographicElement geographicElement, String field) {
        super(geographicElement.getDenominazione(), geographicElement.getGeoField());
        this.field = field;
    }

    public String getField() {
        return this.field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setDenominazione(String denominazione) {
        super.denominazione = denominazione;
    }

    public void setGeoField(DPCData.GeoField geoField) {
        super.geoField = geoField;
    }


    public int compareTo(FieldGeographicElement o) {
        return super.compareTo(o) + this.field.compareTo(o.field);
    }
}
