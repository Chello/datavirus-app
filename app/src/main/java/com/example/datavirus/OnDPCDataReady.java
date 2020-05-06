package com.example.datavirus;

/**
 * This interface should be implemented if an UI wants to be updated by external calls from another instance
 */
public interface OnDPCDataReady {

    /**
     * Called when the DPC data is ready from the parser
     * @param data the DPCData obtained by the parser
     */
    void onDPCDataReady(DPCData data);
}
