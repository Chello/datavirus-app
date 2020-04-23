package com.example.datavirus;

/**
 * This interface should be implemented if an UI wants to be updated by external calls from another instance
 */
public interface UpdateUI {

    public void updateData(DPCData data);

    public void setParser(DataParser parser);
}
