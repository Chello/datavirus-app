package com.example.datavirus;

/**
 * This interface should be implemented in classes for listening the DPCGeoPicker
 */
public interface OnDPCGeoListener {
    /**
     * Called when the GeoPicker has the value choosen by the user
     * @param element the element chosen by the user
     */
    void onDPCGeoClick(GeographicElement element);
}