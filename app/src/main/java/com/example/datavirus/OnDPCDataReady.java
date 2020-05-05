package com.example.datavirus;

/**
 * This interface should be implemented if an UI wants to be updated by external calls from another instance
 */
public interface OnDPCDataReady {

    //public void updateData(DPCData.DailyReport[] report);

    void setDPCData(DPCData data);
}
